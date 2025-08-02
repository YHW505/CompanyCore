package com.example.companycore.model.dto;

/**
 * 일정 테이블의 행 데이터를 표현하는 DTO 클래스
 * JavaFX Property를 사용하여 데이터 바인딩을 지원
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class ScheduleRow {
    private final javafx.beans.property.SimpleStringProperty time;
    private final javafx.beans.property.SimpleStringProperty monday;
    private final javafx.beans.property.SimpleStringProperty tuesday;
    private final javafx.beans.property.SimpleStringProperty wednesday;
    private final javafx.beans.property.SimpleStringProperty thursday;
    private final javafx.beans.property.SimpleStringProperty friday;
    private final javafx.beans.property.SimpleStringProperty saturday;

    /**
     * ScheduleRow 생성자
     * 
     * @param time 시간
     * @param monday 월요일 일정
     * @param tuesday 화요일 일정
     * @param wednesday 수요일 일정
     * @param thursday 목요일 일정
     * @param friday 금요일 일정
     * @param saturday 토요일 일정
     */
    public ScheduleRow(String time, String monday, String tuesday, String wednesday, 
                      String thursday, String friday, String saturday) {
        this.time = new javafx.beans.property.SimpleStringProperty(time);
        this.monday = new javafx.beans.property.SimpleStringProperty(monday);
        this.tuesday = new javafx.beans.property.SimpleStringProperty(tuesday);
        this.wednesday = new javafx.beans.property.SimpleStringProperty(wednesday);
        this.thursday = new javafx.beans.property.SimpleStringProperty(thursday);
        this.friday = new javafx.beans.property.SimpleStringProperty(friday);
        this.saturday = new javafx.beans.property.SimpleStringProperty(saturday);
    }

    // ==================== Property Getter 메서드 ====================
    
    public javafx.beans.property.StringProperty timeProperty() { 
        return time; 
    }
    
    public javafx.beans.property.StringProperty mondayProperty() { 
        return monday; 
    }
    
    public javafx.beans.property.StringProperty tuesdayProperty() { 
        return tuesday; 
    }
    
    public javafx.beans.property.StringProperty wednesdayProperty() { 
        return wednesday; 
    }
    
    public javafx.beans.property.StringProperty thursdayProperty() { 
        return thursday; 
    }
    
    public javafx.beans.property.StringProperty fridayProperty() { 
        return friday; 
    }
    
    public javafx.beans.property.StringProperty saturdayProperty() { 
        return saturday; 
    }
} 