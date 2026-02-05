package com.example.studygroup.service; // 1. 주소(패키지) 선언

import com.example.studygroup.domain.User;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 2. "이 파일은 서비스 역할을 해"라고 스프링에게 알려줌
@RequiredArgsConstructor // 3. userRepository를 자동으로 연결
@Transactional(readOnly = true)
public class UserService { // 4. 클래스 울타리 시작!

    private final UserRepository userRepository;

    @Transactional // 데이터를 저장하는 기능이므로 트랜잭션 적용
    public void register(SignupRequest request) {
        // 빌더를 사용해 화면에서 받은 데이터를 유저 객체로 만듭니다.
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // 실제로는 암호화가 필요해요!
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        userRepository.save(user); // 실제 DB(MySQL)에 저장하는 마법의 주문
    }

    // UserService.java 파일 안에 추가하세요!

    public boolean login(String username, String password) {
        // 1. DB에서 해당 아이디를 가진 유저를 찾습니다.
        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password)) // 2. 비밀번호가 일치하는지 확인합니다.
                .orElse(false); // 3. 유저가 없거나 비번이 틀리면 false를 반환합니다.
    }
} // 5. 클래스 울타리 끝!