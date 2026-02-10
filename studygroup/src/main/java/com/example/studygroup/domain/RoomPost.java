package com.example.studygroup.domain.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_posts")
@Getter @Setter
@NoArgsConstructor
public class RoomPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 스터디 룸의 글인지
    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // 작성자
    @Column(nullable = false)
    private Long writerId;

    @Column(nullable = false, length = 50)
    private String writerName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
