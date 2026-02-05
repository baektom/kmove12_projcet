package com.example.studygroup.controller;

import com.example.studygroup.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/")
    public String home(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null) {
            System.out.println("사용자가 입력한 검색어: " + keyword);
        }

        // ⭐ findAllStudies() 안에 keyword를 넣어주어야 에러가 사라집니다!
        model.addAttribute("studyList", studyService.findAllStudies(keyword));
        return "study/home";
    }
}
