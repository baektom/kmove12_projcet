package com.example.studygroup.service;

import com.example.studygroup.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;

    public List<Object> searchStudy(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }
        return studyRepository.findByKeyword(keyword).stream().map(Object::toString).toList();
    }
}