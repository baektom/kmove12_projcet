package com.example.studygroup.domain.keyword;

import com.example.studygroup.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyKeyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  private Study study;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "keyword_id", nullable = false)
  private Keyword keyword;

  public StudyKeyword(Study study, Keyword keyword) {
    this.study = study;
    this.keyword = keyword;
  }
}
