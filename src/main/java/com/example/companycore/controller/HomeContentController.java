package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HomeContentController {
    
    @FXML
    private HBox toggleSwitch;
    
    @FXML
    private HBox toggleButton;
    
    @FXML
    private Label toggleText;
    
    @FXML
    private Label userStatusLabel;
    
    private boolean isWorking = false;
    
    @FXML
    public void initialize() {
        updateToggleState(false);
    }
    
    @FXML
    public void handleToggleClick() {
        isWorking = !isWorking;
        updateToggleState(isWorking);
    }
    
    private void updateToggleState(boolean working) {
        if (toggleSwitch != null && toggleText != null && userStatusLabel != null) {
            if (working) {
                toggleSwitch.getStyleClass().add("active");
                toggleText.setText("근무중");
                userStatusLabel.setText("근무중");
                userStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                toggleSwitch.getStyleClass().remove("active");
                toggleText.setText("퇴근");
                userStatusLabel.setText("퇴근");
                userStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
    }
} 