package com.example.studygroup.config;

import com.example.studygroup.domain.message.ChatMessage;
import com.example.studygroup.repository.message.ChatMessageRepository;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private static List<WebSocketSession> list = new ArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 예: "1 : 안녕하세요"
        String[] parts = payload.split(" : ", 2);

        if (parts.length == 2) {
            try {
                Long userId = Long.parseLong(parts[0]);
                String msgContent = parts[1];

                // ✅ 살려낸 부분 1: DB에 실시간 채팅 저장
                userRepository.findById(userId).ifPresent(user -> {
                    ChatMessage chat = new ChatMessage();
                    chat.setUser(user);
                    chat.setMessage(msgContent);
                    chatMessageRepository.save(chat);
                });
            } catch (Exception e) {
                e.printStackTrace(); // 에러 발생 시 콘솔에 원인을 출력합니다.
            }
        }

        // 모든 접속자에게 메시지 전달
        for(WebSocketSession sess : list) {
            sess.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);

        try {
            // ✅ 살려낸 부분 2: 채팅방 입장 시 과거 기록 50개 불러오기
            List<ChatMessage> history = chatMessageRepository.findTop50ByOrderBySendTimeAsc();
            for (ChatMessage msg : history) {
                // 유저 이름과 메시지를 합쳐서 보냅니다.
                String pastMessage = msg.getUser().getName() + " : " + msg.getMessage();
                session.sendMessage(new TextMessage(pastMessage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        list.remove(session);
    }
}