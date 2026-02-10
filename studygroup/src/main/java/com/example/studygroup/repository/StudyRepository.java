package com.example.studygroup.repository;

import com.example.studygroup.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    /**
     * ✅ 키워드/검색어로 필터링 (전체보기 페이지용)
     * isVisible = true 조건을 추가하여 관리자가 숨긴 글은 검색되지 않도록 했습니다.
     */
    @Query("""
        select distinct s
        from Study s
        left join s.studyKeywords sk
        left join sk.keyword k
        where s.isVisible = true
          and (:keywordId is null or k.id = :keywordId)
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

    /**
     * ✅ 주목받는 스터디 TOP3 (노출 설정된 것 중 조회수 높은 순)
     */
    List<Study> findTop3ByIsVisibleTrueOrderByViewCountDesc();

    /**
     * ✅ 최신 프리뷰 5개 (노출 설정된 것 중 최신 등록 순)
     */
    List<Study> findTop5ByIsVisibleTrueOrderByCreatedAtDesc();

    /**
     * ✅ 제목 포함 검색 (필요 시 유지)
     */
    List<Study> findByTitleContaining(String keyword);
}