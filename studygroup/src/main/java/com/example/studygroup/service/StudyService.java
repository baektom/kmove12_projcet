package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import com.example.studygroup.domain.keyword.Keyword;
import com.example.studygroup.domain.keyword.StudyKeyword;
import com.example.studygroup.domain.RecruitStatus;
import com.example.studygroup.domain.Study;
import com.example.studygroup.dto.request.study.StudyCreateRequest;
import com.example.studygroup.dto.request.study.StudyUpdateRequest;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.repository.keyword.KeywordRepository;
import com.example.studygroup.repository.keyword.StudyKeywordRepository;
import com.example.studygroup.repository.StudyRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
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
    private final KeywordRepository keywordRepository;
    private final StudyKeywordRepository studyKeywordRepository;

    // ✅ 메인: 주목받는(조회수 TOP3) - 숨겨진 글 제외
    public List<StudyDto> findFeaturedStudies() {
        return studyRepository.findTop3ByIsVisibleTrueOrderByViewCountDesc()
                .stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // ✅ 메인: 프리뷰(최신 5개) - 숨겨진 글 제외
    public List<StudyDto> findPreviewStudies() {
        return studyRepository.findTop5ByIsVisibleTrueOrderByCreatedAtDesc()
                .stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // ✅ 전체보기: 키워드/검색어 필터
    public Page<StudyDto> searchStudies(Long keywordId, String q, int page) {
        return studyRepository.search(keywordId, q, PageRequest.of(page, 10))
                .map(StudyDto::new);
    }

    // ✅ 상세보기: 조회수 증가 필요 → readOnly 해제
    @Transactional
    public StudyDetailDto findStudyById(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        study.increaseViewCount();

        List<String> keywordNames = studyKeywordRepository.findKeywordNamesByStudyId(study.getId());
        return new StudyDetailDto(study, keywordNames);
    }

    @Transactional
    public Long createStudy(StudyCreateRequest request, Long userId, String coverImagePath) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Study study = Study.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .currentParticipants(1)
                .maxParticipants(request.getMaxParticipants())
                .author(author)
                .coverImage(coverImagePath)
                .isVisible(true)
                .build();

        Study savedStudy = studyRepository.save(study);

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
    public void updateStudy(Long studyId, StudyUpdateRequest request, Long userId, String coverImagePath) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        if (!study.isAuthor(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        study.update(request.getTitle(), request.getContent(), request.getMaxParticipants(), coverImagePath);
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
    public void deleteStudyByAdmin(Long id) {
        if (!studyRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 스터디가 존재하지 않습니다. id=" + id);
        }
        studyRepository.deleteById(id);
    }

    @Transactional
    public void hideStudy(Long studyId) {
        studyRepository.findById(studyId).ifPresent(Study::hide);
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

    // --- 관리자 페이지용 데이터 조회 ---

    public long countAllStudies() {
        return studyRepository.count();
    }

    public List<StudyDto> findAllStudies() {
        return studyRepository.findAll().stream()
                .map(StudyDto::new).collect(Collectors.toList());
    }

    public List<StudyDto> findRecentStudies(int limit) {
        return studyRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id")))
                .getContent().stream().map(StudyDto::new).collect(Collectors.toList());
    }

    // --- 내부 DTO 클래스 ---

    @Getter
    public static class StudyDto {
        private final Long id;
        private final String title;
        private final int currentParticipants;
        private final int maxParticipants;
        private final RecruitStatus status; // ⭐ 500 에러 해결을 위해 Enum 타입 유지
        private final String authorName;
        private final int viewCount;
        private final LocalDateTime createdAt; // ⭐ 관리자 페이지 날짜 출력을 위해 추가

        public StudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus(); // ⭐ Enum 객체 그대로 전달
            this.authorName = study.getAuthor().getName();
            this.viewCount = study.getViewCount();
            this.createdAt = study.getCreatedAt();
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
        private List<String> keywordNames;
        private final String coverImage;

        public StudyDetailDto(Study study, List<String> keywordNames) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.id = study.getId();
            this.title = study.getTitle();
            this.content = study.getContent();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.authorId = study.getAuthor().getId();
            this.authorName = study.getAuthor().getName();
            this.createdAt = study.getCreatedAt().format(formatter);
            this.updatedAt = study.getUpdatedAt().format(formatter);
            this.viewCount = study.getViewCount();
            this.coverImage = study.getCoverImage();
            this.keywordNames = keywordNames;
        }
    }
}