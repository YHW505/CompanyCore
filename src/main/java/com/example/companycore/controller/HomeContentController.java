package com.example.companycore.controller;

import com.example.companycore.controller.core.MainController;
import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.model.entity.Attendance;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.AttendanceApiClient;
import com.example.companycore.service.NoticeApiClient;
import com.example.companycore.service.UserApiClient;
import com.example.companycore.service.ApprovalApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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

    private static boolean isWorking = false;
    private static boolean statusInitialized = false;

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
                        if (response == ButtonType.OK) {
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
                    HBox.setHgrow(contentVBox, Priority.ALWAYS);


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
                Map<String, Object> pendingApprovals = ApprovalApiClient.getInstance().getMyPendingWithPagination(departmentId, 0, 10, "requestDate", "desc");
                Platform.runLater(() -> {
                    System.out.println("pendingApprovals ==========" + pendingApprovals);
                    if (pendingApprovalBox != null) {
                        pendingApprovalBox.setVisible(true);
                        List<Map<String, Object>> contentList = (List<Map<String, Object>>) pendingApprovals.get("content");
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
}