package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
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
     * 회원가입 로직 (아이디 "moon"은 관리자로 등록)
     */
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
                .role(UserRole.USER) // 기본 권한
                .build();

        // ⭐ 관리자 계정 자동 부여 로직
        if ("moon".equals(request.getUsername())) {
            user.setRole(UserRole.ADMIN);
        }

        userRepository.save(user);
    }

    /**
     * ⭐ [추가됨] 이름과 이메일로 아이디 찾기
     */
    public Optional<String> findUsername(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(User::getUsername);
    }

    /**
     * 비밀번호 업데이트 (이전 비밀번호와 동일 여부 체크 포함)
     */
    @Transactional
    public String updatePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    // 이전 비밀번호와 같은지 확인
                    if (passwordEncoder.matches(newPassword, user.getPassword())) {
                        return "same_password";
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return "success";
                })
                .orElse("not_found");
    }

    /**
     * 가입 정보 존재 여부 및 이메일 중복 체크
     */
    public boolean checkUserExists(String type, String name, String email, String username) {
        if ("signup".equals(type)) {
            // 회원가입 시: 이미 존재하는 이메일인지 체크 (중복 가입 방지)
            return !userRepository.findByEmail(email).isPresent();
        } else if ("id".equals(type)) {
            // ID 찾기 시: 이름과 이메일이 일치하는 계정이 있는지 체크
            return userRepository.findByNameAndEmail(name, email).isPresent();
        } else if ("pw".equals(type)) {
            // PW 찾기 시: 아이디, 이름, 이메일이 모두 일치하는지 체크
            return userRepository.findByUsernameAndNameAndEmail(username, name, email).isPresent();
        }
        return false;
    }

    /**
     * 관리자 권한 부여 (운영용)
     */
    @Transactional
    public void grantAdminRole(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> user.updateRole(UserRole.ADMIN));
    }
}