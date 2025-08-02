package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.Role;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final LongProperty userId;
    private final StringProperty employeeCode;
    private final StringProperty username;
    private final ObjectProperty<LocalDate> joinDate;
    private final StringProperty password;
    private final IntegerProperty positionId;
    private final IntegerProperty departmentId;
    private final ObjectProperty<Role> role;
    private final IntegerProperty isFirstLogin;
    private final IntegerProperty isActive;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final StringProperty email;
    private final StringProperty phone;
    private final ObjectProperty<LocalDate> birthDate;
    
    // 관계 객체들
    private final ObjectProperty<Position> position;
    private final ObjectProperty<Department> department;
    private final ObservableList<Attendance> attendances;
    private final ObservableList<Schedule> schedules;
    private final ObservableList<Task> assignedTasks;
    private final ObservableList<Task> createdTasks;
    private final ObservableList<LeaveRequest> leaves;
    private final ObservableList<Message> sentMessages;
    private final ObservableList<Message> receivedMessages;

    public User() {
        this.userId = new SimpleLongProperty();
        this.employeeCode = new SimpleStringProperty();
        this.username = new SimpleStringProperty();
        this.joinDate = new SimpleObjectProperty<>();
        this.password = new SimpleStringProperty();
        this.positionId = new SimpleIntegerProperty();
        this.departmentId = new SimpleIntegerProperty();
        this.role = new SimpleObjectProperty<>();
        this.isFirstLogin = new SimpleIntegerProperty();
        this.isActive = new SimpleIntegerProperty();
        this.createdAt = new SimpleObjectProperty<>();
        this.email = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.birthDate = new SimpleObjectProperty<>();
        
        // 관계 객체들 초기화
        this.position = new SimpleObjectProperty<>();
        this.department = new SimpleObjectProperty<>();
        this.attendances = FXCollections.observableArrayList();
        this.schedules = FXCollections.observableArrayList();
        this.assignedTasks = FXCollections.observableArrayList();
        this.createdTasks = FXCollections.observableArrayList();
        this.leaves = FXCollections.observableArrayList();
        this.sentMessages = FXCollections.observableArrayList();
        this.receivedMessages = FXCollections.observableArrayList();
    }

    public User(Long userId, String employeeCode, String username, LocalDate joinDate,
                String password, Integer positionId, Integer departmentId, Role role,
                Integer isFirstLogin, Integer isActive, LocalDateTime createdAt,
                String email, String phone, LocalDate birthDate) {
        this.userId = new SimpleLongProperty(userId);
        this.employeeCode = new SimpleStringProperty(employeeCode);
        this.username = new SimpleStringProperty(username);
        this.joinDate = new SimpleObjectProperty<>(joinDate);
        this.password = new SimpleStringProperty(password);
        this.positionId = new SimpleIntegerProperty(positionId);
        this.departmentId = new SimpleIntegerProperty(departmentId);
        this.role = new SimpleObjectProperty<>(role);
        this.isFirstLogin = new SimpleIntegerProperty(isFirstLogin);
        this.isActive = new SimpleIntegerProperty(isActive);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.birthDate = new SimpleObjectProperty<>(birthDate);
        
        // 관계 객체들 초기화
        this.position = new SimpleObjectProperty<>();
        this.department = new SimpleObjectProperty<>();
        this.attendances = FXCollections.observableArrayList();
        this.schedules = FXCollections.observableArrayList();
        this.assignedTasks = FXCollections.observableArrayList();
        this.createdTasks = FXCollections.observableArrayList();
        this.leaves = FXCollections.observableArrayList();
        this.sentMessages = FXCollections.observableArrayList();
        this.receivedMessages = FXCollections.observableArrayList();
    }

    // UserId
    public Long getUserId() { return userId.get(); }
    public void setUserId(Long userId) { this.userId.set(userId); }
    public LongProperty userIdProperty() { return userId; }

    // EmployeeCode
    public String getEmployeeCode() { return employeeCode.get(); }
    public void setEmployeeCode(String employeeCode) { this.employeeCode.set(employeeCode); }
    public StringProperty employeeCodeProperty() { return employeeCode; }

    // Username
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    // JoinDate
    public LocalDate getJoinDate() { return joinDate.get(); }
    public void setJoinDate(LocalDate joinDate) { this.joinDate.set(joinDate); }
    public ObjectProperty<LocalDate> joinDateProperty() { return joinDate; }

    // Password
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    // PositionId
    public Integer getPositionId() { return positionId.get(); }
    public void setPositionId(Integer positionId) { this.positionId.set(positionId); }
    public IntegerProperty positionIdProperty() { return positionId; }

    // DepartmentId
    public Integer getDepartmentId() { return departmentId.get(); }
    public void setDepartmentId(Integer departmentId) { this.departmentId.set(departmentId); }
    public IntegerProperty departmentIdProperty() { return departmentId; }

    // Role
    public Role getRole() { return role.get(); }
    public void setRole(Role role) { this.role.set(role); }
    public ObjectProperty<Role> roleProperty() { return role; }

    // IsFirstLogin
    public Integer getIsFirstLogin() { return isFirstLogin.get(); }
    public void setIsFirstLogin(Integer isFirstLogin) { this.isFirstLogin.set(isFirstLogin); }
    public IntegerProperty isFirstLoginProperty() { return isFirstLogin; }

    // IsActive
    public Integer getIsActive() { return isActive.get(); }
    public void setIsActive(Integer isActive) { this.isActive.set(isActive); }
    public IntegerProperty isActiveProperty() { return isActive; }

    // CreatedAt
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // Email
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // Phone
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }

    // BirthDate
    public LocalDate getBirthDate() { return birthDate.get(); }
    public void setBirthDate(LocalDate birthDate) { this.birthDate.set(birthDate); }
    public ObjectProperty<LocalDate> birthDateProperty() { return birthDate; }

    // Position
    public Position getPosition() { return position.get(); }
    public void setPosition(Position position) { this.position.set(position); }
    public ObjectProperty<Position> positionProperty() { return position; }

    // Department
    public Department getDepartment() { return department.get(); }
    public void setDepartment(Department department) { this.department.set(department); }
    public ObjectProperty<Department> departmentProperty() { return department; }

    // Attendances
    public ObservableList<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { 
        this.attendances.clear();
        this.attendances.addAll(attendances);
    }

    // Schedules
    public ObservableList<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { 
        this.schedules.clear();
        this.schedules.addAll(schedules);
    }

    // AssignedTasks
    public ObservableList<Task> getAssignedTasks() { return assignedTasks; }
    public void setAssignedTasks(List<Task> assignedTasks) { 
        this.assignedTasks.clear();
        this.assignedTasks.addAll(assignedTasks);
    }

    // CreatedTasks
    public ObservableList<Task> getCreatedTasks() { return createdTasks; }
    public void setCreatedTasks(List<Task> createdTasks) { 
        this.createdTasks.clear();
        this.createdTasks.addAll(createdTasks);
    }

    // Leaves
    public ObservableList<LeaveRequest> getLeaves() { return leaves; }
    public void setLeaves(List<LeaveRequest> leaves) { 
        this.leaves.clear();
        this.leaves.addAll(leaves);
    }

    // SentMessages
    public ObservableList<Message> getSentMessages() { return sentMessages; }
    public void setSentMessages(List<Message> sentMessages) { 
        this.sentMessages.clear();
        this.sentMessages.addAll(sentMessages);
    }

    // ReceivedMessages
    public ObservableList<Message> getReceivedMessages() { return receivedMessages; }
    public void setReceivedMessages(List<Message> receivedMessages) { 
        this.receivedMessages.clear();
        this.receivedMessages.addAll(receivedMessages);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId.get() +
                ", employeeCode='" + employeeCode.get() + '\'' +
                ", username='" + username.get() + '\'' +
                ", email='" + email.get() + '\'' +
                ", role=" + role.get() +
                ", positionId=" + positionId.get() +
                ", departmentId=" + departmentId.get() +
                ", isActive=" + isActive.get() +
                '}';
    }
}
