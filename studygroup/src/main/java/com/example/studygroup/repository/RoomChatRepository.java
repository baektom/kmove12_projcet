package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomChatRepository extends JpaRepository<RoomChat, Long> {

    // 한 스터디의 채팅 기록(오래된 순)
    List<RoomChat> findByStudyIdOrderByIdAsc(Long studyId);
}
