package com.example.companycore.model.dto;

/**
 * 회의 데이터를 표현하는 DTO 클래스
 * JavaFX Property를 사용하여 데이터 바인딩을 지원
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class MeetingItem {
    private final javafx.beans.property.StringProperty title;
    private final javafx.beans.property.StringProperty department;
    private final javafx.beans.property.StringProperty author;
    private final javafx.beans.property.StringProperty date;
    private final javafx.beans.property.StringProperty description;
    private final javafx.beans.property.StringProperty location;
    private final javafx.beans.property.StringProperty attachmentContent;
    private final javafx.beans.property.StringProperty attachmentPath;
    private final javafx.beans.property.LongProperty attachmentSize;
    // 첨부파일 필드를 서버와 일치시킴
    private final javafx.beans.property.StringProperty attachmentFilename;
    private final javafx.beans.property.StringProperty attachmentContentType;

    /**
     * MeetingItem 생성자
     * 
     * @param title 제목
     * @param department 부서
     * @param author 작성자
     * @param date 날짜
     * @param description 설명
     * @param location 위치
     * @param attachmentContent 첨부파일 내용 (Base64)
     * @param attachmentPath 첨부파일 경로
     * @param attachmentSize 첨부파일 크기
     * @param attachmentFilename 첨부파일명
     * @param attachmentContentType 첨부파일 타입
     */
    public MeetingItem(String title, String department, String author, String date, 
                      String description, String location,
                      String attachmentContent, String attachmentPath, Long attachmentSize,
                      String attachmentFilename, String attachmentContentType) {
        this.title = new javafx.beans.property.SimpleStringProperty(title);
        this.department = new javafx.beans.property.SimpleStringProperty(department);
        this.author = new javafx.beans.property.SimpleStringProperty(author);
        this.date = new javafx.beans.property.SimpleStringProperty(date);
        this.description = new javafx.beans.property.SimpleStringProperty(description != null ? description : "");
        this.location = new javafx.beans.property.SimpleStringProperty(location != null ? location : "");
        this.attachmentContent = new javafx.beans.property.SimpleStringProperty(attachmentContent != null ? attachmentContent : "");
        this.attachmentPath = new javafx.beans.property.SimpleStringProperty(attachmentPath != null ? attachmentPath : "");
        this.attachmentSize = new javafx.beans.property.SimpleLongProperty(attachmentSize != null ? attachmentSize : 0L);
        this.attachmentFilename = new javafx.beans.property.SimpleStringProperty(attachmentFilename != null ? attachmentFilename : "");
        this.attachmentContentType = new javafx.beans.property.SimpleStringProperty(attachmentContentType != null ? attachmentContentType : "");
    }

    // ==================== Getter/Setter 메서드 ====================
    
    public String getTitle() { 
        return title.get(); 
    }
    
    public void setTitle(String title) { 
        this.title.set(title); 
    }
    
    public javafx.beans.property.StringProperty titleProperty() { 
        return title; 
    }

    public String getDepartment() { 
        return department.get(); 
    }
    
    public void setDepartment(String department) { 
        this.department.set(department); 
    }
    
    public javafx.beans.property.StringProperty departmentProperty() { 
        return department; 
    }

    public String getAuthor() { 
        return author.get(); 
    }
    
 public void setAuthor(String author) { 
        this.author.set(author); 
    }
    
    public javafx.beans.property.StringProperty authorProperty() { 
        return author; 
    }

    public String getDate() { 
        return date.get(); 
    }
    
    public void setDate(String date) { 
        this.date.set(date); 
    }
    
    public javafx.beans.property.StringProperty dateProperty() { 
        return date; 
    }

    public String getDescription() { 
        return description.get(); 
    }
    
    public void setDescription(String description) { 
        this.description.set(description); 
    }
    
    public javafx.beans.property.StringProperty descriptionProperty() { 
        return description; 
    }

    public String getLocation() { 
        return location.get(); 
    }
    
    public void setLocation(String location) { 
        this.location.set(location); 
    }
    
    public javafx.beans.property.StringProperty locationProperty() { 
        return location; 
    }

    public String getAttachmentContent() { 
        return attachmentContent.get(); 
    }
    
    public void setAttachmentContent(String attachmentContent) { 
        this.attachmentContent.set(attachmentContent); 
    }
    
    public javafx.beans.property.StringProperty attachmentContentProperty() { 
        return attachmentContent; 
    }

    public String getAttachmentPath() { 
        return attachmentPath.get(); 
    }
    
    public void setAttachmentPath(String attachmentPath) { 
        this.attachmentPath.set(attachmentPath); 
    }
    
    public javafx.beans.property.StringProperty attachmentPathProperty() { 
        return attachmentPath; 
    }

    public Long getAttachmentSize() { 
        return attachmentSize.get(); 
    }
    
    public void setAttachmentSize(Long attachmentSize) { 
        this.attachmentSize.set(attachmentSize); 
    }
    
    public javafx.beans.property.LongProperty attachmentSizeProperty() { 
        return attachmentSize; 
    }

    // 첨부파일 관련 getter/setter 추가
    public String getAttachmentFilename() { 
        return attachmentFilename.get(); 
    }
    
    public void setAttachmentFilename(String attachmentFilename) { 
        this.attachmentFilename.set(attachmentFilename); 
    }
    
    public javafx.beans.property.StringProperty attachmentFilenameProperty() { 
        return attachmentFilename; 
    }

    public String getAttachmentContentType() { 
        return attachmentContentType.get(); 
    }
    
    public void setAttachmentContentType(String attachmentContentType) { 
        this.attachmentContentType.set(attachmentContentType); 
    }
    
    public javafx.beans.property.StringProperty attachmentContentTypeProperty() { 
        return attachmentContentType; 
    }
} 