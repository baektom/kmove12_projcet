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

        return userService.authenticate(username, password)
                .map(user -> {
                    session.setAttribute("loginUserId", user.getId());
                    return "redirect:/";
                })
                .orElse("redirect:/login?error");
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

    // 가입 전에 인증 확인 + 성공 후 플래그 삭제
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

        // ✅ 가입 성공하면 인증 플래그 제거(재사용 방지)
        session.removeAttribute("emailVerified");
        session.removeAttribute("verifiedEmail");
        session.removeAttribute("authEmail");

        return "redirect:/welcome";
    }


    @GetMapping("/welcome") // ⭐ 환영 페이지 주소 추가
    public String welcomePage() {
        // templates/auth/welcome.html 파일을 찾아갑니다.
        return "auth/welcome";
    }

//    @GetMapping("/mypage")
//    public String mypagePage() {
//        return "user/mypage";
//    }
}