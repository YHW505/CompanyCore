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
        System.out.println("🔍 NoticeDetailController.setNoticeItem 호출됨");
        System.out.println("  - notice: " + (notice != null ? "not null" : "null"));
        if (notice != null) {
            System.out.println("  - noticeId: " + notice.getNoticeId());
            System.out.println("  - title: " + notice.getTitle());
            System.out.println("  - department: " + notice.getDepartment());
            System.out.println("  - author: " + notice.getAuthor());
            System.out.println("  - content length: " + (notice.getContent() != null ? notice.getContent().length() : "null"));
        }
        
        this.noticeItem = notice;
        populateData();
    }

    /**
     * 데이터 채우기
     */
    private void populateData() {
        System.out.println("🔍 populateData 호출됨");
        if (noticeItem == null) {
            System.out.println("❌ noticeItem이 null입니다.");
            return;
        }

        System.out.println("  - 제목: " + noticeItem.getTitle());
        System.out.println("  - 부서: " + noticeItem.getDepartment());
        System.out.println("  - 작성자: " + noticeItem.getAuthor());
        System.out.println("  - 날짜: " + noticeItem.getDate());
        System.out.println("  - 내용 길이: " + (noticeItem.getContent() != null ? noticeItem.getContent().length() : "null"));
        System.out.println("  - 중요 여부: " + noticeItem.isImportant());

        // UI 컴포넌트가 null인지 확인
        if (titleLabel == null) {
            System.out.println("❌ titleLabel이 null입니다.");
            return;
        }
        if (departmentLabel == null) {
            System.out.println("❌ departmentLabel이 null입니다.");
            return;
        }
        if (authorLabel == null) {
            System.out.println("❌ authorLabel이 null입니다.");
            return;
        }
        if (dateLabel == null) {
            System.out.println("❌ dateLabel이 null입니다.");
            return;
        }
        if (contentTextArea == null) {
            System.out.println("❌ contentTextArea가 null입니다.");
            return;
        }

        titleLabel.setText(noticeItem.getTitle());
        departmentLabel.setText(noticeItem.getDepartment());
        authorLabel.setText(noticeItem.getAuthor());
        dateLabel.setText(noticeItem.getDate() != null ? noticeItem.getDate().toString() : "");
        contentTextArea.setText(noticeItem.getContent());

        // 중요 공지사항 표시
        importantLabel.setVisible(noticeItem.isImportant());

        // 첨부파일 처리
        processAttachments();
        
        System.out.println("✅ populateData 완료");
    }

    /**
     * 첨부파일 처리
     */
    private void processAttachments() {
        attachmentFiles.clear();

        // 사용자의 요청에 따라 hasAttachments 플래그를 무시하고 항상 첨부파일 필드를 확인하도록 수정
        if (noticeItem.getAttachmentFilename() != null && !noticeItem.getAttachmentFilename().isEmpty()) {
            String filename = noticeItem.getAttachmentFilename();
            Long attachmentSize = noticeItem.getAttachmentSize();

            // 디버깅 로그
            System.out.println("첨부파일 강제 처리 모드: " + filename + ", 크기: " + attachmentSize);

            String fileSize;
            if (attachmentSize != null && attachmentSize > 0) {
                fileSize = formatFileSize(attachmentSize);
            } else {
                // 크기 정보가 없으면 Base64 내용에서 크기 계산
                if (noticeItem.getAttachmentContent() != null && !noticeItem.getAttachmentContent().isEmpty()) {
                    try {
                        byte[] fileBytes = java.util.Base64.getDecoder().decode(noticeItem.getAttachmentContent());
                        fileSize = formatFileSize(fileBytes.length);
                        System.out.println("Base64에서 계산된 크기: " + fileBytes.length + " bytes");
                    } catch (Exception e) {
                        fileSize = "크기 정보 없음";
                        System.out.println("Base64 디코딩 실패: " + e.getMessage());
                    }
                } else {
                    fileSize = "크기 정보 없음";
                }
            }

            attachmentFiles.add(filename);
            attachmentSection.setVisible(true);
            downloadButton.setVisible(true);
        } else {
            // 첨부파일 이름이 없는 경우, 첨부파일 섹션을 숨깁니다.
            attachmentSection.setVisible(false);
            downloadButton.setVisible(false);
        }
    }
    
    /**
     * 파일 크기 포맷팅
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * 첨부파일 다운로드 버튼 클릭 이벤트
     */
    @FXML
    private void handleDownloadAttachment() {
        String selectedFile = attachmentListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            // 파일명에서 크기 정보 제거
            String filename = extractFilename(selectedFile);
            downloadAttachment(filename);
        } else {
            showAlert("알림", "다운로드할 파일을 선택해주세요.", Alert.AlertType.WARNING);
        }
    }
    
    /**
     * 파일명에서 크기 정보 제거
     */
    private String extractFilename(String displayText) {
        if (displayText.contains(" (")) {
            return displayText.substring(0, displayText.lastIndexOf(" ("));
        }
        return displayText;
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
                System.out.println("파일 다운로드 완료: " + filename + " (" + fileBytes.length + " bytes) - Base64 내용 생략");
            } catch (Exception e) {
                System.out.println("Base64 디코딩 오류: " + e.getMessage());
                throw new IOException("파일 내용을 디코딩할 수 없습니다: " + e.getMessage());
            }
        } else {
            // 파일 내용이 없는 경우
            throw new IOException("첨부파일 내용이 없습니다.");
        }
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