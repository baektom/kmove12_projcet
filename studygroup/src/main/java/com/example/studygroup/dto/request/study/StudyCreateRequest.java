package com.example.studygroup.dto.request.study;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudyCreateRequest {
    private String title;
    private String content;
    private int maxParticipants;

    // ✅ 키워드 선택(체크박스 여러개)
    private List<Long> keywordIds;
}
