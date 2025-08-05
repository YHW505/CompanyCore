package com.example.companycore.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * 메시지 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto {
    private Long messageId;
    private Long senderId;
    private String receiverEmail;
    private String title;
    private String content;
    private String messageType; // MESSAGE, EMAIL, NOTICE
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private String senderName;
    private String receiverName;

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

    // 전체 생성자
    public MessageDto(Long messageId, Long senderId, String receiverEmail, String title, String content,
                     String messageType, Boolean isRead, LocalDateTime sentAt, LocalDateTime readAt,
                     String senderName, String receiverName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverEmail = receiverEmail;
        this.title = title;
        this.content = content;
        this.messageType = messageType;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    // Getter & Setter
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageType() { return "EMAIL"; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    @Override
    public String toString() {
        return "MessageDto{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverEmail +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", messageType='" + messageType + '\'' +
                ", isRead=" + isRead +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                '}';
    }
} 