package com.example.studygroup.dto.request.study;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyUpdateRequest {
    private String title;
    private String content;
    private int maxParticipants;
}
