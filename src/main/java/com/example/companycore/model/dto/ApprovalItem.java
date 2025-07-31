package com.example.companycore.model.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public class ApprovalItem {

    private String id;
    private StringProperty title;
    private StringProperty department;
    private StringProperty author;
    private StringProperty date;
    private String content;

    // 상태: "대기", "승인", "거부" 등
    private StringProperty status;

    private List<String> attachments;  // 첨부파일 리스트

    public ApprovalItem(String id, String title, String department, String author,
                        String date, String content, String status) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.content = content;
        this.status = new SimpleStringProperty(status);
    }

    // attachments getter/setter
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    // 기존 getter/setter
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDepartment() {
        return department.get();
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public String getContent() {
        return content;
    }

    // status 프로퍼티 및 getter/setter 추가
    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }
}