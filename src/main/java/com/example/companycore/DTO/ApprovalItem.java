package com.example.companycore.DTO;

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
    private StringProperty action;
    private List<String> attachments;  // 첨부파일 리스트

    public ApprovalItem(String id, String title, String department, String author,
                        String date, String content, String action) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.content = content;
        this.action = new SimpleStringProperty(action);
    }

    // attachments getter/setter 추가
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    // 기존 getter (StringProperty -> String 변환)
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

    public StringProperty actionProperty() {
        return action;
    }

    public String getAction() {
        return action.get();
    }
}