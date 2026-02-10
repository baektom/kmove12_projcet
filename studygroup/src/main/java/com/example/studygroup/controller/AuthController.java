package com.example.studygroup.controller;

import com.example.studygroup.domain.User;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.exception.DuplicateEmailException;
import com.example.studygroup.exception.DuplicateUsernameException;
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
            session.setAttribute("loginUserRole", user.getRole().name());
            return "redirect:/";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- 2. ID/PW 찾기 관련 ---
    @GetMapping("/find-auth")
    public String findAuthPage() {
        return "auth/find-auth";
    }

    @PostMapping("/find-id")
    @ResponseBody
    public String findId(@RequestParam String name, @RequestParam String email) {
        return userService.findUsername(name, email).orElse("not_found");
    }

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

        try {
            userService.register(request);
        } catch (DuplicateUsernameException e) {
            return "redirect:/signup?duplicateUsername";
        } catch (DuplicateEmailException e) {
            return "redirect:/signup?duplicateEmail";
        }

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
