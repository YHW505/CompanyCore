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
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.util.Optional;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import com.example.companycore.model.entity.User;

/**
 * 회의 등록 폼 컨트롤러
 */
public class MeetingFormController {

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
    
    // 업무 캘린더 관련 필드들
    @FXML private Button addScheduleBtn;
    @FXML private VBox scheduleContainer;
    
    // 첫 번째 일정 관련 필드들
    @FXML private DatePicker startDatePicker1;
    @FXML private DatePicker endDatePicker1;
    @FXML private TextArea scheduleContentArea1;
    @FXML private Button addParticipantBtn1;
    @FXML private ListView<String> participantList1;

    private ApiClient apiClient;
    private MeetingApiClient meetingApiClient;
    private File selectedFile;
    private String attachmentContent;
    private String attachmentFilename;
    private String attachmentContentType;
    private Long attachmentSize;
    private MeetingListController parentController;
    private boolean isEditMode = false;

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
        // 오늘 날짜로 초기화
        datePicker.setValue(LocalDate.now());
        
        // 업무 캘린더 날짜 초기화
        startDatePicker1.setValue(LocalDate.now());
        endDatePicker1.setValue(LocalDate.now().plusDays(2));
        
        // 입력 검증
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateFormForUI();
        });
        
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateFormForUI();
        });
        
        descriptionField.setPromptText("회의 내용을 입력하세요...");
        scheduleContentArea1.setPromptText("일정 내용을 입력하세요...");
        
        // 참여자 목록 초기화
        participantList1.setItems(FXCollections.observableArrayList());
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

                if (!FileUtil.isFileSizeValid(selectedFile, 50)) {
                    new Alert(Alert.AlertType.ERROR, "파일 크기가 너무 큽니다. (최대 50MB)").showAndWait();
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
                fileInfoLabel.setText(String.format("파일명: %s | 크기: %s | 타입: %s", 
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

            // 회의 시간 설정
            LocalDate selectedDate = datePicker.getValue();
            LocalDateTime startTime = selectedDate.atStartOfDay().plusHours(9); // 오전 9시
            LocalDateTime endTime = startTime.plusHours(1); // 1시간 회의

            MeetingApiClient.MeetingDto createdMeeting;

            if (isEditMode) {
                // 수정 모드
                MeetingApiClient.MeetingDto meetingDto = new MeetingApiClient.MeetingDto();
                meetingDto.setTitle(titleField.getText().trim());
                meetingDto.setDescription(descriptionField.getText().trim());
                meetingDto.setStartTime(startTime);
                meetingDto.setEndTime(endTime);
                meetingDto.setLocation(locationField.getText().trim());
                meetingDto.setDepartment(department);
                meetingDto.setAuthor(author);
                
                if (selectedFile != null) {
                    meetingDto.setAttachmentFilename(attachmentFilename);
                    meetingDto.setAttachmentContentType(attachmentContentType);
                    meetingDto.setAttachmentSize(attachmentSize);
                    meetingDto.setAttachmentContent(attachmentContent);
                }

                createdMeeting = meetingApiClient.updateMeeting(1L, meetingDto); // 임시로 1L 사용
            } else {
                // 생성 모드
                if (selectedFile != null) {
                    // 첨부파일이 있는 경우 새로운 메서드 사용
                    createdMeeting = meetingApiClient.createMeetingWithAttachment(
                        titleField.getText().trim(),
                        descriptionField.getText().trim(),
                        startTime,
                        endTime,
                        locationField.getText().trim(),
                        department,
                        author,
                        attachmentFilename,
                        attachmentContentType,
                        attachmentSize,
                        attachmentContent
                    );
                } else {
                    // 첨부파일이 없는 경우 기존 메서드 사용
                    MeetingApiClient.MeetingDto meetingDto = new MeetingApiClient.MeetingDto();
                    meetingDto.setTitle(titleField.getText().trim());
                    meetingDto.setDescription(descriptionField.getText().trim());
                    meetingDto.setStartTime(startTime);
                    meetingDto.setEndTime(endTime);
                    meetingDto.setLocation(locationField.getText().trim());
                    meetingDto.setDepartment(department);
                    meetingDto.setAuthor(author);

                    createdMeeting = meetingApiClient.createMeeting(meetingDto);
                }
            }

            if (createdMeeting != null) {
                String message = isEditMode ? "회의가 성공적으로 수정되었습니다." : "회의가 성공적으로 등록되었습니다.";
                new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
                
                // 부모 컨트롤러에 새로고침 요청
                if (parentController != null) {
                    parentController.loadDataFromServer();
                }
                
                closeDialog();
            } else {
                String message = isEditMode ? "회의 수정에 실패했습니다." : "회의 등록에 실패했습니다.";
                new Alert(Alert.AlertType.ERROR, message).showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            String message = isEditMode ? "회의 수정 중 오류가 발생했습니다: " : "회의 등록 중 오류가 발생했습니다: ";
            new Alert(Alert.AlertType.ERROR, message + e.getMessage()).showAndWait();
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
     * 일정 추가 버튼 클릭 시 호출되는 메서드
     */
    @FXML
    private void onAddSchedule() {
        int scheduleCount = scheduleContainer.getChildren().size() + 1;
        createScheduleSection(scheduleCount);
    }
    
    /**
     * 일정 삭제 버튼 클릭 시 호출되는 메서드
     */
    @FXML
    private void onRemoveSchedule() {
        // 첫 번째 일정은 삭제 불가
        if (scheduleContainer.getChildren().size() > 1) {
            scheduleContainer.getChildren().remove(scheduleContainer.getChildren().size() - 1);
        }
    }
    
    /**
     * 새로운 일정 섹션을 생성합니다.
     */
    private void createScheduleSection(int scheduleNumber) {
        VBox scheduleSection = new VBox(10);
        scheduleSection.setStyle("-fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f8f9fa;");
        
        // 제목과 삭제 버튼
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleLabel = new Label("일정 " + scheduleNumber);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Button removeBtn = new Button("삭제");
        removeBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> scheduleContainer.getChildren().remove(scheduleSection));
        headerBox.getChildren().addAll(titleLabel, removeBtn);
        
        // 날짜 선택 영역
        HBox dateBox = new HBox(20);
        
        // 시작일
        VBox startDateBox = new VBox(5);
        HBox startLabelBox = new HBox(5);
        startLabelBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label startIcon = new Label();
        startIcon.setStyle("-fx-background-color: #6c757d; -fx-background-radius: 2; -fx-min-width: 12; -fx-min-height: 12;");
        Label startLabel = new Label("시작일");
        startLabelBox.getChildren().addAll(startIcon, startLabel);
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(LocalDate.now());
        startDateBox.getChildren().addAll(startLabelBox, startDatePicker);
        
        // 종료일
        VBox endDateBox = new VBox(5);
        HBox endLabelBox = new HBox(5);
        endLabelBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label endIcon = new Label();
        endIcon.setStyle("-fx-background-color: #007bff; -fx-background-radius: 2; -fx-min-width: 12; -fx-min-height: 12;");
        Label endLabel = new Label("종료일");
        endLabelBox.getChildren().addAll(endIcon, endLabel);
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now().plusDays(2));
        endDateBox.getChildren().addAll(endLabelBox, endDatePicker);
        
        dateBox.getChildren().addAll(startDateBox, endDateBox);
        
        // 내용 입력 영역
        VBox contentBox = new VBox(5);
        Label contentLabel = new Label("내용");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("일정 내용을 입력하세요...");
        contentArea.setPrefRowCount(6);
        contentArea.setWrapText(true);
        contentBox.getChildren().addAll(contentLabel, contentArea);
        
        // 참여자 추가 버튼
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Button addParticipantBtn = new Button("+ 참여자 추가");
        addParticipantBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        addParticipantBtn.setOnAction(e -> showParticipantSelectionDialog());
        buttonBox.getChildren().add(addParticipantBtn);
        
        // 참여자 목록 영역
        VBox participantBox = new VBox(5);
        Label participantLabel = new Label("참여자 목록");
        participantLabel.setStyle("-fx-font-weight: bold;");
        ListView<String> participantList = new ListView<>();
        participantList.setPrefHeight(80);
        participantList.setItems(FXCollections.observableArrayList());
        participantBox.getChildren().addAll(participantLabel, participantList);
        
        scheduleSection.getChildren().addAll(headerBox, dateBox, contentBox, buttonBox, participantBox);
        scheduleContainer.getChildren().add(scheduleSection);
    }
    
    /**
     * 참여자 선택 다이얼로그를 표시합니다.
     */
    private void showParticipantSelectionDialog() {
        try {
            // 현재 사용자의 부서 정보 가져오기
            var currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                new Alert(Alert.AlertType.ERROR, "사용자 정보를 가져올 수 없습니다.").showAndWait();
                return;
            }
            
            // 현재 사용자의 부서명 가져오기
            String currentUserDepartment = currentUser.getDepartmentName();
            if (currentUserDepartment == null || currentUserDepartment.trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "부서 정보를 가져올 수 없습니다.").showAndWait();
                return;
            }
            
            // 부서의 모든 사용자 목록 가져오기 (실제 API 호출)
            List<User> departmentUsers = apiClient.getUsersByDepartment(currentUserDepartment);
            if (departmentUsers == null || departmentUsers.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "해당 부서에 다른 사용자가 없습니다.").showAndWait();
                return;
            }
            
            // 사용자 목록을 표시용 문자열로 변환하고 User 객체와 매핑
            ObservableList<String> userDisplayList = FXCollections.observableArrayList();
            List<User> availableUsers = new ArrayList<>();
            
            for (User user : departmentUsers) {
                // 현재 사용자는 제외
                if (user.getUserId().equals(currentUser.getUserId())) {
                    continue;
                }
                
                String userDisplay = String.format("%s (%s)", 
                    user.getUsername(), 
                    user.getPosition() != null ? user.getPosition().getPositionName() : "직급 없음");
                userDisplayList.add(userDisplay);
                availableUsers.add(user);
            }
            
            // 참여자 선택 다이얼로그 생성
            Dialog<List<User>> dialog = new Dialog<>();
            dialog.setTitle("참여자 선택");
            dialog.setHeaderText("참여할 사람들을 선택하세요");
            
            // 다이얼로그 버튼 설정
            ButtonType confirmButtonType = new ButtonType("확인", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
            
            // 체크박스 리스트 생성
            VBox content = new VBox(5);
            ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();
            
            for (String userDisplay : userDisplayList) {
                CheckBox checkBox = new CheckBox(userDisplay);
                checkBoxes.add(checkBox);
                content.getChildren().add(checkBox);
            }
            
            dialog.getDialogPane().setContent(content);
            
            // 결과 처리
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    List<User> selectedUsers = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        CheckBox checkBox = checkBoxes.get(i);
                        if (checkBox.isSelected()) {
                            selectedUsers.add(availableUsers.get(i));
                        }
                    }
                    return selectedUsers;
                }
                return null;
            });
            
            Optional<List<User>> result = dialog.showAndWait();
            result.ifPresent(selectedUsers -> {
                if (!selectedUsers.isEmpty()) {
                    // 선택된 참여자들을 현재 일정의 참여자 목록에 추가
                    for (User user : selectedUsers) {
                        String userDisplay = String.format("%s (%s)", 
                            user.getUsername(), 
                            user.getPosition() != null ? user.getPosition().getPositionName() : "직급 없음");
                        participantList1.getItems().add(userDisplay);
                    }
                    new Alert(Alert.AlertType.INFORMATION, 
                        selectedUsers.size() + "명의 참여자가 추가되었습니다.").showAndWait();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "참여자 선택 중 오류가 발생했습니다: " + e.getMessage()).showAndWait();
        }
    }
    
    /**
     * 참여자 추가 버튼 클릭 시 호출되는 메서드 (기존 일정용)
     */
    @FXML
    private void onAddParticipant() {
        showParticipantSelectionDialog();
    }

    /**
     * 부모 컨트롤러 설정
     */
    public void setParentController(MeetingListController parentController) {
        this.parentController = parentController;
    }

    /**
     * 기존 회의 데이터로 폼을 초기화합니다.
     * 
     * @param item 수정할 회의 아이템
     */
    public void setMeetingItem(MeetingItem item) {
        this.isEditMode = true;
        
        // 기존 데이터로 폼 초기화
        titleField.setText(item.getTitle());
        descriptionField.setText(item.getDescription() != null ? item.getDescription() : "");
        locationField.setText(item.getLocation() != null ? item.getLocation() : "");
        datePicker.setValue(LocalDate.parse(item.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 제출 버튼 텍스트 변경
        submitBtn.setText("수정");
        
        // 기존 첨부파일이 있으면 표시
        if (item.getAttachmentFilename() != null && !item.getAttachmentFilename().isEmpty()) {
            attachmentFilename = item.getAttachmentFilename();
            attachmentContentType = item.getAttachmentContentType();
            attachmentSize = item.getAttachmentSize();
            attachmentContent = item.getAttachmentContent();
            
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