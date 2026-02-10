package com.example.studygroup.repository;

import com.example.studygroup.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // ✅ UserService.findUsername에서 사용하는 메서드
    Optional<User> findByNameAndEmail(String name, String email);

    Optional<User> findByUsernameAndNameAndEmail(String username, String name, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNameAndEmail(String name, String email);

    boolean existsByUsernameAndEmail(String username, String email);
}
