package com.example.studygroup.repository;

import com.example.studygroup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByNameAndEmail(String name, String email);

    // ⭐ 이 부분이 빠져있어서 에러가 발생했습니다! 추가해 주세요.
    Optional<User> findByEmail(String email);

    // PW 찾기용 3가지 정보 일치 확인
    Optional<User> findByUsernameAndNameAndEmail(String username, String name, String email);
}
