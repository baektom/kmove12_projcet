package com.example.studygroup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private int currentParticipants; // 500 에러 해결을 위한 필드

    private int maxParticipants;

    @Builder
    public Study(String title, int currentParticipants, int maxParticipants) {
        this.title = title;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
    }
}

