package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.exception.DuplicateEmailException;
import com.example.studygroup.exception.DuplicateUsernameException;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ MailController용 검증 로직
     */
    public boolean checkUserExists(String type, String name, String email, String username) {
        if ("signup".equalsIgnoreCase(type)) {
            return !userRepository.existsByEmail(email);
        } else {
            if (username != null && !username.isEmpty()) {
                return userRepository.existsByUsernameAndEmail(username, email);
            }
            return userRepository.existsByNameAndEmail(name, email);
        }
    }

    /**
     * ✅ 로그인 인증 (암호화 매칭 적용)
     */
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    /**
     * ✅ 회원가입
     * - (필수) 가입 시 서버 재검증: username/email 중복 체크
     * - (필수) DB 유니크 제약 위반 예외도 사용자 친화 메시지로 변환 (레이스컨디션 대비)
     */
    @Transactional
    public void register(SignupRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        // 1) 서버 재검증 (사전 방어)
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
        // 너희 프로젝트는 email에 unique 제약은 없지만, 서비스/메일 인증 흐름상 중복을 막는 게 UX상 좋음
        if (!email.isEmpty() && userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        java.time.LocalDate birthDate = request.getBirthDate();
        if (birthDate == null) {
            birthDate = java.time.LocalDate.of(1900, 1, 1);
        }

        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .email(email)
            .phoneNumber(request.getPhoneNumber())
            .birthDate(birthDate)
            .role(UserRole.USER)
            .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // 2) DB 예외까지 변환 (동시 가입 등)
            if (isDuplicateUsername(e)) {
                throw new DuplicateUsernameException();
            }
            throw e;
        }
    }

    private boolean isDuplicateUsername(DataIntegrityViolationException e) {
        // MySQL 메시지 예: Duplicate entry 'xxx' for key 'user.username'
        Throwable most = e.getMostSpecificCause();
        String msg = (most == null || most.getMessage() == null) ? "" : most.getMessage();
        msg = msg.toLowerCase();
        return msg.contains("duplicate") && (msg.contains("user.username") || msg.contains("username"));
    }

    /**
     * ✅ 마이페이지 내 비밀번호 변경
     */
    @Transactional
    public void updatePasswordById(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    /**
     * ✅ 비밀번호 재설정 (아이디/비번 찾기용)
     */
    @Transactional
    public String updatePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
            .map(user -> {
                user.updatePassword(passwordEncoder.encode(newPassword));
                return "success";
            }).orElse("not_found");
    }

    /**
     * ✅ 회원 탈퇴
     */
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // --- [통계 및 조회 메서드 복구] ---

    public long countAllUsers() {
        return userRepository.count();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findRecentUsers(int limit) {
        return userRepository.findAll();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public Optional<String> findUsername(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
            .map(User::getUsername);
    }
}
