package com.example.studygroup.controller;

import com.example.studygroup.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studies")
public class StudyController {
    private final StudyService studyService;

    @GetMapping("/search")
    public ResponseEntity<List<Object>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(studyService.searchStudy(keyword));
    }
}