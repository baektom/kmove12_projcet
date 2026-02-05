package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // DB 테이블임을 선언합니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 함부로 생성하지 못하게 보호합니다.
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // 로그인 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String nickname; // 닉네임

    private String email;

    @Builder // 생성자를 안전하게 만들기 위한 빌더 패턴입니다.
    public User(String username, String password, String nickname, String email) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
}
