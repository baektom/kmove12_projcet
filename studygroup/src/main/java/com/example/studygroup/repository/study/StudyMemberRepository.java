package com.example.studygroup.repository.study;

import com.example.studygroup.domain.study.MemberStatus;
import com.example.studygroup.domain.study.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    
    // 특정 스터디의 멤버 목록 조회
    List<StudyMember> findByStudyId(Long studyId);
    
    // 특정 스터디의 특정 상태 멤버 조회
    List<StudyMember> findByStudyIdAndStatus(Long studyId, MemberStatus status);
    
    // 특정 유저의 스터디 멤버십 조회
    List<StudyMember> findByUserId(Long userId);
    
    // 특정 유저의 승인된 스터디 멤버십 조회
    List<StudyMember> findByUserIdAndStatus(Long userId, MemberStatus status);
    
    // 특정 유저가 특정 스터디의 멤버인지 확인
    Optional<StudyMember> findByStudyIdAndUserId(Long studyId, Long userId);
    
    // 스터디의 승인된 멤버 수 카운트
    long countByStudyIdAndStatus(Long studyId, MemberStatus status);
}
