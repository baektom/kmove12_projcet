package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;

    private String profileImage; // 프로필 이미지 경로

    @Builder
    public User(String username, String password, String name, String email, String phoneNumber, LocalDate birthDate, String profileImage) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }

    // 프로필 이미지 업데이트
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // 회원 정보 수정
    public void updateInfo(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
