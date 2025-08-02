package com.example.companycore.model.entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class Position {
    private final IntegerProperty positionId;
    private final StringProperty positionCode;
    private final StringProperty positionName;
    private final IntegerProperty levelOrder;
    private final ObservableList<User> users;

    public Position() {
        this.positionId = new SimpleIntegerProperty();
        this.positionCode = new SimpleStringProperty();
        this.positionName = new SimpleStringProperty();
        this.levelOrder = new SimpleIntegerProperty();
        this.users = FXCollections.observableArrayList();
    }

    public Position(Integer positionId, String positionCode, String positionName, Integer levelOrder) {
        this.positionId = new SimpleIntegerProperty(positionId);
        this.positionCode = new SimpleStringProperty(positionCode);
        this.positionName = new SimpleStringProperty(positionName);
        this.levelOrder = new SimpleIntegerProperty(levelOrder);
        this.users = FXCollections.observableArrayList();
    }

    // PositionId
    public Integer getPositionId() { return positionId.get(); }
    public void setPositionId(Integer positionId) { this.positionId.set(positionId); }
    public IntegerProperty positionIdProperty() { return positionId; }

    // PositionCode
    public String getPositionCode() { return positionCode.get(); }
    public void setPositionCode(String positionCode) { this.positionCode.set(positionCode); }
    public StringProperty positionCodeProperty() { return positionCode; }

    // PositionName
    public String getPositionName() { return positionName.get(); }
    public void setPositionName(String positionName) { this.positionName.set(positionName); }
    public StringProperty positionNameProperty() { return positionName; }

    // LevelOrder
    public Integer getLevelOrder() { return levelOrder.get(); }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder.set(levelOrder); }
    public IntegerProperty levelOrderProperty() { return levelOrder; }

    // Users
    public ObservableList<User> getUsers() { return users; }
    public void setUsers(List<User> users) { 
        this.users.clear();
        this.users.addAll(users);
    }

    @Override
    public String toString() {
        return "Position{" +
                "positionId=" + positionId.get() +
                ", positionCode='" + positionCode.get() + '\'' +
                ", positionName='" + positionName.get() + '\'' +
                ", levelOrder=" + levelOrder.get() +
                '}';
    }
}
