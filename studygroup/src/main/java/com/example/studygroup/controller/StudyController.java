package com.example.studygroup.controller;

import com.example.studygroup.domain.study.RecruitStatus;
import com.example.studygroup.dto.request.study.StudyCreateRequest;
import com.example.studygroup.dto.request.study.StudyUpdateRequest;
import com.example.studygroup.service.KeywordService;
import com.example.studygroup.service.StudyService;
import com.example.studygroup.service.StudyMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// 페이지로 변경 import문
import org.springframework.data.domain.Page;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final KeywordService keywordService;
    private final StudyMemberService studyMemberService;

    // ✅ 메인: 조회수 TOP3 + 최신 프리뷰 + 키워드 리스트
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredStudies", studyService.findFeaturedStudies());
        model.addAttribute("previewStudies", studyService.findPreviewStudies());
        model.addAttribute("keywordList", keywordService.findPopularTop5());
        return "study/home";
    }

    // ✅ 전체보기 페이지(검색/키워드 필터)
    @GetMapping("/studies")
    public String studies(@RequestParam(required = false) Long keywordId,
                          @RequestParam(required = false) String q,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {

        Page<StudyService.StudyDto> studyPage = studyService.searchStudies(keywordId, q, page);

        model.addAttribute("studyPage", studyPage);
        model.addAttribute("studyList", studyPage.getContent());

        model.addAttribute("keywordList", keywordService.findAll()); // 전체보기에서는 전체 키워드가 더 자연스러움
        model.addAttribute("selectedKeywordId", keywordId);
        model.addAttribute("q", q);
        model.addAttribute("page", page);

        return "study/list";
    }

    // 스터디 작성 페이지
    @GetMapping("/study/create")
    public String createPage(HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }
        model.addAttribute("keywordList", keywordService.findAll());
        return "study/create";
    }

    // 스터디 작성 처리
    @PostMapping("/study/create")
    public String create(@ModelAttribute StudyCreateRequest request, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        Long studyId = studyService.createStudy(request, loginUserId);
        return "redirect:/study/" + studyId;
    }

    // 스터디 상세 페이지(조회수 +1은 service에서 처리)
    @GetMapping("/study/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session, HttpServletRequest request) {
        StudyService.StudyDetailDto study = studyService.findStudyById(id);
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        String referer = request.getHeader("Referer"); // 이전 페이지
        model.addAttribute("returnUrl", referer);

        model.addAttribute("study", study);
        model.addAttribute("isAuthor", loginUserId != null && loginUserId.equals(study.getAuthorId()));
        boolean hasApplied = loginUserId != null && studyMemberService.hasApplied(id, loginUserId);
        model.addAttribute("hasApplied", hasApplied);
        return "study/detail";
    }

    // 스터디 수정 페이지
    @GetMapping("/study/{id}/edit")
    public String editPage(@PathVariable Long id, Model model, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        StudyService.StudyDetailDto study = studyService.findStudyById(id);

        // 작성자 권한 체크
        if (!loginUserId.equals(study.getAuthorId())) {
            return "redirect:/study/" + id + "?error=unauthorized";
        }

        model.addAttribute("study", study);
        return "study/edit";
    }

    // 스터디 수정 처리
    @PostMapping("/study/{id}/edit")
    public String update(@PathVariable Long id,
                        @ModelAttribute StudyUpdateRequest request,
                        HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        try {
            studyService.updateStudy(id, request, loginUserId);
            return "redirect:/study/" + id;
        } catch (IllegalStateException e) {
            return "redirect:/study/" + id + "?error=unauthorized";
        }
    }

    // 스터디 삭제
    @PostMapping("/study/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) String returnUrl,
                         HttpServletRequest request,
                         HttpSession session) {

        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        try {
            studyService.deleteStudy(id, loginUserId);

            // 1) 파라미터 returnUrl 우선
            String target = returnUrl;

            // 2) 없으면 referer 헤더 fallback
            if (target == null || target.isBlank()) {
                target = request.getHeader("Referer");
            }

            // 3) 그래도 없으면 기본
            if (target == null || target.isBlank()) {
                return "redirect:/studies?deleted=true";
            }

            // 외부 redirect 방지(간단 버전)
            if (target.startsWith("http")) {
                return "redirect:/studies?deleted=true";
            }

            return "redirect:" + target;

        } catch (IllegalStateException e) {
            return "redirect:/study/" + id + "?error=unauthorized";
        }
    }



    // 모집 상태 변경
    @PostMapping("/study/{id}/status")
    public String changeStatus(@PathVariable Long id,
                              @RequestParam RecruitStatus status,
                              HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        try {
            studyService.changeRecruitStatus(id, status, loginUserId);
            return "redirect:/study/" + id;
        } catch (IllegalStateException e) {
            return "redirect:/study/" + id + "?error=unauthorized";
        }
    }
    @GetMapping("/study/{id}/room")
    public String studyRoom(@PathVariable Long id, Model model, HttpSession session) {

        // 로그인 유저
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        // 스터디 정보 조회
        StudyService.StudyDetailDto study = studyService.findStudyById(id);

        model.addAttribute("study", study);
        model.addAttribute("loginUserId", loginUserId);

        return "study/studyRoom";
    }

}



