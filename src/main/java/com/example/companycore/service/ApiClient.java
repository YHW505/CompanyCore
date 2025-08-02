package com.example.companycore.service;

import com.example.companycore.model.dto.LoginRequest;
import com.example.companycore.model.dto.LoginResponse;

/**
 * 통합 API 클라이언트
 * 각 기능별 API 클라이언트들을 통합 관리하는 역할
 */
public class ApiClient {
    private static ApiClient instance;
    private final UserApiClient userApiClient;
    private final TaskApiClient taskApiClient;
    private final AttendanceApiClient attendanceApiClient;
    private final LeaveApiClient leaveApiClient;
    private final MessageApiClient messageApiClient;

    private ApiClient() {
        this.userApiClient = UserApiClient.getInstance();
        this.taskApiClient = TaskApiClient.getInstance();
        this.attendanceApiClient = AttendanceApiClient.getInstance();
        this.leaveApiClient = LeaveApiClient.getInstance();
        this.messageApiClient = MessageApiClient.getInstance();
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }

    // 인증 관련 메서드들
    public LoginResponse login(LoginRequest loginRequest) {
        return userApiClient.login(loginRequest);
    }

    public boolean authenticate(String employeeCode, String password) {
        boolean result = userApiClient.authenticate(employeeCode, password);
        if (result) {
            // 모든 API 클라이언트에 토큰 공유
            String token = userApiClient.getAuthToken();
            taskApiClient.setAuthToken(token);
            attendanceApiClient.setAuthToken(token);
            leaveApiClient.setAuthToken(token);
            messageApiClient.setAuthToken(token);
        }
        return result;
    }

    public String getAuthToken() {
        return userApiClient.getAuthToken();
    }

    public boolean hasValidToken() {
        return userApiClient.hasValidToken();
    }

    public void clearToken() {
        userApiClient.clearToken();
        taskApiClient.clearToken();
        attendanceApiClient.clearToken();
        leaveApiClient.clearToken();
        messageApiClient.clearToken();
    }

    // User API 관련 메서드들
    public com.example.companycore.model.entity.User getCurrentUser() {
        return userApiClient.getCurrentUser();
    }

    public java.util.List<com.example.companycore.model.entity.User> getUsers() {
        return userApiClient.getUsers();
    }

    public boolean updateUser(com.example.companycore.model.entity.User user) {
        return userApiClient.updateUser(user);
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        return userApiClient.changePassword(currentPassword, newPassword);
    }

    public com.example.companycore.model.entity.User createUser(com.example.companycore.model.entity.User user) {
        return userApiClient.createUser(user);
    }

    public java.util.List<com.example.companycore.model.dto.NoticeItem> getNotices() {
        return userApiClient.getNotices();
    }

    // Task API 관련 메서드들
    public java.util.List<com.example.companycore.model.entity.Task> getTasks() {
        return taskApiClient.getTasks();
    }

    public java.util.List<com.example.companycore.model.entity.Task> getTasksAssignedToUser(Long userId) {
        return taskApiClient.getTasksAssignedToUser(userId);
    }

    public java.util.List<com.example.companycore.model.entity.Task> getTasksByStatus(String status) {
        return taskApiClient.getTasksByStatus(status);
    }

    public java.util.List<com.example.companycore.model.entity.Task> getTasksByType(String taskType) {
        return taskApiClient.getTasksByType(taskType);
    }

    public java.util.List<com.example.companycore.model.entity.Task> searchTasks(String keyword, String searchIn) {
        return taskApiClient.searchTasks(keyword, searchIn);
    }

    public java.util.List<com.example.companycore.model.entity.Task> filterTasks(Long assignedTo, Long assignedBy, 
                                                                                String status, String taskType,
                                                                                String startDate, String endDate, 
                                                                                Integer page, Integer size,
                                                                                String sortBy, String sortDir) {
        return taskApiClient.filterTasks(assignedTo, assignedBy, status, taskType, 
                                       startDate, endDate, page, size, sortBy, sortDir);
    }

    public com.example.companycore.model.entity.Task createTask(com.example.companycore.model.entity.Task task) {
        return taskApiClient.createTask(task);
    }

    public boolean updateTask(Long taskId, com.example.companycore.model.entity.Task task) {
        return taskApiClient.updateTask(taskId, task);
    }

    public boolean deleteTask(Long taskId) {
        return taskApiClient.deleteTask(taskId);
    }

    public com.example.companycore.model.entity.Task getTaskById(Long taskId) {
        return taskApiClient.getTaskById(taskId);
    }

    // Attendance API 관련 메서드들
    public boolean checkIn(Long userId) {
        return attendanceApiClient.checkIn(userId);
    }

    public boolean checkOut(Long userId) {
        return attendanceApiClient.checkOut(userId);
    }

    public java.util.List<com.example.companycore.model.entity.Attendance> getUserAttendance(Long userId) {
        return attendanceApiClient.getUserAttendance(userId);
    }

    public java.util.List<com.example.companycore.model.entity.Attendance> getUserAttendanceByDateRange(Long userId, String startDate, String endDate) {
        return attendanceApiClient.getUserAttendanceByDateRange(userId, startDate, endDate);
    }

    public java.util.List<com.example.companycore.model.entity.Attendance> getAttendanceByDate(String workDate) {
        return attendanceApiClient.getAttendanceByDate(workDate);
    }

    public String getTodayDashboard() {
        return attendanceApiClient.getTodayDashboard();
    }

    public String getUserAttendanceStats(Long userId, String startDate, String endDate) {
        return attendanceApiClient.getUserAttendanceStats(userId, startDate, endDate);
    }

