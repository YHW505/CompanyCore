package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.TaskStatus;
import com.example.companycore.model.entity.Enum.TaskType;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private final IntegerProperty taskId;
    private final LongProperty assignedBy;
    private final LongProperty assignedTo;
    private final ObjectProperty<TaskType> taskType;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty attachment;
    private final ObjectProperty<TaskStatus> status;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final ObjectProperty<LocalDateTime> createdAt;

    public Task() {
        this.taskId = new SimpleIntegerProperty();
        this.assignedBy = new SimpleLongProperty();
        this.assignedTo = new SimpleLongProperty();
        this.taskType = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.attachment = new SimpleStringProperty();
        this.status = new SimpleObjectProperty<>();
        this.startDate = new SimpleObjectProperty<>();
        this.endDate = new SimpleObjectProperty<>();
        this.createdAt = new SimpleObjectProperty<>();
    }

    public Task(Integer taskId, Long assignedBy, Long assignedTo, TaskType taskType, 
                String title, String description, String attachment, TaskStatus status,
                LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.taskId = new SimpleIntegerProperty(taskId);
        this.assignedBy = new SimpleLongProperty(assignedBy);
        this.assignedTo = new SimpleLongProperty(assignedTo);
        this.taskType = new SimpleObjectProperty<>(taskType);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.attachment = new SimpleStringProperty(attachment);
        this.status = new SimpleObjectProperty<>(status);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    // TaskId
    public Integer getTaskId() { return taskId.get(); }
    public void setTaskId(Integer taskId) { this.taskId.set(taskId); }
    public IntegerProperty taskIdProperty() { return taskId; }

    // AssignedBy
    public Long getAssignedBy() { return assignedBy.get(); }
    public void setAssignedBy(Long assignedBy) { this.assignedBy.set(assignedBy); }
    public LongProperty assignedByProperty() { return assignedBy; }

    // AssignedTo
    public Long getAssignedTo() { return assignedTo.get(); }
    public void setAssignedTo(Long assignedTo) { this.assignedTo.set(assignedTo); }
    public LongProperty assignedToProperty() { return assignedTo; }

    // TaskType
    public TaskType getTaskType() { return taskType.get(); }
    public void setTaskType(TaskType taskType) { this.taskType.set(taskType); }
    public ObjectProperty<TaskType> taskTypeProperty() { return taskType; }

    // Title
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    // Description
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    // Attachment
    public String getAttachment() { return attachment.get(); }
    public void setAttachment(String attachment) { this.attachment.set(attachment); }
    public StringProperty attachmentProperty() { return attachment; }

    // Status
    public TaskStatus getStatus() { return status.get(); }
    public void setStatus(TaskStatus status) { this.status.set(status); }
    public ObjectProperty<TaskStatus> statusProperty() { return status; }

    // StartDate
    public LocalDate getStartDate() { return startDate.get(); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }

    // EndDate
    public LocalDate getEndDate() { return endDate.get(); }
    public void setEndDate(LocalDate endDate) { this.endDate.set(endDate); }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }

    // CreatedAt
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId.get() +
                ", assignedBy=" + assignedBy.get() +
                ", assignedTo=" + assignedTo.get() +
                ", taskType=" + taskType.get() +
                ", title='" + title.get() + '\'' +
                ", status=" + status.get() +
                ", startDate=" + startDate.get() +
                ", endDate=" + endDate.get() +
                '}';
    }
}
