package com.example.studygroup.controller;

import com.example.studygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

  private final UserRepository userRepository;

  @GetMapping("/check-username")
  public Map<String, Object> checkUsername(@RequestParam String username) {
    String u = username == null ? "" : username.trim();
    if (u.isEmpty()) {
      return Map.of(
          "username", u,
          "available", false,
          "message", "아이디를 입력해주세요."
      );
    }

    boolean exists = userRepository.existsByUsername(u);
    return Map.of(
        "username", u,
        "available", !exists,
        "message", exists ? "중복된 아이디가 있습니다." : "가입 가능한 아이디입니다."
    );
  }
}
