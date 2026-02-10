package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomChatMessageRepository extends JpaRepository<RoomChatMessage, Long> {

    // 최근 100개(오래된→최신 순)
    List<RoomChatMessage> findTop100ByStudyIdOrderByIdAsc(Long studyId);
}
