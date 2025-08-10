package com.example.companycore.controller.calendar;

import com.example.companycore.model.entity.User;
import com.example.companycore.model.entity.Task;
import com.example.companycore.service.TaskApiClient;
import com.example.companycore.service.UserApiClient;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MeetingApiClient;
import javafx.application.Platform;

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
    @FXML private VBox selectedDateTasksContainer;

    // ==================== 상태 관리 ====================

    /** 현재 선택된 날짜 */
    private LocalDate currentDate = LocalDate.now();

    /** 현재 선택된 날짜 (클릭된 날짜) */
    private LocalDate selectedDate = null;

    /** 월 표시 포맷터 */
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    /** 일정 데이터 */
    private List<Schedule> schedules = new ArrayList<>();

    /** API 클라이언트 */
    private final ApiClient apiClient = ApiClient.getInstance();
    private final UserApiClient userApiClient = UserApiClient.getInstance();
    private final TaskApiClient taskApiClient = TaskApiClient.getInstance();

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
        // 내 업무 로드
        loadMyTasks();
        // 달력 초기화
        updateMonthDisplay();
        // 업무 목록 로드
        loadMyTasksForTaskList();
        // 다가오는 일정 로드
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
     * 업무 목록 새로고침
     */
    @FXML
    public void handleRefreshMeetings() {
        loadMyTasksForTaskList();
        showAlert("새로고침", "업무 목록이 새로고침되었습니다.", Alert.AlertType.INFORMATION);
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

        // 해당 날짜의 일반 일정들 가져오기
        List<Schedule> daySchedules = schedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .collect(Collectors.toList());

        // 해당 날짜에 걸쳐있는 '가져온' 업무들 가져오기
        List<Task> dayImportedTasks = apiClient.getGlobalImportedTasks().stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .collect(Collectors.toList());

        // 모든 항목을 합치고 표시
        List<String> allItemsToDisplay = new ArrayList<>();
        for (Schedule schedule : daySchedules) {
            String displayText = schedule.getTitle();
            if (schedule.getAssignerName() != null && !schedule.getAssignerName().isEmpty()) {
                displayText += " (" + schedule.getAssignerName() + ")";
            }
            allItemsToDisplay.add(displayText + " (일정)"); // Differentiate schedules
        }
        for (Task task : dayImportedTasks) {
            allItemsToDisplay.add(task.getTitle() + " (업무)"); // Differentiate tasks
        }

        // 최대 3개만 표시
        int displayLimit = 3;
        for (int i = 0; i < Math.min(allItemsToDisplay.size(), displayLimit); i++) {
            Label itemLabel = new Label(allItemsToDisplay.get(i));
            // Style for imported tasks (e.g., green background)
            if (allItemsToDisplay.get(i).endsWith(" (업무)")) {
                itemLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: #3498db;"); // Plain text, light blue color
            } else { // Style for regular schedules (blue background)
                itemLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: white; -fx-background-color: #3498db; -fx-padding: 1 3; -fx-background-radius: 2;");
            }
            itemLabel.setMaxWidth(50);
            itemLabel.setWrapText(true);
            scheduleContainer.getChildren().add(itemLabel);
        }

        // 더 많은 항목이 있으면 표시
        if (allItemsToDisplay.size() > displayLimit) {
            Label moreLabel = new Label("+" + (allItemsToDisplay.size() - displayLimit) + "개");
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
        updateMonthDisplay(); // Redraw to highlight the selected date

        // Show selected date info
        selectedDateLabel.setText("선택된 날짜: " + date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

        // Combine assigned schedules and imported tasks for the selected date
        Set<Schedule> dayUniqueSchedules = new HashSet<>();

        // Add assigned schedules for the selected date
        schedules.stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .forEach(dayUniqueSchedules::add);

        // Convert imported tasks to schedules for the selected date and add to the set
        apiClient.getGlobalImportedTasks().stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .forEach(task -> {
                    String time = "하루 종일"; // Placeholder
                    String category = task.getTaskType() != null ? task.getTaskType().toString() : "업무";
                    dayUniqueSchedules.add(new Schedule(
                        task.getTitle(),
                        date, // Use the clicked date
                        time,
                        "온라인", // Placeholder
                        category,
                        "Imported" // Indicate it's an imported task
                    ));
                });

        List<Schedule> daySchedulesToDisplay = new ArrayList<>(dayUniqueSchedules);
        daySchedulesToDisplay.sort((s1, s2) -> s1.getTitle().compareTo(s2.getTitle())); // Sort by title for consistent display

        selectedDateScheduleCount.setText("일정: " + daySchedulesToDisplay.size() + "개");
        selectedDateInfo.setVisible(true);

        // Populate the tasks container for the selected date
        selectedDateTasksContainer.getChildren().clear(); // Clear previous tasks
        if (daySchedulesToDisplay.isEmpty()) {
            Label noTasksLabel = new Label("해당 날짜에 업무가 없습니다.");
            noTasksLabel.setStyle("-fx-padding: 10; -fx-text-fill: #7f8c8d;");
            selectedDateTasksContainer.getChildren().add(noTasksLabel);
        } else {
            for (Schedule schedule : daySchedulesToDisplay) {
                addSelectedDateTask(schedule);
            }
        }
    }

    /**
     * 선택된 날짜의 업무 항목을 UI에 추가
     * @param schedule 추가할 스케줄
     */
    private void addSelectedDateTask(Schedule schedule) {
        VBox taskItem = new VBox(3);
        taskItem.setStyle("-fx-padding: 8; -fx-background-color: #f0f8ff; -fx-background-radius: 4;");

        String titleText = schedule.getTitle();
        if (schedule.getAssignerName() != null && !schedule.getAssignerName().isEmpty()) {
            titleText += " (From: " + schedule.getAssignerName() + ")";
        }
        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2a2a2a;");

        Label timeLabel = new Label("시간: " + schedule.getTime());
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        Label categoryLabel = new Label("분류: " + schedule.getCategory());
        categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        taskItem.getChildren().addAll(titleLabel, timeLabel, categoryLabel);
        selectedDateTasksContainer.getChildren().add(taskItem);
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
        LocalDate sevenDaysLater = today.plusDays(7);
        // Combine assigned schedules and imported tasks into a Set for de-duplication
        Set<Schedule> uniqueSchedules = new HashSet<>(schedules);

        // Convert imported tasks to schedules and add to the set
        for (Task task : apiClient.getGlobalImportedTasks()) {
            LocalDate startDate = task.getStartDate();
            LocalDate endDate = task.getEndDate();
            if (startDate != null && endDate != null) {
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String time = "하루 종일"; // Placeholder
                    String category = task.getTaskType() != null ? task.getTaskType().toString() : "업무";
                    // For imported tasks, assignerName might not be directly available or relevant,
                    // so we can use a placeholder or leave it null/empty.
                    // Let's use "Imported" as assignerName for now.
                    uniqueSchedules.add(new Schedule(
                        task.getTitle(),
                        date,
                        time,
                        "온라인", // Placeholder
                        category,
                        "Imported" // Indicate it's an imported task
                    ));
                }
            }
        }

        LocalDate threeDaysLater = today.plusDays(4); // Changed to 4 to include 3 days from tomorrow
        List<Schedule> filteredSchedules = uniqueSchedules.stream() // Use uniqueSchedules here
                .filter(schedule -> schedule.getDate().isAfter(today) && schedule.getDate().isBefore(threeDaysLater))
                .sorted((s1, s2) -> s1.getDate().compareTo(s2.getDate()))
                .collect(Collectors.toList());

        Set<String> displayedTitles = new HashSet<>();
        List<Schedule> upcomingSchedules = new ArrayList<>();

        for (Schedule schedule : filteredSchedules) {
            if (!displayedTitles.contains(schedule.getTitle())) {
                upcomingSchedules.add(schedule);
                displayedTitles.add(schedule.getTitle());
            }
            if (upcomingSchedules.size() >= 5) { // Apply limit after de-duplication
                break;
            }
        }

        for (Schedule schedule : upcomingSchedules) {
            addUpcomingSchedule(schedule.getTitle(),
                              schedule.getDate().toString(),
                              schedule.getTime(),
                              schedule.getLocation(),
                              schedule.getCategory());
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

    // ==================== 업무 목록 관련 메서드 ====================

    /**
     * 나의 업무 목록을 로드하고 표시
     */
    private void loadMyTasksForTaskList() {
        // 로딩 메시지 표시 (임시)
        meetingNotesContainer.getChildren().clear();
        meetingNotesContainer.getChildren().add(new Label("업무 목록을 불러오는 중..."));

        // 네트워크 작업을 위한 백그라운드 스레드 생성
        new Thread(() -> {
            try {
                User currentUser = apiClient.getCurrentUser();
                if (currentUser == null) {
                    Platform.runLater(() -> {
                        meetingNotesContainer.getChildren().clear();
                        meetingNotesContainer.getChildren().add(new Label("로그인 정보가 없어 업무를 불러올 수 없습니다."));
                    });
                    return;
                }
                Long userId = currentUser.getUserId();

                // 네트워크 요청 (getTasksAssignedToUser로 변경)
                List<Task> tasks = taskApiClient.getTasksAssignedToUser(userId);

                // UI 업데이트는 JavaFX 애플리케이션 스레드에서 수행
                Platform.runLater(() -> {
                    meetingNotesContainer.getChildren().clear(); // 로딩 메시지 제거

                    if (tasks == null || tasks.isEmpty()) { // null 또는 비어있는 경우
                        meetingNotesContainer.getChildren().add(new Label("할당된 업무가 없습니다."));
                    } else {
                        for (Task task : tasks) {
                            // null 태스크 방지 (방어적 코딩)
                            System.out.println("task======================" +  task);
                            if (task != null) {
                                addTaskToTaskList(task);
                            }
                        }
                    }
                });

            } catch (Exception e) {
                // 오류 발생 시 UI 업데이트
                Platform.runLater(() -> {
                    meetingNotesContainer.getChildren().clear();
                    System.out.println("내 업무 목록을 가져오는 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                    meetingNotesContainer.getChildren().add(new Label("업무를 불러오는 중 오류가 발생했습니다."));
                });
            }
        }).start();
    }

    /**
     * 업무 항목을 목록에 추가
     *
     * @param task 추가할 업무 객체
     */
    private void addTaskToTaskList(Task task) {
        VBox taskItem = new VBox(8);
        taskItem.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #ecf0f1; -fx-border-width: 1; -fx-border-radius: 8;");

        // 업무 정보
        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String period = "기간: " + (task.getStartDate() != null ? task.getStartDate().format(formatter) : "미정") +
                        " ~ " + (task.getEndDate() != null ? task.getEndDate().format(formatter) : "미정");
        Label dateLabel = new Label(period);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        String statusText = "상태: " + (task.getStatus() != null ? task.getStatus().toString() : "미지정");
        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-wrap-text: true;");
        descriptionLabel.setMaxWidth(400);

        taskItem.getChildren().addAll(titleLabel, dateLabel, statusLabel, descriptionLabel);

        HBox buttonBox = new HBox(10); // Spacing between buttons
        Button importTaskButton = new Button("업무 가져오기");
        importTaskButton.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 4 8;");
        importTaskButton.setOnAction(event -> {
            // Add the task to the list of imported tasks
            apiClient.addGlobalImportedTask(task);
            updateMonthDisplay(); // Redraw calendar to show the task titles
            showAlert("업무 가져오기", "업무 '" + task.getTitle() + "'을(를) 달력에 표시했습니다.", Alert.AlertType.INFORMATION);
        });

        Button removeTaskButton = new Button("달력에서 제거");
        removeTaskButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 4 8;");
        removeTaskButton.setOnAction(event -> handleRemoveTaskFromCalendar(task)); // Call new method

        buttonBox.getChildren().addAll(importTaskButton, removeTaskButton);
        taskItem.getChildren().add(buttonBox); // Add the HBox containing buttons to the VBox

        taskItem.setOnMouseClicked(event -> {
            // Existing click handler for the VBox, might be redundant with the button
            // showAlert("업무 선택", "업무 '" + task.getTitle() + "'을(를) 선택했습니다.", Alert.AlertType.INFORMATION);
            // TODO: 업무 상세 정보 표시 또는 수정 기능 구현
        });

        meetingNotesContainer.getChildren().add(taskItem);
    }

    /**
     * 달력에서 업무를 제거하는 핸들러
     * @param task 제거할 업무 객체
     */
    private void handleRemoveTaskFromCalendar(Task task) {
        apiClient.removeGlobalImportedTask(task); // Assuming ApiClient has this method
        updateMonthDisplay(); // Refresh calendar
        showAlert("업무 제거", "업무 '" + task.getTitle() + "'을(를) 달력에서 제거했습니다.", Alert.AlertType.INFORMATION);
    }

    private void loadMyTasks() {
        schedules.clear(); // 기존 스케줄 초기화
        User currentUser = apiClient.getCurrentUser();

        if (currentUser == null) {
            System.out.println("User not logged in, cannot fetch tasks.");
            return;
        }
        Long userId = currentUser.getUserId();

        try {
            List<Task> tasks = taskApiClient.getMyAssignedTasks(userId);
            for (Task task : tasks) {
                User assigner = userApiClient.getUserById(task.getAssignedBy());
                String assignerName = (assigner != null) ? assigner.getUsername() : "Unknown";

                LocalDate startDate = task.getStartDate();
                LocalDate endDate = task.getEndDate();
                if (startDate != null && endDate != null) {
                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        String time = "하루 종일";
                        String category = task.getTaskType() != null ? task.getTaskType().toString() : "업무";
                        schedules.add(new Schedule(
                            task.getTitle(),
                            date,
                            time,
                            "온라인", // Task does not have a location, using a placeholder
                            category,
                            assignerName
                        ));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("내 업무 목록을 가져오는 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
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
        private String assignerName;

        public Schedule(String title, LocalDate date, String time, String location, String category, String assignerName) {
            this.title = title;
            this.date = date;
            this.time = time;
            this.location = location;
            this.category = category;
            this.assignerName = assignerName;
        }

        // Getter 메서드들
        public String getTitle() { return title; }
        public LocalDate getDate() { return date; }
        public String getTime() { return time; }
        public String getLocation() { return location; }
        public String getCategory() { return category; }
        public String getAssignerName() { return assignerName; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Schedule schedule = (Schedule) o;
            return title.equals(schedule.title) &&
                   date.equals(schedule.date) &&
                   time.equals(schedule.time) &&
                   location.equals(schedule.location) &&
                   category.equals(schedule.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, date, time, location, category);
        }
    }
}