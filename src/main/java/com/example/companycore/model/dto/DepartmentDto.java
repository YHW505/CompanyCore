package com.example.companycore.model.dto;

/**
 * Department 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class DepartmentDto {
    private Integer departmentId;
    private String departmentCode;
    private String departmentName;

    // 기본 생성자
    public DepartmentDto() {}

    // 생성자
    public DepartmentDto(Integer departmentId, String departmentCode, String departmentName) {
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
    }

    // 문자열로부터 생성하는 생성자 (Jackson 역직렬화용)
    public DepartmentDto(String departmentName) {
        this.departmentName = departmentName;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    @Override
    public String toString() {
        return "DepartmentDto{" +
                "departmentId=" + departmentId +
                ", departmentCode='" + departmentCode + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
} 