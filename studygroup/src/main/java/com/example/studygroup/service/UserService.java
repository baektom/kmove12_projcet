package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.dto.request.auth.SignupRequest;
import com.example.studygroup.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

        user.updateRole(UserRole.USER);

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

        if ("signup".equalsIgnoreCase(type)) {
            if (email == null) return false;
            // 회원가입: 이메일이 이미 있으면 발송 불가
            return !userRepository.existsByEmail(email);
        }

        if ("id".equalsIgnoreCase(type)) {
            if (name == null || email == null) return false;
            // 아이디 찾기: name+email 존재해야 발송 가능
            return userRepository.existsByNameAndEmail(name, email);
        }

        if ("pw".equalsIgnoreCase(type)) {
            if (username == null || name == null || email == null) return false;
            // 비번 찾기: username+name+email 존재해야 발송 가능
            return userRepository.existsByUsernameAndNameAndEmail(username, name, email);
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
     * 유저 삭제 기능 (✅ 관리자 계정 삭제 방지 포함)
     */
    @Transactional
    public void deleteUser(Long id) {
        User targetUser = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다. id=" + id));

        // ✅ 관리자 계정 삭제 방지
        if (targetUser.getRole() == UserRole.ADMIN) {
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다.");
        }

        userRepository.delete(targetUser);
    }
}