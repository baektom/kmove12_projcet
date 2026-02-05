package com.example.studygroup.controller;

import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // ⭐ 이 줄이 추가되어야 에러가 안 납니다!
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // --- 로그인 관련 ---
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        if (userService.login(username, password)) {
            session.setAttribute("loginUser", username);
            return "redirect:/";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- 회원가입 및 환영 페이지 관련 ---
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest request) { // ⭐ @ModelAttribute 추가
        userService.register(request);
        // 가입 완료 후 환영 페이지(/welcome)로 리다이렉트합니다.
        return "redirect:/welcome";
    }

    @GetMapping("/welcome") // ⭐ 환영 페이지 주소 추가
    public String welcomePage() {
        // templates/auth/welcome.html 파일을 찾아갑니다.
        return "auth/welcome";
    }
}