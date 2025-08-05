package com.example.companycore.controller.tasks;

import com.example.companycore.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ApprovalRequestFormController {

    @FXML private TextField titleField;
    @FXML private Label departmentLabel;
    @FXML private TextArea contentArea;
    @FXML private VBox dropZone;
    @FXML private Button addFileBtn;
    @FXML private Button removeFileBtn;
    @FXML private ListView<String> attachmentList;
    @FXML private Button cancelBtn;
    @FXML private Button submitBtn;

    private File selectedFile;
    private String attachmentContent;
    private Long attachmentSize;
    private ApiClient apiClient;

    @FXML
    public void initialize() {
        // ApiClient 인스턴스 가져오기
        this.apiClient = ApiClient.getInstance();
        
        // 현재 사용자의 부서 정보 설정
        setCurrentUserDepartment();
        
        // 첨부파일 관련 초기화
        attachmentList.setVisible(false);
        attachmentList.setManaged(false);
    }
    
    /**
     * 현재 사용자의 부서 정보를 설정합니다.
     */
    private void setCurrentUserDepartment() {
        try {
            var currentUser = apiClient.getCurrentUser();
            if (currentUser != null && currentUser.getDepartmentName() != null) {
                departmentLabel.setText(currentUser.getDepartmentName());
            } else {
                departmentLabel.setText("부서 정보 없음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            departmentLabel.setText("부서 정보 로드 실패");
        }
    }

    @FXML
    private void onAddFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("모든 파일", "*.*"),
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        Stage stage = (Stage) addFileBtn.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // 파일 내용을 Base64로 인코딩
                byte[] fileContent = Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
                attachmentContent = Base64.getEncoder().encodeToString(fileContent);
                attachmentSize = selectedFile.length();

                // ListView에 파일명 표시
                attachmentList.setVisible(true);
                attachmentList.setManaged(true);
                attachmentList.setItems(FXCollections.observableArrayList(selectedFile.getName()));

                // 버튼 상태 변경
                removeFileBtn.setDisable(false);
                addFileBtn.setText("파일 변경");

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "파일 읽기 실패: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void onRemoveFile() {
        selectedFile = null;
        attachmentContent = null;
        attachmentSize = null;

        // ListView 숨기기
        attachmentList.setVisible(false);
        attachmentList.setManaged(false);
        attachmentList.getItems().clear();

        // 버튼 상태 변경
        removeFileBtn.setDisable(true);
        addFileBtn.setText("파일 선택");
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSubmit() {
        // 입력 검증
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "제목을 입력해주세요.").showAndWait();
            return;
        }

        // 부서는 현재 사용자 정보에서 자동으로 설정되므로 검증 불필요

        if (contentArea.getText() == null || contentArea.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "내용을 입력해주세요.").showAndWait();
            return;
        }

        // 결재 요청 데이터 생성
        try {
            // 여기서 서버로 데이터를 전송하는 로직을 구현할 수 있습니다
            // 현재는 성공 메시지만 표시
            new Alert(Alert.AlertType.INFORMATION, "결재 요청이 성공적으로 제출되었습니다.").showAndWait();
            
            // 창 닫기
            Stage stage = (Stage) submitBtn.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "결재 요청 제출 실패: " + e.getMessage()).showAndWait();
        }
    }

    // 파일 크기 포맷팅 유틸리티 메서드
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
} 