package com.example.companycore.controller.calendar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MeetingApiClient;

/**
 * 캘린더 화면을 관리하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 업무를 표시할 달력 (월간)
 * - 당일로부터 일정기간내 다가오는 일정
 * - 최신 회의록 표시
 * - 회의록에서 해당 업무 캘린더 내용 가져오기
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
    
    /** 캘린더 제목 (년월 표시) */
    @FXML private Label calendarTitle;
    
    /** 캘린더 그리드 */
    @FXML private GridPane calendarGrid;
    
    /** 다가오는 일정 컨테이너 */
    @FXML private VBox upcomingSchedulesContainer;
    
    /** 회의록 컨테이너 */
    @FXML private VBox meetingNotesContainer;
    
    /** 다가오는 일정 새로고침 */
    @FXML private Button refreshUpcomingButton;
    
    /** 회의록 새로고침 */
    @FXML private Button refreshMeetingsButton;
    
    /** 선택된 날짜 정보 */
    @FXML private VBox selectedDateInfo;
    @FXML private Label selectedDateLabel;
    @FXML private Label selectedDateScheduleCount;

    // ==================== 상태 관리 ====================
    
    /** 현재 선택된 날짜 */
    private LocalDate currentDate = LocalDate.now();
    
    /** 현재 선택된 날짜 (클릭된 날짜) */
    private LocalDate selectedDate = null;
    
    /** 월 표시 포맷터 */
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    
    /** 일정 데이터 */
    private List<Schedule> schedules = new ArrayList<>();
    
    /** 회의록 데이터 */
    private List<MeetingNote> meetingNotes = new ArrayList<>();
    
    /** API 클라이언트 */
    private final ApiClient apiClient = ApiClient.getInstance();

    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 달력 초기화, 일정 로드를 수행
     * 
     * @param location FXML 파일의 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 달력 초기화
        updateMonthDisplay();
        
        // 회의록 로드
        loadMeetingNotes();
        
        // 다가오는 일정 및 회의록 로드
        loadUpcomingSchedules();
    }
    
    // ==================== 새로고침 기능 ====================
    
    /**
     * 다가오는 일정 새로고침
     */
    @FXML
    public void handleRefreshUpcoming() {
        loadUpcomingSchedules();
        showAlert("새로고침", "다가오는 일정이 새로고침되었습니다.", Alert.AlertType.INFORMATION);
    }
    
    /**
     * 회의록 새로고침
     */
    @FXML
    public void handleRefreshMeetings() {
        loadMeetingNotes();
        showAlert("새로고침", "회의록이 새로고침되었습니다.", Alert.AlertType.INFORMATION);
    }
    
    // ==================== 달력 관련 메서드 ====================
    
    /**
     * 이전 달로 이동
     */
    @FXML
    public void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        updateMonthDisplay();
    }
    
    /**
     * 다음 달로 이동
     */
    @FXML
    public void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateMonthDisplay();
    }
    
    /**
     * 월 표시를 업데이트
     * 현재 월의 달력을 그리드에 표시하고 일정을 표시
     */
    private void updateMonthDisplay() {
        // 제목 업데이트
        calendarTitle.setText(currentDate.format(monthFormatter));
        
        // 기존 날짜들 제거
        calendarGrid.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        
        // 현재 월의 첫 번째 날과 마지막 날 계산
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        
        // 첫 번째 날의 요일 (월요일이 1, 일요일이 7)
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        if (firstDayOfWeek == 7) firstDayOfWeek = 0; // 일요일을 0으로 변환
        
        // 달력 그리드에 날짜 추가
        int dayOfMonth = 1;
        int row = 1;
        
        for (int week = 0; week < 6; week++) {
            for (int col = 0; col < 7; col++) {
                if (week == 0 && col < firstDayOfWeek) {
                    // 첫 주의 빈 칸들
                    continue;
                }
                
                if (dayOfMonth > lastDayOfMonth.getDayOfMonth()) {
                    // 월의 마지막 날을 넘어서면 종료
                    break;
                }
                
                LocalDate date = yearMonth.atDay(dayOfMonth);
                addDateToGrid(date, row, col, true);
                dayOfMonth++;
            }
            row++;
        }
    }
    
    /**
     * 그리드에 날짜를 추가 (일정 표시 포함)
     * 
     * @param date 추가할 날짜
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @param isCurrentMonth 현재 월의 날짜인지 여부
     */
    private void addDateToGrid(LocalDate date, int row, int col, boolean isCurrentMonth) {
        VBox dateContainer = new VBox(2);
        dateContainer.setStyle("-fx-padding: 5; -fx-alignment: top-center; -fx-min-height: 60;");
        
        // 날짜 라벨
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // 일정 표시 영역
        VBox scheduleContainer = new VBox(1);
        scheduleContainer.setStyle("-fx-padding: 2;");
        
        // 해당 날짜의 일정들 가져오기
        List<Schedule> daySchedules = schedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .limit(3) // 최대 3개만 표시
                .collect(Collectors.toList());
        
        for (Schedule schedule : daySchedules) {
            Label scheduleLabel = new Label(schedule.getTitle());
            scheduleLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: white; -fx-background-color: #3498db; -fx-padding: 1 3; -fx-background-radius: 2;");
            scheduleLabel.setMaxWidth(50);
            scheduleLabel.setWrapText(true);
            scheduleContainer.getChildren().add(scheduleLabel);
        }
        
        // 더 많은 일정이 있으면 표시
        long totalSchedules = schedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .count();
        
        if (totalSchedules > 3) {
            Label moreLabel = new Label("+" + (totalSchedules - 3) + "개");
            moreLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: #7f8c8d;");
            scheduleContainer.getChildren().add(moreLabel);
        }
        
        dateContainer.getChildren().addAll(dateLabel, scheduleContainer);
        
        // 배경 스타일 설정
        String backgroundStyle = "";
        if (date.equals(LocalDate.now())) {
            // 오늘 날짜 강조
            backgroundStyle = "-fx-background-color: #3498db; -fx-background-radius: 5;";
            dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        } else if (date.equals(selectedDate)) {
            // 선택된 날짜 강조
            backgroundStyle = "-fx-background-color: #e74c3c; -fx-background-radius: 5;";
            dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        } else if (isCurrentMonth) {
            // 현재 월의 날짜
            backgroundStyle = "-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ecf0f1; -fx-border-width: 1;";
        } else {
            // 다른 월의 날짜
            backgroundStyle = "-fx-background-color: #f8f9fa; -fx-background-radius: 5;";
            dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;");
        }
        
        dateContainer.setStyle(dateContainer.getStyle() + backgroundStyle);
        
        // 클릭 이벤트 추가
        dateContainer.setOnMouseClicked(event -> handleDateClick(date));
        
        // 그리드에 추가
        GridPane.setRowIndex(dateContainer, row);
        GridPane.setColumnIndex(dateContainer, col);
        calendarGrid.getChildren().add(dateContainer);
    }
    
    /**
     * 날짜 클릭 처리
     * 
     * @param date 클릭된 날짜
     */
    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        updateMonthDisplay(); // 선택된 날짜 스타일 업데이트를 위해 다시 그리기
        
        // 선택된 날짜 정보 표시
        selectedDateLabel.setText("선택된 날짜: " + date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        
        // 해당 날짜의 일정 개수 계산
        long scheduleCount = schedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .count();
        selectedDateScheduleCount.setText("일정: " + scheduleCount + "개");
        
        selectedDateInfo.setVisible(true);
        
        showAlert("날짜 선택", 
                 date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "을 선택했습니다.", 
                 Alert.AlertType.INFORMATION);
    }
    
    // ==================== 다가오는 일정 관련 메서드 ====================
    
    /**
     * 다가오는 일정을 로드하고 표시
     */
    private void loadUpcomingSchedules() {
        // 기존 일정들 제거
        upcomingSchedulesContainer.getChildren().clear();
        
        // 현재 날짜 이후의 일정들을 가져와서 표시
        LocalDate today = LocalDate.now();
        List<Schedule> upcomingSchedules = schedules.stream()
                .filter(schedule -> schedule.getDate().isAfter(today) || schedule.getDate().equals(today))
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .limit(5) // 최대 5개만 표시
                .collect(Collectors.toList());
        
        for (Schedule schedule : upcomingSchedules) {
            addUpcomingSchedule(schedule.getTitle(), 
                              schedule.getDate().toString(), 
                              schedule.getTime(), 
                              schedule.getLocation(),
                              schedule.getCategory());
        }
        
        // 샘플 데이터 (일정이 없을 때)
        if (upcomingSchedules.isEmpty()) {
            addUpcomingSchedule("팀 미팅", "2025-01-20", "14:00", "회의실 A", "회의");
            addUpcomingSchedule("프로젝트 마감", "2025-01-25", "18:00", "온라인", "프로젝트");
            addUpcomingSchedule("고객 미팅", "2025-01-28", "10:00", "회의실 B", "미팅");
        }
    }
    
    /**
     * 다가오는 일정 항목을 추가
     * 
     * @param title 일정 제목
     * @param date 일정 날짜
     * @param time 일정 시간
     * @param location 일정 장소
     * @param category 일정 카테고리
     */
    private void addUpcomingSchedule(String title, String date, String time, String location, String category) {
        VBox scheduleItem = new VBox(5);
        scheduleItem.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        
        Label dateTimeLabel = new Label(date + " " + time);
        dateTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        Label locationLabel = new Label(location);
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        Label categoryLabel = new Label(category);
        categoryLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #3498db; -fx-background-color: #ecf0f1; -fx-padding: 2 6; -fx-background-radius: 3;");
        
        scheduleItem.getChildren().addAll(titleLabel, dateTimeLabel, locationLabel, categoryLabel);
        
        // 클릭 이벤트 추가
        scheduleItem.setOnMouseClicked(event -> handleScheduleClick(title));
        
        upcomingSchedulesContainer.getChildren().add(scheduleItem);
    }
    
    // ==================== 회의록 관련 메서드 ====================
    
        /**
     * 회의록을 로드하고 표시
     */
    private void loadMeetingNotes() {
        // 기존 회의록들 제거
        meetingNotesContainer.getChildren().clear();

        try {
            // 실제 API에서 회의 데이터 가져오기
            List<MeetingApiClient.MeetingDto> apiMeetings = apiClient.getAllMeetings();
            
            if (apiMeetings != null && !apiMeetings.isEmpty()) {
                // API 데이터를 MeetingNote로 변환
                for (MeetingApiClient.MeetingDto apiMeeting : apiMeetings) {
                    MeetingNote meetingNote = convertToMeetingNote(apiMeeting);
                    meetingNotes.add(meetingNote);
                }
            }
        } catch (Exception e) {
            System.out.println("회의 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // 최신 회의록 2-3개 표시
        List<MeetingNote> recentMeetings = meetingNotes.stream()
                .sorted((m1, m2) -> m2.getDate().compareTo(m1.getDate())) // 최신순 정렬
                .limit(3) // 최대 3개만 표시
                .collect(Collectors.toList());

        for (MeetingNote meeting : recentMeetings) {
            addMeetingNote(meeting.getTitle(),
                          meeting.getDate().toString(),
                          meeting.getParticipants(),
                          meeting.getSummary(),
                          meeting.getId());
        }

        // 샘플 데이터 (회의록이 없을 때)
        if (recentMeetings.isEmpty()) {
            addMeetingNote("주간 팀 회의", "2025-01-15", "김팀장, 이사원, 박사원", "이번 주 진행상황 점검 및 다음 주 계획 수립", "meeting_001");
            addMeetingNote("프로젝트 킥오프", "2025-01-10", "전체 팀원", "새로운 프로젝트 시작 및 역할 분담", "meeting_002");
        }
    }
    
    /**
     * 회의록 항목을 추가 (일정 가져오기 버튼 포함)
     * 
     * @param title 회의 제목
     * @param date 회의 날짜
     * @param participants 참석자
     * @param summary 회의 요약
     * @param meetingId 회의 ID
     */
    private void addMeetingNote(String title, String date, String participants, String summary, String meetingId) {
        VBox meetingItem = new VBox(8);
        meetingItem.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #ecf0f1; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // 회의 정보
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        Label participantsLabel = new Label(participants);
        participantsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        Label summaryLabel = new Label(summary);
        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-wrap-text: true;");
        summaryLabel.setMaxWidth(400);
        
        // 일정 가져오기 버튼
        Button importButton = new Button("+ 일정 가져오기");
        importButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;");
        importButton.setOnAction(event -> handleImportFromMeeting(meetingId, title));
        
        meetingItem.getChildren().addAll(titleLabel, dateLabel, participantsLabel, summaryLabel, importButton);
        
        // 클릭 이벤트 추가 (버튼 제외)
        meetingItem.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof Button)) {
                handleMeetingClick(title);
            }
        });
        
        meetingNotesContainer.getChildren().add(meetingItem);
    }
    
    /**
     * 특정 회의록에서 일정 가져오기
     * 
     * @param meetingId 회의 ID
     * @param meetingTitle 회의 제목
     */
    @FXML
    public void handleImportFromMeeting(String meetingId, String meetingTitle) {
        // TODO: 해당 회의록의 업무 캘린더 내용 가져오기
        List<Schedule> importedSchedules = importSchedulesFromMeeting(meetingId);
        
        if (!importedSchedules.isEmpty()) {
            schedules.addAll(importedSchedules);
            updateMonthDisplay();
            loadUpcomingSchedules();
            showAlert("일정 가져오기", 
                     "회의록 '" + meetingTitle + "'에서 " + importedSchedules.size() + "개의 일정을 가져왔습니다.", 
                     Alert.AlertType.INFORMATION);
        } else {
            showAlert("일정 가져오기", "가져올 일정이 없습니다.", Alert.AlertType.INFORMATION);
        }
    }
    
        /**
     * 특정 회의록에서 일정 가져오기 (실제 API 연동)
     *
     * @param meetingId 회의 ID
     * @return 가져온 일정 목록
     */
    private List<Schedule> importSchedulesFromMeeting(String meetingId) {
        List<Schedule> importedSchedules = new ArrayList<>();

        try {
            // 실제 API에서 특정 회의 정보 가져오기
            Long meetingIdLong = Long.parseLong(meetingId);
            MeetingApiClient.MeetingDto meeting = apiClient.getMeetingById(meetingIdLong);
            
            if (meeting != null) {
                // 회의 정보를 기반으로 일정 생성
                LocalDate meetingDate = meeting.getStartTime() != null ? 
                    meeting.getStartTime().toLocalDate() : LocalDate.now();
                
                String time = meeting.getStartTime() != null ? 
                    meeting.getStartTime().toLocalTime().toString().substring(0, 5) : "14:00";
                
                // 회의를 일정으로 변환
                importedSchedules.add(new Schedule(
                    meeting.getTitle(),
                    meetingDate,
                    time,
                    meeting.getLocation() != null ? meeting.getLocation() : "회의실",
                    "회의"
                ));
                
                // 회의 설명에서 추가 일정 추출 (간단한 예시)
                if (meeting.getDescription() != null && meeting.getDescription().contains("후속")) {
                    importedSchedules.add(new Schedule(
                        meeting.getTitle() + " 후속 미팅",
                        meetingDate.plusDays(1),
                        "10:00",
                        meeting.getLocation() != null ? meeting.getLocation() : "회의실",
                        "회의"
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("회의 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            // API 호출 실패 시 기존 샘플 데이터 사용
            switch (meetingId) {
                case "meeting_001":
                    importedSchedules.add(new Schedule("주간 미팅", LocalDate.now().plusDays(1), "09:00", "회의실 A", "회의"));
                    importedSchedules.add(new Schedule("프로젝트 리뷰", LocalDate.now().plusDays(3), "14:00", "온라인", "미팅"));
                    break;
                case "meeting_002":
                    importedSchedules.add(new Schedule("프로젝트 킥오프 미팅", LocalDate.now().plusDays(2), "10:00", "회의실 B", "프로젝트"));
                    importedSchedules.add(new Schedule("역할 분담 회의", LocalDate.now().plusDays(4), "15:00", "회의실 C", "프로젝트"));
                    importedSchedules.add(new Schedule("초기 계획 수립", LocalDate.now().plusDays(5), "11:00", "온라인", "프로젝트"));
                    break;
                default:
                    importedSchedules.add(new Schedule("일반 미팅", LocalDate.now().plusDays(1), "14:00", "회의실 A", "회의"));
                    break;
            }
        }

        return importedSchedules;
    }
    
    // ==================== 이벤트 처리 메서드 ====================
    
    /**
     * 일정 클릭 처리
     * 
     * @param scheduleTitle 일정 제목
     */
    @FXML
    public void handleScheduleClick(String scheduleTitle) {
        showAlert("일정 선택", 
                 "일정 '" + scheduleTitle + "'을 선택했습니다.", 
                 Alert.AlertType.INFORMATION);
        
        // TODO: 일정 상세 정보 표시 또는 수정 기능 구현
    }
    
    /**
     * 회의록 클릭 처리
     * 
     * @param meetingTitle 회의 제목
     */
    @FXML
    public void handleMeetingClick(String meetingTitle) {
        showAlert("회의록 선택", 
                 "회의록 '" + meetingTitle + "'을 선택했습니다.", 
                 Alert.AlertType.INFORMATION);
        
        // TODO: 회의록 상세 정보 표시 또는 수정 기능 구현
    }
    
    // ==================== 데이터 로드 메서드 ====================
    
    /**
     * API MeetingDto를 MeetingNote로 변환
     */
    private MeetingNote convertToMeetingNote(MeetingApiClient.MeetingDto apiMeeting) {
        LocalDate meetingDate = apiMeeting.getStartTime() != null ? 
            apiMeeting.getStartTime().toLocalDate() : LocalDate.now();
        
        String participants = "참석자 정보 없음"; // API에서 참석자 정보가 없으므로 기본값
        String summary = apiMeeting.getDescription() != null ? 
            apiMeeting.getDescription() : "회의 내용 없음";
        
        return new MeetingNote(
            apiMeeting.getTitle(),
            meetingDate,
            participants,
            summary,
            apiMeeting.getMeetingId().toString()
        );
    }

    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 알림창을 표시
     * 
     * @param title 제목
     * @param content 내용
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
     * 일정 데이터 클래스
     */
    private static class Schedule {
        private String title;
        private LocalDate date;
        private String time;
        private String location;
        private String category;
        
        public Schedule(String title, LocalDate date, String time, String location, String category) {
            this.title = title;
            this.date = date;
            this.time = time;
            this.location = location;
            this.category = category;
        }
        
        // Getter 메서드들
        public String getTitle() { return title; }
        public LocalDate getDate() { return date; }
        public String getTime() { return time; }
        public String getLocation() { return location; }
        public String getCategory() { return category; }
    }
    
    /**
     * 회의록 데이터 클래스
     */
    private static class MeetingNote {
        private String title;
        private LocalDate date;
        private String participants;
        private String summary;
        private String id;
        
        public MeetingNote(String title, LocalDate date, String participants, String summary, String id) {
            this.title = title;
            this.date = date;
            this.participants = participants;
            this.summary = summary;
            this.id = id;
        }
        
        // Getter 메서드들
        public String getTitle() { return title; }
        public LocalDate getDate() { return date; }
        public String getParticipants() { return participants; }
        public String getSummary() { return summary; }
        public String getId() { return id; }
    }
} 