    public String getMonthlyAttendanceStats(Long userId, int year, int month) {
        return attendanceApiClient.getMonthlyAttendanceStats(userId, year, month);
    }

    public java.util.List<com.example.companycore.model.entity.Attendance> getAttendanceByStatus(String status, String date, Long userId) {
        return attendanceApiClient.getAttendanceByStatus(status, date, userId);
    }

    public java.util.List<com.example.companycore.model.entity.Attendance> getNotCheckedOutAttendance(Long userId) {
        return attendanceApiClient.getNotCheckedOutAttendance(userId);
    }

    public com.example.companycore.model.entity.Attendance createAttendance(com.example.companycore.model.entity.Attendance attendance) {
        return attendanceApiClient.createAttendance(attendance);
    }

    public boolean updateAttendance(Long attendanceId, com.example.companycore.model.entity.Attendance attendance) {
        return attendanceApiClient.updateAttendance(attendanceId, attendance);
    }

    public boolean deleteAttendance(Long attendanceId) {
        return attendanceApiClient.deleteAttendance(attendanceId);
    }

    public com.example.companycore.model.entity.Attendance getAttendanceById(Long attendanceId) {
        return attendanceApiClient.getAttendanceById(attendanceId);
    }

    // 개별 API 클라이언트 접근자
    public UserApiClient getUserApiClient() {
        return userApiClient;
    }

    public TaskApiClient getTaskApiClient() {
        return taskApiClient;
    }

    public AttendanceApiClient getAttendanceApiClient() {
        return attendanceApiClient;
    }

    // Leave API 관련 메서드들
    public java.util.List<com.example.companycore.model.dto.LeaveRequestDto> getAllLeaveRequests() {
        return leaveApiClient.getAllLeaveRequests();
    }

    public com.example.companycore.model.dto.LeaveRequestDto getLeaveRequestById(Long leaveId) {
        return leaveApiClient.getLeaveRequestById(leaveId);
    }

    public java.util.List<com.example.companycore.model.dto.LeaveRequestDto> getLeaveRequestsByUserId(Long userId) {
        return leaveApiClient.getLeaveRequestsByUserId(userId);
    }

    public com.example.companycore.model.dto.LeaveRequestDto createLeaveRequest(com.example.companycore.model.dto.LeaveRequestDto leaveRequest) {
        return leaveApiClient.createLeaveRequest(leaveRequest);
    }

    public boolean approveLeaveRequest(Long leaveId, Long approverId) {
        return leaveApiClient.approveLeaveRequest(leaveId, approverId);
    }

    public boolean rejectLeaveRequest(Long leaveId, Long rejectedBy, String rejectionReason) {
        return leaveApiClient.rejectLeaveRequest(leaveId, rejectedBy, rejectionReason);
    }

    public boolean cancelLeaveRequest(Long leaveId, Long userId) {
        return leaveApiClient.cancelLeaveRequest(leaveId, userId);
    }

    public boolean updateLeaveRequest(Long leaveId, com.example.companycore.model.dto.LeaveRequestDto leaveRequest) {
        return leaveApiClient.updateLeaveRequest(leaveId, leaveRequest);
    }

    public java.util.List<com.example.companycore.model.dto.LeaveRequestDto> searchLeaveRequests(String status, String type, Long userId, 
                                                                                               String startDate, String endDate) {
        return leaveApiClient.searchLeaveRequests(status, type, userId, startDate, endDate);
    }

    public boolean deleteLeaveRequest(Long leaveId) {
        return leaveApiClient.deleteLeaveRequest(leaveId);
    }

    public LeaveApiClient getLeaveApiClient() {
        return leaveApiClient;
    }

    // Message API 관련 메서드들
    public com.example.companycore.model.dto.MessageDto sendMessage(com.example.companycore.model.dto.MessageDto message, Long senderId) {
        return messageApiClient.sendMessage(message, senderId);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getMessages(Long userId, String type, String messageType, 
                                                                                   String keyword, Boolean unreadOnly) {
        return messageApiClient.getMessages(userId, type, messageType, keyword, unreadOnly);
    }

    public com.example.companycore.model.dto.MessageDto getMessageById(Long messageId, Long userId) {
        return messageApiClient.getMessageById(messageId, userId);
    }

    public boolean updateMessageStatus(Long messageId, Long userId, String action) {
        return messageApiClient.updateMessageStatus(messageId, userId, action);
    }

    public boolean bulkUpdateMessages(Long userId, java.util.List<Long> messageIds, String action) {
        return messageApiClient.bulkUpdateMessages(userId, messageIds, action);
    }

    public com.example.companycore.model.dto.MessageDto replyToMessage(Long messageId, Long userId, String title, String content) {
        return messageApiClient.replyToMessage(messageId, userId, title, content);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getConversation(Long userId, Long otherUserId) {
        return messageApiClient.getConversation(userId, otherUserId);
    }

    public com.fasterxml.jackson.databind.JsonNode getMessageDashboard(Long userId) {
        return messageApiClient.getMessageDashboard(userId);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getReceivedMessages(Long userId, String messageType, String keyword, Boolean unreadOnly) {
        return messageApiClient.getReceivedMessages(userId, messageType, keyword, unreadOnly);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getSentMessages(Long userId, String messageType, String keyword) {
        return messageApiClient.getSentMessages(userId, messageType, keyword);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getAllMessages(Long userId, String messageType, String keyword) {
        return messageApiClient.getAllMessages(userId, messageType, keyword);
    }

    public java.util.List<com.example.companycore.model.dto.MessageDto> getUnreadMessages(Long userId) {
        return messageApiClient.getUnreadMessages(userId);
    }

    public MessageApiClient getMessageApiClient() {
        return messageApiClient;
    }
}
