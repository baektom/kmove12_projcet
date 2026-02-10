package com.example.studygroup.controller;

import com.example.studygroup.domain.MemberStatus;
import com.example.studygroup.service.StudyMemberService;
import com.example.studygroup.service.StudyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;
    private final StudyService studyService;

    /**
     * 참가 신청 페이지
     * GET /study/{id}/apply
     */
    @GetMapping("/study/{id}/apply")
    public String applyPage(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        // 스터디 정보
        StudyService.StudyDetailDto study = studyService.findStudyById(id);

        // 작성자는 신청 페이지 접근 불가
        if (study.getAuthorId() != null && study.getAuthorId().equals(loginUserId)) {
            return "redirect:/study/" + id;
        }

        // 신청 상태 확인
        MemberStatus status = studyMemberService.getApplicationStatus(id, loginUserId);

        // 이미 승인/대기면 신청 페이지로 못 오게 (상세로 돌려보냄)
        if (status == MemberStatus.APPROVED || status == MemberStatus.PENDING) {
            return "redirect:/study/" + id;
        }

        boolean isReapply = (status == MemberStatus.REJECTED);

        model.addAttribute("study", study);
        model.addAttribute("isReapply", isReapply);
        return "study/apply";
    }

    /**
     * 참가 신청 제출
     * POST /study/{id}/apply
     */
    @PostMapping("/study/{id}/apply")
    public String applySubmit(@PathVariable Long id,
                              @RequestParam String applicationMessage,
                              HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        try {
            studyMemberService.applyForStudy(id, loginUserId, applicationMessage);
            return "redirect:/study/" + id + "?applied=true";
        } catch (IllegalStateException e) {
            // 이미 신청했거나 작성자인 경우 등
            return "redirect:/study/" + id + "?error=already_applied";
        }
    }

    /**
     * 작성자(관리자)가 신청 관리 페이지 보기
     * GET /study/{id}/applications
     */
    @GetMapping("/study/{id}/applications")
    public String applicationsPage(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        StudyService.StudyDetailDto study = studyService.findStudyById(id);

        // 작성자만 접근 가능
        if (study.getAuthorId() == null || !study.getAuthorId().equals(loginUserId)) {
            return "redirect:/study/" + id + "?error=unauthorized";
        }

        model.addAttribute("study", study);
        model.addAttribute("pendingMembers", studyMemberService.getPendingMembers(id, loginUserId));
        model.addAttribute("approvedMembers", studyMemberService.getApprovedMembers(id));

        return "study/applications";
    }

    /**
     * 신청 승인
     * POST /study/member/{memberId}/approve
     */
    @PostMapping("/study/member/{memberId}/approve")
    public String approve(@PathVariable Long memberId,
                          @RequestParam Long studyId,
                          HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        studyMemberService.approveMember(memberId, loginUserId);
        return "redirect:/study/" + studyId + "/applications";
    }

    /**
     * 신청 거부
     * POST /study/member/{memberId}/reject
     */
    @PostMapping("/study/member/{memberId}/reject")
    public String reject(@PathVariable Long memberId,
                         @RequestParam Long studyId,
                         HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        studyMemberService.rejectMember(memberId, loginUserId);
        return "redirect:/study/" + studyId + "/applications";
    }
}
