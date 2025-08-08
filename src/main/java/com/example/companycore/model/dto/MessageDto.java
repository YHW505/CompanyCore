package com.example.companycore.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 메시지 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto {
    private Integer messageId;
    private Long senderId;
    private Long receiverId;
    private String messageType; // MESSAGE, EMAIL, NOTICE
    private String title;
    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;
    
    // 첨부파일 관련 필드들
    private String attachmentContentType;
    private Long attachmentSize;
    private String attachmentContent;
    private String attachmentFilename;
    
    // 사용자 정보 필드들
    private String senderName;
    private String senderEmployeeCode;
    private String senderPositionName;
    private String senderDepartmentName;
    private String senderEmail;
    private String receiverName;
    private String receiverEmployeeCode;
    private String receiverPositionName;
    private String receiverDepartmentName;
    private String receiverEmail;

    // equals & hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageDto that = (MessageDto) obj;
        return Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }

    // 기본 생성자
    public MessageDto() {}

    // 메시지 전송용 생성자
    public MessageDto(String receiverEmail, String title, String content, String messageType) {
        this.receiverEmail = receiverEmail;
        this.title = title;
        this.content = content;
        this.messageType = messageType;
        this.isRead = false;
    }

    // 전체 생성자 (서버 응답용)
    public MessageDto(Integer messageId, Long senderId, Long receiverId, String messageType,
                      String title, String content, Boolean isRead, LocalDateTime sentAt,
                      String attachmentContentType, Long attachmentSize, String attachmentContent, String attachmentFilename,
                      String senderName, String senderEmployeeCode, String senderPositionName, String senderDepartmentName, String senderEmail,
                      String receiverName, String receiverEmployeeCode, String receiverPositionName, String receiverDepartmentName, String receiverEmail) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.attachmentContent = attachmentContent;
        this.attachmentFilename = attachmentFilename;
        this.senderName = senderName;
        this.senderEmployeeCode = senderEmployeeCode;
        this.senderPositionName = senderPositionName;
        this.senderDepartmentName = senderDepartmentName;
        this.senderEmail = senderEmail;
        this.receiverName = receiverName;
        this.receiverEmployeeCode = receiverEmployeeCode;
        this.receiverPositionName = receiverPositionName;
        this.receiverDepartmentName = receiverDepartmentName;
        this.receiverEmail = receiverEmail;
    }

    // Getter & Setter
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    // 첨부파일 관련 Getter & Setter
    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }

    public String getAttachmentContent() { return attachmentContent; }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

    public String getAttachmentFilename() { return attachmentFilename; }
    public String setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename;
        return attachmentFilename;
    }

    // 사용자 정보 관련 Getter & Setter
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderEmployeeCode() { return senderEmployeeCode; }
    public void setSenderEmployeeCode(String senderEmployeeCode) { this.senderEmployeeCode = senderEmployeeCode; }

    public String getSenderPositionName() { return senderPositionName; }
    public void setSenderPositionName(String senderPositionName) { this.senderPositionName = senderPositionName; }

    public String getSenderDepartmentName() { return senderDepartmentName; }
    public void setSenderDepartmentName(String senderDepartmentName) { this.senderDepartmentName = senderDepartmentName; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverEmployeeCode() { return receiverEmployeeCode; }
    public void setReceiverEmployeeCode(String receiverEmployeeCode) { this.receiverEmployeeCode = receiverEmployeeCode; }

    public String getReceiverPositionName() { return receiverPositionName; }
    public void setReceiverPositionName(String receiverPositionName) { this.receiverPositionName = receiverPositionName; }

    public String getReceiverDepartmentName() { return receiverDepartmentName; }
    public void setReceiverDepartmentName(String receiverDepartmentName) { this.receiverDepartmentName = receiverDepartmentName; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }



    @Override
    public String toString() {
        return "MessageDto{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageType='" + messageType + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                ", sentAt=" + sentAt +
                ", hasAttachment=" + (attachmentContent != null && !attachmentContent.isEmpty()) +
                ", attachmentSize=" + attachmentSize +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                '}';
    }
}