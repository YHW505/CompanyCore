package com.example.companycore.model.entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class Department {
    private final IntegerProperty departmentId;
    private final StringProperty departmentCode;
    private final StringProperty departmentName;
    private final ObservableList<User> users;

    public Department() {
        this.departmentId = new SimpleIntegerProperty();
        this.departmentCode = new SimpleStringProperty();
        this.departmentName = new SimpleStringProperty();
        this.users = FXCollections.observableArrayList();
    }

    public Department(Integer departmentId, String departmentCode, String departmentName) {
        this.departmentId = new SimpleIntegerProperty(departmentId);
        this.departmentCode = new SimpleStringProperty(departmentCode);
        this.departmentName = new SimpleStringProperty(departmentName);
        this.users = FXCollections.observableArrayList();
    }

    // DepartmentId
    public Integer getDepartmentId() { return departmentId.get(); }
    public void setDepartmentId(Integer departmentId) { this.departmentId.set(departmentId); }
    public IntegerProperty departmentIdProperty() { return departmentId; }

    // DepartmentCode
    public String getDepartmentCode() { return departmentCode.get(); }
    public void setDepartmentCode(String departmentCode) { this.departmentCode.set(departmentCode); }
    public StringProperty departmentCodeProperty() { return departmentCode; }

    // DepartmentName
    public String getDepartmentName() { return departmentName.get(); }
    public void setDepartmentName(String departmentName) { this.departmentName.set(departmentName); }
    public StringProperty departmentNameProperty() { return departmentName; }

    // Users
    public ObservableList<User> getUsers() { return users; }
    public void setUsers(List<User> users) { 
        this.users.clear();
        this.users.addAll(users);
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId.get() +
                ", departmentCode='" + departmentCode.get() + '\'' +
                ", departmentName='" + departmentName.get() + '\'' +
                '}';
    }
}
