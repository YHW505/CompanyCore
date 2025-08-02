package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE("ACTIVE"), 
    INACTIVE("INACTIVE");
    
    private final String value;
    
    UserStatus(String value) {
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