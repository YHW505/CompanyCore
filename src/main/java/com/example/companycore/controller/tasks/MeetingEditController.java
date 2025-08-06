package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.MeetingItem;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MeetingApiClient;
import com.example.companycore.util.FileUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 회의 수정 폼 컨트롤러
 */
public class MeetingEditController {

    @FXML private DatePicker datePicker;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField locationField;
    @FXML private VBox dropZone;
    @FXML private Button addFileBtn;
    @FXML private Button removeFileBtn;
    @FXML private ListView<String> attachmentList;
    @FXML private Label fileInfoLabel;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    private ApiClient apiClient;
    private MeetingApiClient meetingApiClient;
    private File selectedFile;
    private String attachmentContent;
    private String attachmentFilename;
    private String attachmentContentType;
    private Long attachmentSize;
    private MeetingItem originalMeeting;
    private MeetingListController parentController;

    @FXML
    public void initialize() {
        apiClient = ApiClient.getInstance();
        meetingApiClient = MeetingApiClient.getInstance();
        setupForm();
        setupFileHandling();
    }

    /**
     * 폼 초기 설정
     */
    private void setupForm() {
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
        fileInfoLabel.setVisible(false);
        fileInfoLabel.setManaged(false);
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
     * 기존 회의 데이터로 폼을 초기화합니다.
     * 
     * @param meeting 수정할 회의 데이터
     */
    public void setMeetingData(MeetingItem meeting) {
        this.originalMeeting = meeting;
        
        // 기존 데이터로 폼 초기화
        titleField.setText(meeting.getTitle());
        descriptionField.setText("회의 내용"); // 기본값
        locationField.setText("회의실"); // 기본값
        datePicker.setValue(LocalDate.parse(meeting.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 기존 첨부파일이 있으면 표시
        if (meeting.getAttachmentFilename() != null && !meeting.getAttachmentFilename().isEmpty()) {
            attachmentFilename = meeting.getAttachmentFilename();
            attachmentContentType = meeting.getAttachmentContentType();
            attachmentSize = meeting.getAttachmentSize();
            attachmentContent = meeting.getAttachmentContent();
            
            // UI 업데이트
            attachmentList.setVisible(true);
            attachmentList.setManaged(true);
            attachmentList.setItems(FXCollections.observableArrayList(attachmentFilename));
            
            fileInfoLabel.setVisible(true);
            fileInfoLabel.setManaged(true);
            fileInfoLabel.setText(String.format("기존 파일: %s | 크기: %s | 타입: %s", 
                attachmentFilename, 
                FileUtil.formatFileSize(attachmentSize != null ? attachmentSize : 0), 
                attachmentContentType != null ? attachmentContentType : "알 수 없음"));
            
            removeFileBtn.setDisable(false);
            addFileBtn.setText("파일 변경");
        }
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
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.jpg", "*.jpeg", "*.png", "*.gif"),
            new FileChooser.ExtensionFilter("압축 파일", "*.zip", "*.rar")
        );

        Stage stage = (Stage) addFileBtn.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // 파일 유효성 검사
                if (!FileUtil.isValidFilename(selectedFile.getName())) {
                    new Alert(Alert.AlertType.ERROR, "유효하지 않은 파일명입니다.").showAndWait();
                    return;
                }

                if (!FileUtil.isFileSizeValid(selectedFile, 10)) {
                    new Alert(Alert.AlertType.ERROR, "파일 크기가 너무 큽니다. (최대 10MB)").showAndWait();
                    return;
                }

                // 파일 내용을 Base64로 인코딩
                attachmentContent = FileUtil.encodeFileToBase64(selectedFile);
                attachmentFilename = selectedFile.getName();
                attachmentContentType = FileUtil.getMimeType(selectedFile.getName());
                attachmentSize = selectedFile.length();

                // ListView에 파일명 표시
                attachmentList.setVisible(true);
                attachmentList.setManaged(true);
                attachmentList.setItems(FXCollections.observableArrayList(selectedFile.getName()));

                // 파일 정보 표시
                fileInfoLabel.setVisible(true);
                fileInfoLabel.setManaged(true);
                fileInfoLabel.setText(String.format("새 파일: %s | 크기: %s | 타입: %s", 
                    attachmentFilename, 
                    FileUtil.formatFileSize(attachmentSize), 
                    attachmentContentType));

                // 버튼 상태 변경
                removeFileBtn.setDisable(false);
                addFileBtn.setText("파일 변경");

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "파일 읽기 실패: " + e.getMessage()).showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "파일 처리 중 오류가 발생했습니다: " + e.getMessage()).showAndWait();
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
        attachmentFilename = null;
        attachmentContentType = null;
        attachmentSize = null;

        // ListView 숨기기
        attachmentList.setVisible(false);
        attachmentList.setManaged(false);
        attachmentList.getItems().clear();

        // 파일 정보 숨기기
        fileInfoLabel.setVisible(false);
        fileInfoLabel.setManaged(false);

        // 버튼 상태 변경
        removeFileBtn.setDisable(true);
        addFileBtn.setText("파일 선택");
    }

    /**
     * 수정 버튼 클릭
     */
    @FXML
    private void onSubmit() {
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "필수 항목을 모두 입력해주세요.").showAndWait();
            return;
        }

        try {
            // 회의 시간 설정
            LocalDate selectedDate = datePicker.getValue();
            LocalDateTime startTime = selectedDate.atStartOfDay().plusHours(9); // 오전 9시
            LocalDateTime endTime = startTime.plusHours(1); // 1시간 회의

            MeetingApiClient.MeetingDto updatedMeeting;

            if (selectedFile != null) {
                // 새 첨부파일이 있는 경우 - 기존 메서드 사용
                MeetingApiClient.MeetingDto meetingDto = new MeetingApiClient.MeetingDto();
                meetingDto.setTitle(titleField.getText().trim());
                meetingDto.setDescription(descriptionField.getText().trim());
                meetingDto.setStartTime(startTime);
                meetingDto.setEndTime(endTime);
                meetingDto.setLocation(locationField.getText().trim());
                meetingDto.setAttachmentFilename(attachmentFilename);
                meetingDto.setAttachmentContentType(attachmentContentType);
                meetingDto.setAttachmentSize(attachmentSize);
                meetingDto.setAttachmentContent(attachmentContent);

                updatedMeeting = meetingApiClient.updateMeeting(1L, meetingDto); // 임시로 1L 사용
            } else {
                // 첨부파일이 없는 경우 기존 메서드 사용
                MeetingApiClient.MeetingDto meetingDto = new MeetingApiClient.MeetingDto();
                meetingDto.setTitle(titleField.getText().trim());
                meetingDto.setDescription(descriptionField.getText().trim());
                meetingDto.setStartTime(startTime);
                meetingDto.setEndTime(endTime);
                meetingDto.setLocation(locationField.getText().trim());

                updatedMeeting = meetingApiClient.updateMeeting(1L, meetingDto); // 임시로 1L 사용
            }

            if (updatedMeeting != null) {
                new Alert(Alert.AlertType.INFORMATION, "회의가 성공적으로 수정되었습니다.").showAndWait();
                
                // 부모 컨트롤러에 새로고침 요청
                if (parentController != null) {
                    parentController.loadMeetingsFromDatabase();
                }
                
                closeDialog();
            } else {
                new Alert(Alert.AlertType.ERROR, "회의 수정에 실패했습니다.").showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "회의 수정 중 오류가 발생했습니다: " + e.getMessage()).showAndWait();
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