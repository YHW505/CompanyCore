package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.MeetingItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class MeetingDetailController {

    @FXML private Label titleLabel;
    @FXML private Label departmentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Text contentText;
    @FXML private VBox attachmentContainer;
    @FXML private VBox attachmentList;

    private MeetingItem meetingItem;

    @FXML
    public void initialize() {
        // 초기화 로직
    }

    /**
     * 회의 아이템을 설정하고 UI를 업데이트합니다.
     * 
     * @param item 표시할 회의 아이템
     */
    public void setMeetingItem(MeetingItem item) {
        this.meetingItem = item;
        updateUI();
    }

    /**
     * UI를 회의 데이터로 업데이트합니다.
     */
    private void updateUI() {
        if (meetingItem == null) return;

        // 기본 정보 설정
        titleLabel.setText(meetingItem.getTitle());
        departmentLabel.setText(meetingItem.getDepartment());
        authorLabel.setText(meetingItem.getAuthor());
        dateLabel.setText(meetingItem.getDate());

        // 내용 설정 (임시로 제목을 내용으로 사용)
        contentText.setText(meetingItem.getTitle());

        // 첨부파일 처리
        processAttachments();
    }

    /**
     * 첨부파일을 처리하고 UI에 표시합니다.
     */
    private void processAttachments() {
        if (meetingItem == null) return;

        String attachmentPath = meetingItem.getAttachmentPath();
        Long attachmentSize = meetingItem.getAttachmentSize();

        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            attachmentContainer.setVisible(true);
            attachmentList.getChildren().clear();

            // 첨부파일 항목 생성
            HBox attachmentItem = createAttachmentItem(attachmentPath, attachmentSize);
            attachmentList.getChildren().add(attachmentItem);
        } else {
            attachmentContainer.setVisible(false);
        }
    }

    /**
     * 첨부파일 항목을 생성합니다.
     * 
     * @param filename 파일명
     * @param fileSize 파일 크기
     * @return 첨부파일 항목 HBox
     */
    private HBox createAttachmentItem(String filename, Long fileSize) {
        HBox item = new HBox(10);
        item.setStyle("-fx-padding: 8; -fx-background-color: white; -fx-background-radius: 4;");

        // 파일 아이콘 (📎)
        Label iconLabel = new Label("📎");
        iconLabel.setStyle("-fx-font-size: 16px;");

        // 파일 정보
        VBox fileInfo = new VBox(2);
        Label nameLabel = new Label(filename);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        String sizeText = formatFileSize(fileSize != null ? fileSize : 0);
        Label sizeLabel = new Label(sizeText);
        sizeLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
        
        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        // 다운로드 버튼
        Button downloadBtn = new Button("다운로드");
        downloadBtn.setStyle("-fx-background-color: #5932EA; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 4 8;");
        downloadBtn.setOnAction(e -> downloadAttachment(filename));

        item.getChildren().addAll(iconLabel, fileInfo, downloadBtn);
        return item;
    }

    /**
     * 첨부파일을 다운로드합니다.
     * 
     * @param filename 파일명
     */
    private void downloadAttachment(String filename) {
        if (meetingItem == null || meetingItem.getAttachmentContent() == null) {
            showAlert("오류", "다운로드할 파일이 없습니다.");
            return;
        }

        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("다운로드 위치 선택");
            File selectedDirectory = directoryChooser.showDialog(getStage());

            if (selectedDirectory != null) {
                String attachmentContent = meetingItem.getAttachmentContent();
                byte[] fileContent = Base64.getDecoder().decode(attachmentContent);
                
                File outputFile = new File(selectedDirectory, filename);
                Files.write(Paths.get(outputFile.getAbsolutePath()), fileContent);
                
                showAlert("성공", "파일이 성공적으로 다운로드되었습니다.\n위치: " + outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일 크기를 포맷팅합니다.
     * 
     * @param bytes 파일 크기 (바이트)
     * @return 포맷팅된 파일 크기 문자열
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * 알림 다이얼로그를 표시합니다.
     * 
     * @param title 제목
     * @param content 내용
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 현재 Stage를 가져옵니다.
     * 
     * @return 현재 Stage
     */
    private Stage getStage() {
        return (Stage) titleLabel.getScene().getWindow();
    }

    /**
     * 닫기 버튼 클릭 시 호출되는 메서드
     */
    @FXML
    private void handleClose() {
        Stage stage = getStage();
        stage.close();
    }
} 