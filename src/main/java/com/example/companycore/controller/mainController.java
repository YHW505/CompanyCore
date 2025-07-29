package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;

public class MainController {
    
    @FXML
    private HBox toggleSwitch;
    
    @FXML
    private HBox toggleButton;
    
    @FXML
    private Label toggleText;
    
    @FXML
    private ImageView logoImage;
    
    @FXML
    private ImageView logoText;
    

    
    @FXML
    private Label userStatusLabel;
    
    @FXML
    private VBox announcementBox;
    
    @FXML
    private VBox taskBox;
    
    @FXML
    private VBox approvalBox;
    
    @FXML
    private GridPane calendarGrid;
    
    @FXML
    public void initialize() {
        // 초기화 작업
        setupWorkStatusToggle();
        setupNavigationHandlers();
    }
    
    private boolean isWorking = true;
    
    private void setupWorkStatusToggle() {
        if (toggleSwitch != null) {
            // 초기 상태 설정
            updateToggleState(true);
        }
    }
    
    @FXML
    public void handleToggleClick() {
        isWorking = !isWorking;
        updateToggleState(isWorking);
        System.out.println("근무 상태 토글: " + (isWorking ? "근무중" : "퇴근"));
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
    

    
    private void setupNavigationHandlers() {
        // 네비게이션 메뉴 클릭 이벤트 처리
        // 실제 구현에서는 각 메뉴에 대한 이벤트 핸들러를 추가
    }
    
    @FXML
    public void handleHomeClick() {
        System.out.println("홈 메뉴 클릭");
        // 홈 화면으로 이동
    }
    
    @FXML
    public void handleAttendanceClick() {
        System.out.println("근태관리 메뉴 클릭");
        // 근태관리 화면으로 이동
    }
    
    @FXML
    public void handleMessengerClick() {
        System.out.println("메신저 메뉴 클릭");
        // 메신저 화면으로 이동
    }
    
    @FXML
    public void handleTasksClick() {
        System.out.println("업무 메뉴 클릭");
        // 업무 화면으로 이동
    }
    
    @FXML
    public void handleCalendarClick() {
        System.out.println("캘린더 메뉴 클릭");
        // 캘린더 화면으로 이동
    }
    
    @FXML
    public void handleProfileClick() {
        System.out.println("프로필 메뉴 클릭");
        // 프로필 화면으로 이동
    }
    
    @FXML
    public void handlePreviousMonth() {
        System.out.println("이전 달로 이동");
        // 캘린더 이전 달로 이동
    }
    
    @FXML
    public void handleNextMonth() {
        System.out.println("다음 달로 이동");
        // 캘린더 다음 달로 이동
    }
    
    // 공지사항 관련 메서드
    public void addAnnouncement(String title, String author, String date) {
        if (announcementBox != null) {
            HBox announcementItem = new HBox(10);
            announcementItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label icon = new Label("👤");
            icon.setFont(Font.font(12));
            
            VBox content = new VBox(2);
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-weight: bold;");
            Label infoLabel = new Label(author + " on " + date);
            infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            
            content.getChildren().addAll(titleLabel, infoLabel);
            announcementItem.getChildren().addAll(icon, content);
            
            announcementBox.getChildren().add(announcementItem);
        }
    }
    
    // 업무 관련 메서드
    public void addTask(String category, String icon) {
        if (taskBox != null) {
            HBox taskItem = new HBox(10);
            taskItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label iconLabel = new Label(icon);
            iconLabel.setFont(Font.font(12));
            
            Region progressBar = new Region();
            progressBar.setStyle("-fx-background-color: #bdc3c7; -fx-pref-width: 200; -fx-pref-height: 8; -fx-background-radius: 4;");
            
            taskItem.getChildren().addAll(iconLabel, progressBar);
            taskBox.getChildren().add(taskItem);
        }
    }
    
    // 결재 서류 관련 메서드
    public void addApprovalDocument(String document) {
        if (approvalBox != null) {
            Label docLabel = new Label("• " + document);
            docLabel.setPadding(new Insets(5, 0, 5, 0));
            approvalBox.getChildren().add(docLabel);
        }
    }
}
