package com.example.studygroup.domain;

public enum MemberRole {
    LEADER("리더"),
    MEMBER("멤버");
    
    private final String description;
    
    MemberRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
