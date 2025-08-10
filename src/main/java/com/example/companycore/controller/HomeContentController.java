package com.example.companycore.controller;

import com.example.companycore.controller.core.MainController;
import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.model.entity.Attendance;
import com.example.companycore.model.entity.User;
import com.example.companycore.model.entity.Task;
import com.example.companycore.service.AttendanceApiClient;
import com.example.companycore.service.NoticeApiClient;
import com.example.companycore.service.UserApiClient;
import com.example.companycore.service.ApprovalApiClient;
import com.example.companycore.service.TaskApiClient;
import com.example.companycore.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HomeContentController {

    @FXML
    private HBox toggleSwitch;

    @FXML
    private HBox toggleButton;

    @FXML
    private Label toggleText;

    @FXML
    private Label userStatusLabel;

    @FXML
    private Label homeUserName;

    @FXML
    private Label homeUserPosition;

    @FXML
    private VBox announcementBox;

    @FXML
    private VBox recentAttendanceBox; // 새로 추가된 VBox

    @FXML
    private VBox pendingApprovalBox; // 새로 추가된 결재 요청 박스

    @FXML
    private Label pendingApprovalCountLabel; // 결재 요청 건수 라벨

    // 캘린더 관련 FXML 컴포넌트들
    @FXML
    private VBox calendarContainer;
    @FXML
    private Label calendarTitle;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Button prevMonthButton;
    @FXML
    private Button nextMonthButton;

    // 선택된 날짜의 업무 리스트 관련 FXML 컴포넌트들
    @FXML
    private VBox selectedDateInfo;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private Label selectedDateScheduleCount;
    @FXML
    private VBox selectedDateTasksContainer;

    private static boolean isWorking = false;
    private static boolean statusInitialized = false;

    // 캘린더 상태 관리
    private LocalDate currentDate = LocalDate.now();
    private LocalDate selectedDate = null; // 선택된 날짜 추가
    private DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private List<Task> myTasks = new ArrayList<>();

    @FXML
    public void initialize() {
        loadAnnouncements();
        loadRecentAttendance(); // 새로운 메서드 호출
        loadPendingApprovals(); // 결재 요청 로드
        loadUserData();

        if (!statusInitialized) {
            checkInitialAttendanceStatus();
            statusInitialized = true;
        } else {
            updateToggleState(isWorking);
        }

        // 10초마다 새로고침
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            loadAnnouncements();
            loadRecentAttendance();
            loadPendingApprovals();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // 캘린더 초기화는 FXML 컴포넌트들이 로드된 후에 수행
        Platform.runLater(() -> {
            initializeCalendar();
            // 선택된 날짜 정보 초기화
            if (selectedDateInfo != null) {
                selectedDateInfo.setVisible(false);
            }
        });
    }

    private void checkInitialAttendanceStatus() {
        new Thread(() -> {
            User currentUser = UserApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "오류", "사용자 정보를 가져올 수 없습니다.");
                });
                return;
            }
            long userId = currentUser.getUserId();
            List<Attendance> notCheckedOut = AttendanceApiClient.getInstance().getNotCheckedOutAttendance(userId);
            Platform.runLater(() -> {
                isWorking = (notCheckedOut != null && !notCheckedOut.isEmpty());
                updateToggleState(isWorking);
            });
        }).start();
    }

    @FXML
    public void handleToggleClick() {
        User currentUser = UserApiClient.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "오류", "사용자 정보를 가져올 수 없습니다.");
            return;
        }
        long userId = currentUser.getUserId();

        new Thread(() -> {
            boolean success;
            String action;
            if (!isWorking) { // Currently "퇴근", want to "출근"
                action = "출근";
                success = AttendanceApiClient.getInstance().checkIn(userId);
                Platform.runLater(() -> {
                    if (success) {
                        isWorking = !isWorking; // Toggle the state only on success
                        updateToggleState(isWorking);
                        showAlert(Alert.AlertType.INFORMATION, "성공", action + " 처리되었습니다.");
                        loadRecentAttendance(); // Refresh recent attendance records
                    } else {
                        showAlert(Alert.AlertType.ERROR, "실패", action + " 기록이 이미 존재합니다.");
                    }
                });
            } else { // Currently "근무중", want to "퇴근"
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("퇴근 확인");
                    alert.setHeaderText(null);
                    alert.setContentText("퇴근하시겠습니까?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            new Thread(() -> {
                                boolean successCheckout = AttendanceApiClient.getInstance().checkOut(userId);
                                Platform.runLater(() -> {
                                    if (successCheckout) {
                                        isWorking = !isWorking; // Toggle the state only on success
                                        updateToggleState(isWorking);
                                        showAlert(Alert.AlertType.INFORMATION, "성공", "퇴근 처리되었습니다.");
                                        loadRecentAttendance(); // Refresh recent attendance records
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "실패", "퇴근 처리에 실패했습니다.");
                                    }
                                });
                            }).start();
                        }
                    });
                });
            }
        }).start();
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

    private void loadUserData () {
        User currentUser = UserApiClient.getInstance().getCurrentUser();
        if(currentUser != null) {
            homeUserName.setText(currentUser.getUsername());
            homeUserPosition.setText(currentUser.getPositionName());
        } else{
            showAlert(Alert.AlertType.ERROR, "불러오기 실패", "사용자 정보를 불러오는데 실패하였습니다.");
            homeUserName.setText("000");
            homeUserPosition.setText("사원");
        }
    }

    private void loadAnnouncements() {
        // 백그라운드 스레드에서 API 호출
        new Thread(() -> {
            List<NoticeItem> announcements = getRecentAnnouncements();

            // UI 업데이트는 JavaFX Application Thread에서 수행
            Platform.runLater(() -> {
                if (announcementBox == null) return;
                announcementBox.getChildren().clear();

                if (announcements == null || announcements.isEmpty()) {
                    Label noNoticeLabel = new Label("최근 공지사항이 없습니다.");
                    noNoticeLabel.setStyle("-fx-text-fill: #7f8c8d;");
                    announcementBox.getChildren().add(noNoticeLabel);
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                for (NoticeItem item : announcements) {
                    if (item == null) continue;

                    HBox announcementItem = new HBox();
                    announcementItem.setAlignment(Pos.CENTER_LEFT);
                    announcementItem.setSpacing(10);
                    announcementItem.setCursor(Cursor.HAND);
                    announcementItem.setOnMouseClicked(event -> {
                        MainController mainController = (MainController) announcementBox.getScene().getUserData();
                        if (mainController != null) {
                            mainController.loadContent("announcements");
                        }
                    });

                    Label iconLabel = new Label("📢");
                    iconLabel.setStyle("-fx-font-size: 12px;");

                    VBox contentVBox = new VBox();
                    Label titleLabel = new Label(item.getTitle() != null ? item.getTitle() : "제목 없음");
                    titleLabel.setStyle("-fx-font-weight: bold;");

                    String author = item.getAuthor() != null ? item.getAuthor() : "";
                    String createdAt = item.getCreatedAt() != null ? item.getCreatedAt().format(formatter) : "";
                    Label detailsLabel = new Label(author + createdAt);
                    detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                    contentVBox.getChildren().addAll(titleLabel, detailsLabel);
                    announcementItem.getChildren().addAll(iconLabel, contentVBox);
                    announcementBox.getChildren().add(announcementItem);
                }
            });
        }).start();
    }

    private List<NoticeItem> getRecentAnnouncements() {
        return NoticeApiClient.getInstance().getRecentNotices();
    }

    // 최근 출근 기록을 로드하는 메서드
    private void loadRecentAttendance() {
        new Thread(() -> {
            User currentUser = UserApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                return; // Or handle error
            }
            long userId = currentUser.getUserId();
            List<Attendance> recentAttendances = AttendanceApiClient.getInstance().getRecentAttendance(userId);

            Platform.runLater(() -> {
                if (recentAttendanceBox == null) return;
                recentAttendanceBox.getChildren().clear();

                if (recentAttendances == null || recentAttendances.isEmpty()) {
                    Label noAttendanceLabel = new Label("최근 출근 기록이 없습니다.");
                    noAttendanceLabel.setStyle("-fx-text-fill: #7f8c8d;");
                    recentAttendanceBox.getChildren().add(noAttendanceLabel);
                    return;
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                for (Attendance attendance : recentAttendances) {
                    if (attendance == null) continue;

                    HBox attendanceItem = new HBox();
                    attendanceItem.setAlignment(Pos.CENTER_LEFT);
                    attendanceItem.setSpacing(10);

                    Label iconLabel = new Label("🕒");
                    iconLabel.setStyle("-fx-font-size: 12px;");

                    VBox contentVBox = new VBox();
                    String workDate = attendance.getWorkDate() != null ? attendance.getWorkDate().format(dateFormatter) : "날짜 없음";
                    Label dateLabel = new Label(workDate);
                    dateLabel.setStyle("-fx-font-weight: bold;");

                    String checkInTime = attendance.getCheckIn() != null ? attendance.getCheckIn().format(timeFormatter) : "미기록";
                    String checkOutTime = attendance.getCheckOut() != null ? attendance.getCheckOut().format(timeFormatter) : "미기록";
                    Label timeLabel = new Label("출근: " + checkInTime + " | 퇴근: " + checkOutTime);
                    timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                    contentVBox.getChildren().addAll(dateLabel, timeLabel);

                    Label statusLabel = new Label(attendance.getStatus().toString());
                    statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2 5; -fx-background-radius: 5;");

                    switch (attendance.getStatus()) {
                        case PRESENT:
                            statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #27ae60; -fx-text-fill: white;");
                            statusLabel.setText("정상출근");
                            break;
                        case LATE:
                            statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #f39c12; -fx-text-fill: white;");
                            statusLabel.setText("지각");
                            break;
                        case ABSENT:
                            statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #e74c3c; -fx-text-fill: white;");
                            statusLabel.setText("미출근");
                            break;
                        case LEAVE:
                            statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #3498db; -fx-text-fill: white;");
                            statusLabel.setText("미퇴근");
                            break;
                    }

                    HBox itemBox = new HBox(iconLabel, contentVBox, statusLabel);
                    itemBox.setSpacing(10);
                    itemBox.setAlignment(Pos.CENTER_LEFT);
                    javafx.scene.layout.HBox.setHgrow(contentVBox, javafx.scene.layout.Priority.ALWAYS);

                    recentAttendanceBox.getChildren().add(itemBox);
                }
            });
        }).start();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void loadPendingApprovals() {
        new Thread(() -> {
            User currentUser = UserApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                Platform.runLater(() -> {
                    if (pendingApprovalBox != null) pendingApprovalBox.setVisible(false);
                });
                return;
            }

            // positionId가 1인 경우에만 박스를 표시
            if (currentUser.getPositionId() != null && currentUser.getPositionId() == 1) {
                Integer departmentId = currentUser.getDepartmentId();
                System.out.println("부서명 =========== " + departmentId);
                java.util.Map<String, Object> pendingApprovals = ApprovalApiClient.getInstance().getMyPendingWithPagination(departmentId, 0, 10, "requestDate", "desc");
                Platform.runLater(() -> {
                    System.out.println("pendingApprovals ==========" + pendingApprovals);
                    if (pendingApprovalBox != null) {
                        pendingApprovalBox.setVisible(true);
                        List<java.util.Map<String, Object>> contentList = (List<java.util.Map<String, Object>>) pendingApprovals.get("content");
                        int count = (contentList != null) ? contentList.size(): 0;
                        pendingApprovalCountLabel.setText(count + "건");
                        pendingApprovalBox.setOnMouseClicked(event -> {
                            MainController mainController = (MainController) pendingApprovalBox.getScene().getUserData();
                            if (mainController != null) {
                                mainController.loadContent("approvalApproval"); // 결재 요청 목록 뷰로 전환
                            }
                        });
                    }
                });
            } else {
                Platform.runLater(() -> {
                    if (pendingApprovalBox != null) pendingApprovalBox.setVisible(false);
                });
            }
        }).start();
    }

    /**
     * 캘린더 초기화
     */
    private void initializeCalendar() {
        if (calendarContainer == null) {
            System.out.println("캘린더 컨테이너가 로드되지 않았습니다.");
            return;
        }

        System.out.println("캘린더 초기화 시작");
        
        // 캘린더 헤더 생성
        createCalendarHeader();
        // 달력 그리드 생성
        createCalendarGrid();
        // 내 업무 로드
        loadMyTasksForCalendar();
        // 달력 업데이트
        updateCalendarDisplay();
        
        System.out.println("캘린더 초기화 완료");
    }

    /**
     * 캘린더 헤더 생성
     */
    private void createCalendarHeader() {
        if (calendarContainer == null) {
            System.out.println("캘린더 컨테이너가 초기화되지 않았습니다.");
            return;
        }

        System.out.println("캘린더 헤더 생성 시작");

        // 기존 내용 제거
        calendarContainer.getChildren().clear();

        // 제목 (년월)
        calendarTitle = new Label();
        calendarTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        calendarContainer.getChildren().add(calendarTitle);

        // 요일 헤더
        HBox dayHeader = new HBox();
        dayHeader.setSpacing(2);
        dayHeader.setAlignment(Pos.CENTER);

        String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
        for (String dayName : dayNames) {
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d; -fx-min-width: 40; -fx-alignment: center;");
            dayHeader.getChildren().add(dayLabel);
        }
        calendarContainer.getChildren().add(dayHeader);

        // 달력 그리드 생성
        calendarGrid = new GridPane();
        calendarGrid.setHgap(2);
        calendarGrid.setVgap(2);
        calendarGrid.setAlignment(Pos.CENTER);

        // 6주 x 7일 그리드 생성
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox dayCell = new VBox(2);
                dayCell.setStyle("-fx-min-width: 40; -fx-min-height: 40; -fx-alignment: top-center; -fx-padding: 2; -fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 1;");
                dayCell.setAlignment(Pos.TOP_CENTER);
                
                // 고유 ID 설정
                dayCell.setId("calendar-cell-" + row + "-" + col);
                
                GridPane.setRowIndex(dayCell, row);
                GridPane.setColumnIndex(dayCell, col);
                calendarGrid.getChildren().add(dayCell);
            }
        }

        calendarContainer.getChildren().add(calendarGrid);

        // 이전/다음 월 버튼
        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        Button prevMonthBtn = new Button("◀");
        prevMonthBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-min-width: 30; -fx-min-height: 25;");
        prevMonthBtn.setOnAction(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendarDisplay();
        });

        Button nextMonthBtn = new Button("▶");
        nextMonthBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-min-width: 30; -fx-min-height: 25;");
        nextMonthBtn.setOnAction(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendarDisplay();
        });

        navigationBox.getChildren().addAll(prevMonthBtn, nextMonthBtn);
        calendarContainer.getChildren().add(navigationBox);

        System.out.println("캘린더 헤더 생성 완료");
    }

    /**
     * 달력 그리드 생성
     */
    private void createCalendarGrid() {
        // 그리드는 createCalendarHeader에서 생성되므로 여기서는 건너뜀
        System.out.println("달력 그리드 생성 완료");
    }

    /**
     * 이전 달로 이동
     */
    private void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        updateCalendarDisplay();
    }

    /**
     * 다음 달로 이동
     */
    private void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateCalendarDisplay();
    }

    /**
     * 달력 표시 업데이트
     */
    private void updateCalendarDisplay() {
        if (calendarTitle == null || calendarGrid == null) {
            System.out.println("캘린더 컴포넌트가 초기화되지 않았습니다.");
            return;
        }

        System.out.println("달력 표시 업데이트 시작");
        
        // 다른 월로 이동했을 때 선택된 날짜 정보 숨기기
        if (selectedDate != null && !selectedDate.getMonth().equals(currentDate.getMonth())) {
            selectedDate = null;
            if (selectedDateInfo != null) {
                selectedDateInfo.setVisible(false);
            }
        }

        // 제목 업데이트
        calendarTitle.setText(currentDate.format(monthFormatter));

        // 기존 날짜 내용 제거
        calendarGrid.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                VBox dayCell = (VBox) node;
                dayCell.getChildren().clear();
            }
        });

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
                addDateToCalendar(date, row, col);
                dayOfMonth++;
            }
            row++;
        }

        System.out.println("달력 표시 업데이트 완료");
    }

    /**
     * 달력에 날짜 추가
     */
    private void addDateToCalendar(LocalDate date, int row, int col) {
        if (calendarGrid == null) {
            System.out.println("캘린더 그리드가 초기화되지 않았습니다.");
            return;
        }

        // 고유 ID를 사용해 해당 위치의 셀 찾기
        String cellId = "calendar-cell-" + row + "-" + col;
        VBox dayCell = null;
        
        for (var node : calendarGrid.getChildren()) {
            if (node instanceof VBox && cellId.equals(node.getId())) {
                dayCell = (VBox) node;
                break;
            }
        }

        if (dayCell == null) {
            System.out.println("날짜 셀을 찾을 수 없습니다: " + cellId);
            return;
        }

        // 기존 내용 제거
        dayCell.getChildren().clear();

        // 날짜 라벨
        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 일정 표시 영역
        VBox scheduleContainer = new VBox(1);
        scheduleContainer.setStyle("-fx-padding: 2;");

        // 해당 날짜의 일반 일정들 가져오기 (내 업무)
        List<Task> dayMyTasks = myTasks.stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .collect(Collectors.toList());

        // 해당 날짜에 걸쳐있는 '가져온' 업무들 가져오기 (ApiClient의 globalImportedTasks)
        List<Task> dayImportedTasks = ApiClient.getInstance().getGlobalImportedTasks().stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .collect(Collectors.toList());

        // 모든 항목을 합치고 표시
        List<String> allItemsToDisplay = new ArrayList<>();
        for (Task task : dayMyTasks) {
            allItemsToDisplay.add(task.getTitle() + " (내 업무)");
        }
        for (Task task : dayImportedTasks) {
            allItemsToDisplay.add(task.getTitle() + " (가져온 업무)");
        }

        // 최대 3개만 표시
        int displayLimit = 3;
        for (int i = 0; i < Math.min(allItemsToDisplay.size(), displayLimit); i++) {
            Label itemLabel = new Label(allItemsToDisplay.get(i));
            // Style for imported tasks (green background)
            if (allItemsToDisplay.get(i).endsWith(" (가져온 업무)")) {
                itemLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: white; -fx-background-color: #27ae60; -fx-padding: 1 3; -fx-background-radius: 2;");
            } else { // Style for my tasks (blue background)
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

        dayCell.getChildren().addAll(dateLabel, scheduleContainer);

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
        } else {
            // 일반 날짜
            backgroundStyle = "-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ecf0f1; -fx-border-width: 1;";
        }

        dayCell.setStyle(dayCell.getStyle() + backgroundStyle);
        
        // 날짜 클릭 이벤트 추가
        dayCell.setOnMouseClicked(event -> handleDateClick(date));
    }

    /**
     * 캘린더용 내 업무 로드
     */
    private void loadMyTasksForCalendar() {
        new Thread(() -> {
            try {
                User currentUser = ApiClient.getInstance().getCurrentUser();
                if (currentUser == null) {
                    System.out.println("현재 사용자 정보를 가져올 수 없습니다.");
                    return;
                }

                Long userId = currentUser.getUserId();
                List<Task> tasks = TaskApiClient.getInstance().getMyAssignedTasks(userId);

                Platform.runLater(() -> {
                    myTasks.clear();
                    if (tasks != null) {
                        myTasks.addAll(tasks);
                        System.out.println("캘린더용 업무 로드 완료: " + tasks.size() + "개");
                    } else {
                        System.out.println("캘린더용 업무가 없습니다.");
                    }
                    updateCalendarDisplay();
                    
                    // 선택된 날짜가 있다면 업무 리스트도 업데이트
                    if (selectedDate != null) {
                        handleDateClick(selectedDate);
                    }
                });
            } catch (Exception e) {
                System.out.println("캘린더용 업무 로드 중 오류: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 달력에서 업무를 제거하는 핸들러
     * @param task 제거할 업무 객체
     */
    private void handleRemoveTaskFromCalendar(Task task) {
        ApiClient.getInstance().removeGlobalImportedTask(task); // Assuming ApiClient has this method
        updateCalendarDisplay(); // Refresh calendar
//        showAlert("업무 제거", "업무 '" + task.getTitle() + "'을(를) 달력에서 제거했습니다.", Alert.AlertType.INFORMATION);
    }

    /**
     * 날짜 클릭 처리
     * @param date 클릭된 날짜
     */
    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        updateCalendarDisplay(); // 선택된 날짜 강조를 위해 달력 다시 그리기

        // 선택된 날짜 정보 표시
        selectedDateLabel.setText("선택된 날짜: " + date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

        // 해당 날짜의 업무들을 수집
        List<Task> dayMyTasks = myTasks.stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .collect(Collectors.toList());

        // 해당 날짜에 걸쳐있는 '가져온' 업무들 가져오기
        List<Task> dayImportedTasks = ApiClient.getInstance().getGlobalImportedTasks().stream()
                .filter(task -> {
                    LocalDate taskStartDate = task.getStartDate();
                    LocalDate taskEndDate = task.getEndDate();
                    return taskStartDate != null && taskEndDate != null &&
                           !date.isBefore(taskStartDate) && !date.isAfter(taskEndDate);
                })
                .collect(Collectors.toList());

        // 모든 업무를 합치고 정렬
        List<Task> allDayTasks = new ArrayList<>();
        allDayTasks.addAll(dayMyTasks);
        allDayTasks.addAll(dayImportedTasks);

        selectedDateScheduleCount.setText("업무: " + allDayTasks.size() + "개");
        selectedDateInfo.setVisible(true);

        // 업무 목록 컨테이너 업데이트
        selectedDateTasksContainer.getChildren().clear();
        if (allDayTasks.isEmpty()) {
            Label noTasksLabel = new Label("해당 날짜에 업무가 없습니다.");
            noTasksLabel.setStyle("-fx-padding: 10; -fx-text-fill: #7f8c8d;");
            selectedDateTasksContainer.getChildren().add(noTasksLabel);
        } else {
            for (Task task : allDayTasks) {
                addSelectedDateTask(task, dayImportedTasks.contains(task));
            }
        }
    }

    /**
     * 선택된 날짜의 업무 항목을 UI에 추가
     * @param task 추가할 업무
     * @param isImported 가져온 업무인지 여부
     */
    private void addSelectedDateTask(Task task, boolean isImported) {
        VBox taskItem = new VBox(3);
        taskItem.setStyle("-fx-padding: 8; -fx-background-color: #f0f8ff; -fx-background-radius: 4;");

        String titleText = task.getTitle();
        if (isImported) {
            titleText += " (가져온 업무)";
        } else {
            titleText += " (내 업무)";
        }
        
        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2a2a2a;");

        // 업무 기간 표시
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String periodText = "기간: " + (task.getStartDate() != null ? task.getStartDate().format(formatter) : "미정") +
                           " ~ " + (task.getEndDate() != null ? task.getEndDate().format(formatter) : "미정");
        Label periodLabel = new Label(periodText);
        periodLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        // 업무 상태 표시
        String statusText = "상태: " + (task.getStatus() != null ? task.getStatus().toString() : "미지정");
        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        // 업무 설명 표시 (있는 경우)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            Label descriptionLabel = new Label("설명: " + task.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
            descriptionLabel.setWrapText(true);
            taskItem.getChildren().addAll(titleLabel, periodLabel, statusLabel, descriptionLabel);
        } else {
            taskItem.getChildren().addAll(titleLabel, periodLabel, statusLabel);
        }

        selectedDateTasksContainer.getChildren().add(taskItem);
    }
}