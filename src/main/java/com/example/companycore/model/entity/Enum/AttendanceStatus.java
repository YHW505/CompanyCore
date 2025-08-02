package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceStatus {
    PRESENT("PRESENT"), 
    ABSENT("ABSENT"), 
    LATE("LATE"), 
    LEAVE("LEAVE");
    
    private final String value;
    
    AttendanceStatus(String value) {
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