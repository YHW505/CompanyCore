package com.example.companycore.controller.attendance;

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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        setupInitialData();
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
        // 사번 필드는 읽기 전용으로 설정
        employeeIdField.setEditable(false);
        employeeIdField.setText("2534001");
        
        // 휴가 종류 콤보박스 초기화
        leaveTypeComboBox.getItems().addAll("연차", "병가", "공가", "반차", "특별휴가");
        leaveTypeComboBox.setValue("연차");
        
        // 기본 날짜 설정
        startDatePicker.setValue(LocalDate.of(2025, 8, 1));
        endDatePicker.setValue(LocalDate.of(2025, 8, 3));
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
        
        // 휴가 요청 처리
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("휴가 요청");
        alert.setHeaderText(null);
        alert.setContentText("휴가 요청이 성공적으로 제출되었습니다.");
        alert.showAndWait();
        
        // 폼 초기화
        clearForm();
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
        
        // 사유 검증
        if (reasonTextArea.getText() == null || reasonTextArea.getText().trim().isEmpty()) {
            showError("휴가 사유를 입력해주세요.");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("입력 오류");
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
        leaveTypeComboBox.setValue("연차");
        startDatePicker.setValue(LocalDate.of(2025, 8, 1));
        endDatePicker.setValue(LocalDate.of(2025, 8, 3));
        reasonTextArea.clear();
        
        // 첨부파일 초기화
        attachedFiles.clear();
        fileListContainer.getChildren().clear();
    }
    
    private Stage getStage() {
        return (Stage) requestButton.getScene().getWindow();
    }
} 