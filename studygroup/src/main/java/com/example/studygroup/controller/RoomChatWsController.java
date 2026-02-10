package com.example.studygroup.controller;

import com.example.studygroup.domain.room.RoomChatMessage;
import com.example.studygroup.repository.RoomChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomChatWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomChatMessageRepository roomChatMessageRepository;

    @MessageMapping("/chat.send")
    public void send(ChatSendRequest req) {
        if (req.studyId == null) return;
        if (req.message == null || req.message.isBlank()) return;

        String writerName = (req.writerName == null || req.writerName.isBlank())
                ? "익명"
                : req.writerName;

        // 1) DB 저장
        RoomChatMessage msg = new RoomChatMessage();
        msg.setStudyId(req.studyId);
        msg.setWriterId(0L); // 지금은 임시
        msg.setWriterName(writerName);
        msg.setMessage(req.message);
        roomChatMessageRepository.save(msg);

        // 2) 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/study." + req.studyId,
                new ChatSendResponse(writerName, req.message)
        );
    }

    public static class ChatSendRequest {
        public Long studyId;
        public String writerName;
        public String message;
    }

    public static class ChatSendResponse {
        public String writerName;
        public String message;

        public ChatSendResponse(String writerName, String message) {
            this.writerName = writerName;
            this.message = message;
        }
    }
}
