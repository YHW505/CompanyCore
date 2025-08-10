package com.example.companycore.model.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 결재 관련 통합 DTO 클래스
 * UI 바인딩과 서버 통신을 모두 지원
 */
public class ApprovalItem {

    // UI용 JavaFX Properties
    private StringProperty id;
    private StringProperty title;
    private StringProperty department;
    private StringProperty author;
    private StringProperty date;
    private StringProperty content;
    private StringProperty status;
    private StringProperty attachmentContent;
    private StringProperty attachmentFilename;
    private LongProperty attachmentSize;

    // 서버 통신용 필드들
    private Long serverId;
    private Long requesterId;
    private Long approverId;
    private UserDto requester;
    private UserDto approver;
    private LocalDateTime requestDate;
    private String rejectionReason;
    private LocalDateTime processedDate;
    private String attachmentContentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // UI용 생성자
    public ApprovalItem(String id, String title, String department, String author,
                        String date, String content, String status) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.content = new SimpleStringProperty(content);
        this.status = new SimpleStringProperty(status);
        this.attachmentContent = new SimpleStringProperty();
        this.attachmentFilename = new SimpleStringProperty();
        this.attachmentSize = new SimpleLongProperty();
    }

    // UI용 생성자 (Long ID 포함)
    public ApprovalItem(Long serverId, String title, String department, String author,
                        String date, String content, String status) {
        this.id = new SimpleStringProperty(String.valueOf(serverId));
        this.serverId = serverId;
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.content = new SimpleStringProperty(content);
        this.status = new SimpleStringProperty(status);
        this.attachmentContent = new SimpleStringProperty();
        this.attachmentFilename = new SimpleStringProperty();
        this.attachmentSize = new SimpleLongProperty();
    }

    // 서버 통신용 생성자
    public ApprovalItem() {
        this.id = new SimpleStringProperty();
        this.title = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.author = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.attachmentContent = new SimpleStringProperty();
        this.attachmentFilename = new SimpleStringProperty();
        this.attachmentSize = new SimpleLongProperty();
    }

    // ApprovalDto로부터 변환하는 정적 메서드 (성능 최적화)
    public static ApprovalItem fromApprovalDto(ApprovalDto dto) {
        ApprovalItem item = new ApprovalItem();
        
        // UI용 필드만 설정 (성능 최적화)
        item.setId(dto.getId() != null ? dto.getId().toString() : "");
        item.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        item.setContent(dto.getContent() != null ? dto.getContent() : "");
        item.setStatus(dto.getStatus() != null ? dto.getStatus() : "");
        
        // 날짜 설정
        if (dto.getRequestDate() != null) {
            item.setDate(dto.getRequestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        // 사용자 정보 설정 (최소한의 정보만)
        if (dto.getRequester() != null) {
            item.setAuthor(dto.getRequester().getUsername() != null ? dto.getRequester().getUsername() : "");
            item.setDepartment(dto.getRequester().getDepartmentName() != null ? 
                dto.getRequester().getDepartmentName() : "");
        }
        
        // 첨부파일 정보 설정
        if (dto.getAttachmentFilename() != null) {
            item.setAttachmentFilename(dto.getAttachmentFilename());
        }
        if (dto.getAttachmentSize() != null) {
            item.setAttachmentSize(dto.getAttachmentSize());
        }
        if (dto.getAttachmentContent() != null) {
            item.setAttachmentContent(dto.getAttachmentContent());
        }
        
        // 필수 서버용 필드만 설정 (성능 최적화)
        item.serverId = dto.getId();
        item.requesterId = dto.getRequesterId();
        
        return item;
    }

    // ApprovalDto로 변환하는 메서드
    public ApprovalDto toApprovalDto() {
        ApprovalDto dto = new ApprovalDto();
        
        // 서버용 필드 설정
        dto.setId(this.serverId);
        dto.setTitle(this.getTitle());
        dto.setContent(this.getContent());
        dto.setRequesterId(this.requesterId);
        dto.setApproverId(this.approverId);
        dto.setRequester(this.requester);
        dto.setApprover(this.approver);
        dto.setRequestDate(this.requestDate);
        dto.setStatus(this.getStatus());
        dto.setRejectionReason(this.rejectionReason);
        dto.setProcessedDate(this.processedDate);
        dto.setAttachmentFilename(this.getAttachmentFilename());
        dto.setAttachmentSize(this.getAttachmentSize());
        dto.setAttachmentContentType(this.attachmentContentType);
        dto.setAttachmentContent(this.getAttachmentContent()); // Base64 인코딩된 첨부파일 내용
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);
        
        return dto;
    }

    // UI용 getter/setter (기존 유지)
    public String getId() { return id.get(); }
    public void setId(String id) { this.id.set(id); }
    public StringProperty idProperty() { return id; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getDepartment() { return department.get(); }
    public void setDepartment(String department) { this.department.set(department); }
    public StringProperty departmentProperty() { return department; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public StringProperty authorProperty() { return author; }

    public String getDate() { return date.get(); }
    public void setDate(String date) { this.date.set(date); }
    public StringProperty dateProperty() { return date; }

    public String getContent() { return content.get(); }
    public void setContent(String content) { this.content.set(content); }
    public StringProperty contentProperty() { return content; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }
    
    // 🆕 상태를 한글로 변환하는 메서드
    public String getStatusKorean() {
        String currentStatus = getStatus();
        if (currentStatus == null) return "알 수 없음";
        switch (currentStatus.toUpperCase()) {
            case "PENDING": return "대기중";
            case "APPROVED": return "승인됨";
            case "REJECTED": return "거부됨";
            default: return currentStatus;
        }
    }

    public String getAttachmentContent() { return attachmentContent.get(); }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent.set(attachmentContent); }
    public StringProperty attachmentContentProperty() { return attachmentContent; }

    public String getAttachmentFilename() { return attachmentFilename.get(); }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename.set(attachmentFilename); }
    public StringProperty attachmentFilenameProperty() { return attachmentFilename; }

    public Long getAttachmentSize() { return attachmentSize.get(); }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize.set(attachmentSize); }
    public LongProperty attachmentSizeProperty() { return attachmentSize; }

    // 서버용 getter/setter
    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }

    public UserDto getRequester() { return requester; }
    public void setRequester(UserDto requester) { this.requester = requester; }

    public UserDto getApprover() { return approver; }
    public void setApprover(UserDto approver) { this.approver = approver; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}