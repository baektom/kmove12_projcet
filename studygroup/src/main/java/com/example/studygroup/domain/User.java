package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder // 빌더 패턴 사용
@NoArgsConstructor // JPA를 위한 기본 생성자
@AllArgsConstructor // ⭐ 빌더가 모든 필드를 인식하게 하려면 이게 꼭 필요합니다!
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
    @Enumerated(EnumType.STRING)
    @Builder.Default // ⭐ 빌더 사용 시 기본값을 USER로 고정하려면 이 어노테이션을 붙여주세요.
    private UserRole role = UserRole.USER;

    public void updateRole(UserRole role) {
        this.role = role;
    }
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
