package com.example.studygroup.domain;

public enum MemberStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거부됨");
    
    private final String description;
    
    MemberStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
