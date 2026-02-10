package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
     * ✅ 회원가입 (비밀번호 암호화 및 생년월일 기본값 처리)
     */
    @Transactional
    public void register(SignupRequest request) {
        java.time.LocalDate birthDate = request.getBirthDate();
        if (birthDate == null) {
            birthDate = java.time.LocalDate.of(1900, 1, 1);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(birthDate)
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
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

    /**
     * ✅ 에러 해결: 관리자 대시보드용 전체 사용자 수 카운트
     */
    public long countAllUsers() {
        return userRepository.count();
    }

    /**
     * ✅ 전체 사용자 목록 조회
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * ✅ 최신 가입자 조회 (관리자용)
     */
    public List<User> findRecentUsers(int limit) {
        // 실제로는 Pageable을 사용하여 상위 N개만 가져오는 로직이 적합합니다.
        return userRepository.findAll();
    }

    /**
     * ✅ ID로 사용자 찾기
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * ✅ 아이디 찾기
     */
    public Optional<String> findUsername(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(User::getUsername);
    }
}