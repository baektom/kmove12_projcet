package com.example.studygroup.controller;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.service.UserService;
import com.example.studygroup.service.StudyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        // 최신 데이터 5건씩 가져오기 (Service에 추가 필요)
        model.addAttribute("recentUsers", userService.findRecentUsers(5));
        model.addAttribute("recentStudies", studyService.findRecentStudies(5));

        return "admin/dashboard";
    }

    /**
     * 1. 유저 관리 페이지
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";

        model.addAttribute("users", userService.findAllUsers());
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
     * 유저 삭제 (✅ 실제 삭제 가능/불가는 UserService가 책임)
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";

        // UserService에서 ADMIN 삭제 방지 처리됨
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    /**
     * 스터디글 삭제 (관리자)
     */
    @PostMapping("/studies/delete/{id}")
    public String deleteStudy(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";

        studyService.deleteStudyByAdmin(id);
        return "redirect:/admin/studies";
    }
}
