package com.example.studygroup.controller;

import com.example.studygroup.domain.MemberStatus;
import com.example.studygroup.domain.room.RoomComment;
import com.example.studygroup.domain.room.RoomNotice;
import com.example.studygroup.domain.room.RoomPost;
import com.example.studygroup.repository.RoomChatMessageRepository;
import com.example.studygroup.repository.RoomCommentRepository;
import com.example.studygroup.repository.RoomNoticeRepository;
import com.example.studygroup.repository.RoomPostRepository;
import com.example.studygroup.service.RoomPhotoService;
import com.example.studygroup.service.StudyMemberService;
import com.example.studygroup.service.StudyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final StudyService studyService;
    private final StudyMemberService studyMemberService;

    // 게시판/댓글/채팅
    private final RoomPostRepository roomPostRepository;
    private final RoomCommentRepository roomCommentRepository;
    private final RoomChatMessageRepository roomChatMessageRepository;

    // 공지
    private final RoomNoticeRepository roomNoticeRepository;

    // 사진
    private final RoomPhotoService roomPhotoService;

    /* =========================
       공통 유틸
     ========================= */

    private Long getLoginUserId(HttpSession session) {
        return (Long) session.getAttribute("loginUserId");
    }

    private String getLoginUserName(HttpSession session, Long loginUserId) {
        String name = (String) session.getAttribute("loginUserName");
        if (name != null && !name.isBlank()) return name;
        return "USER" + loginUserId;
    }

    private boolean canEnterRoom(Long studyId, Long loginUserId) {
        var study = studyService.findStudyById(studyId);

        // 작성자면 OK
        if (loginUserId != null && loginUserId.equals(study.getAuthorId())) return true;

        // 승인된 멤버만
        MemberStatus status = studyMemberService.getApplicationStatus(studyId, loginUserId);
        return status == MemberStatus.APPROVED;
    }

    /* =========================
       룸 홈 (사진/대표사진)
     ========================= */

    @GetMapping("/study/{id}/room")
    public String roomHome(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("photos", roomPhotoService.list(id));
        model.addAttribute("coverPhoto", roomPhotoService.getCover(id));
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("activeTab", "home");

        return "study/studyRoom";
    }

    @PostMapping("/study/{id}/room/photos")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        roomPhotoService.upload(id, loginUserId, getLoginUserName(session, loginUserId), file);
        return "redirect:/study/" + id + "/room";
    }

    @PostMapping("/study/{id}/room/photos/{photoId}/delete")
    public String deletePhoto(@PathVariable Long id,
                              @PathVariable Long photoId,
                              HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        roomPhotoService.delete(id, photoId, loginUserId);
        return "redirect:/study/" + id + "/room";
    }

    @PostMapping("/study/{id}/room/photos/{photoId}/cover")
    public String setCover(@PathVariable Long id,
                           @PathVariable Long photoId,
                           HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        roomPhotoService.setCover(id, photoId, loginUserId);
        return "redirect:/study/" + id + "/room";
    }

    /* =========================
       게시판
     ========================= */

    @GetMapping("/study/{id}/room/board")
    public String board(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("posts", roomPostRepository.findByStudyIdOrderByIdDesc(id));
        model.addAttribute("activeTab", "board");

        return "study/board";
    }

    @PostMapping("/study/{id}/room/board/write")
    public String writePost(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String content,
                            HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        RoomPost post = new RoomPost();
        post.setStudyId(id);
        post.setTitle(title);
        post.setContent(content);
        post.setWriterId(loginUserId);
        post.setWriterName(getLoginUserName(session, loginUserId));

        roomPostRepository.save(post);
        return "redirect:/study/" + id + "/room/board";
    }

    @GetMapping("/study/{id}/room/board/{postId}")
    public String postDetail(@PathVariable Long id,
                             @PathVariable Long postId,
                             Model model,
                             HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        RoomPost post = roomPostRepository.findById(postId).orElse(null);
        if (post == null || !post.getStudyId().equals(id)) {
            return "redirect:/study/" + id + "/room/board";
        }

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("post", post);
        model.addAttribute("comments", roomCommentRepository.findByPostIdOrderByIdAsc(postId));
        model.addAttribute("activeTab", "board");

        return "study/boardDetail";
    }

    @PostMapping("/study/{id}/room/board/{postId}/comment")
    public String writeComment(@PathVariable Long id,
                               @PathVariable Long postId,
                               @RequestParam String content,
                               HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        RoomPost post = roomPostRepository.findById(postId).orElse(null);
        if (post == null || !post.getStudyId().equals(id)) {
            return "redirect:/study/" + id + "/room/board";
        }

        RoomComment c = new RoomComment();
        c.setStudyId(id);
        c.setPostId(postId);
        c.setContent(content);
        c.setWriterId(loginUserId);
        c.setWriterName(getLoginUserName(session, loginUserId));

        roomCommentRepository.save(c);
        return "redirect:/study/" + id + "/room/board/" + postId;
    }

    /* =========================
       공지 (목록/작성/삭제)
     ========================= */

    @GetMapping("/study/{id}/room/notice")
    public String notice(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("notices", roomNoticeRepository.findByStudyIdOrderByIdDesc(id));
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("activeTab", "notice");

        return "study/notice";
    }

    @PostMapping("/study/{id}/room/notice/write")
    public String writeNotice(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String content,
                              HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        RoomNotice notice = new RoomNotice();
        notice.setStudyId(id);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setWriterId(loginUserId);
        notice.setWriterName(getLoginUserName(session, loginUserId));

        roomNoticeRepository.save(notice);
        return "redirect:/study/" + id + "/room/notice";
    }

    @PostMapping("/study/{id}/room/notice/{noticeId}/delete")
    public String deleteNotice(@PathVariable Long id,
                               @PathVariable Long noticeId,
                               HttpSession session) {

        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        RoomNotice notice = roomNoticeRepository.findById(noticeId).orElse(null);
        if (notice == null || !notice.getStudyId().equals(id)) {
            return "redirect:/study/" + id + "/room/notice";
        }

        // 작성자만 삭제 가능
        if (!notice.getWriterId().equals(loginUserId)) {
            return "redirect:/study/" + id + "/room/notice";
        }

        roomNoticeRepository.delete(notice);
        return "redirect:/study/" + id + "/room/notice";
    }

    /* =========================
       채팅/멤버
     ========================= */

    @GetMapping("/study/{id}/room/chat")
    public String chat(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("activeTab", "chat");
        model.addAttribute("messages", roomChatMessageRepository.findTop100ByStudyIdOrderByIdAsc(id));

        return "study/chat";
    }

    @GetMapping("/study/{id}/room/members")
    public String members(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";
        if (!canEnterRoom(id, loginUserId)) return "redirect:/study/" + id + "?error=not_approved";

        model.addAttribute("study", studyService.findStudyById(id));
        model.addAttribute("activeTab", "members");
        return "study/members";
    }
}
