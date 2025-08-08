package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.ApprovalItem;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ApprovalDetailController {

    @FXML private Label titleLabel;
    @FXML private Label departmentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;
    @FXML private Text contentText;
    @FXML private VBox attachmentContainer;
    @FXML private VBox attachmentList;

    private ApprovalItem approvalItem;
//    private boolean isProgress = true;

//    public boolean getIsProgress(){
//        return isProgress;
//    }


    @FXML
    public void initialize() {
        // 초기화 로직
    }

    /**
     * 결재 아이템을 설정하고 UI를 업데이트합니다.
     * 
     * @param item 표시할 결재 아이템
     */
    public void setApprovalItem(ApprovalItem item) {
        this.approvalItem = item;
        // 서버에서 상세 정보를 가져와서 UI 업데이트
        loadDetailFromServer();
    }



    /**
     * 서버에서 상세 정보를 가져와서 UI를 업데이트합니다.
     */
    private void loadDetailFromServer() {
        if (approvalItem == null) return;
        
        try {
            // 서버에서 상세 정보 가져오기
            com.example.companycore.service.ApprovalApiClient approvalApiClient = 
                com.example.companycore.service.ApprovalApiClient.getInstance();
            
            com.example.companycore.model.dto.ApprovalDto detailDto = 
                approvalApiClient.getApprovalById(approvalItem.getServerId());
            
            if (detailDto != null) {
                // 상세 정보로 ApprovalItem 업데이트
                this.approvalItem = com.example.companycore.model.dto.ApprovalItem.fromApprovalDto(detailDto);
                updateUI();
            } else {
                // 서버에서 정보를 가져올 수 없는 경우 기본 정보로 표시
                updateUI();
            }
        } catch (Exception e) {
            System.err.println("상세 정보 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
            // 오류 발생 시 기본 정보로 표시
            updateUI();
        }
    }

    /**
     * UI를 결재 데이터로 업데이트합니다.
     */
    private void updateUI() {
        if (approvalItem == null) return;

        // 기본 정보 설정
        titleLabel.setText(approvalItem.getTitle());
        departmentLabel.setText(approvalItem.getDepartment());
        authorLabel.setText(approvalItem.getAuthor());
        dateLabel.setText(approvalItem.getDate());
        statusLabel.setText(approvalItem.getStatusKorean());

        // 상태에 따른 색상 설정
        setStatusColor(approvalItem.getStatus());

        // 내용 설정
        contentText.setText(approvalItem.getContent() != null ? approvalItem.getContent() : "내용 없음");

        // 첨부파일 처리
        processAttachments();
    }

    /**
     * 상태에 따른 색상을 설정합니다.
     * 
     * @param status 상태
     */
    private void setStatusColor(String status) {
        if (status == null) return;

        switch (status) {
            case "승인됨":
                statusLabel.setStyle("-fx-background-color: #d4edda; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #c3e6cb; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #155724; -fx-font-weight: bold;");
                break;
            case "거부됨":
                statusLabel.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #f5c6cb; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                break;
            case "대기중":
            default:
                statusLabel.setStyle("-fx-background-color: #fff3cd; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ffeaa7; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #856404; -fx-font-weight: bold;");
                break;
        }
    }

    /**
     * 첨부파일을 처리하고 UI에 표시합니다.
     */
    private void processAttachments() {
        if (approvalItem == null) return;

        String attachmentFilename = approvalItem.getAttachmentFilename();
        Long attachmentSize = approvalItem.getAttachmentSize();

        // 디버깅 정보 출력
        System.out.println("🔍 첨부파일 정보 확인:");
        System.out.println("  - 파일명: " + attachmentFilename);
        System.out.println("  - 파일 크기: " + attachmentSize);
        System.out.println("  - 첨부파일 내용 존재: " + (approvalItem.getAttachmentContent() != null && !approvalItem.getAttachmentContent().isEmpty()));

        // 첨부파일이 있는 경우 표시 (파일명이나 크기가 있으면)
        if ((attachmentFilename != null && !attachmentFilename.isEmpty()) || 
            (attachmentSize != null && attachmentSize > 0)) {
            
            attachmentContainer.setVisible(true);
            attachmentList.getChildren().clear();

            // 실제 파일명 사용 (파일명이 없으면 기본값 사용)
            String filename = attachmentFilename != null && !attachmentFilename.isEmpty() 
                ? attachmentFilename 
                : "첨부파일";

            // 첨부파일 항목 생성
            HBox attachmentItem = createAttachmentItem(filename, attachmentSize);
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

        // 파일 정보 (파일명과 크기를 한 줄에 표시)
        HBox fileInfo = new HBox(8);
        Label nameLabel = new Label(filename);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        String sizeText = formatFileSize(fileSize != null ? fileSize : 0);
        Label sizeLabel = new Label("(" + sizeText + ")");
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
        if (approvalItem == null) {
            showAlert("오류", "결재 정보가 없습니다.");
            return;
        }

        try {
            // 다운로드 위치 선택
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("다운로드 위치 선택");
            File selectedDirectory = directoryChooser.showDialog(getStage());
            
            if (selectedDirectory != null) {
                // 먼저 로컬에서 첨부파일 내용 확인
                String attachmentContent = approvalItem.getAttachmentContent();
                
                if (attachmentContent != null && !attachmentContent.trim().isEmpty()) {
                    // 로컬에 내용이 있으면 바로 다운로드
                    downloadActualFile(selectedDirectory, filename, attachmentContent);
                    showAlert("성공", "파일이 성공적으로 다운로드되었습니다.");
                } else {
                    // 로컬에 내용이 없으면 서버에서 가져오기
                    loadAttachmentContentAndDownload(selectedDirectory, filename);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 서버에서 첨부파일 내용을 가져와서 다운로드합니다.
     * 
     * @param directory 다운로드 디렉토리
     * @param filename 파일명
     */
    private void loadAttachmentContentAndDownload(File directory, String filename) {
        // API를 사용하지 않고 로컬 데이터만 사용
        System.err.println("❌ 첨부파일 내용이 로컬에 없습니다.");
        showAlert("오류", "첨부파일 내용이 없습니다.\n\n업로드 시 첨부파일 내용이 저장되지 않았습니다.");
    }

    /**
     * 실제 파일 다운로드 (Base64 디코딩)
     */
    private void downloadActualFile(File directory, String filename, String attachmentContent) throws IOException {
        Path targetPath = Paths.get(directory.getAbsolutePath(), filename);
        
        // 서버에서 가져온 Base64로 인코딩된 파일 내용 사용
        if (attachmentContent != null && !attachmentContent.trim().isEmpty()) {
            try {
                // Base64 디코딩
                byte[] fileBytes = java.util.Base64.getDecoder().decode(attachmentContent);
                Files.write(targetPath, fileBytes);
                                 System.out.println("✅ 첨부파일 다운로드 완료: " + filename + " (" + fileBytes.length + " bytes) - Base64 내용 생략");
            } catch (Exception e) {
                System.out.println("Base64 디코딩 오류: " + e.getMessage());
                throw new IOException("첨부파일 내용을 디코딩할 수 없습니다: " + e.getMessage());
            }
        } else {
            // 파일 내용이 없는 경우 오류 발생
            throw new IOException("첨부파일 내용이 없습니다.");
        }
    }

    /**
     * Base64 문자열이 유효한지 확인합니다.
     * 
     * @param str 확인할 문자열
     * @return 유효한 Base64인 경우 true
     */
    private boolean isValidBase64(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Base64 디코딩 시도
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
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