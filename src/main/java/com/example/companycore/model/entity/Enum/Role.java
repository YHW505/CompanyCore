package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN("ADMIN"), 
    MANAGER("MANAGER"), 
    EMPLOYEE("EMPLOYEE");
    
    private final String value;
    
    Role(String value) {
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