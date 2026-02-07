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
@RequiredArgsConstructor // Repository를 자동으로 주입(연결)해줍니다.
public class StudyController {

    // 이제 데이터를 직접 쓰지 않고, Repository를 통해 DB에서 가져옵니다.
    private final StudyPostRepository studyPostRepository;

    @GetMapping("/study")
    public String list(Model model) {
        // DB에 저장된 모든 스터디 목록을 가져와서 studies 변수에 담습니다.
        List<StudyPost> studies = studyPostRepository.findAll();

        // HTML에 "studies"라는 이름으로 데이터를 보냅니다.
        model.addAttribute("studies", studies);
        return "study/list";
    }

    @GetMapping("/study/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // DB에서 해당 ID의 글을 찾고, 없으면 에러를 던집니다.
        StudyPost post = studyPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        // HTML에서 사용하는 변수명(title, content)에 맞춰 데이터를 담아줍니다.
        model.addAttribute("title", post.getTitle());
        model.addAttribute("content", post.getContent());
        model.addAttribute("post", post);

        return "study/detail";
    }
}