package com.example.studygroup.repository.study;

import com.example.studygroup.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    // 제목으로 스터디를 검색하는 기능
    List<Study> findByTitleContaining(String keyword);
}
