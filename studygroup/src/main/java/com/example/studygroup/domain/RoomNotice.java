package com.example.studygroup.domain.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class RoomNotice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long writerId;

    @Column(nullable = false)
    private String writerName;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
