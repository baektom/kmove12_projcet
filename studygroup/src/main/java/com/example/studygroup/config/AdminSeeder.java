package com.example.studygroup.config;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.UserRole;
import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;

@Component
@Profile("dev") // dev 프로필에서만 실행
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.username}")
  private String adminUsername;

  @Value("${app.admin.password}")
  private String adminPassword;

  @Value("${app.admin.name}")
  private String adminName;

  @Value("${app.admin.email}")
  private String adminEmail;

  @Override
  public void run(String... args) {
    userRepository.findByUsername(adminUsername).ifPresentOrElse(admin -> {
      // ✅ 있으면 업데이트
      admin.setName(adminName);
      admin.setEmail(adminEmail);
      admin.setRole(UserRole.ADMIN);
      // 비밀번호는 원하면 매번 갱신/또는 조건부 갱신
      admin.setPassword(passwordEncoder.encode(adminPassword));

      // 필수값(혹시 null이면 채우기)
      if (admin.getBirthDate() == null) admin.setBirthDate(LocalDate.of(1990, 1, 1));
      if (admin.getPhoneNumber() == null) admin.setPhoneNumber("01000000000");

      userRepository.save(admin);
      System.out.println("[DEV] Admin account updated: " + adminUsername);

    }, () -> {
      // ✅ 없으면 생성
      User admin = new User();
      admin.setUsername(adminUsername);
      admin.setName(adminName);
      admin.setEmail(adminEmail);
      admin.setPassword(passwordEncoder.encode(adminPassword));
      admin.setRole(UserRole.ADMIN);
      admin.setBirthDate(LocalDate.of(1990, 1, 1));
      admin.setPhoneNumber("01000000000");

      userRepository.save(admin);
      System.out.println("[DEV] Admin account seeded: " + adminUsername);
    });
  }

}
