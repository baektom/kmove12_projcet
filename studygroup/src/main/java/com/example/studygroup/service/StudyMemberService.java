package com.example.studygroup.service;

import com.example.studygroup.domain.User;
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

        // 작성자는 신청할 수 없음
        if (study.isAuthor(userId)) {
            throw new IllegalStateException("작성자는 참가 신청을 할 수 없습니다.");
        }

        // 기존 신청 확인
        var existingMember = studyMemberRepository.findByStudyIdAndUserId(studyId, userId);

        if (existingMember.isPresent()) {
            StudyMember member = existingMember.get();
            // 거부된 경우 재신청 가능
            if (member.getStatus() == MemberStatus.REJECTED) {
                member.reapply(applicationMessage);
                return;
            }
            // 대기중이거나 승인된 경우
            throw new IllegalStateException("이미 참가 신청한 스터디입니다.");
        }

        // 새로운 신청
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

        // 작성자 권한 확인
        if (!member.getStudy().isAuthor(authorId)) {
            throw new IllegalStateException("승인 권한이 없습니다.");
        }

        member.approve();

        // 스터디 현재 참여 인원 증가
        Study study = member.getStudy();
        study.incrementParticipants();
    }

    // 참가 거부
    @Transactional
    public void rejectMember(Long memberId, Long authorId) {
        StudyMember member = studyMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        // 작성자 권한 확인
        if (!member.getStudy().isAuthor(authorId)) {
            throw new IllegalStateException("거부 권한이 없습니다.");
        }

        member.reject();
    }

    // 대기중인 신청 목록 조회 (작성자용)
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

    // 승인된 멤버 목록 조회
    public List<StudyMemberDto> getApprovedMembers(Long studyId) {
        return studyMemberRepository.findByStudyIdAndStatus(studyId, MemberStatus.APPROVED)
                .stream()
                .map(StudyMemberDto::new)
                .collect(Collectors.toList());
    }

    // 내가 참가한 스터디 목록
    public List<MyStudyDto> getMyStudies(Long userId) {
        return studyMemberRepository.findByUserIdAndStatus(userId, MemberStatus.APPROVED)
                .stream()
                .map(member -> new MyStudyDto(member.getStudy()))
                .collect(Collectors.toList());
    }

    // 특정 사용자가 특정 스터디에 신청한 상태 확인
    public MemberStatus getApplicationStatus(Long studyId, Long userId) {
        return studyMemberRepository.findByStudyIdAndUserId(studyId, userId)
                .map(StudyMember::getStatus)
                .orElse(null);
    }

    // 재신청 가능 여부 확인
    public boolean canReapply(Long studyId, Long userId) {
        return studyMemberRepository.findByStudyIdAndUserId(studyId, userId)
                .map(member -> member.getStatus() == MemberStatus.REJECTED)
                .orElse(false);
    }

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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.appliedAt = member.getJoinedAt().format(formatter);
        }
    }

    @Getter
    public static class MyStudyDto {
        private final Long id;
        private final String title;
        private final int currentParticipants;
        private final int maxParticipants;
        private final String status;

        public MyStudyDto(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.currentParticipants = study.getCurrentParticipants();
            this.maxParticipants = study.getMaxParticipants();
            this.status = study.getStatus().getDescription();
        }
    }
}
