package com.example.studygroup.domain;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.keyword.StudyKeyword;
import jakarta.persistence.*;
import lombok.*; // ⭐ Setter, AllArgsConstructor 등을 모두 포함하기 위해 .*로 변경

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Study {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int currentParticipants;
    private int maxParticipants;

    // ✅ 조회수 추가
    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private RecruitStatus status = RecruitStatus.RECRUITING; // 기본값: 모집중

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author; // 작성자

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ 키워드 연결(중간 엔티티)
    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyKeyword> studyKeywords = new ArrayList<>();

    /**
     * ⭐ 스터디글 노출 여부 (기본값 true)
     */
    @Builder.Default
    private boolean isVisible = true;

    // --- 비즈니스 로직 ---

    // 숨김 처리 메서드
    public void hide() {
        this.isVisible = false;
    }

    // 비즈니스 로직: 스터디 정보 수정
    public void update(String title, String content, int maxParticipants) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직: 모집 상태 변경
    public void changeStatus(RecruitStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    // 권한 체크: 작성자인지 확인
    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }

    // ✅ 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 참여자 수 증가
    public void incrementParticipants() {
        if (this.currentParticipants >= this.maxParticipants) {
            throw new IllegalStateException("최대 참여 인원을 초과할 수 없습니다.");
        }
        this.currentParticipants++;
    }

    // JPA가 엔티티를 저장하기 전에 시간을 자동으로 기록합니다.
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}