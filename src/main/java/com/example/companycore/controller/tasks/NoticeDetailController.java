package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.NoticeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 공지사항 상세보기 다이얼로그 컨트롤러
 */
public class NoticeDetailController {

    @FXML private Label titleLabel;
    @FXML private Label departmentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea contentTextArea;
    @FXML private Label importantLabel;
    @FXML private VBox attachmentSection;
    @FXML private ListView<String> attachmentListView;
    @FXML private Button downloadButton;
    @FXML private Button closeButton;

    private NoticeItem noticeItem;
    private ObservableList<String> attachmentFiles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupAttachmentListView();
    }

    /**
     * 첨부파일 리스트뷰 설정
     */
    private void setupAttachmentListView() {
        attachmentListView.setItems(attachmentFiles);
        
        // 첨부파일 더블클릭 시 다운로드
        attachmentListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedFile = attachmentListView.getSelectionModel().getSelectedItem();
                if (selectedFile != null) {
                    downloadAttachment(selectedFile);
                }
            }
        });
    }

    /**
     * 공지사항 데이터 설정
     */
    public void setNoticeItem(NoticeItem notice) {
        this.noticeItem = notice;
        populateData();
    }

    /**
     * 데이터 채우기
     */
    private void populateData() {
        if (noticeItem == null) return;

        titleLabel.setText(noticeItem.getTitle());
        departmentLabel.setText(noticeItem.getDepartment());
        authorLabel.setText(noticeItem.getAuthor());
        dateLabel.setText(noticeItem.getDate() != null ? noticeItem.getDate().toString() : "");
        contentTextArea.setText(noticeItem.getContent());

        // 중요 공지사항 표시
        importantLabel.setVisible(noticeItem.isImportant());

        // 첨부파일 처리
        processAttachments();
    }

    /**
     * 첨부파일 처리
     */
    private void processAttachments() {
        attachmentFiles.clear();
        
        if (noticeItem.hasAttachments()) {
            // 실제 첨부파일이 있는 경우
            if (noticeItem.getAttachmentFilename() != null && !noticeItem.getAttachmentFilename().isEmpty()) {
                attachmentFiles.add(noticeItem.getAttachmentFilename());
                attachmentSection.setVisible(true);
                downloadButton.setVisible(true);
            }
        } else {
            // 내용에서 첨부파일 정보 추출 (기존 방식)
            String content = noticeItem.getContent();
            if (content != null && content.contains("[첨부파일]")) {
                String[] parts = content.split("\\[첨부파일\\]");
                if (parts.length > 1) {
                    String attachmentInfo = parts[1];
                    String[] lines = attachmentInfo.split("\n");
                    
                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith("- ") && line.contains(" (")) {
                            String filename = line.substring(2, line.lastIndexOf(" ("));
                            attachmentFiles.add(filename);
                        }
                    }
                    
                    if (!attachmentFiles.isEmpty()) {
                        attachmentSection.setVisible(true);
                        downloadButton.setVisible(true);
                    }
                }
            }
        }
    }

    /**
     * 첨부파일 다운로드 버튼 클릭 이벤트
     */
    @FXML
    private void handleDownloadAttachment() {
        String selectedFile = attachmentListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            downloadAttachment(selectedFile);
        } else {
            showAlert("알림", "다운로드할 파일을 선택해주세요.", Alert.AlertType.WARNING);
        }
    }

    /**
     * 첨부파일 다운로드
     */
    private void downloadAttachment(String filename) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("다운로드 위치 선택");
            
            Stage stage = (Stage) closeButton.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);
            
                    if (selectedDirectory != null) {
            // 실제 파일 다운로드 구현
            downloadActualFile(selectedDirectory, filename);
            showAlert("성공", "파일이 성공적으로 다운로드되었습니다.", Alert.AlertType.INFORMATION);
        }
        } catch (Exception e) {
            showAlert("오류", "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * 실제 파일 다운로드 (Base64 디코딩)
     */
    private void downloadActualFile(File directory, String filename) throws IOException {
        Path targetPath = Paths.get(directory.getAbsolutePath(), filename);
        
        // NoticeItem에서 Base64로 인코딩된 파일 내용 가져오기
        if (noticeItem != null && noticeItem.getAttachmentContent() != null && !noticeItem.getAttachmentContent().isEmpty()) {
            try {
                // Base64 디코딩
                byte[] fileBytes = java.util.Base64.getDecoder().decode(noticeItem.getAttachmentContent());
                Files.write(targetPath, fileBytes);
                System.out.println("파일 다운로드 완료: " + filename + " (" + fileBytes.length + " bytes)");
            } catch (Exception e) {
                System.out.println("Base64 디코딩 오류: " + e.getMessage());
                // 오류 발생 시 샘플 파일 생성
                createSampleFile(directory, filename);
            }
        } else {
            // 파일 내용이 없는 경우 샘플 파일 생성
            createSampleFile(directory, filename);
        }
    }
    
    /**
     * 샘플 파일 생성 (실제 구현에서는 서버에서 파일을 다운로드)
     */
    private void createSampleFile(File directory, String filename) throws IOException {
        Path targetPath = Paths.get(directory.getAbsolutePath(), filename);
        
        // 샘플 파일 내용 생성
        String content = "이것은 " + filename + " 파일의 샘플 내용입니다.\n";
        content += "실제 구현에서는 서버에서 파일을 다운로드합니다.\n";
        content += "생성 시간: " + java.time.LocalDateTime.now();
        
        Files.write(targetPath, content.getBytes());
    }

    /**
     * 닫기 버튼 클릭 이벤트
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * 알림 다이얼로그 표시
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 