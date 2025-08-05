package com.example.companycore.model.dto;

import com.example.companycore.model.entity.*;

/**
 * Entity와 DTO 간의 변환을 위한 유틸리티 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class DtoConverter {

    /**
     * User Entity를 UserDto로 변환
     */
    public static UserDto toUserDto(User user) {
        if (user == null) return null;
        
        return new UserDto(
            user.getUserId(),
            user.getEmployeeCode(),
            user.getUsername(),
            user.getJoinDate(),
            user.getEmail(),
            user.getPhone(),
            user.getBirthDate(),
            user.getPositionId(),
            user.getPosition() != null ? user.getPosition().getPositionName() : null,
            user.getDepartmentId(),
            user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null,
            user.getRole(),
            user.getIsFirstLogin(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }

    /**
     * UserDto를 User Entity로 변환
     */
    public static User toUser(UserDto userDto) {
        if (userDto == null) return null;
        
        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setEmployeeCode(userDto.getEmployeeCode());
        user.setUsername(userDto.getUsername());
        user.setJoinDate(userDto.getJoinDate());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setBirthDate(userDto.getBirthDate());
        user.setPositionId(userDto.getPositionId());
        user.setDepartmentId(userDto.getDepartmentId());
        user.setRole(userDto.getRole());
        user.setIsFirstLogin(userDto.getIsFirstLogin());
        user.setIsActive(userDto.getIsActive());
        user.setCreatedAt(userDto.getCreatedAt());
        
        return user;
    }

    /**
     * Task Entity를 TaskDto로 변환
     */
    public static TaskDto toTaskDto(Task task) {
        if (task == null) return null;
        
        return new TaskDto(
            task.getTaskId(),
            task.getAssignedBy(),
            task.getAssignedTo(),
            task.getTaskType(),
            task.getTitle(),
            task.getDescription(),
            task.getAttachment(),
            task.getStatus(),
            task.getStartDate(),
            task.getEndDate(),
            task.getCreatedAt()
        );
    }

    /**
     * TaskDto를 Task Entity로 변환
     */
    public static Task toTask(TaskDto taskDto) {
        if (taskDto == null) return null;
        
        Task task = new Task();
        task.setTaskId(taskDto.getTaskId());
        task.setAssignedBy(taskDto.getAssignedBy());
        task.setAssignedTo(taskDto.getAssignedTo());
        task.setTaskType(taskDto.getTaskType());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setAttachment(taskDto.getAttachment());
        task.setStatus(taskDto.getStatus());
        task.setStartDate(taskDto.getStartDate());
        task.setEndDate(taskDto.getEndDate());
        task.setCreatedAt(taskDto.getCreatedAt());
        
        return task;
    }

    /**
     * Schedule Entity를 ScheduleDto로 변환
     */
    public static ScheduleDto toScheduleDto(Schedule schedule) {
        if (schedule == null) return null;
        
        return new ScheduleDto(
            schedule.getScheduleId(),
            schedule.getUserId(),
            schedule.getScheduleType(),
            schedule.getTitle(),
            schedule.getDescription(),
            schedule.getAttendees(),
            schedule.getStartDatetime(),
            schedule.getEndDatetime(),
            schedule.getMeetingMinutes(),
            schedule.getCreatedAt()
        );
    }

    /**
     * ScheduleDto를 Schedule Entity로 변환
     */
    public static Schedule toSchedule(ScheduleDto scheduleDto) {
        if (scheduleDto == null) return null;
        
        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleDto.getScheduleId());
        schedule.setUserId(scheduleDto.getUserId());
        schedule.setScheduleType(scheduleDto.getScheduleType());
        schedule.setTitle(scheduleDto.getTitle());
        schedule.setDescription(scheduleDto.getDescription());
        schedule.setAttendees(scheduleDto.getAttendees());
        schedule.setStartDatetime(scheduleDto.getStartDatetime());
        schedule.setEndDatetime(scheduleDto.getEndDatetime());
        schedule.setMeetingMinutes(scheduleDto.getMeetingMinutes());
        schedule.setCreatedAt(scheduleDto.getCreatedAt());
        
        return schedule;
    }

    /**
     * Attendance Entity를 AttendanceDto로 변환
     */
    public static AttendanceDto toAttendanceDto(Attendance attendance) {
        if (attendance == null) return null;
        
        return new AttendanceDto(
            attendance.getAttendanceId(),
            attendance.getUserId(),
            attendance.getCheckIn(),
            attendance.getCheckOut(),
            attendance.getWorkHours(),
            attendance.getWorkDate(),
            attendance.getStatus()
        );
    }

    /**
     * AttendanceDto를 Attendance Entity로 변환
     */
    public static Attendance toAttendance(AttendanceDto attendanceDto) {
        if (attendanceDto == null) return null;
        
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(attendanceDto.getAttendanceId());
        attendance.setUserId(attendanceDto.getUserId());
        attendance.setCheckIn(attendanceDto.getCheckIn());
        attendance.setCheckOut(attendanceDto.getCheckOut());
        attendance.setWorkHours(attendanceDto.getWorkHours());
        attendance.setWorkDate(attendanceDto.getWorkDate());
        attendance.setStatus(attendanceDto.getStatus());
        
        return attendance;
    }

    /**
     * Department Entity를 DepartmentDto로 변환
     */
    public static DepartmentDto toDepartmentDto(Department department) {
        if (department == null) return null;
        
        return new DepartmentDto(
            department.getDepartmentId(),
            department.getDepartmentCode(),
            department.getDepartmentName()
        );
    }

    /**
     * DepartmentDto를 Department Entity로 변환
     */
    public static Department toDepartment(DepartmentDto departmentDto) {
        if (departmentDto == null) return null;
        
        Department department = new Department();
        department.setDepartmentId(departmentDto.getDepartmentId());
        department.setDepartmentCode(departmentDto.getDepartmentCode());
        department.setDepartmentName(departmentDto.getDepartmentName());
        
        return department;
    }

    /**
     * Position Entity를 PositionDto로 변환
     */
    public static PositionDto toPositionDto(Position position) {
        if (position == null) return null;
        
        return new PositionDto(
            position.getPositionId(),
            position.getPositionCode(),
            position.getPositionName(),
            position.getLevelOrder()
        );
    }

    /**
     * PositionDto를 Position Entity로 변환
     */
    public static Position toPosition(PositionDto positionDto) {
        if (positionDto == null) return null;
        
        Position position = new Position();
        position.setPositionId(positionDto.getPositionId());
        position.setPositionCode(positionDto.getPositionCode());
        position.setPositionName(positionDto.getPositionName());
        position.setLevelOrder(positionDto.getLevelOrder());
        
        return position;
    }

    /**
     * Message Entity를 MessageDto로 변환
     */
    public static MessageDto toMessageDto(Message message) {
        if (message == null) return null;

        MessageDto dto = new MessageDto(
                message.getMessageId() != null ? message.getMessageId().longValue() : null, // Long messageId
                message.getSenderId(),                      // Long senderId
                message.getSenderEmail(),                   // String senderEmail ✅ 순서 변경됨
                message.getReceiverEmail(),                 // String receiverEmail
                message.getTitle(),                         // String title
                message.getContent(),                       // String content
                message.getMessageType() != null ? message.getMessageType().toString() : null, // String messageType
                message.getIsRead(),                        // Boolean isRead
                message.getSentAt(),                        // LocalDateTime sentAt
                null,                                       // LocalDateTime readAt
                null,                                       // String senderName
                null                                        // String receiverName
        );
        return dto;
    }

    /**
     * MessageDto를 Message Entity로 변환
     */
    public static Message toMessage(MessageDto messageDto) {
        if (messageDto == null) return null;

        Message message = new Message();
        message.setMessageId(messageDto.getMessageId() != null ? messageDto.getMessageId().intValue() : null);
        message.setSenderId(messageDto.getSenderId());
        message.setReceiverEmail(messageDto.getReceiverEmail());
        message.setSenderEmail(messageDto.getSenderEmail()); // ✅ 추가
        if (messageDto.getMessageType() != null) {
            message.setMessageType(com.example.companycore.model.entity.Enum.MessageType.valueOf(messageDto.getMessageType()));
        }
        message.setTitle(messageDto.getTitle());
        message.setContent(messageDto.getContent());
        message.setIsRead(messageDto.getIsRead());
        message.setSentAt(messageDto.getSentAt());

        return message;
    }

    /**
     * LeaveRequest Entity를 LeaveRequestDto로 변환
     */
    public static LeaveRequestDto toLeaveRequestDto(LeaveRequest leaveRequest) {
        if (leaveRequest == null) return null;
        
        return new LeaveRequestDto(
            leaveRequest.getLeaveId() != null ? leaveRequest.getLeaveId().longValue() : null,
            leaveRequest.getUserId(),
            leaveRequest.getLeaveType() != null ? leaveRequest.getLeaveType().toString() : null,
            leaveRequest.getStartDate(),
            leaveRequest.getEndDate(),
            leaveRequest.getReason(),
            leaveRequest.getStatus() != null ? leaveRequest.getStatus().toString() : null,
            leaveRequest.getApprovedBy(),
            null, // rejectedBy (LeaveRequest 엔티티에 없음)
            null, // rejectionReason (LeaveRequest 엔티티에 없음)
            leaveRequest.getAppliedAt() != null ? leaveRequest.getAppliedAt().toLocalDate() : null,
            leaveRequest.getAppliedAt() != null ? leaveRequest.getAppliedAt().toLocalDate() : null
        );
    }

    /**
     * LeaveRequestDto를 LeaveRequest Entity로 변환
     */
    public static LeaveRequest toLeaveRequest(LeaveRequestDto leaveRequestDto) {
        if (leaveRequestDto == null) return null;
        
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(leaveRequestDto.getLeaveId() != null ? leaveRequestDto.getLeaveId().intValue() : null);
        leaveRequest.setUserId(Long.parseLong(leaveRequestDto.getEmployeeId()));
        if (leaveRequestDto.getLeaveType() != null) {
            leaveRequest.setLeaveType(com.example.companycore.model.entity.Enum.LeaveType.valueOf(leaveRequestDto.getLeaveType()));
        }
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setStatus(com.example.companycore.model.entity.Enum.LeaveStatus.PENDING);
        leaveRequest.setAppliedAt(java.time.LocalDateTime.now());
        
        return leaveRequest;
    }
} 