package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.PENDING; // 기본값: 대기중

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER; // 기본값: 일반 멤버

    @Column(columnDefinition = "TEXT")
    private String applicationMessage; // 참가 신청 사유

    private LocalDateTime joinedAt;

    @Builder
    public StudyMember(Study study, User user, MemberRole role, String applicationMessage) {
        this.study = study;
        this.user = user;
        this.role = role;
        this.applicationMessage = applicationMessage;
        this.joinedAt = LocalDateTime.now();
    }

    // 참가 승인
    public void approve() {
        this.status = MemberStatus.APPROVED;
    }

    // 참가 거부
    public void reject() {
        this.status = MemberStatus.REJECTED;
    }

    // 거부된 신청을 다시 대기중으로 변경 (재신청)
    public void reapply(String newMessage) {
        if (this.status == MemberStatus.REJECTED) {
            this.status = MemberStatus.PENDING;
            this.applicationMessage = newMessage;
            this.joinedAt = LocalDateTime.now();
        }
    }
}
