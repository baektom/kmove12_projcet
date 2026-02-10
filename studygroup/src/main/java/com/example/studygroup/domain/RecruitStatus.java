package com.example.studygroup.domain;

public enum RecruitStatus {
    RECRUITING("모집중"),
    CLOSED("마감");

    private final String description;

    RecruitStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
