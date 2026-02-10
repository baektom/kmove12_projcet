package com.example.studygroup.domain;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.keyword.StudyKeyword;
import jakarta.persistence.*;
import lombok.*;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 스터디 대문 사진
    private String coverImage;

    private int currentParticipants;
    private int maxParticipants;

    // 조회수
    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private RecruitStatus status = RecruitStatus.RECRUITING;

    // 스터디글 노출 여부
    @Builder.Default
    @Column(nullable = false)
    private boolean isVisible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 키워드 연결(중간 엔티티)
    @Builder.Default
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyKeyword> studyKeywords = new ArrayList<>();

    // 생성자 (Builder용)
    @Builder
    public Study(String title,
                 String content,
                 int currentParticipants,
                 int maxParticipants,
                 User author) {

        this.title = title;
        this.content = content;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.author = author;
        this.coverImage = coverImage;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- 비즈니스 로직 ---

    // 숨김 처리
    public void hide() {
        this.isVisible = false;
    }

    // 스터디 정보 수정
    public void update(String title, String content, int maxParticipants, String coverImage) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        if (coverImage != null) this.coverImage = coverImage;
        this.updatedAt = LocalDateTime.now();
    }

    // 모집 상태 변경
    public void changeStatus(RecruitStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    // 권한 체크: 작성자인지 확인
    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }

    // 조회수 증가
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

    // JPA 저장 전 자동 시간 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
