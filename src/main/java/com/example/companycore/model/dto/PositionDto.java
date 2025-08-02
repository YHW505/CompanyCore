package com.example.companycore.model.dto;

/**
 * Position 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class PositionDto {
    private Integer positionId;
    private String positionCode;
    private String positionName;
    private Integer levelOrder;

    // 기본 생성자
    public PositionDto() {}

    // 생성자
    public PositionDto(Integer positionId, String positionCode, String positionName, Integer levelOrder) {
        this.positionId = positionId;
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.levelOrder = levelOrder;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getPositionId() { return positionId; }
    public void setPositionId(Integer positionId) { this.positionId = positionId; }

    public String getPositionCode() { return positionCode; }
    public void setPositionCode(String positionCode) { this.positionCode = positionCode; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }

    @Override
    public String toString() {
        return "PositionDto{" +
                "positionId=" + positionId +
                ", positionCode='" + positionCode + '\'' +
                ", positionName='" + positionName + '\'' +
                ", levelOrder=" + levelOrder +
                '}';
    }
} 