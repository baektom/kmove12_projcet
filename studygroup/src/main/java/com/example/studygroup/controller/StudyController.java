package com.example.studygroup.controller;

import com.example.studygroup.domain.study.RecruitStatus;
import com.example.studygroup.dto.request.study.StudyCreateRequest;
import com.example.studygroup.dto.request.study.StudyUpdateRequest;
import com.example.studygroup.service.StudyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/")
    public String home(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null) {
            System.out.println("사용자가 입력한 검색어: " + keyword);
        }

        model.addAttribute("studyList", studyService.findAllStudies(keyword));
        return "study/home";
    }

    // 스터디 작성 페이지
    @GetMapping("/study/create")
    public String createPage(HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }
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

    // 스터디 상세 페이지
    @GetMapping("/study/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        StudyService.StudyDetailDto study = studyService.findStudyById(id);
        Long loginUserId = (Long) session.getAttribute("loginUserId");

        model.addAttribute("study", study);
        model.addAttribute("isAuthor", loginUserId != null && loginUserId.equals(study.getAuthorId()));
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
    public String delete(@PathVariable Long id, HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        try {
            studyService.deleteStudy(id, loginUserId);
            return "redirect:/?deleted=true";
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
}
