package com.example.studygroup.controller;

import com.example.studygroup.domain.StudyPost;
import com.example.studygroup.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyPostRepository studyPostRepository;

    @GetMapping("/study")
    public String list(Model model) {
        List<StudyPost> studies = studyPostRepository.findAll();
        model.addAttribute("studies", studies);
        return "study/list";
    }

    @GetMapping("/study/{id}")
    public String detail(@PathVariable Long id, Model model) {
        StudyPost post = studyPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        model.addAttribute("title", post.getTitle());
        model.addAttribute("content", post.getContent());
        model.addAttribute("post", post);
        return "study/detail";
    }
}