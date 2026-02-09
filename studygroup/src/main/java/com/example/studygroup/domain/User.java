package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.*; // ⭐ Lombok 어노테이션들을 가져옵니다.

import java.time.LocalDate;

@Entity
@Getter
@Setter // ⭐ 이 어노테이션을 추가하면 setPassword 메서드가 자동으로 생깁니다!
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password; // 👈 이 필드에 대한 setter가 필요합니다.

    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;

    // User 클래스 내부에 추가
    @Enumerated(EnumType.STRING) // DB에 문자열(USER, ADMIN)로 저장
    @Column(nullable = false)
    private UserRole role = UserRole.USER; // 기본값은 일반 유저로 설정

    // 관리자로 격상시키는 메서드 (관리자 관리 기능용)
    public void updateRole(UserRole role) {
        this.role = role;
    }

    // nickname은 아까 DB에서 삭제했으니 자바 코드에서도 지워주는 게 좋습니다!
}
