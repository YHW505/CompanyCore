package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LeaveType {
    ANNUAL("ANNUAL"), 
    SICK("SICK"), 
    PERSONAL("PERSONAL"), 
    MATERNITY("MATERNITY"), 
    PATERNITY("PATERNITY");
    
    private final String value;
    
    LeaveType(String value) {
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