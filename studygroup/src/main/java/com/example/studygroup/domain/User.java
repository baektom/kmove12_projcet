package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    private LocalDateTime createdAt;

    // --- [연관 관계 설정: 회원 탈퇴 에러 해결] ---

    /**
     * ✅ 유저와 스터디 간의 1:N 관계
     * cascade = CascadeType.ALL: 유저 삭제 시 작성한 스터디 게시글도 자동 삭제
     * orphanRemoval = true: 리스트에서 제거된 스터디 객체를 DB에서도 삭제
     */
    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Study> studies = new ArrayList<>();

    // 만약 참가 신청(StudyMember) 기능이 있다면 아래 줄의 주석을 해제하세요.
    // @Builder.Default
    // @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<StudyMember> studyMembers = new ArrayList<>();


    // --- [기본 로직 및 비즈니스 메서드] ---

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfileImage(String profileImagePath) {
        this.profileImage = profileImagePath;
    }

    public void updateInfo(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void updateRole(UserRole role) {
        this.role = role;
    }
}