package com.example.companycore.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private Long userId;
    private String employeeCode;
    private String username;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private String password;
    private Integer positionId;
    private String department;
    private String role;
    private Boolean isFirstLogin;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public User() {}

    // Getter & Setter (모든 필드에 대해)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsFirstLogin() { return isFirstLogin; }
    public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
