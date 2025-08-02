package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ScheduleType {
    MEETING("MEETING"), 
    EVENT("EVENT"), 
    TASK("TASK");
    
    private final String value;
    
    ScheduleType(String value) {
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