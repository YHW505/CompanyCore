package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.AttendanceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class AttendanceDto {
    private Integer attendanceId;
    private Long userId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private BigDecimal workHours;
    private LocalDate workDate;
    private AttendanceStatus status;

    // 기본 생성자
    public AttendanceDto() {}

    // 생성자
    public AttendanceDto(Integer attendanceId, Long userId, LocalDateTime checkIn,
                        LocalDateTime checkOut, BigDecimal workHours, LocalDate workDate,
                        AttendanceStatus status) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workHours = workHours;
        this.workDate = workDate;
        this.status = status;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId = attendanceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public BigDecimal getWorkHours() { return workHours; }
    public void setWorkHours(BigDecimal workHours) { this.workHours = workHours; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "AttendanceDto{" +
                "attendanceId=" + attendanceId +
                ", userId=" + userId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", workHours=" + workHours +
                ", workDate=" + workDate +
                ", status=" + status +
                '}';
    }
} 