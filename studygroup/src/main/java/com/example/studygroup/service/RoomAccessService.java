package com.example.studygroup.service;

import com.example.studygroup.domain.MemberStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomAccessService {

    private final StudyService studyService;
    private final StudyMemberService studyMemberService;

    public Long getLoginUserId(HttpSession session) {
        return (Long) session.getAttribute("loginUserId");
    }

    /** 작성자 or APPROVED */
    public boolean canEnterRoom(Long studyId, Long loginUserId) {
        var study = studyService.findStudyById(studyId);

        if (loginUserId != null && loginUserId.equals(study.getAuthorId())) return true;

        MemberStatus status = studyMemberService.getApplicationStatus(studyId, loginUserId);
        return status == MemberStatus.APPROVED;
    }

    /** 스터디 작성자 여부 */
    public boolean isStudyAuthor(Long studyId, Long loginUserId) {
        var study = studyService.findStudyById(studyId);
        return loginUserId != null && loginUserId.equals(study.getAuthorId());
    }

}
