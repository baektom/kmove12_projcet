package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용으로 설정
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

    private static final List<String> ADMIN_LIST = List.of("moon", "king");
    /**
     * 회원가입 로직
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
                .role(UserRole.USER)
                .build();

        if (ADMIN_LIST.contains(request.getUsername())) {
            user.updateRole(UserRole.ADMIN);
        }

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
     * 비밀번호 업데이트
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
     * 유저 존재 여부 체크
     */
    public boolean checkUserExists(String type, String name, String email, String username) {
        if ("signup".equals(type)) {
            return !userRepository.findByEmail(email).isPresent();
        } else if ("id".equals(type)) {
            return userRepository.findByNameAndEmail(name, email).isPresent();
        } else if ("pw".equals(type)) {
            return userRepository.findByUsernameAndNameAndEmail(username, name, email).isPresent();
        }
        return false;
    }

    // --- 관리자 대시보드용 메서드 ---

    public long countAllUsers() {
        return userRepository.count();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findRecentUsers(int limit) {
        return userRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }

    /**
     * ⭐ [수정됨] 유저 삭제 기능
     * readOnly = true를 제거하고 @Transactional을 새로 걸어주어야 실제 삭제가 반영됩니다.
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다. id=" + id);
        }
        userRepository.deleteById(id);
    }
}