package com.example.studygroup.controller;

import com.example.studygroup.service.MailService;
import com.example.studygroup.service.UserService; // UserService 추가
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    public String sendMail(@RequestParam String email,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String username,
                           @RequestParam String type,
                           HttpSession session) {

        // 1. 회원가입(signup)일 때는 이름/아이디 체크를 건너뛰거나 중복만 체크
        boolean canSend = userService.checkUserExists(type, name, email, username);

        if (!canSend) {
            // id/pw 찾기 시에는 '정보 없음', signup 시에는 '이미 가입된 이메일' 의미
            return "not_found";
        }

        // 3. 가입 정보가 확인된 경우에만 인증번호 생성 및 발송
        String code = mailService.createCode(); // 6자리 난수 생성
        mailService.sendEmail(email, code);     // 실제 메일 발송

        // 4. 생성된 코드를 세션에 저장
        session.setAttribute("authCode", code);
        session.setAttribute("authEmail", email);

        return "success";
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