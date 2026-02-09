package com.example.studygroup.service;

import com.example.studygroup.domain.keyword.Keyword;
import com.example.studygroup.repository.keyword.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

  private final KeywordRepository keywordRepository;

  public List<Keyword> findAll() {
    return keywordRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Keyword> findPopularTop5() {
    List<Keyword> popular = keywordRepository.findPopularKeywords(PageRequest.of(0, 5));
    if (popular.size() < 5) {
      // 연결된 키워드가 아직 적으면 전체 키워드에서 채움
      List<Keyword> all = keywordRepository.findAll();
      for (Keyword k : all) {
        if (popular.size() >= 5) break;
        if (popular.stream().noneMatch(p -> p.getId().equals(k.getId()))) {
          popular.add(k);
        }
      }
    }
    return popular.stream().limit(5).toList();
  }
}
