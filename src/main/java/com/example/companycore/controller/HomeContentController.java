package com.example.companycore.controller;

import com.example.companycore.controller.core.MainController;
import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.service.NoticeApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    private boolean isWorking = false;

    @FXML
    public void initialize() {
        updateToggleState(false);
        loadAnnouncements();
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
} 