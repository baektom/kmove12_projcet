package com.example.studygroup.controller;

import com.example.studygroup.service.UserService;
import com.example.studygroup.service.StudyService;
import com.example.studygroup.domain.User; // 유저 엔티티 임포트 확인
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List; // ⭐ List 임포트 추가

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final StudyService studyService;

    // 관리자 권한 체크 공통 로직
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("loginUserRole");
        return "ADMIN".equals(role);
    }

    /**
     * 6. 전체 대시보드 조회
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";

        model.addAttribute("totalUsers", userService.countAllUsers());
        model.addAttribute("totalStudies", studyService.countAllStudies());
        model.addAttribute("recentUsers", userService.findRecentUsers(5));
        model.addAttribute("recentStudies", studyService.findRecentStudies(5));

        return "admin/dashboard";
    }

    /**
     * 1. 유저 관리 페이지 (⭐ adminList 통합 버전)
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";

        model.addAttribute("users", userService.findAllUsers());

        // ⭐ 관리자 아이디 리스트를 모델에 담아 보냅니다.
        // 나중에 이 리스트는 DB에서 조회해오는 방식으로 발전시키면 더 좋습니다!
        List<String> adminList = List.of("moon", "king");
        model.addAttribute("adminList", adminList);

        return "admin/users";
    }

    /**
     * 2. 스터디글 관리 페이지
     */
    @GetMapping("/studies")
    public String studyManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        model.addAttribute("studies", studyService.findAllStudies());
        return "admin/studies";
    }

    /**
     * 유저 삭제 처리
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";

        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    /**
     * 스터디글 삭제 처리
     */
    @PostMapping("/studies/delete/{id}")
    public String deleteStudy(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";

        studyService.deleteStudyByAdmin(id);
        return "redirect:/admin/studies";
    }
}