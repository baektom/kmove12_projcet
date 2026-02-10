package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPostRepository extends JpaRepository<RoomPost, Long> {

    // ✅ 특정 스터디의 게시글 최신순
    List<RoomPost> findByStudyIdOrderByIdDesc(Long studyId);
}
