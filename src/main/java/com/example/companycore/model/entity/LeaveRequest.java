package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.LeaveStatus;
import com.example.companycore.model.entity.Enum.LeaveType;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private final IntegerProperty leaveId;
    private final LongProperty userId;
    private final ObjectProperty<LeaveType> leaveType;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final StringProperty reason;
    private final ObjectProperty<LeaveStatus> status;
    private final LongProperty approvedBy;
    private final ObjectProperty<LocalDateTime> approvedAt;
    private final ObjectProperty<LocalDateTime> appliedAt;

    public LeaveRequest() {
        this.leaveId = new SimpleIntegerProperty();
        this.userId = new SimpleLongProperty();
        this.leaveType = new SimpleObjectProperty<>();
        this.startDate = new SimpleObjectProperty<>();
        this.endDate = new SimpleObjectProperty<>();
        this.reason = new SimpleStringProperty();
        this.status = new SimpleObjectProperty<>();
        this.approvedBy = new SimpleLongProperty();
        this.approvedAt = new SimpleObjectProperty<>();
        this.appliedAt = new SimpleObjectProperty<>();
    }

    public LeaveRequest(Integer leaveId, Long userId, LeaveType leaveType, LocalDate startDate,
                       LocalDate endDate, String reason, LeaveStatus status, Long approvedBy,
                       LocalDateTime approvedAt, LocalDateTime appliedAt) {
        this.leaveId = new SimpleIntegerProperty(leaveId);
        this.userId = new SimpleLongProperty(userId);
        this.leaveType = new SimpleObjectProperty<>(leaveType);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.reason = new SimpleStringProperty(reason);
        this.status = new SimpleObjectProperty<>(status);
        this.approvedBy = new SimpleLongProperty(approvedBy);
        this.approvedAt = new SimpleObjectProperty<>(approvedAt);
        this.appliedAt = new SimpleObjectProperty<>(appliedAt);
    }

    // LeaveId
    public Integer getLeaveId() { return leaveId.get(); }
    public void setLeaveId(Integer leaveId) { this.leaveId.set(leaveId); }
    public IntegerProperty leaveIdProperty() { return leaveId; }

    // UserId
    public Long getUserId() { return userId.get(); }
    public void setUserId(Long userId) { this.userId.set(userId); }
    public LongProperty userIdProperty() { return userId; }

    // LeaveType
    public LeaveType getLeaveType() { return leaveType.get(); }
    public void setLeaveType(LeaveType leaveType) { this.leaveType.set(leaveType); }
    public ObjectProperty<LeaveType> leaveTypeProperty() { return leaveType; }

    // StartDate
    public LocalDate getStartDate() { return startDate.get(); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }

    // EndDate
    public LocalDate getEndDate() { return endDate.get(); }
    public void setEndDate(LocalDate endDate) { this.endDate.set(endDate); }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }

    // Reason
    public String getReason() { return reason.get(); }
    public void setReason(String reason) { this.reason.set(reason); }
    public StringProperty reasonProperty() { return reason; }

    // Status
    public LeaveStatus getStatus() { return status.get(); }
    public void setStatus(LeaveStatus status) { this.status.set(status); }
    public ObjectProperty<LeaveStatus> statusProperty() { return status; }

    // ApprovedBy
    public Long getApprovedBy() { return approvedBy.get(); }
    public void setApprovedBy(Long approvedBy) { this.approvedBy.set(approvedBy); }
    public LongProperty approvedByProperty() { return approvedBy; }

    // ApprovedAt
    public LocalDateTime getApprovedAt() { return approvedAt.get(); }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt.set(approvedAt); }
    public ObjectProperty<LocalDateTime> approvedAtProperty() { return approvedAt; }

    // AppliedAt
    public LocalDateTime getAppliedAt() { return appliedAt.get(); }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt.set(appliedAt); }
    public ObjectProperty<LocalDateTime> appliedAtProperty() { return appliedAt; }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "leaveId=" + leaveId.get() +
                ", userId=" + userId.get() +
                ", leaveType=" + leaveType.get() +
                ", startDate=" + startDate.get() +
                ", endDate=" + endDate.get() +
                ", reason='" + reason.get() + '\'' +
                ", status=" + status.get() +
                ", approvedBy=" + approvedBy.get() +
                ", approvedAt=" + approvedAt.get() +
                ", appliedAt=" + appliedAt.get() +
                '}';
    }
}
