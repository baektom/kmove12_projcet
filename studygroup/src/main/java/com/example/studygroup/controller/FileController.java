package com.example.studygroup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID; // ✅ UUID 임포트 추가

@Controller
@RequestMapping("/files")
public class FileController {

    // ✅ 경로를 한 곳에서 관리 (프로젝트 루트의 uploads/profiles/)
    private final String PATH = "uploads/profiles/";

    /**
     * 업로드된 파일 목록 보기
     */
    @GetMapping("/upload")
    public String show(Model model) {
        File dir = new File(PATH);
        // ✅ 저장하는 곳과 보여주는 곳의 경로를 일치시킴
        if (dir.exists()) {
            model.addAttribute("files", dir.list());
        }
        return "studyRoom";
    }

    /**
     * 파일 업로드 처리
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        // 1. 파일 선택 여부 확인
        if (file.isEmpty()) {
            model.addAttribute("error", "파일이 선택되지 않았습니다.");
            return "studyRoom";
        }

        // 2. 디렉토리가 없으면 생성
        File uploadDir = new File(PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 3. 파일명 중복 방지를 위한 UUID 적용
        // ✅ 한글 파일명 깨짐 및 중복 덮어쓰기 방지
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 4. 절대 경로로 파일 저장
        // ✅ 스프링 부트가 업로드된 파일을 물리적 위치에 정확히 저장하도록 함
        File dest = new File(uploadDir.getAbsolutePath() + File.separator + fileName);
        file.transferTo(dest);

        // 5. 업데이트된 파일 목록을 모델에 담아 반환
        model.addAttribute("files", uploadDir.list());
        model.addAttribute("message", "파일 업로드 성공: " + originalFileName);

        return "studyRoom";
    }
}