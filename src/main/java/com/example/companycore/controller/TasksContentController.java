package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TasksContentController {

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    public void initialize() {
        // 우선순위 ComboBox 아이템 설정
        ObservableList<String> priorities = FXCollections.observableArrayList(
            "높음", "보통", "낮음"
        );
        priorityComboBox.setItems(priorities);
    }
} 