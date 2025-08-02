package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.ScheduleType;
import java.time.LocalDateTime;

/**
 * Schedule 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class ScheduleDto {
    private Integer scheduleId;
    private Long userId;
    private ScheduleType scheduleType;
    private String title;
    private String description;
    private String attendees;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String meetingMinutes;
    private LocalDateTime createdAt;

    // 기본 생성자
    public ScheduleDto() {}

    // 생성자
    public ScheduleDto(Integer scheduleId, Long userId, ScheduleType scheduleType,
                      String title, String description, String attendees,
                      LocalDateTime startDatetime, LocalDateTime endDatetime,
                      String meetingMinutes, LocalDateTime createdAt) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.scheduleType = scheduleType;
        this.title = title;
        this.description = description;
        this.attendees = attendees;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.meetingMinutes = meetingMinutes;
        this.createdAt = createdAt;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ScheduleType getScheduleType() { return scheduleType; }
    public void setScheduleType(ScheduleType scheduleType) { this.scheduleType = scheduleType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }

    public LocalDateTime getStartDatetime() { return startDatetime; }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime = startDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public String getMeetingMinutes() { return meetingMinutes; }
    public void setMeetingMinutes(String meetingMinutes) { this.meetingMinutes = meetingMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ScheduleDto{" +
                "scheduleId=" + scheduleId +
                ", userId=" + userId +
                ", scheduleType=" + scheduleType +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", attendees='" + attendees + '\'' +
                ", startDatetime=" + startDatetime +
                ", endDatetime=" + endDatetime +
                ", meetingMinutes='" + meetingMinutes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 