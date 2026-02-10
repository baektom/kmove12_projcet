package com.example.studygroup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/files")
public class FileController {

    @GetMapping("/upload")
    public String show(Model model) {
        File dir = new File("uploads");
        if (dir.exists()) {
            model.addAttribute("files", dir.list());
        }
        return "studyRoom";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) throws Exception {

        if (file.isEmpty()) {
            model.addAttribute("error", "파일 선택 안 함");
            return "studyRoom";
        }

        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) uploadDir.mkdir();

        file.transferTo(new File("uploads/" + file.getOriginalFilename()));

        model.addAttribute("files", uploadDir.list());
        return "studyRoom";
    }
}