package com.example.companycore.controller.attendance;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.fxml.Initializable;
import com.example.companycore.model.dto.AttendanceDto;
import com.example.companycore.service.ApiClient;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

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
    private List<AttendanceDto> attendanceRecords = new ArrayList<>();
    private ApiClient apiClient = ApiClient.getInstance();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPaginationHandlers();
        loadAttendanceRecordsFromServer();
        loadPageData(currentPage);
    }
    
    private void loadAttendanceRecordsFromServer() {
        try {
            // TODO: ApiClient에 해당 메서드 구현 필요
            // attendanceRecords = apiClient.getAttendanceRecordsByUser(1L);
            System.out.println("출근 기록 데이터 로드 (API 구현 필요)");
        } catch (Exception e) {
            System.err.println("서버에서 출근 기록 데이터를 가져오는 중 오류 발생: " + e.getMessage());
        }
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
        
        // 페이지네이션 로직
        int pageSize = 10;
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, attendanceRecords.size());
        
        if (startIndex < attendanceRecords.size()) {
            List<AttendanceDto> pageData = attendanceRecords.subList(startIndex, endIndex);
            for (AttendanceDto record : pageData) {
                addAttendanceRow(record);
            }
        }
        
        // 빈 행으로 채우기
        int remainingRows = 10 - (endIndex - startIndex);
        for (int i = 0; i < remainingRows; i++) {
            addTableRow("", "", "", "", "#f8f9fa", "#6c757d");
        }
    }
    
    private void addAttendanceRow(AttendanceDto record) {
        String date = record.getWorkDate() != null ? record.getWorkDate().toString() : "";
        String clockIn = record.getCheckIn() != null ? record.getCheckIn().toString() : "";
        String clockOut = record.getCheckOut() != null ? record.getCheckOut().toString() : "";
        String status = record.getStatus() != null ? record.getStatus().toString() : "";
        
        String buttonColor = "#28a745"; // 기본 녹색
        String textColor = "white";
        
        if ("LATE".equals(status)) {
            buttonColor = "#dc3545"; // 빨간색
        } else if ("ABSENT".equals(status)) {
            buttonColor = "#6c757d"; // 회색
        }
        
        addTableRow(date, clockIn, clockOut, status, buttonColor, textColor);
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