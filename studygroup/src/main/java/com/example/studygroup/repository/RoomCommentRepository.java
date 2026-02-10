package com.example.studygroup.repository;

import com.example.studygroup.domain.room.RoomComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomCommentRepository extends JpaRepository<RoomComment, Long> {

    List<RoomComment> findByPostIdOrderByIdAsc(Long postId);
}
