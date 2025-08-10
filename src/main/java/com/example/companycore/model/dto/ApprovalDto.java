package com.example.companycore.model.dto;

import java.time.LocalDateTime;

/**
 * 결재 관련 DTO 클래스
 * 서버와의 통신을 위한 데이터 전송 객체
 */
public class ApprovalDto {
    private Long id;
    private String title;
    private String content;
    private Long requesterId;
    private Long approverId;
    private UserDto requester;
    private UserDto approver;
    private LocalDateTime requestDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String rejectionReason;
    private LocalDateTime processedDate;
    // 첨부파일 필드를 서버와 일치시킴
    private String attachmentFilename;
    private String attachmentContentType;
    private Long attachmentSize;
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ApprovalDto() {}

    public ApprovalDto(String title, String content, Long approverId, String attachmentPath) {
        this.title = title;
        this.content = content;
        this.approver = new UserDto();
        this.approver.setUserId(approverId);
        this.attachmentFilename = attachmentPath; // attachmentPath를 attachmentFilename으로 매핑
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // 🆕 상태를 한글로 변환하는 메서드
    public String getStatusKorean() {
        if (status == null) return "알 수 없음";
        switch (status.toUpperCase()) {
            case "PENDING": return "대기중";
            case "APPROVED": return "승인됨";
            case "REJECTED": return "거부됨";
            default: return status;
        }
    }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }

    // 첨부파일 관련 getter/setter 수정
    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }

    public String getAttachmentContent() { return attachmentContent; }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

    // 기존 attachmentPath 호환성을 위한 메서드
    public String getAttachmentPath() { return attachmentFilename; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentFilename = attachmentPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ApprovalDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", requesterId=" + requesterId +
                ", approverId=" + approverId +
                ", requester=" + requester +
                ", approver=" + approver +
                ", requestDate=" + requestDate +
                ", status='" + status + '\'' +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", processedDate=" + processedDate +
                ", attachmentFilename='" + attachmentFilename + '\'' +
                ", attachmentContentType='" + attachmentContentType + '\'' +
                ", attachmentSize=" + attachmentSize +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 