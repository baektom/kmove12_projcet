package com.example.studygroup.repository.message;

import com.example.studygroup.domain.message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 괄호 안을 ChatMessage, Long으로 아주 깔끔하게 정리했습니다.
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 최근 메시지 50개만 시간 순으로 가져오는 기능
    List<ChatMessage> findTop50ByOrderBySendTimeAsc();
}