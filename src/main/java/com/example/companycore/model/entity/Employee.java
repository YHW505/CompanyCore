package com.example.companycore.model.entity;

import javafx.beans.property.*;

public class Employee {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty employeeId;
    private final StringProperty department;
    private final StringProperty address;
    private final StringProperty phoneNumber;
    private final StringProperty email;
    private final StringProperty position;
    private final StringProperty password;

    public Employee() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.employeeId = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.position = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
    }

    public Employee(int id, String name, String employeeId, String department, 
                   String address, String phoneNumber, String email, String position, String password) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.employeeId = new SimpleStringProperty(employeeId);
        this.department = new SimpleStringProperty(department);
        this.address = new SimpleStringProperty(address);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.email = new SimpleStringProperty(email);
        this.position = new SimpleStringProperty(position);
        this.password = new SimpleStringProperty(password);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Name
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // Employee ID
    public String getEmployeeId() { return employeeId.get(); }
    public void setEmployeeId(String employeeId) { this.employeeId.set(employeeId); }
    public StringProperty employeeIdProperty() { return employeeId; }

    // Department
    public String getDepartment() { return department.get(); }
    public void setDepartment(String department) { this.department.set(department); }
    public StringProperty departmentProperty() { return department; }

    // Address
    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    // Phone Number
    public String getPhoneNumber() { return phoneNumber.get(); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    // Email
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // Position
    public String getPosition() { return position.get(); }
    public void setPosition(String position) { this.position.set(position); }
    public StringProperty positionProperty() { return position; }

    // Password
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id.get() +
                ", name='" + name.get() + '\'' +
                ", employeeId='" + employeeId.get() + '\'' +
                ", department='" + department.get() + '\'' +
                ", address='" + address.get() + '\'' +
                ", phoneNumber='" + phoneNumber.get() + '\'' +
                ", email='" + email.get() + '\'' +
                ", position='" + position.get() + '\'' +
                '}';
    }
} 