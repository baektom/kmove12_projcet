package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.keyword.Keyword;
import com.example.studygroup.domain.keyword.StudyKeyword;
import com.example.studygroup.domain.study.RecruitStatus;
import com.example.studygroup.domain.study.Study;
import com.example.studygroup.dto.request.study.StudyCreateRequest;
import com.example.studygroup.dto.request.study.StudyUpdateRequest;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.repository.keyword.KeywordRepository;
import com.example.studygroup.repository.keyword.StudyKeywordRepository;
import com.example.studygroup.repository.study.StudyRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// 페이지로 변경 import문
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    // ✅ 키워드 저장용
    private final KeywordRepository keywordRepository;
    private final StudyKeywordRepository studyKeywordRepository;

    // ✅ 메인: 주목받는(조회수 TOP3)
    public List<StudyDto> findFeaturedStudies() {
        return studyRepository.findTop3ByOrderByViewCountDesc()
            .stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // ✅ 메인: 프리뷰(최신 6개)
    public List<StudyDto> findPreviewStudies() {
        return studyRepository.findTop5ByOrderByCreatedAtDesc()
            .stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // ✅ 전체보기: 키워드/검색어 필터
    public Page<StudyDto> searchStudies(Long keywordId, String q, int page) {
        return studyRepository.search(keywordId, q, PageRequest.of(page, 10))
            .map(StudyDto::new);
    }

    public List<StudyDto> findAllStudies(String keyword) {
        List<Study> studies;
        if (keyword != null && !keyword.isEmpty()) {
            studies = studyRepository.findByTitleContaining(keyword);
        } else {
            studies = studyRepository.findAll();
        }
        return studies.stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // ✅ 상세보기: 조회수 증가 필요 → readOnly 해제
    @Transactional
    public StudyDetailDto findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        study.increaseViewCount();
        return new StudyDetailDto(study);
    }

    @Transactional
    public Long createStudy(StudyCreateRequest request, Long userId) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Study study = Study.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .currentParticipants(1)
            .maxParticipants(request.getMaxParticipants())
            .author(author)
            .build();

        Study savedStudy = studyRepository.save(study);

        // ✅ 키워드 연결 저장
        List<Long> keywordIds = request.getKeywordIds() == null ? Collections.emptyList() : request.getKeywordIds();
        if (!keywordIds.isEmpty()) {
            List<Keyword> keywords = keywordRepository.findAllById(keywordIds);
            for (Keyword k : keywords) {
                studyKeywordRepository.save(new StudyKeyword(savedStudy, k));
            }
        }

        return savedStudy.getId();
    }

    @Transactional
    public void updateStudy(Long studyId, StudyUpdateRequest request, Long userId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        study.update(request.getTitle(), request.getContent(), request.getMaxParticipants());
    }

    @Transactional
    public void deleteStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        studyRepository.delete(study);
    }

    @Transactional
    public void changeRecruitStatus(Long studyId, RecruitStatus status, Long userId) {
        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

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
        private final int viewCount;

        public StudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.authorName = study.getAuthor().getUsername();
            this.viewCount = study.getViewCount();
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
        private final int viewCount;

        public StudyDetailDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.content = study.getContent();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.authorId = study.getAuthor().getId();
            this.authorName = study.getAuthor().getUsername();
            this.viewCount = study.getViewCount();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.createdAt = study.getCreatedAt().format(formatter);
            this.updatedAt = study.getUpdatedAt().format(formatter);
        }
    }
}