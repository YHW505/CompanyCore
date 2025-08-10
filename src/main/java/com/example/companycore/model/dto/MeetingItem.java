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

    /**
     * MeetingItem 생성자
     * 
     * @param title 제목
     * @param department 부서
     * @param author 작성자
     * @param date 날짜
     */
    public MeetingItem(String title, String department, String author, String date) {
        this.title = new javafx.beans.property.SimpleStringProperty(title);
        this.department = new javafx.beans.property.SimpleStringProperty(department);
        this.author = new javafx.beans.property.SimpleStringProperty(author);
        this.date = new javafx.beans.property.SimpleStringProperty(date);
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
} 