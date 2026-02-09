package com.example.studygroup.service;

import com.example.studygroup.domain.Study;
import com.example.studygroup.repository.StudyRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;

    public List<StudyDto> findAllStudies(String keyword) {
        List<Study> studies;
        if (keyword != null && !keyword.isEmpty()) {
            studies = studyRepository.findByTitleContaining(keyword);
        } else {
            studies = studyRepository.findAll();
        }
        return studies.stream().map(StudyDto::new).collect(Collectors.toList());
    }

    @Getter
    public static class StudyDto {
        private final Long id;
        private final String title;
        private final int currentParticipants; // HTML에서 찾는 변수명과 일치시켜 500 에러 해결
        private final int maxParticipants;

        public StudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
        }
    }
}