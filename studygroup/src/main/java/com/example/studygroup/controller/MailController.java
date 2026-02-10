package com.example.studygroup.controller;

import com.example.studygroup.service.MailService;
import com.example.studygroup.service.UserService; // UserService 추가
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail") // 공통 경로 설정
public class MailController {

    private final MailService mailService;
    private final UserService userService; // 1. UserService 주입

    /**
     * 1. 인증번호 발송 요청 처리 (가입 정보 검증 로직 포함)
     * 주소: POST /mail/send
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestParam String email,
                                           @RequestParam String type,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String username,
                                           HttpSession session) {

        boolean canSend = userService.checkUserExists(type, name, email, username);

        if (!canSend) {
            if ("signup".equalsIgnoreCase(type)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate_email");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not_found");
        }

        String code = mailService.createCode();
        mailService.sendEmail(email, code);

        session.setAttribute("authCode", code);
        session.setAttribute("authEmail", email);

        return ResponseEntity.ok("success");
    }

    /**
     * 2. 인증번호 검증 요청 처리
     * 주소: POST /mail/verify
     */
    @PostMapping("/verify")
    public boolean verifyCode(@RequestParam("code") String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("authCode");
        String savedEmail = (String) session.getAttribute("authEmail");

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("authCode"); // 1회용 사용 후 삭제
            session.setAttribute("emailVerified", true);
            session.setAttribute("verifiedEmail", savedEmail);
            return true;
        }
        return false;
    }
}