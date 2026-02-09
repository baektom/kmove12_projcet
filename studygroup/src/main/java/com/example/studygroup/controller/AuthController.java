package com.example.studygroup.controller;

import com.example.studygroup.domain.User;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // --- 1. 로그인/로그아웃 관련 ---
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loginUserId", user.getId());
            // ⭐ 관리자 버튼 노출을 위해 권한(Role) 정보를 세션에 저장합니다.
            session.setAttribute("loginUserRole", user.getRole().name());
            return "redirect:/";
        }
        // 로그인 실패 시 'error' 파라미터를 들고 로그인 페이지로 돌아갑니다.
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- 2. ID/PW 찾기 관련 (추가된 부분) ---
    /**
     * ID/PW 찾기 통합 페이지로 이동
     */
    @GetMapping("/find-auth")
    public String findAuthPage() {
        // templates/auth/find-auth.html 파일을 찾아갑니다.
        return "auth/find-auth";
    }

    /**
     * 아이디 찾기 결과 처리 (AJAX용)
     */
    @PostMapping("/find-id")
    @ResponseBody
    public String findId(@RequestParam String name, @RequestParam String email) {
        return userService.findUsername(name, email).orElse("not_found");
    }

    /**
     * 비밀번호 재설정 처리 (AJAX용)
     */
    @PostMapping("/reset-password")
    @ResponseBody
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        return userService.updatePassword(username, newPassword);
    }

    // --- 3. 회원가입 및 환영 페이지 관련 ---
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest request, HttpSession session) {
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
        String verifiedEmail = (String) session.getAttribute("verifiedEmail");

        if (emailVerified == null || !emailVerified) {
            return "redirect:/signup?emailNotVerified";
        }
        if (verifiedEmail == null || !verifiedEmail.equals(request.getEmail())) {
            return "redirect:/signup?emailMismatch";
        }

        userService.register(request);

        // 가입 성공 시 세션에서 인증 정보 삭제 (재사용 방지)
        session.removeAttribute("emailVerified");
        session.removeAttribute("verifiedEmail");
        session.removeAttribute("authEmail");

        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "auth/welcome";
    }
}