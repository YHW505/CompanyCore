package com.example.companycore.model.entity;

import com.example.companycore.model.entity.Enum.MessageType;
import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Message {
    private final IntegerProperty messageId;
    private final LongProperty senderId;
    private final StringProperty receiverEmail;
    private final ObjectProperty<MessageType> messageType;
    private final StringProperty title;
    private final StringProperty content;
    private final BooleanProperty isRead;
    private final ObjectProperty<LocalDateTime> sentAt;
    private final StringProperty senderEmail;



    public Message() {
        this.messageId = new SimpleIntegerProperty();
        this.senderId = new SimpleLongProperty();
        this.senderEmail = new SimpleStringProperty(); // senderEmail도 기본 생성
        this.receiverEmail = new SimpleStringProperty();
        this.messageType = new SimpleObjectProperty<>();
        this.title = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.isRead = new SimpleBooleanProperty(false);
        this.sentAt = new SimpleObjectProperty<>();
    }

    public Message(Integer messageId, Long senderId, String receiverEmail, MessageType messageType,
                   String title, String content, Boolean isRead, LocalDateTime sentAt, StringProperty senderEmail) {
        this.messageId = new SimpleIntegerProperty(messageId);
        this.senderId = new SimpleLongProperty(senderId);
        this.receiverEmail = new SimpleStringProperty(receiverEmail);
        this.messageType = new SimpleObjectProperty<>(messageType);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.isRead = new SimpleBooleanProperty(isRead);
        this.sentAt = new SimpleObjectProperty<>(sentAt);
        this.senderEmail = senderEmail;
    }

    // MessageId
    public Integer getMessageId() { return messageId.get(); }
    public void setMessageId(Integer messageId) { this.messageId.set(messageId); }
    public IntegerProperty messageIdProperty() { return messageId; }

    // SenderId
    public Long getSenderId() { return senderId.get(); }
    public void setSenderId(Long senderId) { this.senderId.set(senderId); }
    public LongProperty senderIdProperty() { return senderId; }

    // ReceiverEmail
    public String getReceiverEmail() { return receiverEmail.get(); }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail.set(receiverEmail); }
    public StringProperty receiverEmailProperty() { return receiverEmail; }

    public String getSenderEmail() {return senderEmail.get();}
    public void setSenderEmail(String senderEmail) {this.senderEmail.set(senderEmail);}
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


    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId.get() +
                ", senderId=" + senderId.get() +
                ", receiverEmail=" + receiverEmail.get() +
                ", messageType=" + messageType.get() +
                ", title='" + title.get() + '\'' +
                ", content='" + content.get() + '\'' +
                ", isRead=" + isRead.get() +
                ", sentAt=" + sentAt.get() +
                '}';
    }
}
