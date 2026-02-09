package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- 로그인 로직 (이 부분이 빠져있어서 에러가 났습니다!) ---
//    public boolean login(String username, String password) {
//        // DB에서 아이디로 유저를 찾고, 비밀번호가 일치하는지 확인합니다.
//        return userRepository.findByUsername(username)
//                .map(user -> user.getPassword().equals(password))
//                .orElse(false);
//    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    // --- 기존 회원가입 로직 ---
    @Transactional
    public void register(SignupRequest request) {
        LocalDate birthDate = LocalDate.of(
                request.getBirthYear(),
                request.getBirthMonth(),
                request.getBirthDay()
        );

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(birthDate)
                .build();

        userRepository.save(user);
    }
}