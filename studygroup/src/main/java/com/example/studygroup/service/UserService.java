package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole; // UserRole import 필수
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

    /**
     * 로그인 인증 로직
     */
    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    /**
     * 회원가입 로직 (특정 계정 관리자 권한 부여 포함)
     */
    @Transactional
    public void register(SignupRequest request) {
        LocalDate birthDate = LocalDate.of(
                request.getBirthYear(),
                request.getBirthMonth(),
                request.getBirthDay()
        );

        // 1. 빌더를 통해 유저 객체 생성 (기본 role은 USER)
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(birthDate)
                .role(UserRole.USER) // 기본값 설정
                .build();

        // 2. 아이디가 "moon"인 경우에만 관리자 권한(ADMIN)으로 변경
        if ("moon".equals(request.getUsername())) {
            user.setRole(UserRole.ADMIN);
        }

        // 3. 최종적으로 DB에 저장
        userRepository.save(user);
    }

    /**
     * 이름과 이메일로 아이디 찾기
     */
    public Optional<String> findUsername(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(User::getUsername);
    }

    /**
     * 비밀번호 재설정 (보안 강화 버전)
     */
    @Transactional
    public String updatePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (passwordEncoder.matches(newPassword, user.getPassword())) {
                        return "same_password";
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return "success";
                })
                .orElse("not_found");
    }

    /**
     * 가입 정보가 실제로 존재하는지 확인 (메일 발송 전 검증용)
     */
    public boolean checkUserExists(String type, String name, String email, String username) {
        if ("id".equals(type)) {
            return userRepository.findByNameAndEmail(name, email).isPresent();
        } else if ("pw".equals(type)) {
            return userRepository.findByUsernameAndNameAndEmail(username, name, email).isPresent();
        } else if ("signup".equals(type)) {
            // 회원가입 시에는 이미 가입된 이메일인지 확인 (중복 가입 방지)
            // userRepository에 findByEmail 메서드가 없다면 추가가 필요합니다.
            return !userRepository.findByEmail(email).isPresent();
        }
        return false;
    }

    /**
     * 관리자 권한 부여 메서드 (운영용)
     */
    @Transactional
    public void grantAdminRole(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> user.updateRole(UserRole.ADMIN));
    }
}