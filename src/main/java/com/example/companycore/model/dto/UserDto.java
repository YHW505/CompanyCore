package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class UserDto {
    private Long userId;
    private String employeeCode;
    private String username;
    private LocalDate joinDate;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Integer positionId;
    private String positionName;
    private Integer departmentId;
    private String departmentName;
    private Role role;
    private Integer isFirstLogin;
    private Integer isActive;
    private LocalDateTime createdAt;

    // 기본 생성자
    public UserDto() {}

    // 생성자
    public UserDto(Long userId, String employeeCode, String username, LocalDate joinDate,
                   String email, String phone, LocalDate birthDate, Integer positionId,
                   String positionName, Integer departmentId, String departmentName,
                   Role role, Integer isFirstLogin, Integer isActive, LocalDateTime createdAt) {
        this.userId = userId;
        this.employeeCode = employeeCode;
        this.username = username;
        this.joinDate = joinDate;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.positionId = positionId;
        this.positionName = positionName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Integer getIsFirstLogin() { return isFirstLogin; }
    public void setIsFirstLogin(Integer isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", employeeCode='" + employeeCode + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", positionName='" + positionName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
