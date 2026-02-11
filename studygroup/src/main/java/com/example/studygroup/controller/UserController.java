package com.example.studygroup.controller;

import com.example.studygroup.domain.User;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.service.StudyMemberService;
import com.example.studygroup.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ 패키지 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private final PasswordEncoder passwordEncoder; // ✅ 1. PasswordEncoder 주입 추가

    /**
     * 마이페이지 메인 화면 조회
     */
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "user/mypage";
    }

    /**
     * 계정 관리 페이지 조회
     */
    @GetMapping("/mypage/account")
    public String accountPage(HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        model.addAttribute("user", user);
        return "user/account";
    }

    /**
     * 회원 정보 및 비밀번호 통합 수정 처리
     * ⭐ 비밀번호는 입력값이 있을 때 '암호화' 후 업데이트됩니다.
     */
    @PostMapping("/mypage/update-info")
    public String updateInfo(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             @RequestParam(required = false) String newPassword,
                             HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 기본 정보 업데이트
        user.updateInfo(name, email, phoneNumber);

        // 2. 새 비밀번호가 입력되었다면 '암호화(Encoding)' 후 업데이트
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            // 평문(1111)을 암호문($2a$10$...)으로 변환합니다.
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.updatePassword(encodedPassword);
        }

        userRepository.save(user);

        return "redirect:/mypage/account?updated=true";
    }

    /**
     * 회원 탈퇴 확인 페이지 이동
     */
    @GetMapping("/mypage/withdraw")
    public String withdrawPage(HttpSession session) {
        if (session.getAttribute("loginUserId") == null) return "redirect:/login";
        return "user/withdraw";
    }

    /**
     * 회원 탈퇴 확정 및 세션 초기화
     */
    @PostMapping("/mypage/withdraw")
    public String withdraw(HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId != null) {
            userService.deleteUser(loginUserId);
            session.invalidate();
        }
        return "redirect:/";
    }

    /**
     * 프로필 이미지 업로드 처리
     */
    @PostMapping("/mypage/upload-profile")
    public String uploadProfile(@RequestParam("profileImage") MultipartFile file,
                                HttpSession session) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";
        if (file.isEmpty()) return "redirect:/mypage/account?error=empty";

        try {
            // ✅ 1. 저장 경로를 프로젝트 루트의 'uploads' 폴더로 변경 (src 내부 X)
            // WebMvcConfig에서 설정한 경로와 일치시켜야 합니다.
            String uploadDir = "uploads/profiles/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;

            // ✅ 2. 절대 경로로 파일 저장 (transferTo가 더 안전합니다)
            File dest = new File(directory.getAbsolutePath() + File.separator + savedFilename);
            file.transferTo(dest);

            // 3. DB 정보 업데이트
            User user = userRepository.findById(loginUserId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            String profileUrl = "/uploads/profiles/" + savedFilename;
            user.updateProfileImage(profileUrl);
            userRepository.save(user);

            // ✅ 4. 중요: 세션 정보 갱신
            // 만약 헤더나 마이페이지에서 세션에 담긴 유저 정보를 쓰고 있다면,
            // 여기서 세션 속성도 새 이미지 경로로 업데이트해줘야 즉시 반영됩니다.
            session.setAttribute("loginUserProfileImage", profileUrl);
            // 만약 'user' 객체 통째로 세션에 있다면:
            // session.setAttribute("user", user);

            return "redirect:/mypage/account?uploaded=true";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/mypage/account?error=upload";
        }
    }

    /**
     * 내가 참가 중인 스터디 목록 조회
     */
    @GetMapping("/mypage/my-studies")
    public String myStudies(@RequestParam(defaultValue = "all") String filter,
                            HttpSession session, Model model) {
        Long loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId == null) return "redirect:/login";

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