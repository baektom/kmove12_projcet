package com.example.studygroup.domain.message;

import com.example.studygroup.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 유저 한 명이 여러 메시지를 쓸 수 있음
    @JoinColumn(name = "sender_id")
    private User user; // 이제 숫자 대신 유저 정보를 통째로 연결합니다!

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sendTime = LocalDateTime.now();
}