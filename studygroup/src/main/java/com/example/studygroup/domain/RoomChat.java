package com.example.studygroup.domain.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_chat")
@Getter @Setter
@NoArgsConstructor
public class RoomChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="study_id", nullable = false)
    private Long studyId;

    @Column(name="writer_id", nullable = false)
    private Long writerId;

    @Column(name="writer_name", nullable = false, length = 50)
    private String writerName;

    @Lob
    @Column(nullable = false)
    private String message;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
