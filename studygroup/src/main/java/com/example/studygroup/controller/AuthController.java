package com.example.studygroup.controller;

import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    // --- 회원가입 관련 (이 부분이 빠져서 404가 났던 거예요!) ---
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup"; // templates/auth/signup.html을 찾아갑니다.
    }

    @PostMapping("/signup")
    public String signup(SignupRequest request) {
        userService.register(request); // 1. DB 저장 (이미 잘 되고 있는 부분)

        // 2. 이 부분을 수정하세요!
        // "redirect:/" 라고 적어야 브라우저가 홈(localhost:8080)으로 다시 접속합니다.
        return "redirect:/";
    }
}