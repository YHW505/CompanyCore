package com.example.companycore.controller;

import com.example.companycore.controller.core.MainController;
import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.model.entity.Attendance;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.AttendanceApiClient;
import com.example.companycore.service.NoticeApiClient;
import com.example.companycore.service.UserApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private VBox announcementBox;

    @FXML
    private VBox recentAttendanceBox; // 새로 추가된 VBox

    private boolean isWorking = false;

    @FXML
    public void initialize() {
        updateToggleState(false);
        loadAnnouncements();
        loadRecentAttendance(); // 새로운 메서드 호출
    }

    @FXML
    public void handleToggleClick() {
        isWorking = !isWorking;
        updateToggleState(isWorking);
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
} 