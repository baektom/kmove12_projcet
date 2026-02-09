package com.example.studygroup.repository.keyword;

import com.example.studygroup.domain.keyword.StudyKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyKeywordRepository extends JpaRepository<StudyKeyword, Long> {

  @Query("select k.name from StudyKeyword sk join sk.keyword k where sk.study.id = :studyId")
  List<String> findKeywordNamesByStudyId(@Param("studyId") Long studyId);
}
