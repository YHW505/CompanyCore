package com.example.companycore.model.entity.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    NOTICE("NOTICE"), 
    PERSONAL("PERSONAL");
    
    private final String value;
    
    MessageType(String value) {
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
