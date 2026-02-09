package com.example.studygroup.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String username;
    private String password;
    private String name;        // 추가된 필드
    private String email;
    private String phoneNumber; // 추가된 필드

    // 생년월일 조각들
    private int birthYear;      // 추가된 필드
    private int birthMonth;     // 추가된 필드
    private int birthDay;       // 추가된 필드
}
