package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.time.DayOfWeek;

public class CalendarController implements Initializable {
    
    @FXML
    private Button prevMonthButton;
    
    @FXML
    private Button nextMonthButton;
    
    @FXML
    private TableView<ScheduleRow> scheduleTable;
    
    @FXML
    private TableColumn<ScheduleRow, String> timeColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> mondayColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> tuesdayColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> wednesdayColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> thursdayColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> fridayColumn;
    
    @FXML
    private TableColumn<ScheduleRow, String> saturdayColumn;
    
    @FXML
    private Label calendarTitle;
    
    @FXML
    private GridPane calendarGrid;
    
    @FXML
    private Button importSchedule1Button;
    
    @FXML
    private Button importSchedule2Button;
    
    private LocalDate currentDate = LocalDate.now();
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 테이블 컬럼 설정
        setupTableColumns();
        
        // 샘플 데이터 추가
        loadSampleScheduleData();
        
        // 달력 초기화
        updateMonthDisplay();
    }
    
    private void setupTableColumns() {
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        
        // 컬럼 너비 설정 - 시간 컬럼은 고정, 나머지는 균등 분배
        timeColumn.setPrefWidth(80);
        timeColumn.setMinWidth(80);
        timeColumn.setMaxWidth(80);
        
        // 요일 컬럼들은 균등하게 분배
        mondayColumn.setPrefWidth(120);
        tuesdayColumn.setPrefWidth(120);
        wednesdayColumn.setPrefWidth(120);
        thursdayColumn.setPrefWidth(120);
        fridayColumn.setPrefWidth(120);
        saturdayColumn.setPrefWidth(120);
        
        // 컬럼들이 사용 가능한 공간을 균등하게 나누도록 설정
        mondayColumn.setMinWidth(100);
        tuesdayColumn.setMinWidth(100);
        wednesdayColumn.setMinWidth(100);
        thursdayColumn.setMinWidth(100);
        fridayColumn.setMinWidth(100);
        saturdayColumn.setMinWidth(100);
        
        // 테이블 행 높이 고정
        scheduleTable.setFixedCellSize(40);
        
        // 테이블 높이를 데이터에 맞게 설정 (헤더 높이 + 데이터 행 높이)
        scheduleTable.setPrefHeight(40 + (9 * 40)); // 헤더(40px) + 데이터 행(9개 × 40px)
    }
    
    private void loadSampleScheduleData() {
        ObservableList<ScheduleRow> data = FXCollections.observableArrayList();
        
        data.add(new ScheduleRow("09:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("10:00", "Webinar", "Webinar", "Webinar", "Webinar", "", ""));
        data.add(new ScheduleRow("11:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("12:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("13:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("14:00", "Figma Workshop", "Figma Workshop", "Figma Workshop", "Figma Workshop", "", ""));
        data.add(new ScheduleRow("15:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("16:00", "", "", "", "", "", ""));
        data.add(new ScheduleRow("17:00", "", "", "", "", "", ""));
        
        scheduleTable.setItems(data);
    }
    
    @FXML
    public void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        updateMonthDisplay();
    }
    
    @FXML
    public void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateMonthDisplay();
    }
    
    @FXML
    public void handleScheduleClick() {
        // TODO: 일정 클릭 시 상세 정보 표시
        showAlert("정보", "일정을 클릭했습니다.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void handleImportSchedule1() {
        // TODO: 7/25일 회의록에서 일정 가져오기
        showAlert("성공", "7/25일 회의록에서 일정을 가져왔습니다.", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void handleImportSchedule2() {
        // TODO: 7월 30일 회의록에서 일정 가져오기
        showAlert("성공", "7월 30일 회의록에서 일정을 가져왔습니다.", Alert.AlertType.INFORMATION);
    }
    
    private void updateMonthDisplay() {
        // 달력 제목 업데이트
        calendarTitle.setText(currentDate.format(monthFormatter));
        
        // 기존 날짜 라벨들 제거
        calendarGrid.getChildren().removeIf(node -> 
            node instanceof Label && !((Label) node).getText().matches("Mo|Tu|We|Th|Fr|Sa|Su"));
        
        // 현재 월의 첫 번째 날과 마지막 날 계산
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        
        // 첫 번째 날의 요일 (1=월요일, 7=일요일)
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        
        // 달력 그리드에 날짜 추가
        int dayOfMonth = 1;
        int row = 1;
        int col = firstDayOfWeek - 1; // 0부터 시작하도록 조정
        
        while (dayOfMonth <= lastDayOfMonth.getDayOfMonth()) {
            if (col >= 7) {
                col = 0;
                row++;
            }
            
            Label dayLabel = new Label(String.valueOf(dayOfMonth));
            dayLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-alignment: center;");
            
            // 오늘 날짜 강조
            if (dayOfMonth == LocalDate.now().getDayOfMonth() && 
                currentDate.getMonth() == LocalDate.now().getMonth() && 
                currentDate.getYear() == LocalDate.now().getYear()) {
                dayLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5; -fx-alignment: center; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10;");
            }
            
            calendarGrid.add(dayLabel, col, row);
            
            dayOfMonth++;
            col++;
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // 일정 행을 위한 내부 클래스
    public static class ScheduleRow {
        private final javafx.beans.property.SimpleStringProperty time;
        private final javafx.beans.property.SimpleStringProperty monday;
        private final javafx.beans.property.SimpleStringProperty tuesday;
        private final javafx.beans.property.SimpleStringProperty wednesday;
        private final javafx.beans.property.SimpleStringProperty thursday;
        private final javafx.beans.property.SimpleStringProperty friday;
        private final javafx.beans.property.SimpleStringProperty saturday;
        
        public ScheduleRow(String time, String monday, String tuesday, String wednesday, 
                         String thursday, String friday, String saturday) {
            this.time = new javafx.beans.property.SimpleStringProperty(time);
            this.monday = new javafx.beans.property.SimpleStringProperty(monday);
            this.tuesday = new javafx.beans.property.SimpleStringProperty(tuesday);
            this.wednesday = new javafx.beans.property.SimpleStringProperty(wednesday);
            this.thursday = new javafx.beans.property.SimpleStringProperty(thursday);
            this.friday = new javafx.beans.property.SimpleStringProperty(friday);
            this.saturday = new javafx.beans.property.SimpleStringProperty(saturday);
        }
        
        public javafx.beans.property.StringProperty timeProperty() { return time; }
        public javafx.beans.property.StringProperty mondayProperty() { return monday; }
        public javafx.beans.property.StringProperty tuesdayProperty() { return tuesday; }
        public javafx.beans.property.StringProperty wednesdayProperty() { return wednesday; }
        public javafx.beans.property.StringProperty thursdayProperty() { return thursday; }
        public javafx.beans.property.StringProperty fridayProperty() { return friday; }
        public javafx.beans.property.StringProperty saturdayProperty() { return saturday; }
    }
} 