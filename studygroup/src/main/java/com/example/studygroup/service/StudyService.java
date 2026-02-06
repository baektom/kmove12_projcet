package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.study.RecruitStatus;
import com.example.studygroup.domain.study.Study;
import com.example.studygroup.dto.request.study.StudyCreateRequest;
import com.example.studygroup.dto.request.study.StudyUpdateRequest;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.repository.study.StudyRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public List<StudyDto> findAllStudies(String keyword) {
        List<Study> studies;
        if (keyword != null && !keyword.isEmpty()) {
            studies = studyRepository.findByTitleContaining(keyword);
        } else {
            studies = studyRepository.findAll();
        }
        return studies.stream().map(StudyDto::new).collect(Collectors.toList());
    }

    public StudyDetailDto findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        return new StudyDetailDto(study);
    }

    @Transactional
    public Long createStudy(StudyCreateRequest request, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Study study = Study.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .currentParticipants(1) // 작성자 포함
                .maxParticipants(request.getMaxParticipants())
                .author(author)
                .build();

        Study savedStudy = studyRepository.save(study);
        return savedStudy.getId();
    }

    @Transactional
    public void updateStudy(Long studyId, StudyUpdateRequest request, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        // 작성자 권한 체크
        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        study.update(request.getTitle(), request.getContent(), request.getMaxParticipants());
    }

    @Transactional
    public void deleteStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        // 작성자 권한 체크
        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        studyRepository.delete(study);
    }

    @Transactional
    public void changeRecruitStatus(Long studyId, RecruitStatus status, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        // 작성자 권한 체크
        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("모집 상태를 변경할 권한이 없습니다.");
        }

        study.changeStatus(status);
    }

    @Getter
    public static class StudyDto {
        private final Long id;
        private final String title;
        private final int currentParticipants;
        private final int maxParticipants;
        private final String status;
        private final String authorName;

        public StudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.authorName = study.getAuthor().getName();
        }
    }

    @Getter
    public static class StudyDetailDto {
        private final Long id;
        private final String title;
        private final String content;
        private final int currentParticipants;
        private final int maxParticipants;
        private final String status;
        private final Long authorId;
        private final String authorName;
        private final String createdAt;
        private final String updatedAt;

        public StudyDetailDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.content = study.getContent();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.authorId = study.getAuthor().getId();
            this.authorName = study.getAuthor().getName();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.createdAt = study.getCreatedAt().format(formatter);
            this.updatedAt = study.getUpdatedAt().format(formatter);
        }
    }
}