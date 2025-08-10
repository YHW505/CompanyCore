package com.example.companycore.controller.attendance;

import com.example.companycore.model.entity.User;
import com.example.companycore.model.dto.LeaveRequestDto;
import com.example.companycore.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LeaveApplicationController implements Initializable {
    
    @FXML
    private ComboBox<String> leaveTypeComboBox;
    
    @FXML
    private TextField employeeIdField;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private TextArea reasonTextArea;
    
    @FXML
    private VBox fileListContainer;
    
    @FXML
    private Button addFileButton;
    
    @FXML
    private Button requestButton;
    
    @FXML
    private Button cancelButton;
    
    private List<File> attachedFiles = new ArrayList<>();
    private ApiClient apiClient;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apiClient = ApiClient.getInstance();
        setupEventHandlers();
        loadCurrentUser();
    }
    
    /**
     * 현재 로그인된 사용자 정보를 로드합니다.
     */
    private void loadCurrentUser() {
        try {
            System.out.println("현재 사용자 정보 로드 시작...");
            
            // 백그라운드에서 사용자 정보 로드
            javafx.concurrent.Task<User> loadUserTask = new javafx.concurrent.Task<User>() {
                @Override
                protected User call() throws Exception {
                    return apiClient.getCurrentUser();
                }
            };
            
            loadUserTask.setOnSucceeded(e -> {
                currentUser = loadUserTask.getValue();
                if (currentUser != null) {
                    setupInitialData();
                    System.out.println("사용자 정보 로드 완료: " + currentUser.getUsername());
                } else {
                    showError("사용자 정보를 불러올 수 없습니다.");
                }
            });
            
            loadUserTask.setOnFailed(e -> {
                showError("사용자 정보 로드 중 오류가 발생했습니다.");
            });
            
            new Thread(loadUserTask).start();
            
        } catch (Exception e) {
            showError("사용자 정보 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void setupEventHandlers() {
        // 파일 추가 버튼
        addFileButton.setOnAction(e -> handleAddFile());
        
        // 요청하기 버튼
        requestButton.setOnAction(e -> handleSubmitRequest());
        
        // 취소 버튼
        cancelButton.setOnAction(e -> handleCancel());
        
        // DatePicker는 기본적으로 달력 기능이 내장되어 있음
    }
    
    private void setupInitialData() {
        // 현재 사용자 정보로 사번 필드 설정
        if (currentUser != null) {
            employeeIdField.setText(currentUser.getEmployeeCode());
        }
        employeeIdField.setEditable(false);
        
        // 휴가 종류 콤보박스 초기화
        leaveTypeComboBox.getItems().addAll("연차", "반차", "병가", "개인사유", "공가", "출산휴가", "육아휴가", "특별휴가");
        leaveTypeComboBox.setValue(null);
        
        // 기본 날짜 설정 - 빈 상태로 초기화
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
    
    private void handleAddFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("파일 선택");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("모든 파일", "*.*"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"),
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx", "*.txt")
        );
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        if (selectedFile != null) {
            attachedFiles.add(selectedFile);
            addFileToList(selectedFile);
        }
    }
    
         
    
    private void addFileToList(File file) {
        HBox fileItem = new HBox(10);
        fileItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        fileItem.setStyle("-fx-padding: 8 0;");
        
        // 파일 아이콘
        Label fileIcon = new Label("📄");
        fileIcon.setStyle("-fx-font-size: 16px;");
        
        // 파일명
        Label fileName = new Label(file.getName());
        fileName.setStyle("-fx-text-fill: #495057;");
        
        // 스페이서
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // 액션 버튼들
        Button linkButton = new Button("🔗");
        linkButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand; -fx-padding: 5;");
        linkButton.setOnAction(e -> handleFileLink(file));
        
        Button previewButton = new Button("📋");
        previewButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #28a745; -fx-cursor: hand; -fx-padding: 5;");
        previewButton.setOnAction(e -> handleFilePreview(file));
        
        Button deleteButton = new Button("✕");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-cursor: hand; -fx-padding: 5;");
        deleteButton.setOnAction(e -> handleFileDelete(file, fileItem));
        
        fileItem.getChildren().addAll(fileIcon, fileName, spacer, linkButton, previewButton, deleteButton);
        fileListContainer.getChildren().add(fileItem);
    }
    
    private void handleFileLink(File file) {
        // 파일 링크 복사 기능
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("파일 링크");
        alert.setHeaderText(null);
        alert.setContentText("파일 링크가 클립보드에 복사되었습니다.");
        alert.showAndWait();
    }
    
    private void handleFilePreview(File file) {
        // 파일 미리보기 기능
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("파일 미리보기");
        alert.setHeaderText(null);
        alert.setContentText("파일 미리보기 기능이 실행됩니다: " + file.getName());
        alert.showAndWait();
    }
    
    private void handleFileDelete(File file, HBox fileItem) {
        // 파일 삭제 확인
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("파일 삭제");
        alert.setHeaderText(null);
        alert.setContentText("파일을 삭제하시겠습니까?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                attachedFiles.remove(file);
                fileListContainer.getChildren().remove(fileItem);
            }
        });
    }
    

    
    private void handleSubmitRequest() {
        // 폼 검증
        if (!validateForm()) {
            return;
        }
        
        // 휴가 요청 DTO 생성
        LeaveRequestDto leaveRequest = createLeaveRequestDto();
        
        // 백그라운드에서 휴가 요청 제출
        javafx.concurrent.Task<LeaveRequestDto> submitTask = new javafx.concurrent.Task<LeaveRequestDto>() {
            @Override
            protected LeaveRequestDto call() throws Exception {
                return apiClient.createLeaveRequest(leaveRequest);
            }
        };
        
        submitTask.setOnSucceeded(e -> {
            LeaveRequestDto createdRequest = submitTask.getValue();
            if (createdRequest != null) {
                showSuccess("휴가 요청이 성공적으로 제출되었습니다.");
                clearForm();
            } else {
                showError("휴가 요청 제출에 실패했습니다.");
            }
        });
        
        submitTask.setOnFailed(e -> {
            showError("휴가 요청 제출 중 오류가 발생했습니다: " + submitTask.getException().getMessage());
        });
        
        new Thread(submitTask).start();
    }
    
    /**
     * 휴가 요청 DTO를 생성합니다.
     */
    private LeaveRequestDto createLeaveRequestDto() {
        LeaveRequestDto leaveRequest = new LeaveRequestDto();
        
        // 사용자 ID 설정
        if (currentUser != null) {
            leaveRequest.setEmployeeId(currentUser.getUserId().toString());
        }
        
        // 휴가 종류 설정
        String leaveType = leaveTypeComboBox.getValue();
        switch (leaveType) {
            case "연차":
                leaveRequest.setLeaveType("ANNUAL");
                break;
            case "반차":
                leaveRequest.setLeaveType("HALF_DAY");
                break;
            case "병가":
                leaveRequest.setLeaveType("SICK");
                break;
            case "개인사유":
                leaveRequest.setLeaveType("PERSONAL");
                break;
            case "공가":
                leaveRequest.setLeaveType("OFFICIAL");
                break;
            case "출산휴가":
                leaveRequest.setLeaveType("MATERNITY");
                break;
            case "육아휴가":
                leaveRequest.setLeaveType("PATERNITY");
                break;
            case "특별휴가":
                leaveRequest.setLeaveType("SPECIAL");
                break;
            default:
                leaveRequest.setLeaveType("ANNUAL");
        }
        
        // 날짜 설정 - 문자열 형식으로 변환
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null) {
            leaveRequest.setStartDate(startDate);
        }
        if (endDate != null) {
            leaveRequest.setEndDate(endDate);
        }
        
        // 사유 설정
        leaveRequest.setReason(reasonTextArea.getText().trim());
        
        // 상태는 PENDING으로 설정 (서버에서 처리)
        leaveRequest.setStatus("PENDING");
        
        return leaveRequest;
    }
    
    private boolean validateForm() {
        // 휴가 종류 검증
        if (leaveTypeComboBox.getValue() == null || leaveTypeComboBox.getValue().isEmpty()) {
            showError("휴가 종류를 선택해주세요.");
            return false;
        }
        
        // 시작일 검증
        if (startDatePicker.getValue() == null) {
            showError("시작일을 선택해주세요.");
            return false;
        }
        
        // 종료일 검증
        if (endDatePicker.getValue() == null) {
            showError("종료일을 선택해주세요.");
            return false;
        }
        
        // 시작일이 종료일보다 늦은 경우
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showError("시작일은 종료일보다 빠를 수 없습니다.");
            return false;
        }
        
        // 시작일이 오늘보다 이전인 경우
        if (startDatePicker.getValue().isBefore(LocalDate.now())) {
            showError("시작일은 오늘 이후로 설정해주세요.");
            return false;
        }
        
        // 사유 검증
        if (reasonTextArea.getText() == null || reasonTextArea.getText().trim().isEmpty()) {
            showError("휴가 사유를 입력해주세요.");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("성공");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void handleCancel() {
        // 취소 확인
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("작성 취소");
        alert.setHeaderText(null);
        alert.setContentText("작성 중인 내용이 모두 사라집니다. 정말 취소하시겠습니까?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearForm();
            }
        });
    }
    
    private void clearForm() {
        // 폼 초기화
        leaveTypeComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reasonTextArea.clear();
        
        // 첨부파일 초기화
        attachedFiles.clear();
        fileListContainer.getChildren().clear();
    }
    
    private Stage getStage() {
        return (Stage) requestButton.getScene().getWindow();
    }
} 