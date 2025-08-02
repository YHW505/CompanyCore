package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.AttendanceStatus;
import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private final IntegerProperty attendanceId;
    private final LongProperty userId;
    private final ObjectProperty<LocalDateTime> checkIn;
    private final ObjectProperty<LocalDateTime> checkOut;
    private final ObjectProperty<BigDecimal> workHours;
    private final ObjectProperty<LocalDate> workDate;
    private final ObjectProperty<AttendanceStatus> status;

    public Attendance() {
        this.attendanceId = new SimpleIntegerProperty();
        this.userId = new SimpleLongProperty();
        this.checkIn = new SimpleObjectProperty<>();
        this.checkOut = new SimpleObjectProperty<>();
        this.workHours = new SimpleObjectProperty<>();
        this.workDate = new SimpleObjectProperty<>();
        this.status = new SimpleObjectProperty<>();
    }

    public Attendance(Integer attendanceId, Long userId, LocalDateTime checkIn, LocalDateTime checkOut,
                     BigDecimal workHours, LocalDate workDate, AttendanceStatus status) {
        this.attendanceId = new SimpleIntegerProperty(attendanceId);
        this.userId = new SimpleLongProperty(userId);
        this.checkIn = new SimpleObjectProperty<>(checkIn);
        this.checkOut = new SimpleObjectProperty<>(checkOut);
        this.workHours = new SimpleObjectProperty<>(workHours);
        this.workDate = new SimpleObjectProperty<>(workDate);
        this.status = new SimpleObjectProperty<>(status);
    }

    // AttendanceId
    public Integer getAttendanceId() { return attendanceId.get(); }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId.set(attendanceId); }
    public IntegerProperty attendanceIdProperty() { return attendanceId; }

    // UserId
    public Long getUserId() { return userId.get(); }
    public void setUserId(Long userId) { this.userId.set(userId); }
    public LongProperty userIdProperty() { return userId; }

    // CheckIn
    public LocalDateTime getCheckIn() { return checkIn.get(); }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn.set(checkIn); }
    public ObjectProperty<LocalDateTime> checkInProperty() { return checkIn; }

    // CheckOut
    public LocalDateTime getCheckOut() { return checkOut.get(); }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut.set(checkOut); }
    public ObjectProperty<LocalDateTime> checkOutProperty() { return checkOut; }

    // WorkHours
    public BigDecimal getWorkHours() { return workHours.get(); }
    public void setWorkHours(BigDecimal workHours) { this.workHours.set(workHours); }
    public ObjectProperty<BigDecimal> workHoursProperty() { return workHours; }

    // WorkDate
    public LocalDate getWorkDate() { return workDate.get(); }
    public void setWorkDate(LocalDate workDate) { this.workDate.set(workDate); }
    public ObjectProperty<LocalDate> workDateProperty() { return workDate; }

    // Status
    public AttendanceStatus getStatus() { return status.get(); }
    public void setStatus(AttendanceStatus status) { this.status.set(status); }
    public ObjectProperty<AttendanceStatus> statusProperty() { return status; }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId.get() +
                ", userId=" + userId.get() +
                ", checkIn=" + checkIn.get() +
                ", checkOut=" + checkOut.get() +
                ", workHours=" + workHours.get() +
                ", workDate=" + workDate.get() +
                ", status=" + status.get() +
                '}';
    }
}
