package com.example.studygroup.controller;

import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*; // @ResponseBody 등을 위해 추가
import com.example.studygroup.domain.User;

import java.util.Optional;

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

        // 1. UserService를 통해 인증된 유저 정보를 가져옵니다.
        Optional<User> userOpt = userService.authenticate(username, password);

        // 2. ⭐ 요청하신 코드 삽입 위치입니다.
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 세션에 유저 객체와 ID 저장
            session.setAttribute("loginUser", user);
            session.setAttribute("loginUserId", user.getId());

            // ⭐ 권한 정보를 세션에 문자열로 저장하여 HTML에서 버튼 노출 여부를 결정합니다.
            session.setAttribute("loginUserRole", user.getRole().name());

            return "redirect:/"; // 로그인 성공 시 메인으로 이동
        } return "redirect:/login?error";
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

        // 가입 성공 시 인증 플래그 제거
        session.removeAttribute("emailVerified");
        session.removeAttribute("verifiedEmail");
        session.removeAttribute("authEmail");

        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "auth/welcome";
    }

    // --- ID/PW 찾기 관련 (새로 추가된 기능) ---

    /**
     * ID/PW 찾기 통합 페이지 이동
     */
    @GetMapping("/find-auth")
    public String findAuthPage() {
        return "auth/find-auth"; // templates/auth/find-auth.html 호출
    }

    /**
     * 이름과 이메일로 아이디 찾기
     * @ResponseBody를 사용하여 결과값을 바로 브라우저로 보냅니다 (AJAX 용)
     */
    @PostMapping("/find-id")
    @ResponseBody
    public String findId(@RequestParam String name, @RequestParam String email, HttpSession session) {
        // 1. 세션에서 인증 성공 여부 확인 (MailController에서 설정한 값)
        Boolean isVerified = (Boolean) session.getAttribute("emailVerified");

        if (isVerified == null || !isVerified) {
            return "not_verified";
        }

        return userService.findUsername(name, email).orElse("not_found");
    }

    /**
     * 비밀번호 재설정 (새로운 비밀번호로 업데이트)
     */
    @PostMapping("/reset-password")
    @ResponseBody
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        // 이메일 인증 완료 후 호출되는 로직
        return userService.updatePassword(username, newPassword);
    }
}