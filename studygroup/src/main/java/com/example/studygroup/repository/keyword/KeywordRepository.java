package com.example.studygroup.repository.keyword;

import com.example.studygroup.domain.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  // ✅ 선택(연결) 많이 된 키워드 TOP
  @Query("""
        select k
        from Keyword k
        join StudyKeyword sk on sk.keyword = k
        group by k
        order by count(sk.id) desc
    """)
  List<Keyword> findPopularKeywords(Pageable pageable);
}