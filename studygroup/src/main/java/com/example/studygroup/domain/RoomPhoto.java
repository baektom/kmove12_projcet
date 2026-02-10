package com.example.studygroup.domain.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(
        name = "room_photo",
        indexes = {
                @Index(name = "idx_room_photo_study_id", columnList = "studyId"),
                @Index(name = "idx_room_photo_study_cover", columnList = "studyId, isCover")
        }
)
public class RoomPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private String uploaderName;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String url; // "/uploads/xxx.jpg"

    /**
     * ✅ 대표(커버) 이미지 여부
     * - 스터디당 1장만 true가 되도록 서비스에서 보장해야 함
     */
    @Column(nullable = false)
    private boolean isCover = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
