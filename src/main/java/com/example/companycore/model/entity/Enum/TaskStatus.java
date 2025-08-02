package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    TODO("TODO"), 
    IN_PROGRESS("IN_PROGRESS"), 
    REVIEW("REVIEW"), 
    DONE("DONE");
    
    private final String value;
    
    TaskStatus(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}