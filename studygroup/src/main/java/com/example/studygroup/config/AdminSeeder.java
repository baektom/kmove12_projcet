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
@Profile("dev")
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
      // ✅ 기존 계정 업데이트 (세터 사용 가능)
      admin.setName(adminName);
      admin.setEmail(adminEmail);
      admin.setRole(UserRole.ADMIN);
      admin.setPassword(passwordEncoder.encode(adminPassword));

      if (admin.getBirthDate() == null) admin.setBirthDate(LocalDate.of(1990, 1, 1));
      if (admin.getPhoneNumber() == null) admin.setPhoneNumber("01000000000");

      userRepository.save(admin);
      System.out.println("[DEV] Admin account updated: " + adminUsername);

    }, () -> {
      // ✅ 에러 해결: new User() 대신 Builder 사용
      User admin = User.builder()
              .username(adminUsername)
              .name(adminName)
              .email(adminEmail)
              .password(passwordEncoder.encode(adminPassword))
              .role(UserRole.ADMIN)
              .birthDate(LocalDate.of(1990, 1, 1))
              .phoneNumber("01000000000")
              .build();

      userRepository.save(admin);
      System.out.println("[DEV] Admin account seeded: " + adminUsername);
    });
  }
}