package com.example.companycore.model.dto;

import javafx.beans.property.*;

import java.time.LocalDate;

public class NoticeItem {
    private final StringProperty title;
    private final StringProperty department;
    private final StringProperty author;
    private final ObjectProperty<LocalDate> date;
    private final BooleanProperty selected;

    public NoticeItem(String title, String department, String author, LocalDate date, boolean selected) {
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleObjectProperty<>(date);
        this.selected = new SimpleBooleanProperty(selected);
    }

    // 기본 생성자 (빈 줄용)
    public NoticeItem() {
        this("", "", "", null, false);
    }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getDepartment() { return department.get(); }
    public void setDepartment(String value) { department.set(value); }
    public StringProperty departmentProperty() { return department; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate value) { date.set(value); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean value) { selected.set(value); }
    public BooleanProperty selectedProperty() { return selected; }
}