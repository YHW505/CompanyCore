package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.ScheduleType;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Schedule {
    private final IntegerProperty scheduleId;
    private final LongProperty userId;
    private final ObjectProperty<ScheduleType> scheduleType;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty attendees;
    private final ObjectProperty<LocalDateTime> startDatetime;
    private final ObjectProperty<LocalDateTime> endDatetime;
    private final StringProperty meetingMinutes;
    private final ObjectProperty<LocalDateTime> createdAt;

    public Schedule() {
        this.scheduleId = new SimpleIntegerProperty();
        this.userId = new SimpleLongProperty();
        this.scheduleType = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.attendees = new SimpleStringProperty();
        this.startDatetime = new SimpleObjectProperty<>();
        this.endDatetime = new SimpleObjectProperty<>();
        this.meetingMinutes = new SimpleStringProperty();
        this.createdAt = new SimpleObjectProperty<>();
    }

    public Schedule(Integer scheduleId, Long userId, ScheduleType scheduleType, String title,
                   String description, String attendees, LocalDateTime startDatetime,
                   LocalDateTime endDatetime, String meetingMinutes, LocalDateTime createdAt) {
        this.scheduleId = new SimpleIntegerProperty(scheduleId);
        this.userId = new SimpleLongProperty(userId);
        this.scheduleType = new SimpleObjectProperty<>(scheduleType);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.attendees = new SimpleStringProperty(attendees);
        this.startDatetime = new SimpleObjectProperty<>(startDatetime);
        this.endDatetime = new SimpleObjectProperty<>(endDatetime);
        this.meetingMinutes = new SimpleStringProperty(meetingMinutes);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    // ScheduleId
    public Integer getScheduleId() { return scheduleId.get(); }
    public void setScheduleId(Integer scheduleId) { this.scheduleId.set(scheduleId); }
    public IntegerProperty scheduleIdProperty() { return scheduleId; }

    // UserId
    public Long getUserId() { return userId.get(); }
    public void setUserId(Long userId) { this.userId.set(userId); }
    public LongProperty userIdProperty() { return userId; }

    // ScheduleType
    public ScheduleType getScheduleType() { return scheduleType.get(); }
    public void setScheduleType(ScheduleType scheduleType) { this.scheduleType.set(scheduleType); }
    public ObjectProperty<ScheduleType> scheduleTypeProperty() { return scheduleType; }

    // Title
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    // Description
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    // Attendees
    public String getAttendees() { return attendees.get(); }
    public void setAttendees(String attendees) { this.attendees.set(attendees); }
    public StringProperty attendeesProperty() { return attendees; }

    // StartDatetime
    public LocalDateTime getStartDatetime() { return startDatetime.get(); }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime.set(startDatetime); }
    public ObjectProperty<LocalDateTime> startDatetimeProperty() { return startDatetime; }

    // EndDatetime
    public LocalDateTime getEndDatetime() { return endDatetime.get(); }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime.set(endDatetime); }
    public ObjectProperty<LocalDateTime> endDatetimeProperty() { return endDatetime; }

    // MeetingMinutes
    public String getMeetingMinutes() { return meetingMinutes.get(); }
    public void setMeetingMinutes(String meetingMinutes) { this.meetingMinutes.set(meetingMinutes); }
    public StringProperty meetingMinutesProperty() { return meetingMinutes; }

    // CreatedAt
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId.get() +
                ", userId=" + userId.get() +
                ", scheduleType=" + scheduleType.get() +
                ", title='" + title.get() + '\'' +
                ", description='" + description.get() + '\'' +
                ", attendees='" + attendees.get() + '\'' +
                ", startDatetime=" + startDatetime.get() +
                ", endDatetime=" + endDatetime.get() +
                ", meetingMinutes='" + meetingMinutes.get() + '\'' +
                ", createdAt=" + createdAt.get() +
                '}';
    }
}
