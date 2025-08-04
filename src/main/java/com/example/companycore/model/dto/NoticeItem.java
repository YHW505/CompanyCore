package com.example.companycore.model.dto;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeItem {
    private final LongProperty noticeId;
    private final StringProperty title;
    private final StringProperty content;
    private final StringProperty department;
    private final StringProperty author;
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;
    private final BooleanProperty selected;
    private final BooleanProperty isImportant;
    
    // 첨부파일 관련 필드
    private final StringProperty attachmentFilename;
    private final StringProperty attachmentContentType;
    private final LongProperty attachmentSize;
    private final BooleanProperty hasAttachments;
    private final StringProperty attachmentContent; // 실제 파일 내용 (Base64)

    public NoticeItem(Long noticeId, String title, String content, String department, String author, 
                     LocalDate date, LocalDateTime createdAt, LocalDateTime updatedAt, 
                     boolean selected, boolean isImportant) {
        this.noticeId = new SimpleLongProperty(noticeId);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleObjectProperty<>(date);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
        this.selected = new SimpleBooleanProperty(selected);
        this.isImportant = new SimpleBooleanProperty(isImportant);
        
        // 첨부파일 필드 초기화
        this.attachmentFilename = new SimpleStringProperty("");
        this.attachmentContentType = new SimpleStringProperty("");
        this.attachmentSize = new SimpleLongProperty(0L);
        this.hasAttachments = new SimpleBooleanProperty(false);
        this.attachmentContent = new SimpleStringProperty("");
    }

    // 기본 생성자
    public NoticeItem() {
        this(0L, "", "", "", "", null, null, null, false, false);
    }

    // 기존 생성자 호환성 유지
    public NoticeItem(String title, String department, String author, LocalDate date, boolean selected) {
        this(0L, title, "", department, author, date, null, null, selected, false);
    }

    // NoticeId
    public Long getNoticeId() { return noticeId.get(); }
    public void setNoticeId(Long value) { noticeId.set(value); }
    public LongProperty noticeIdProperty() { return noticeId; }

    // Title
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    // Content
    public String getContent() { return content.get(); }
    public void setContent(String value) { content.set(value); }
    public StringProperty contentProperty() { return content; }

    // Department
    public String getDepartment() { return department.get(); }
    public void setDepartment(String value) { department.set(value); }
    public StringProperty departmentProperty() { return department; }

    // Author
    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    // Date
    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate value) { date.set(value); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    // CreatedAt
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // UpdatedAt
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    // Selected
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }
    public BooleanProperty selectedProperty() { return selected; }

    // IsImportant
    public boolean isImportant() { return isImportant.get(); }
    public void setImportant(boolean value) { isImportant.set(value); }
    public BooleanProperty importantProperty() { return isImportant; }
    
    // 첨부파일 관련 메서드
    public String getAttachmentFilename() { return attachmentFilename.get(); }
    public void setAttachmentFilename(String value) { attachmentFilename.set(value); }
    public StringProperty attachmentFilenameProperty() { return attachmentFilename; }
    
    public String getAttachmentContentType() { return attachmentContentType.get(); }
    public void setAttachmentContentType(String value) { attachmentContentType.set(value); }
    public StringProperty attachmentContentTypeProperty() { return attachmentContentType; }
    
    public Long getAttachmentSize() { return attachmentSize.get(); }
    public void setAttachmentSize(Long value) { attachmentSize.set(value); }
    public LongProperty attachmentSizeProperty() { return attachmentSize; }
    
    public boolean hasAttachments() { return hasAttachments.get(); }
    public void setHasAttachments(boolean value) { hasAttachments.set(value); }
    public BooleanProperty hasAttachmentsProperty() { return hasAttachments; }

    // AttachmentContent
    public String getAttachmentContent() { return attachmentContent.get(); }
    public void setAttachmentContent(String value) { attachmentContent.set(value); }
    public StringProperty attachmentContentProperty() { return attachmentContent; }

    @Override
    public String toString() {
        return "NoticeItem{" +
                "noticeId=" + noticeId.get() +
                ", title='" + title.get() + '\'' +
                ", department='" + department.get() + '\'' +
                ", author='" + author.get() + '\'' +
                ", date=" + date.get() +
                ", selected=" + selected.get() +
                ", hasAttachments=" + hasAttachments.get() +
                '}';
    }
}