package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.service.ApiClient;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.UserApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 등록/수정 다이얼로그 컨트롤러
 */
public class NoticeEditController {

    @FXML private TextField titleTextField;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private TextField authorTextField;
    @FXML private TextArea contentTextArea;
    @FXML private ListView<File> fileListView;
    @FXML private Button addFileButton;
    @FXML private Button removeFileButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ApiClient apiClient;
    private NoticeItem editingNotice; // 수정 모드일 때 사용
    private boolean isEditMode = false;
    private NoticeController parentController; // 부모 컨트롤러 참조
    private ObservableList<File> attachedFiles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        apiClient = ApiClient.getInstance();
        setupDepartmentComboBox();
        setupValidation();
        setupFileListView();
    }

    /**
     * 부서 콤보박스 설정
     */
    private void setupDepartmentComboBox() {
        departmentComboBox.getItems().addAll(
            "인사팀", "개발팀", "마케팅팀", "영업팀", "기획팀", "디자인팀", "운영팀", "기타"
        );
    }

    /**
     * 첨부파일 리스트뷰 설정
     */
    private void setupFileListView() {
        fileListView.setItems(attachedFiles);
        fileListView.setCellFactory(param -> new ListCell<File>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    setText(file.getName() + " (" + formatFileSize(file.length()) + ")");
                }
            }
        });
        
        // 파일 선택 시 삭제 버튼 활성화
        fileListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                removeFileButton.setDisable(newValue == null);
            }
        );
        removeFileButton.setDisable(true);
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
     * 입력 검증 설정
     */
    private void setupValidation() {
        // 제목 필드 검증
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateInputForUI();
        });

        // 부서 콤보박스 검증
        departmentComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateInputForUI();
        });

        // 작성자 필드 검증
        authorTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateInputForUI();
        });

        // 내용 필드 검증
        contentTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            validateInputForUI();
        });
    }

    /**
     * 입력 검증
     */
    private boolean validateInput() {
        if (titleTextField.getText() == null || titleTextField.getText().trim().isEmpty()) {
            return false;
        }
        if (departmentComboBox.getValue() == null) {
            return false;
        }
        if (authorTextField.getText() == null || authorTextField.getText().trim().isEmpty()) {
            return false;
        }
        if (contentTextArea.getText() == null || contentTextArea.getText().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 입력 검증 (UI 업데이트용)
     */
    private void validateInputForUI() {
        boolean isValid = validateInput();
        saveButton.setDisable(!isValid);
    }

    /**
     * 새 공지사항 등록 모드로 설정
     */
    public void setNewNoticeMode() {
        isEditMode = false;
        editingNotice = null;
        clearForm();
        saveButton.setText("등록");
        
        // 로그인한 사용자 정보로 부서와 작성자 자동 설정
        setCurrentUserInfo();
    }
    
    /**
     * 현재 로그인한 사용자 정보를 설정
     */
    private void setCurrentUserInfo() {
        try {
            // ApiClient를 통해 현재 로그인된 사용자 정보 가져오기
            User currentUser = apiClient.getCurrentUser();

            if (currentUser != null) {
                // 사용자 이름과 부서 설정
                authorTextField.setText(currentUser.getUsername());
                departmentComboBox.setValue(currentUser.getDepartmentName());

                // 필드 비활성화
                authorTextField.setDisable(true);
                departmentComboBox.setDisable(true);

                System.out.println("현재 사용자 정보 설정 완료: " + currentUser.getUsername() + " / " + currentUser.getDepartmentName());
            } else {
                // 사용자 정보를 가져올 수 없을 경우의 예외 처리
                System.out.println("현재 사용자 정보를 가져올 수 없습니다. 기본값을 사용합니다.");
                authorTextField.setText("사용자");
                departmentComboBox.setValue("기타"); // 기본값을 "기타"로 변경
                authorTextField.setDisable(true);
                departmentComboBox.setDisable(true);
            }
        } catch (Exception e) {
            System.err.println("사용자 정보 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류 발생 시 예외 처리
            authorTextField.setText("사용자");
            departmentComboBox.setValue("기타");
            authorTextField.setDisable(true);
            departmentComboBox.setDisable(true);
        }
    }

    /**
     * 기존 공지사항 수정 모드로 설정
     */
    public void setEditNoticeMode(NoticeItem notice) {
        isEditMode = true;
        editingNotice = notice;
        populateForm(notice);
        saveButton.setText("수정");
        
        // 수정 모드에서도 부서와 작성자 필드 비활성화
        departmentComboBox.setDisable(true);
        authorTextField.setDisable(true);
    }

    /**
     * 부모 컨트롤러 설정
     */
    public void setParentController(NoticeController parentController) {
        this.parentController = parentController;
    }

    /**
     * 폼 초기화
     */
    private void clearForm() {
        titleTextField.clear();
        departmentComboBox.setValue(null);
        authorTextField.clear();
        contentTextArea.clear();
        attachedFiles.clear();
    }

    /**
     * 폼에 데이터 채우기
     */
    private void populateForm(NoticeItem notice) {
        titleTextField.setText(notice.getTitle());
        departmentComboBox.setValue(notice.getDepartment());
        authorTextField.setText(notice.getAuthor());
        contentTextArea.setText(notice.getContent());
        // 첨부파일은 현재 API에서 지원하지 않으므로 빈 리스트로 설정
        attachedFiles.clear();
    }

    /**
     * 파일 추가 버튼 클릭 이벤트
     */
    @FXML
    private void handleAddFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        
        // 파일 필터 설정
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("모든 파일", "*.*"),
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.jpg", "*.jpeg", "*.png", "*.gif"),
            new FileChooser.ExtensionFilter("압축 파일", "*.zip", "*.rar")
        );
        
        Stage stage = (Stage) addFileButton.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                if (file.exists() && file.canRead()) {
                    // 파일 크기 제한 (50MB)
                    if (file.length() > 50 * 1024 * 1024) {
                        showAlert("파일 크기 제한", 
                                file.getName() + " 파일이 50MB를 초과합니다.", 
                                Alert.AlertType.WARNING);
                        continue;
                    }
                    
                    // 중복 파일 체크
                    boolean isDuplicate = attachedFiles.stream()
                            .anyMatch(existingFile -> existingFile.getName().equals(file.getName()));
                    
                    if (isDuplicate) {
                        showAlert("중복 파일", 
                                file.getName() + " 파일이 이미 첨부되어 있습니다.", 
                                Alert.AlertType.WARNING);
                        continue;
                    }
                    
                    attachedFiles.add(file);
                }
            }
        }
    }

    /**
     * 파일 삭제 버튼 클릭 이벤트
     */
    @FXML
    private void handleRemoveFile() {
        File selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            attachedFiles.remove(selectedFile);
        }
    }

    /**
     * 저장 버튼 클릭 이벤트
     */
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            showAlert("입력 오류", "필수 항목을 모두 입력해주세요.", Alert.AlertType.WARNING);
            return;
        }

        try {
            NoticeItem notice = new NoticeItem();
            notice.setTitle(titleTextField.getText().trim());
            notice.setDepartment(departmentComboBox.getValue());
            notice.setAuthor(authorTextField.getText().trim());
            notice.setContent(contentTextArea.getText().trim());
            notice.setDate(LocalDate.now());
            notice.setCreatedAt(LocalDateTime.now());
            notice.setUpdatedAt(LocalDateTime.now());
            
            // 첨부파일 정보 설정
            if (!attachedFiles.isEmpty()) {
                // 첫 번째 첨부파일 정보 설정 (실제로는 여러 파일을 지원해야 함)
                File firstFile = attachedFiles.get(0);
                notice.setAttachmentFilename(firstFile.getName());
                notice.setAttachmentContentType(getContentType(firstFile));
                notice.setAttachmentSize(firstFile.length());
                notice.setHasAttachments(true);
                
                // 실제 파일 내용을 Base64로 인코딩하여 저장
                try {
                    byte[] fileBytes = java.nio.file.Files.readAllBytes(firstFile.toPath());
                    String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
                    notice.setAttachmentContent(base64Content);
                    System.out.println("첨부파일 정보 설정: " + firstFile.getName() + " (" + formatFileSize(firstFile.length()) + ") - Base64 인코딩 완료");
                } catch (Exception e) {
                    System.out.println("파일 읽기 오류: " + e.getMessage());
                    notice.setHasAttachments(false);
                }
            } else {
                notice.setHasAttachments(false);
            }

            boolean success = false;
            String message = "";

            if (isEditMode && editingNotice != null) {
                // 수정 모드
                NoticeItem updatedNotice = apiClient.updateNotice(editingNotice.getNoticeId(), notice);
                if (updatedNotice != null) {
                    success = true;
                    message = "공지사항이 성공적으로 수정되었습니다.";
                } else {
                    message = "공지사항 수정에 실패했습니다.";
                }
            } else {
                // 등록 모드
                NoticeItem createdNotice = apiClient.createNotice(notice);
                System.out.println(createdNotice);
                if (createdNotice != null) {
                    success = true;
                    message = "공지사항이 성공적으로 등록되었습니다.";
                } else {
                    message = "공지사항 등록에 실패했습니다.";
                }
            }

            if (success) {
                showAlert("성공", message, Alert.AlertType.INFORMATION);
                
                // 부모 컨트롤러에 새로고침 요청
                if (parentController != null) {
                    // 부모 컨트롤러의 데이터 새로고침 메서드 호출
                    parentController.refreshData();
                }
                
                closeDialog();
            } else {
                showAlert("실패", message, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("오류", "처리 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 파일의 Content-Type 반환
     */
    private String getContentType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "application/msword";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 취소 버튼 클릭 이벤트
     */
    @FXML
    private void handleCancel() {
        closeDialog();
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

    /**
     * 다이얼로그 닫기
     */
    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 