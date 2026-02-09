package com.example.studygroup.controller;

import com.example.studygroup.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController // JSON 형태로 응답을 보내기 위해 @RestController를 사용합니다.
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    /**
     * 1. 인증번호 발송 요청 처리
     * 주소: POST /mail/send
     */
    @PostMapping("/mail/send")
    public String sendMail(@RequestParam("email") String email, HttpSession session) {
        // 6자리 난수 생성
        String code = mailService.createCode();

        // 실제 메일 발송
        mailService.sendEmail(email, code);

        // 생성된 코드를 세션에 저장 (나중에 검증하기 위함)
        // 3분 동안만 유효하게 설정하고 싶다면 별도의 로직이 필요하지만,
        // 우선은 세션에 저장하는 기본 방식을 사용합니다.
        session.setAttribute("authCode", code);
        session.setAttribute("authEmail", email);

        return "success";
    }

    /**
     * 2. 인증번호 검증 요청 처리
     * 주소: POST /mail/verify
     */

    /*
    * verify에서 email까지 검증 + 플래그 저장으로 수정
    * */
    @PostMapping("/mail/verify")
    public boolean verifyCode(@RequestParam("code") String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("authCode");
        String savedEmail = (String) session.getAttribute("authEmail");

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("authCode"); // 1회용
            session.setAttribute("emailVerified", true);
            session.setAttribute("verifiedEmail", savedEmail);
            return true;
        }
        return false;
    }
}
