package com.example.studygroup.controller;

import com.example.studygroup.domain.User;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.service.StudyMemberService;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StudyMemberService studyMemberService;
    private final UserRepository userRepository;

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "user/mypage";
    }

    // 계정 관리 페이지
    @GetMapping("/mypage/account")
    public String accountPage(HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "user/account";
    }

    // 프로필 이미지 업로드
    @PostMapping("/mypage/upload-profile")
    public String uploadProfile(@RequestParam("profileImage") MultipartFile file,
                                HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            return "redirect:/mypage/account?error=empty";
        }

        try {
            // 파일 저장 디렉토리 설정
            String uploadDir = "studygroup/src/main/resources/static/uploads/profiles/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path filePath = Paths.get(uploadDir, savedFilename);
            Files.write(filePath, file.getBytes());

            // DB 업데이트
            User user = userRepository.findById(loginUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            user.updateProfileImage("/uploads/profiles/" + savedFilename);
            userRepository.save(user);

            return "redirect:/mypage/account?uploaded=true";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/mypage/account?error=upload";
        }
    }

    // 회원 정보 수정
    @PostMapping("/mypage/update-info")
    public String updateInfo(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updateInfo(name, email, phoneNumber);
        userRepository.save(user);

        return "redirect:/mypage/account?updated=true";
    }

    // My 스터디 페이지
    @GetMapping("/mypage/my-studies")
    public String myStudies(@RequestParam(defaultValue = "all") String filter,
                            HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/login";
        }

        List<StudyMemberService.MyStudyDto> myStudies;

        switch (filter) {
            case "created":
                myStudies = studyMemberService.getMyCreatedStudies(loginUserId);
                break;
            case "joined":
                myStudies = studyMemberService.getMyStudies(loginUserId);
                break;
            default:
                myStudies = studyMemberService.getAllMyStudies(loginUserId);
        }

        model.addAttribute("myStudies", myStudies);
        model.addAttribute("currentFilter", filter);

        return "user/my-studies";
    }
}
