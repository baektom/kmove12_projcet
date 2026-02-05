package com.example.studygroup.service; // 패키지 경로 확인!

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // 1. 6자리 인증번호 생성 (image_cffda5.png의 line 21 에러 해결)
    public String createCode() {
        return String.valueOf((int)(Math.random() * (999999 - 100000 + 1)) + 100000);
    }

    // 2. 메일 전송 (image_cffda5.png의 line 24 에러 해결)
    public void sendEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[STUDY MATE] 회원가입 인증번호입니다.");
        message.setText("인증번호는 [" + code + "] 입니다. 3분 이내에 입력해주세요.");
        mailSender.send(message);
    }
}
