package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.TaskStatus;
import com.example.companycore.model.entity.Enum.TaskType;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class TaskDto {
    private Integer taskId;
    private Long assignedBy;
    private Long assignedTo;
    private TaskType taskType;
    private String title;
    private String description;
    private String attachment;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    // 기본 생성자
    public TaskDto() {}

    // 생성자
    public TaskDto(Integer taskId, Long assignedBy, Long assignedTo, TaskType taskType,
                   String title, String description, String attachment, TaskStatus status,
                   LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.taskType = taskType;
        this.title = title;
        this.description = description;
        this.attachment = attachment;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "TaskDto{" +
                "taskId=" + taskId +
                ", assignedBy=" + assignedBy +
                ", assignedTo=" + assignedTo +
                ", taskType=" + taskType +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
} 