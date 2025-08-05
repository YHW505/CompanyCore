package com.example.companycore.service;

import com.example.companycore.model.dto.LoginRequest;
import com.example.companycore.model.dto.LoginResponse;
import com.example.companycore.model.dto.ApprovalDto;
import com.example.companycore.model.entity.User;

import java.util.List;

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
    private final MeetingApiClient meetingApiClient;
    private final ApprovalApiClient approvalApiClient;
    private final NoticeApiClient noticeApiClient;

    private ApiClient() {
        this.userApiClient = UserApiClient.getInstance();
        this.taskApiClient = TaskApiClient.getInstance();
        this.attendanceApiClient = AttendanceApiClient.getInstance();
        this.leaveApiClient = LeaveApiClient.getInstance();
        this.messageApiClient = MessageApiClient.getInstance();
        this.meetingApiClient = MeetingApiClient.getInstance();
        this.approvalApiClient = ApprovalApiClient.getInstance();
        this.noticeApiClient = NoticeApiClient.getInstance();
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
            // All API clients share the token
            String token = userApiClient.getAuthToken();
            taskApiClient.setAuthToken(token);
            attendanceApiClient.setAuthToken(token);
            leaveApiClient.setAuthToken(token);
            messageApiClient.setAuthToken(token);
            meetingApiClient.setAuthToken(token);
            approvalApiClient.setAuthToken(token);
            noticeApiClient.setAuthToken(token);
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
        meetingApiClient.clearToken();
        approvalApiClient.clearToken();
        noticeApiClient.clearToken();
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

    public boolean deleteUser(Long userId) {
        return userApiClient.deleteUser(userId);
    }

    public java.util.List<com.example.companycore.model.dto.NoticeItem> getNotices() {
        return noticeApiClient.getAllNotices();
    }

    // 새로 추가된 User API 메서드들
    public com.example.companycore.model.entity.User getUserById(Long userId) {
        return userApiClient.getUserById(userId);
    }

    public com.example.companycore.model.entity.User getUserByEmail(String email) {
        return userApiClient.getUserByEmailAsUser(email);
    }

    public com.example.companycore.model.entity.User getUserByEmployeeCode(String employeeCode) {
        return userApiClient.getUserByEmployeeCode(employeeCode);
    }

    public java.util.List<com.example.companycore.model.entity.User> getUsersByDepartment(String departmentName) {
        return userApiClient.getUsersByDepartment(departmentName);
    }

    public java.util.List<com.example.companycore.model.entity.User> getUsersByRole(String role) {
        return userApiClient.getUsersByRole(role);
    }

    public java.util.List<com.example.companycore.model.entity.User> searchUsersByName(String name) {
        return userApiClient.searchUsersByName(name);
    }

    public java.util.List<com.example.companycore.model.entity.User> getActiveUsers() {
        return userApiClient.getActiveUsers();
    }

    public java.util.List<com.example.companycore.model.entity.User> filterUsers(String department, String role, String status, String position, Integer page, Integer size) {
        return userApiClient.filterUsers(department, role, status, position, page, size);
    }

    public boolean updateFirstLoginStatus() {
        return userApiClient.updateFirstLoginStatus();
    }

    public boolean checkEmailExists(String email) {
        return userApiClient.checkEmailExists(email);
    }

    public boolean checkEmployeeCodeExists(String employeeCode) {
        return userApiClient.checkEmployeeCodeExists(employeeCode);
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
        return taskApiClient.filterTasks(assignedTo, assignedBy, status, taskType, startDate, endDate, page, size, sortBy, sortDir);
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

    // API 클라이언트 접근자들
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

    // Meeting API 관련 메서드들
    public java.util.List<MeetingApiClient.MeetingDto> getAllMeetings() {
        return meetingApiClient.getAllMeetings();
    }

    public MeetingApiClient.MeetingDto getMeetingById(Long meetingId) {
        return meetingApiClient.getMeetingById(meetingId);
    }

    public MeetingApiClient.MeetingDto createMeeting(MeetingApiClient.MeetingDto meetingDto) {
        return meetingApiClient.createMeeting(meetingDto);
    }

    public MeetingApiClient.MeetingDto updateMeeting(Long meetingId, MeetingApiClient.MeetingDto meetingDto) {
        return meetingApiClient.updateMeeting(meetingId, meetingDto);
    }

    public boolean deleteMeeting(Long meetingId) {
        return meetingApiClient.deleteMeeting(meetingId);
    }

    public java.util.List<MeetingApiClient.MeetingDto> getMeetingsByDate(String date) {
        return meetingApiClient.getMeetingsByDate(date);
    }

    public java.util.List<MeetingApiClient.MeetingDto> getMeetingsByLocation(String location) {
        return meetingApiClient.getMeetingsByLocation(location);
    }

    public MeetingApiClient getMeetingApiClient() {
        return meetingApiClient;
    }

    // Approval API 관련 메서드들
    public ApprovalDto createApproval(ApprovalDto approvalDto) {
        return approvalApiClient.createApproval(approvalDto);
    }

    public ApprovalDto approveApproval(Long approvalId) {
        return approvalApiClient.approveApproval(approvalId);
    }

    public ApprovalDto rejectApproval(Long approvalId, String rejectionReason) {
        return approvalApiClient.rejectApproval(approvalId, rejectionReason);
    }

    public ApprovalDto getApprovalById(Long approvalId) {
        return approvalApiClient.getApprovalById(approvalId);
    }

    public java.util.List<ApprovalDto> getMyRequests() {
        return approvalApiClient.getMyRequests();
    }

    public java.util.List<ApprovalDto> getMyApprovals() {
        return approvalApiClient.getMyApprovals();
    }

    public java.util.List<ApprovalDto> getMyPending() {
        return approvalApiClient.getMyPending();
    }

    public java.util.List<ApprovalDto> getAllApprovals() {
        return approvalApiClient.getAllApprovals();
    }

    public boolean deleteApproval(Long approvalId) {
        return approvalApiClient.deleteApproval(approvalId);
    }

    public java.util.List<ApprovalDto> searchApprovalsByTitle(String title) {
        return approvalApiClient.searchApprovalsByTitle(title);
    }

    public ApprovalApiClient getApprovalApiClient() {
        return approvalApiClient;
    }

    // Notice API 관련 메서드들
    public java.util.List<com.example.companycore.model.dto.NoticeItem> getAllNotices() {
        return noticeApiClient.getAllNotices();
    }

    public com.example.companycore.model.dto.NoticeItem getNoticeById(Long noticeId) {
        return noticeApiClient.getNoticeById(noticeId);
    }

    public com.example.companycore.model.dto.NoticeItem createNotice(com.example.companycore.model.dto.NoticeItem notice) {
        return noticeApiClient.createNotice(notice);
    }

    public com.example.companycore.model.dto.NoticeItem updateNotice(Long noticeId, com.example.companycore.model.dto.NoticeItem notice) {
        return noticeApiClient.updateNotice(noticeId, notice);
    }

    public boolean deleteNotice(Long noticeId) {
        return noticeApiClient.deleteNotice(noticeId);
    }

    public NoticeApiClient getNoticeApiClient() {
        return noticeApiClient;
    }
}
