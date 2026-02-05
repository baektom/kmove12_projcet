package com.example.studygroup.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String username;
    private String password;
    private String passwordConfirm;
    private String nickname; // 화면의 '이름'
    private String email;
    // 테이블에 없는 생년월일, 휴대폰 번호 등은 우선 제외하거나
    // 나중에 테이블 컬럼을 추가한 뒤 연결할 수 있습니다.
}
