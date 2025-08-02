package com.example.companycore.model.dto;

/**
 * 휴가신청 데이터를 담는 DTO 클래스 (UI용)
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class LeaveRequestDto {
    private String leaveType;
    private String employeeId;
    private String employeeName;
    private String startDate;
    private String endDate;
    private String status; // "pending", "approved", "rejected"
    
    /**
     * LeaveRequestDto 생성자
     * 
     * @param leaveType 휴가 유형
     * @param employeeId 사원 ID
     * @param employeeName 사원 이름
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param status 승인 상태
     */
    public LeaveRequestDto(String leaveType, String employeeId, String employeeName, 
                          String startDate, String endDate, String status) {
        this.leaveType = leaveType;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
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
    
    public String getStartDate() { 
        return startDate; 
    }
    
    public void setStartDate(String startDate) { 
        this.startDate = startDate; 
    }
    
    public String getEndDate() { 
        return endDate; 
    }
    
    public void setEndDate(String endDate) { 
        this.endDate = endDate; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    @Override
    public String toString() {
        return "LeaveRequestDto{" +
                "leaveType='" + leaveType + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 