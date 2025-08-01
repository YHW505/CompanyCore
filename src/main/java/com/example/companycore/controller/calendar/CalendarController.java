package com.example.companycore.controller.calendar;

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

/**
 * 캘린더 화면을 관리하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 월별 캘린더 표시 및 네비게이션
 * - 일정 테이블 표시 및 관리
 * - 일정 데이터 로드 및 표시
 * - 일정 가져오기 기능
 * - 날짜 선택 및 일정 클릭 처리
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class CalendarController implements Initializable {
    
    // ==================== FXML UI 컴포넌트 ====================
    
    /** 이전 달 버튼 */
    @FXML private Button prevMonthButton;
    
    /** 다음 달 버튼 */
    @FXML private Button nextMonthButton;
    
    /** 일정 테이블 */
    @FXML private TableView<ScheduleRow> scheduleTable;
    
    /** 테이블 컬럼들 */
    @FXML private TableColumn<ScheduleRow, String> timeColumn;      // 시간 컬럼
    @FXML private TableColumn<ScheduleRow, String> mondayColumn;    // 월요일 컬럼
    @FXML private TableColumn<ScheduleRow, String> tuesdayColumn;   // 화요일 컬럼
    @FXML private TableColumn<ScheduleRow, String> wednesdayColumn; // 수요일 컬럼
    @FXML private TableColumn<ScheduleRow, String> thursdayColumn;  // 목요일 컬럼
    @FXML private TableColumn<ScheduleRow, String> fridayColumn;    // 금요일 컬럼
    @FXML private TableColumn<ScheduleRow, String> saturdayColumn;  // 토요일 컬럼
    
    /** 캘린더 제목 (년월 표시) */
    @FXML private Label calendarTitle;
    
    /** 캘린더 그리드 */
    @FXML private GridPane calendarGrid;
    
    /** 일정 가져오기 버튼들 */
    @FXML private Button importSchedule1Button;
    @FXML private Button importSchedule2Button;

    // ==================== 상태 관리 ====================
    
    /** 현재 선택된 날짜 */
    private LocalDate currentDate = LocalDate.now();
    
    /** 월 표시 포맷터 */
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 테이블 설정, 샘플 데이터 로드, 달력 초기화를 수행
     * 
     * @param location FXML 파일의 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 테이블 컬럼 설정
        setupTableColumns();
        
        // 샘플 데이터 추가
        loadSampleScheduleData();
        
        // 달력 초기화
        updateMonthDisplay();
    }
    
    // ==================== 테이블 설정 메서드 ====================
    
    /**
     * 일정 테이블의 컬럼들을 설정
     * 컬럼 바인딩, 너비 설정, 정렬 등을 수행
     */
    private void setupTableColumns() {
        // 컬럼 바인딩 설정
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
        
        // 모든 컬럼 중앙 정렬
        timeColumn.setStyle("-fx-alignment: CENTER;");
        mondayColumn.setStyle("-fx-alignment: CENTER;");
        tuesdayColumn.setStyle("-fx-alignment: CENTER;");
        wednesdayColumn.setStyle("-fx-alignment: CENTER;");
        thursdayColumn.setStyle("-fx-alignment: CENTER;");
        fridayColumn.setStyle("-fx-alignment: CENTER;");
        saturdayColumn.setStyle("-fx-alignment: CENTER;");
    }
    
    // ==================== 데이터 로드 메서드 ====================
    
    /**
     * 샘플 일정 데이터를 로드하여 테이블에 표시
     * 실제 구현에서는 데이터베이스나 API에서 데이터를 가져옴
     */
    private void loadSampleScheduleData() {
        ObservableList<ScheduleRow> scheduleData = FXCollections.observableArrayList();
        
        // 샘플 일정 데이터 추가
        scheduleData.add(new ScheduleRow("09:00", "회의", "개발", "회의", "개발", "회의", ""));
        scheduleData.add(new ScheduleRow("10:00", "개발", "회의", "개발", "회의", "개발", ""));
        scheduleData.add(new ScheduleRow("11:00", "회의", "개발", "회의", "개발", "회의", ""));
        scheduleData.add(new ScheduleRow("12:00", "점심", "점심", "점심", "점심", "점심", ""));
        scheduleData.add(new ScheduleRow("13:00", "개발", "회의", "개발", "회의", "개발", ""));
        scheduleData.add(new ScheduleRow("14:00", "회의", "개발", "회의", "개발", "회의", ""));
        scheduleData.add(new ScheduleRow("15:00", "개발", "회의", "개발", "회의", "개발", ""));
        scheduleData.add(new ScheduleRow("16:00", "회의", "개발", "회의", "개발", "회의", ""));
        scheduleData.add(new ScheduleRow("17:00", "개발", "회의", "개발", "회의", "개발", ""));
        scheduleData.add(new ScheduleRow("18:00", "퇴근", "퇴근", "퇴근", "퇴근", "퇴근", ""));
        
        scheduleTable.setItems(scheduleData);
    }

    // ==================== 이벤트 핸들러 메서드 ====================
    
    /**
     * 이전 달 버튼 클릭 시 호출되는 메서드
     * 현재 날짜를 한 달 이전으로 변경하고 달력 표시를 업데이트
     */
    @FXML
    public void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        updateMonthDisplay();
    }
    
    /**
     * 다음 달 버튼 클릭 시 호출되는 메서드
     * 현재 날짜를 한 달 다음으로 변경하고 달력 표시를 업데이트
     */
    @FXML
    public void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateMonthDisplay();
    }
    
    /**
     * 일정 클릭 시 호출되는 메서드
     * 선택된 일정에 대한 상세 정보를 표시
     */
    @FXML
    public void handleScheduleClick() {
        showAlert("일정", "선택된 일정의 상세 정보를 표시합니다.", Alert.AlertType.INFORMATION);
    }
    
    /**
     * 일정 가져오기 버튼 1 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleImportSchedule1() {
        showAlert("일정 가져오기", "일정 가져오기 기능 1을 실행합니다.", Alert.AlertType.INFORMATION);
    }
    
    /**
     * 일정 가져오기 버튼 2 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleImportSchedule2() {
        showAlert("일정 가져오기", "일정 가져오기 기능 2를 실행합니다.", Alert.AlertType.INFORMATION);
    }

    // ==================== 달력 표시 메서드 ====================
    
    /**
     * 현재 선택된 월의 달력을 표시
     * 달력 제목 업데이트 및 그리드에 날짜 표시
     */
    private void updateMonthDisplay() {
        // 달력 제목 업데이트
        calendarTitle.setText(currentDate.format(monthFormatter));
        
        // 달력 그리드 초기화
        calendarGrid.getChildren().clear();
        
        // 현재 월의 첫 번째 날과 마지막 날 계산
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        
        // 첫 번째 날의 요일 (월요일이 1, 일요일이 7)
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int firstDayValue = firstDayOfWeek.getValue();
        
        // 달력 그리드에 날짜 표시
        int row = 0;
        int col = firstDayValue - 1; // 월요일부터 시작하므로 -1
        
        // 이전 달의 날짜들 표시 (빈칸 채우기)
        LocalDate previousMonth = firstDayOfMonth.minusDays(firstDayValue);
        for (int i = 0; i < firstDayValue - 1; i++) {
            LocalDate date = previousMonth.plusDays(i + 1);
            addDateToGrid(date, row, i, false);
        }
        
        // 현재 달의 날짜들 표시
        for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            addDateToGrid(date, row, col, true);
            col++;
            if (col > 6) { // 일요일이면 다음 주로
                col = 0;
                row++;
            }
        }
        
        // 다음 달의 날짜들 표시 (빈칸 채우기)
        int remainingCells = 42 - (firstDayValue - 1) - yearMonth.lengthOfMonth(); // 6주 * 7일 = 42
        LocalDate nextMonth = lastDayOfMonth.plusDays(1);
        for (int i = 0; i < remainingCells; i++) {
            LocalDate date = nextMonth.plusDays(i);
            addDateToGrid(date, row, col, false);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * 달력 그리드에 날짜를 추가
     * 
     * @param date 추가할 날짜
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @param isCurrentMonth 현재 달의 날짜인지 여부
     */
    private void addDateToGrid(LocalDate date, int row, int col, boolean isCurrentMonth) {
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-alignment: CENTER; -fx-padding: 5;");
        
        if (!isCurrentMonth) {
            dateLabel.setStyle("-fx-text-fill: gray; -fx-alignment: CENTER; -fx-padding: 5;");
        }
        
        calendarGrid.add(dateLabel, col, row);
    }

    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 알림 다이얼로그를 표시
     * 
     * @param title 알림 제목
     * @param content 알림 내용
     * @param alertType 알림 타입
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ==================== 내부 클래스 ====================
    
    /**
     * 일정 테이블의 행 데이터를 표현하는 내부 클래스
     * JavaFX Property를 사용하여 데이터 바인딩을 지원
     */
    public static class ScheduleRow {
        private final javafx.beans.property.SimpleStringProperty time;
        private final javafx.beans.property.SimpleStringProperty monday;
        private final javafx.beans.property.SimpleStringProperty tuesday;
        private final javafx.beans.property.SimpleStringProperty wednesday;
        private final javafx.beans.property.SimpleStringProperty thursday;
        private final javafx.beans.property.SimpleStringProperty friday;
        private final javafx.beans.property.SimpleStringProperty saturday;

        /**
         * ScheduleRow 생성자
         * 
         * @param time 시간
         * @param monday 월요일 일정
         * @param tuesday 화요일 일정
         * @param wednesday 수요일 일정
         * @param thursday 목요일 일정
         * @param friday 금요일 일정
         * @param saturday 토요일 일정
         */
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

        // ==================== Property Getter 메서드 ====================
        
        public javafx.beans.property.StringProperty timeProperty() { return time; }
        public javafx.beans.property.StringProperty mondayProperty() { return monday; }
        public javafx.beans.property.StringProperty tuesdayProperty() { return tuesday; }
        public javafx.beans.property.StringProperty wednesdayProperty() { return wednesday; }
        public javafx.beans.property.StringProperty thursdayProperty() { return thursday; }
        public javafx.beans.property.StringProperty fridayProperty() { return friday; }
        public javafx.beans.property.StringProperty saturdayProperty() { return saturday; }
    }
} 