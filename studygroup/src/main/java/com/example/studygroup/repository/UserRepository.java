package com.example.studygroup.repository;

import com.example.studygroup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이 한 줄이 있어야 아이디로 유저 정보를 가져올 수 있습니다!
    Optional<User> findByUsername(String username);
}
