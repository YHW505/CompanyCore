package com.example.companycore.controller.attendance;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceRecordController implements Initializable {
    
    @FXML
    private VBox tableData;
    
    @FXML
    private Button page1Button;
    
    @FXML
    private Button page2Button;
    
    @FXML
    private Button page3Button;
    
    @FXML
    private Button page4Button;
    
    @FXML
    private Button page40Button;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    private int currentPage = 1;
    private int totalPages = 40;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPaginationHandlers();
        loadPageData(currentPage);
    }
    
    private void setupPaginationHandlers() {
        // 페이지 버튼 클릭 이벤트
        page1Button.setOnAction(e -> loadPage(1));
        page2Button.setOnAction(e -> loadPage(2));
        page3Button.setOnAction(e -> loadPage(3));
        page4Button.setOnAction(e -> loadPage(4));
        page40Button.setOnAction(e -> loadPage(40));
        
        // 이전/다음 버튼 클릭 이벤트
        prevButton.setOnAction(e -> {
            if (currentPage > 1) {
                loadPage(currentPage - 1);
            }
        });
        
        nextButton.setOnAction(e -> {
            if (currentPage < totalPages) {
                loadPage(currentPage + 1);
            }
        });
    }
    
    private void loadPage(int page) {
        currentPage = page;
        loadPageData(page);
        updatePaginationUI();
    }
    
    private void loadPageData(int page) {
        // 테이블 데이터를 클리어
        tableData.getChildren().clear();
        
        if (page == 1) {
            // 첫 번째 페이지 데이터 (기존 데이터)
            addTableRow("2025.08.01", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.02", "08:30", "18:00", "지각", "#f8d7da", "#721c24");
            addTableRow("2025.08.03", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.04", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.05", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.06", "08:15", "18:00", "지각", "#f8d7da", "#721c24");
            addTableRow("2025.08.07", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.08", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.09", "08:00", "18:00", "출근", "#d4edda", "#155724");
            addTableRow("2025.08.10", "08:00", "18:00", "출근", "#d4edda", "#155724");
        } else {
            // 다른 페이지들은 빈 데이터 (나중에 데이터베이스에서 가져올 예정)
            for (int i = 0; i < 10; i++) {
                addTableRow("", "", "", "", "#f8f9fa", "#6c757d");
            }
        }
    }
    
    private void addTableRow(String date, String clockIn, String clockOut, String status, String buttonColor, String textColor) {
        HBox row = new HBox();
        row.setStyle("-fx-padding: 15 25; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");
        
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-alignment: CENTER; -fx-min-width: 280; -fx-pref-width: 280;");
        
        Label clockInLabel = new Label(clockIn);
        clockInLabel.setStyle("-fx-alignment: CENTER; -fx-min-width: 280; -fx-pref-width: 280;");
        
        Label clockOutLabel = new Label(clockOut);
        clockOutLabel.setStyle("-fx-alignment: CENTER; -fx-min-width: 280; -fx-pref-width: 280;");
        
        Button statusButton = new Button(status);
        statusButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-padding: 6 15; -fx-alignment: CENTER; -fx-cursor: hand; -fx-min-width: 160; -fx-pref-width: 160;", buttonColor, textColor));
        
        row.getChildren().addAll(dateLabel, clockInLabel, clockOutLabel, statusButton);
        tableData.getChildren().add(row);
    }
    
    private void updatePaginationUI() {
        // 모든 페이지 버튼 스타일 초기화
        page1Button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        page2Button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        page3Button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        page4Button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        page40Button.setStyle("-fx-background-color: transparent; -fx-text-fill: #6c757d; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
        
        // 현재 페이지 버튼 하이라이트
        switch (currentPage) {
            case 1:
                page1Button.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
                break;
            case 2:
                page2Button.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
                break;
            case 3:
                page3Button.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
                break;
            case 4:
                page4Button.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
                break;
            case 40:
                page40Button.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-cursor: hand;");
                break;
        }
    }
} 