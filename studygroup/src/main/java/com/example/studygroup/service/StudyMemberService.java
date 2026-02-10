package com.example.studygroup.service;

import com.example.studygroup.domain.User;
import java.util.ArrayList;
import com.example.studygroup.domain.MemberRole;
import com.example.studygroup.domain.MemberStatus;
import com.example.studygroup.domain.Study;
import com.example.studygroup.domain.StudyMember;
import com.example.studygroup.repository.UserRepository;
import com.example.studygroup.repository.StudyMemberRepository;
import com.example.studygroup.repository.StudyRepository;
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
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    // 참가 신청
    @Transactional
    public void applyForStudy(Long studyId, Long userId, String applicationMessage) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (study.isAuthor(userId)) {
            throw new IllegalStateException("작성자는 참가 신청을 할 수 없습니다.");
        }

        var existingMember = studyMemberRepository.findByStudyIdAndUserId(studyId, userId);

        if (existingMember.isPresent()) {
            StudyMember member = existingMember.get();
            if (member.getStatus() == MemberStatus.REJECTED) {
                member.reapply(applicationMessage);
                return;
            }
            throw new IllegalStateException("이미 참가 신청한 스터디입니다.");
        }

        StudyMember member = StudyMember.builder()
                .study(study)
                .user(user)
                .role(MemberRole.MEMBER)
                .applicationMessage(applicationMessage)
                .build();

        studyMemberRepository.save(member);
    }

    // 참가 승인
    @Transactional
    public void approveMember(Long memberId, Long authorId) {
        StudyMember member = studyMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        if (!member.getStudy().isAuthor(authorId)) {
            throw new IllegalStateException("승인 권한이 없습니다.");
        }

        member.approve();
        member.getStudy().incrementParticipants();
    }

    // 참가 거부
    @Transactional
    public void rejectMember(Long memberId, Long authorId) {
        StudyMember member = studyMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        if (!member.getStudy().isAuthor(authorId)) {
            throw new IllegalStateException("거부 권한이 없습니다.");
        }

        member.reject();
    }

    // 대기중 신청 목록
    public List<StudyMemberDto> getPendingMembers(Long studyId, Long authorId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        if (!study.isAuthor(authorId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        return studyMemberRepository.findByStudyIdAndStatus(studyId, MemberStatus.PENDING)
                .stream()
                .map(StudyMemberDto::new)
                .collect(Collectors.toList());
    }

    // 승인된 멤버 목록
    public List<StudyMemberDto> getApprovedMembers(Long studyId) {
        return studyMemberRepository.findByStudyIdAndStatus(studyId, MemberStatus.APPROVED)
                .stream()
                .map(StudyMemberDto::new)
                .collect(Collectors.toList());
    }

    // 내가 참가한 스터디
    public List<MyStudyDto> getMyStudies(Long userId) {
        return studyMemberRepository.findByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .stream()
                .map(member -> new MyStudyDto(member.getStudy()))
                .collect(Collectors.toList());
    }

    // 신청 상태 조회
    public MemberStatus getApplicationStatus(Long studyId, Long userId) {
        return studyMemberRepository.findByStudyIdAndUserId(studyId, userId)
                .map(StudyMember::getStatus)
                .orElse(null);
    }

    // 재신청 가능 여부
    public boolean canReapply(Long studyId, Long userId) {
        return studyMemberRepository.findByStudyIdAndUserId(studyId, userId)
                .map(member -> member.getStatus() == MemberStatus.REJECTED)
                .orElse(false);
    }

    // ✅🔥 추가된 메서드 (컨트롤러 에러 해결 핵심)
    public boolean hasApplied(Long studyId, Long userId) {
        return studyMemberRepository
                .findByStudyIdAndUserId(studyId, userId)
                .isPresent();
    }

    // ================= DTO =================

    @Getter
    public static class StudyMemberDto {
        private final Long id;
        private final Long userId;
        private final String userName;
        private final String userEmail;
        private final String status;
        private final String applicationMessage;
        private final String appliedAt;

        public StudyMemberDto(StudyMember member) {
            this.id = member.getId();
            this.userId = member.getUser().getId();
            this.userName = member.getUser().getName();
            this.userEmail = member.getUser().getEmail();
            this.status = member.getStatus().getDescription();
            this.applicationMessage = member.getApplicationMessage();
            this.appliedAt = member.getJoinedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    @Getter
    public static class MyStudyDto {
        private final Long id;
        private final String title;
        private final int currentParticipants;
        private final int maxParticipants;
        private final String status;
        private final String coverImage;
        private final boolean isCreator; // 내가 만든 스터디인지 여부

        public MyStudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.coverImage = study.getCoverImage();
            this.isCreator = false;
        }

        public MyStudyDto(Study study, boolean isCreator) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
            this.coverImage = study.getCoverImage();
            this.isCreator = isCreator;
        }
    }


    // 내가 만든 스터디 목록
    public List<MyStudyDto> getMyCreatedStudies(Long userId) {
        return studyRepository.findByAuthorId(userId)
                .stream()
                .map(MyStudyDto::new)
                .collect(Collectors.toList());
    }

    // 내가 참가한 스터디 + 내가 만든 스터디 통합
    public List<MyStudyDto> getAllMyStudies(Long userId) {
        List<MyStudyDto> result = new ArrayList<>();

        // 내가 만든 스터디 (isCreator = true)
        List<Study> createdStudies = studyRepository.findByAuthorId(userId);
        for (Study study : createdStudies) {
            result.add(new MyStudyDto(study, true));
        }

        // 내가 참가한 스터디 (isCreator = false)
        List<StudyMember> joinedMembers = studyMemberRepository.findByUserIdAndStatus(userId, MemberStatus.APPROVED);
        for (StudyMember member : joinedMembers) {
            // 내가 만든 스터디는 중복 방지
            if (!member.getStudy().isAuthor(userId)) {
                result.add(new MyStudyDto(member.getStudy(), false));
            }
        }

        // 최신순 정렬
        result.sort((a, b) -> b.getId().compareTo(a.getId()));

        return result;
    }
}

