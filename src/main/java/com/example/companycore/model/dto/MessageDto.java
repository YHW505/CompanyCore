package com.example.companycore.model.dto;

import com.example.companycore.model.entity.Enum.MessageType;
import java.time.LocalDateTime;

/**
 * Message 정보를 전달하기 위한 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class MessageDto {
    private Integer messageId;
    private Long senderId;
    private Long receiverId;
    private MessageType messageType;
    private String title;
    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;

    // 기본 생성자
    public MessageDto() {}

    // 생성자
    public MessageDto(Integer messageId, Long senderId, Long receiverId, MessageType messageType,
                     String title, String content, Boolean isRead, LocalDateTime sentAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    // ==================== Getter/Setter 메서드 ====================

    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return "MessageDto{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageType=" + messageType +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                ", sentAt=" + sentAt +
                '}';
    }
} 