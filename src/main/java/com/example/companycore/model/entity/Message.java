package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.MessageType;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Message {
    private final IntegerProperty messageId;
    private final LongProperty senderId;
    private final LongProperty receiverId;
    private final ObjectProperty<MessageType> messageType;
    private final StringProperty title;
    private final StringProperty content;
    private final BooleanProperty isRead;
    private final ObjectProperty<LocalDateTime> sentAt;
    
    // 첨부파일 관련 필드들
    private final StringProperty attachmentContentType;
    private final LongProperty attachmentSize;
    private final StringProperty attachmentContent;
    private final StringProperty attachmentFilename;
    
    // 사용자 정보 필드들
    private final StringProperty senderName;
    private final StringProperty senderEmployeeCode;
    private final StringProperty senderPositionName;
    private final StringProperty senderDepartmentName;
    private final StringProperty senderEmail;
    private final StringProperty receiverName;
    private final StringProperty receiverEmployeeCode;
    private final StringProperty receiverPositionName;
    private final StringProperty receiverDepartmentName;
    private final StringProperty receiverEmail;



    public Message() {
        this.messageId = new SimpleIntegerProperty();
        this.senderId = new SimpleLongProperty();
        this.receiverId = new SimpleLongProperty();
        this.messageType = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.isRead = new SimpleBooleanProperty(false);
        this.sentAt = new SimpleObjectProperty<>();
        
        // 첨부파일 관련 필드들 초기화
        this.attachmentContentType = new SimpleStringProperty();
        this.attachmentSize = new SimpleLongProperty();
        this.attachmentContent = new SimpleStringProperty();
        this.attachmentFilename = new SimpleStringProperty();
        
        // 사용자 정보 필드들 초기화
        this.senderName = new SimpleStringProperty();
        this.senderEmployeeCode = new SimpleStringProperty();
        this.senderPositionName = new SimpleStringProperty();
        this.senderDepartmentName = new SimpleStringProperty();
        this.senderEmail = new SimpleStringProperty();
        this.receiverName = new SimpleStringProperty();
        this.receiverEmployeeCode = new SimpleStringProperty();
        this.receiverPositionName = new SimpleStringProperty();
        this.receiverDepartmentName = new SimpleStringProperty();
        this.receiverEmail = new SimpleStringProperty();
    }

    public Message(Integer messageId, Long senderId, Long receiverId, MessageType messageType,
                   String title, String content, Boolean isRead, LocalDateTime sentAt) {
        this.messageId = new SimpleIntegerProperty(messageId);
        this.senderId = new SimpleLongProperty(senderId);
        this.receiverId = new SimpleLongProperty(receiverId);
        this.messageType = new SimpleObjectProperty<>(messageType);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.isRead = new SimpleBooleanProperty(isRead);
        this.sentAt = new SimpleObjectProperty<>(sentAt);
        
        // 첨부파일 관련 필드들 초기화
        this.attachmentContentType = new SimpleStringProperty();
        this.attachmentSize = new SimpleLongProperty();
        this.attachmentContent = new SimpleStringProperty();
        this.attachmentFilename = new SimpleStringProperty();
        
        // 사용자 정보 필드들 초기화
        this.senderName = new SimpleStringProperty();
        this.senderEmployeeCode = new SimpleStringProperty();
        this.senderPositionName = new SimpleStringProperty();
        this.senderDepartmentName = new SimpleStringProperty();
        this.senderEmail = new SimpleStringProperty();
        this.receiverName = new SimpleStringProperty();
        this.receiverEmployeeCode = new SimpleStringProperty();
        this.receiverPositionName = new SimpleStringProperty();
        this.receiverDepartmentName = new SimpleStringProperty();
        this.receiverEmail = new SimpleStringProperty();
    }

    // MessageId
    public Integer getMessageId() { return messageId.get(); }
    public void setMessageId(Integer messageId) { this.messageId.set(messageId); }
    public IntegerProperty messageIdProperty() { return messageId; }

    // SenderId
    public Long getSenderId() { return senderId.get(); }
    public void setSenderId(Long senderId) { this.senderId.set(senderId); }
    public LongProperty senderIdProperty() { return senderId; }

    // ReceiverId
    public Long getReceiverId() { return receiverId.get(); }
    public void setReceiverId(Long receiverId) { this.receiverId.set(receiverId); }
    public LongProperty receiverIdProperty() { return receiverId; }

    // ReceiverEmail
    public String getReceiverEmail() { return receiverEmail.get(); }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail.set(receiverEmail); }
    public StringProperty receiverEmailProperty() { return receiverEmail; }

    // SenderEmail
    public String getSenderEmail() { return senderEmail.get(); }
    public void setSenderEmail(String senderEmail) { this.senderEmail.set(senderEmail); }
    public StringProperty senderEmailProperty() { return senderEmail; }


    // MessageType
    public MessageType getMessageType() { return messageType.get(); }
    public void setMessageType(MessageType messageType) { this.messageType.set(messageType); }
    public ObjectProperty<MessageType> messageTypeProperty() { return messageType; }

    // Title
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    // Content
    public String getContent() { return content.get(); }
    public void setContent(String content) { this.content.set(content); }
    public StringProperty contentProperty() { return content; }

    // IsRead
    public Boolean getIsRead() { return isRead.get(); }
    public void setIsRead(Boolean isRead) { this.isRead.set(isRead); }
    public BooleanProperty isReadProperty() { return isRead; }

    // SentAt
    public LocalDateTime getSentAt() { return sentAt.get(); }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt.set(sentAt); }
    public ObjectProperty<LocalDateTime> sentAtProperty() { return sentAt; }

    // 첨부파일 관련 Getter/Setter
    public String getAttachmentContentType() { return attachmentContentType.get(); }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType.set(attachmentContentType); }
    public StringProperty attachmentContentTypeProperty() { return attachmentContentType; }

    public Long getAttachmentSize() { return attachmentSize.get(); }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize.set(attachmentSize); }
    public LongProperty attachmentSizeProperty() { return attachmentSize; }

    public String getAttachmentContent() { return attachmentContent.get(); }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent.set(attachmentContent); }
    public StringProperty attachmentContentProperty() { return attachmentContent; }

    public String getAttachmentFilename() { return attachmentFilename.get(); }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename.set(attachmentFilename); }
    public StringProperty attachmentFilenameProperty() { return attachmentFilename; }

    // 사용자 정보 관련 Getter/Setter
    public String getSenderName() { return senderName.get(); }
    public void setSenderName(String senderName) { this.senderName.set(senderName); }
    public StringProperty senderNameProperty() { return senderName; }

    public String getSenderEmployeeCode() { return senderEmployeeCode.get(); }
    public void setSenderEmployeeCode(String senderEmployeeCode) { this.senderEmployeeCode.set(senderEmployeeCode); }
    public StringProperty senderEmployeeCodeProperty() { return senderEmployeeCode; }

    public String getSenderPositionName() { return senderPositionName.get(); }
    public void setSenderPositionName(String senderPositionName) { this.senderPositionName.set(senderPositionName); }
    public StringProperty senderPositionNameProperty() { return senderPositionName; }

    public String getSenderDepartmentName() { return senderDepartmentName.get(); }
    public void setSenderDepartmentName(String senderDepartmentName) { this.senderDepartmentName.set(senderDepartmentName); }
    public StringProperty senderDepartmentNameProperty() { return senderDepartmentName; }

    public String getReceiverName() { return receiverName.get(); }
    public void setReceiverName(String receiverName) { this.receiverName.set(receiverName); }
    public StringProperty receiverNameProperty() { return receiverName; }

    public String getReceiverEmployeeCode() { return receiverEmployeeCode.get(); }
    public void setReceiverEmployeeCode(String receiverEmployeeCode) { this.receiverEmployeeCode.set(receiverEmployeeCode); }
    public StringProperty receiverEmployeeCodeProperty() { return receiverEmployeeCode; }

    public String getReceiverPositionName() { return receiverPositionName.get(); }
    public void setReceiverPositionName(String receiverPositionName) { this.receiverPositionName.set(receiverPositionName); }
    public StringProperty receiverPositionNameProperty() { return receiverPositionName; }

    public String getReceiverDepartmentName() { return receiverDepartmentName.get(); }
    public void setReceiverDepartmentName(String receiverDepartmentName) { this.receiverDepartmentName.set(receiverDepartmentName); }
    public StringProperty receiverDepartmentNameProperty() { return receiverDepartmentName; }


    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId.get() +
                ", senderId=" + senderId.get() +
                ", receiverId=" + receiverId.get() +
                ", messageType=" + messageType.get() +
                ", title='" + title.get() + '\'' +
                ", content='" + content.get() + '\'' +
                ", isRead=" + isRead.get() +
                ", sentAt=" + sentAt.get() +
                ", hasAttachment=" + (attachmentContent.get() != null && !attachmentContent.get().isEmpty()) +
                ", attachmentSize=" + attachmentSize.get() +
                ", senderName='" + senderName.get() + '\'' +
                ", receiverName='" + receiverName.get() + '\'' +
                '}';
    }
}
