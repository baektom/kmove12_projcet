package com.example.studygroup.domain.study;

import com.example.studygroup.domain.User;
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
    
    private LocalDateTime joinedAt;
    
    @Builder
    public StudyMember(Study study, User user, MemberRole role) {
        this.study = study;
        this.user = user;
        this.role = role;
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
}
