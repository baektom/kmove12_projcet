package com.example.studygroup.repository;

import com.example.studygroup.domain.study.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudyRepository extends JpaRepository<StudyRoom, Long> {
    @Query("SELECT DISTINCT r FROM StudyRoom r " +
            "LEFT JOIN r.tags t " +
            "WHERE (r.title LIKE %:keyword% OR t.tagName = :keyword) " +
            "AND r.status = 'RECRUITING'")
    List<StudyRoom> findByKeyword(@Param("keyword") String keyword);
}