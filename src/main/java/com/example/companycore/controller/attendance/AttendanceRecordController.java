package com.example.companycore.controller.attendance;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.companycore.model.dto.AttendanceDto;
import com.example.companycore.model.entity.Attendance;
import com.example.companycore.service.ApiClient;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class AttendanceRecordController implements Initializable {
    
    @FXML
    private VBox tableData;
    
    @FXML
    private Pagination pagination;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private int visibleRowCount = 10;
    private List<AttendanceDto> attendanceRecords = new ArrayList<>();
    private ObservableList<AttendanceDto> viewData = FXCollections.observableArrayList();
    private ApiClient apiClient = ApiClient.getInstance();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== AttendanceRecordController 초기화 시작 ===");
        setupPagination();
        loadAttendanceRecordsFromServer();
        updatePagination();
        System.out.println("=== AttendanceRecordController 초기화 완료 ===");
    }
    
    private void loadAttendanceRecordsFromServer() {
        try {
            // 실제 API 호출
            List<Attendance> attendanceList = apiClient.getUserAttendance(1L);
            
            // Attendance 엔티티를 AttendanceDto로 변환
            attendanceRecords.clear();
            for (Attendance attendance : attendanceList) {
                AttendanceDto dto = convertToDto(attendance);
                attendanceRecords.add(dto);
            }
            
            viewData.clear();
            viewData.addAll(attendanceRecords);
            
            System.out.println("출근 기록 데이터 로드 완료: " + attendanceRecords.size() + "개");
        } catch (Exception e) {
            System.err.println("서버에서 출근 기록 데이터를 가져오는 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private AttendanceDto convertToDto(Attendance attendance) {
        return new AttendanceDto(
            attendance.getAttendanceId(),
            attendance.getUserId(),
            attendance.getCheckIn(),
            attendance.getCheckOut(),
            attendance.getWorkHours(),
            attendance.getWorkDate(),
            attendance.getStatus()
        );
    }
    
    /**
     * 페이지네이션 UI를 구성하고 설정
     */
    private void setupPagination() {
        System.out.println("=== 페이지네이션 설정 시작 ===");
        pagination.setVisible(true);
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
        System.out.println("페이지네이션 설정 완료 - visible: " + pagination.isVisible() + ", pageCount: " + pagination.getPageCount());
        updatePagination();
    }
    
    /**
     * 페이지네이션 업데이트
     */
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(Math.max(1, totalPages));
        
        // 페이지네이션을 명시적으로 보이도록 설정
        pagination.setVisible(true);
        
        // 현재 페이지가 총 페이지 수를 초과하면 마지막 페이지로 설정
        if (pagination.getCurrentPageIndex() >= totalPages) {
            pagination.setCurrentPageIndex(Math.max(0, totalPages - 1));
        }
        
        // 첫 페이지 데이터 로드
        if (viewData.size() > 0) {
            int startIndex = 0;
            int endIndex = Math.min(visibleRowCount, viewData.size());
            List<AttendanceDto> pageData = viewData.subList(startIndex, endIndex);
            loadPageData(pageData);
            System.out.println("페이지네이션 업데이트: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        } else {
            tableData.getChildren().clear();
            System.out.println("페이지네이션 업데이트: 빈 목록");
        }
    }
    
    /**
     * 페이지 생성
     */
    private Region createPage(int pageIndex) {
        // 페이지 인덱스가 유효한지 확인
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        if (pageIndex >= totalPages) {
            return new Region();
        }
        
        // 현재 페이지의 데이터 계산
        int startIndex = pageIndex * visibleRowCount;
        int endIndex = Math.min(startIndex + visibleRowCount, viewData.size());
        
        // 현재 페이지의 데이터만 테이블에 설정
        List<AttendanceDto> pageData = viewData.subList(startIndex, endIndex);
        loadPageData(pageData);
        
        System.out.println("페이지 " + (pageIndex + 1) + " 로드: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        
        return new Region();
    }
    
    private void loadPageData(List<AttendanceDto> pageData) {
        // 기존 데이터 클리어
        tableData.getChildren().clear();
        
        // 현재 페이지의 데이터만 표시
        for (AttendanceDto record : pageData) {
            addAttendanceRow(record);
        }
        
        // 빈 행으로 채우기 (10개 행 고정)
        int remainingRows = visibleRowCount - pageData.size();
        for (int i = 0; i < remainingRows; i++) {
            addTableRow("", "", "", "", "#f8f9fa", "#6c757d");
        }
    }
    
    private void addAttendanceRow(AttendanceDto record) {
        // 날짜 포맷팅: 2023-03-01 → 2023년 3월 1일
        String date = formatDate(record.getWorkDate());
        
        // 시간 포맷팅: 08:30:00 → 오전 8시 30분
        String clockIn = formatTime(record.getCheckIn());
        String clockOut = formatTime(record.getCheckOut());
        
        // 상태 표시 개선
        String status = formatStatus(record.getStatus());
        String buttonColor = getStatusColor(record.getStatus());
        String textColor = "white";
        
        addTableRow(date, clockIn, clockOut, status, buttonColor, textColor);
    }
    
    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "";
        return String.format("%d년 %d월 %d일", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
    
    private String formatTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        
        String ampm = hour < 12 ? "오전" : "오후";
        int displayHour = hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour);
        
        return String.format("%s %d시 %02d분", ampm, displayHour, minute);
    }
    
    private String formatStatus(com.example.companycore.model.entity.Enum.AttendanceStatus status) {
        if (status == null) return "";
        
        switch (status) {
            case PRESENT: return "정상";
            case LATE: return "지각";
            case ABSENT: return "결근";
            case LEAVE: return "휴가";
            default: return status.toString();
        }
    }
    
    private String getStatusColor(com.example.companycore.model.entity.Enum.AttendanceStatus status) {
        if (status == null) return "#6c757d";
        
        switch (status) {
            case PRESENT: return "#28a745"; // 녹색
            case LATE: return "#ffc107"; // 노란색
            case ABSENT: return "#dc3545"; // 빨간색
            case LEAVE: return "#17a2b8"; // 파란색
            default: return "#6c757d"; // 회색
        }
    }
    
    private void addTableRow(String date, String clockIn, String clockOut, String status, String buttonColor, String textColor) {
        HBox row = new HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER); // 세로 중앙 정렬 추가
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
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
    

} 