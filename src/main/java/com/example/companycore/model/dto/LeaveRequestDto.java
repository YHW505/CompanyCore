package com.example.companycore.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/**
 * 휴가 신청 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaveRequestDto {
    private Long leaveId;
    private Long userId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private Long approvedBy;
    private Long rejectedBy;
    private String rejectionReason;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // 기본 생성자
    public LeaveRequestDto() {}

    // 생성자
    public LeaveRequestDto(Long userId, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "PENDING"; // 기본값
    }

    // 전체 생성자
    public LeaveRequestDto(Long leaveId, Long userId, String leaveType, LocalDate startDate, LocalDate endDate, 
                          String reason, String status, Long approvedBy, Long rejectedBy, String rejectionReason,
                          LocalDate createdAt, LocalDate updatedAt) {
        this.leaveId = leaveId;
        this.userId = userId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.rejectedBy = rejectedBy;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter & Setter
    public Long getLeaveId() { return leaveId; }
    public void setLeaveId(Long leaveId) { this.leaveId = leaveId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // 호환성을 위한 메서드
    public String getEmployeeId() { return userId != null ? userId.toString() : null; }
    public void setEmployeeId(String employeeId) { 
        this.userId = employeeId != null ? Long.parseLong(employeeId) : null; 
    }

    public String getEmployeeName() { return "사원"; } // 기본값
    public void setEmployeeName(String employeeName) { /* 구현 필요시 추가 */ }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Long getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(Long rejectedBy) { this.rejectedBy = rejectedBy; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "LeaveRequestDto{" +
                "leaveId=" + leaveId +
                ", userId=" + userId +
                ", leaveType='" + leaveType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", approvedBy=" + approvedBy +
                ", rejectedBy=" + rejectedBy +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 