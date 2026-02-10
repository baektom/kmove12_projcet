package com.example.studygroup.domain; // ⭐ 패키지 경로를 domain으로 통일하여 빌드 에러를 해결했습니다.

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor // Builder 사용을 위한 전체 생성자
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default // 빌더 사용 시 기본값 유지
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.PENDING;

    private LocalDateTime joinedAt;

    /**
     * ⭐ 데이터 저장 전 자동으로 시간을 기록합니다.
     */
    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }

    // --- 비즈니스 로직 (Domain Methods) ---

    /**
     * 참가 승인 처리 (ACCEPTED 상태로 변경)
     */
    public void approve() {
        this.status = MemberStatus.APPROVED;
    }

    /**
     * 참가 거절 처리 (REJECTED 상태로 변경)
     */
    public void reject() {
        this.status = MemberStatus.REJECTED;
    }
}