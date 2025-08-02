package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String token;
    private String employeeCode;
    private LocalDate joinDate;
    private Integer positionId;
    private Integer departmentId;
    private Role role;
    private Boolean isFirstLogin;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // 기본 생성자
    public LoginResponse() {}

    // Getter 메서드들
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter 메서드들
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setIsFirstLogin(Boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", joinDate=" + joinDate +
                ", positionId=" + positionId +
                ", departmentId=" + departmentId +
                ", role='" + role + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
