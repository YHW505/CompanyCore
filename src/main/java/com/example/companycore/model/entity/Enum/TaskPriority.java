package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    HIGH("HIGH"), 
    MEDIUM("MEDIUM"), 
    LOW("LOW");
    
    private final String value;
    
    TaskPriority(String value) {
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