package com.example.companycore.model.dto;

public class LoginRequest {
    private String employeeCode;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String employeeCode, String password) {
        this.employeeCode = employeeCode;
        this.password = password;
    }

    // Getters and Setters
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}