package com.example.studygroup.repository.study;

import com.example.studygroup.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// 페이지로 변경 import문
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    // 기존
    List<Study> findByTitleContaining(String keyword);

    // ✅ 키워드/검색어로 필터링 (전체보기 페이지용)
    @Query("""
    select distinct s
    from Study s
    left join s.studyKeywords sk
    left join sk.keyword k
    where (:keywordId is null or k.id = :keywordId)
      and (
           :q is null or :q = ''
           or lower(s.title) like lower(concat('%', :q, '%'))
           or lower(k.name)  like lower(concat('%', :q, '%'))
      )
    order by s.createdAt desc
""")
    Page<Study> search(@Param("keywordId") Long keywordId,
                       @Param("q") String q,
                       Pageable pageable);

    // ✅ 주목받는(조회수 TOP3)
    List<Study> findTop3ByOrderByViewCountDesc();

    // ✅ 메인 프리뷰(최신 5개)
    List<Study> findTop5ByOrderByCreatedAtDesc();
}
