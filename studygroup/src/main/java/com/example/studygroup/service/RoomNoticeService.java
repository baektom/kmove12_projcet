package com.example.studygroup.service;

import com.example.studygroup.domain.room.RoomNotice;
import com.example.studygroup.repository.RoomNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomNoticeService {

    private final RoomNoticeRepository roomNoticeRepository;

    @Transactional(readOnly = true)
    public List<RoomNotice> list(Long studyId) {
        return roomNoticeRepository.findByStudyIdOrderByIdDesc(studyId);
    }

    public void write(Long studyId, Long writerId, String writerName, String title, String content) {
        RoomNotice n = new RoomNotice();
        n.setStudyId(studyId);
        n.setWriterId(writerId);
        n.setWriterName(writerName);
        n.setTitle(title);
        n.setContent(content);

        roomNoticeRepository.save(n);
    }

    public void delete(Long studyId, Long noticeId, Long loginUserId) {
        RoomNotice n = roomNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        if (!n.getStudyId().equals(studyId)) throw new SecurityException("잘못된 접근");

        // ✅ 작성자만 삭제 가능 (원하면 방장도 가능하게 바꿔줄게)
        if (!n.getWriterId().equals(loginUserId)) throw new SecurityException("삭제 권한 없음");

        roomNoticeRepository.delete(n);
    }
}
