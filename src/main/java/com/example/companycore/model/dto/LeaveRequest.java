package com.example.companycore.model.dto;

/**
 * 휴가신청 데이터를 담는 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class LeaveRequest {
    private String leaveType;
    private String employeeId;
    private String employeeName;
    private String date;
    private String status; // "pending", "approved", "rejected"
    
    /**
     * LeaveRequest 생성자
     * 
     * @param leaveType 휴가 유형
     * @param employeeId 사원 ID
     * @param employeeName 사원 이름
     * @param date 신청 날짜
     * @param status 승인 상태
     */
    public LeaveRequest(String leaveType, String employeeId, String employeeName, String date, String status) {
        this.leaveType = leaveType;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.status = status;
    }
    
    // ==================== Getter/Setter 메서드 ====================
    
    public String getLeaveType() { 
        return leaveType; 
    }
    
    public void setLeaveType(String leaveType) { 
        this.leaveType = leaveType; 
    }
    
    public String getEmployeeId() { 
        return employeeId; 
    }
    
    public void setEmployeeId(String employeeId) { 
        this.employeeId = employeeId; 
    }
    
    public String getEmployeeName() { 
        return employeeName; 
    }
    
    public void setEmployeeName(String employeeName) { 
        this.employeeName = employeeName; 
    }
    
    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
} 