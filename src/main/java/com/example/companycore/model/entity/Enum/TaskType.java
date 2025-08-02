package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskType {
    DEVELOPMENT("DEVELOPMENT"), 
    DESIGN("DESIGN"), 
    TESTING("TESTING");
    
    private final String value;
    
    TaskType(String value) {
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
