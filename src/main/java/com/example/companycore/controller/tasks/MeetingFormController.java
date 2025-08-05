package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.MeetingItem;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MeetingApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 회의 등록 폼 컨트롤러
 */
public class MeetingFormController {

    @FXML private DatePicker datePicker;
    @FXML private TextField titleField;
    @FXML private VBox dropZone;
    @FXML private Button addFileBtn;
    @FXML private Button removeFileBtn;
    @FXML private ListView<String> attachmentList;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    private ApiClient apiClient;
    private File selectedFile;
    private String attachmentContent;
    private Long attachmentSize;
    private MeetingListController parentController;

    @FXML
    public void initialize() {
        apiClient = ApiClient.getInstance();
        setupForm();
        setupFileHandling();
    }

    /**
     * 폼 초기 설정
     */
    private void setupForm() {
        // 오늘 날짜로 초기화
        datePicker.setValue(LocalDate.now());
        
        // 입력 검증
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateFormForUI();
        });
        
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateFormForUI();
        });
    }

    /**
     * 파일 처리 설정
     */
    private void setupFileHandling() {
        attachmentList.setVisible(false);
        attachmentList.setManaged(false);
        removeFileBtn.setDisable(true);
    }

    /**
     * 폼 검증 (UI 업데이트용)
     */
    private void validateFormForUI() {
        if (submitBtn == null) {
            return; // submitBtn이 아직 초기화되지 않았으면 무시
        }
        boolean isValid = !titleField.getText().trim().isEmpty() && 
                         datePicker.getValue() != null;
        submitBtn.setDisable(!isValid);
    }

    /**
     * 파일 추가 버튼 클릭
     */
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
                byte[] fileContent = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                attachmentContent = Base64.getEncoder().encodeToString(fileContent);
                attachmentSize = selectedFile.length();

                // ListView에 파일명 표시
                attachmentList.setVisible(true);
                attachmentList.setManaged(true);
                attachmentList.setItems(FXCollections.observableArrayList(selectedFile.getName()));

                // 버튼 상태 변경
                removeFileBtn.setDisable(false);
                addFileBtn.setText("파일 변경");

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "파일 읽기 실패: " + e.getMessage()).showAndWait();
            }
        }
    }

    /**
     * 파일 제거 버튼 클릭
     */
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

    /**
     * 등록 버튼 클릭
     */
    @FXML
    private void onSubmit() {
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "필수 항목을 모두 입력해주세요.").showAndWait();
            return;
        }

        try {
            // 현재 사용자 정보 가져오기
            var currentUser = apiClient.getCurrentUser();
            String author = currentUser != null ? currentUser.getUsername() : "Unknown";
            String department = currentUser != null ? currentUser.getDepartmentName() : "Unknown";

            // MeetingDto 생성
            MeetingApiClient.MeetingDto meetingDto = new MeetingApiClient.MeetingDto();
            meetingDto.setTitle(titleField.getText().trim());
            meetingDto.setDescription("회의 내용"); // 기본값 설정
            meetingDto.setStartTime(LocalDateTime.now()); // 기본값 설정
            meetingDto.setEndTime(LocalDateTime.now().plusHours(1)); // 기본값 설정
            meetingDto.setLocation("회의실"); // 기본값 설정
            meetingDto.setDepartment(department);
            meetingDto.setAuthor(author);
            
            // 첨부파일 정보 설정 (MeetingDto에는 attachmentPath만 있음)
            if (selectedFile != null) {
                meetingDto.setAttachmentPath(selectedFile.getName());
            }

            // API 호출하여 회의 등록
            MeetingApiClient.MeetingDto createdMeeting = apiClient.createMeeting(meetingDto);

            if (createdMeeting != null) {
                new Alert(Alert.AlertType.INFORMATION, "회의가 성공적으로 등록되었습니다.").showAndWait();
                
                // 부모 컨트롤러에 새로고침 요청
                if (parentController != null) {
                    parentController.loadMeetingsFromDatabase();
                }
                
                closeDialog();
            } else {
                new Alert(Alert.AlertType.ERROR, "회의 등록에 실패했습니다.").showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "회의 등록 중 오류가 발생했습니다: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * 취소 버튼 클릭
     */
    @FXML
    private void onCancel() {
        closeDialog();
    }

    /**
     * 부모 컨트롤러 설정
     */
    public void setParentController(MeetingListController parentController) {
        this.parentController = parentController;
    }

    /**
     * 다이얼로그 닫기
     */
    private void closeDialog() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * 폼 검증
     */
    private boolean validateForm() {
        return !titleField.getText().trim().isEmpty() && 
               datePicker.getValue() != null;
    }
}