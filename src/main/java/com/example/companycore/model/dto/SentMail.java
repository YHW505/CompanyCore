package com.example.companycore.model.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 보낸 메일 데이터를 표현하는 DTO 클래스
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class SentMail {
    private String recipient;
    private String subject;
    private String content;
    private String date;
    private String attachment;
    
    /**
     * SentMail 생성자
     * 
     * @param recipient 수신자
     * @param subject 제목
     * @param content 내용
     * @param attachment 첨부파일
     */
    public SentMail(String recipient, String subject, String content, String attachment) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.attachment = attachment;
        this.date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd"));
    }
    
    // ==================== Getter/Setter 메서드 ====================
    
    public String getRecipient() { 
        return recipient; 
    }
    
    public void setRecipient(String recipient) { 
        this.recipient = recipient; 
    }
    
    public String getSubject() { 
        return subject; 
    }
    
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
    
    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content; 
    }
    
    public String getDate() { 
        return date; 
    }
    
    public void setDate(String date) { 
        this.date = date; 
    }
    
    public String getAttachment() { 
        return attachment; 
    }
    
    public void setAttachment(String attachment) { 
        this.attachment = attachment; 
    }
} 