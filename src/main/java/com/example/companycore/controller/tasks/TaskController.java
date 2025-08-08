//package com.example.companycore.controller.tasks;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Window;
//import com.example.companycore.model.entity.Task;
//import com.example.companycore.model.entity.Enum.TaskType;
//import com.example.companycore.model.entity.Enum.TaskStatus;
//import com.example.companycore.service.ApiClient;
//import java.awt.*;
//import java.awt.Desktop;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//public class TaskController {
//
//    @FXML private TextField titleField;
//    @FXML private TextArea contentArea;
//    @FXML private VBox dropZone;
//    @FXML private Button addFileBtn;
//    @FXML private Button removeFileBtn; // 삭제 버튼 추가
//    @FXML private ListView<String> attachmentList; // 파일 이름 표시
//    @FXML private Button cancelBtn;
//    @FXML private Button submitBtn;
//
//    // 실제 선택된 파일 보관 리스트
//    private final ObservableList<File> attachments = FXCollections.observableArrayList();
//    private ApiClient apiClient = ApiClient.getInstance();
//
//    @FXML
//    private void initialize() {
//        // 시작 시 첨부 리스트 숨김
//        if (attachmentList != null) {
//            attachmentList.getItems().clear();
//            attachmentList.setVisible(false);
//            attachmentList.setManaged(false);
//        }
//        if (removeFileBtn != null) {
//            removeFileBtn.setDisable(true);  // 초기에는 비활성화
//        }
//
//        // dropZone 클릭 시 파일 선택 버튼 실행
//        if (dropZone != null && addFileBtn != null) {
//            dropZone.setOnMouseClicked(e -> addFileBtn.fire());
//        }
//
//        // 리스트뷰 더블 클릭 시 파일 열기
//        if (attachmentList != null) {
//            attachmentList.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2) {
//                    int index = attachmentList.getSelectionModel().getSelectedIndex();
//                    if (index >= 0 && index < attachments.size()) {
//                        File fileToOpen = attachments.get(index);
//                        try {
//                            if (Desktop.isDesktopSupported()) {
//                                Desktop.getDesktop().open(fileToOpen);
//                            } else {
//                                showAlert("이 시스템에서 파일 열기를 지원하지 않습니다.");
//                            }
//                        } catch (IOException e) {
//                            showAlert("파일을 열 수 없습니다: " + e.getMessage());
//                        }
//                    }
//                }
//            });
//
//            // 선택 변경 시 삭제 버튼 활성화/비활성화 처리
//            attachmentList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
//                if (removeFileBtn != null) {
//                    removeFileBtn.setDisable(newSel == null);
//                }
//            });
//        }
//    }
//
//    @FXML
//    private void onRemoveFile(ActionEvent event) {
//        int selectedIndex = attachmentList.getSelectionModel().getSelectedIndex();
//        if (selectedIndex >= 0 && selectedIndex < attachments.size()) {
//            attachments.remove(selectedIndex);
//            attachmentList.getItems().remove(selectedIndex);
//
//            // 선택 초기화 및 삭제 버튼 비활성화
//            attachmentList.getSelectionModel().clearSelection();
//            if (removeFileBtn != null) {
//                removeFileBtn.setDisable(true);
//            }
//
//            refreshAttachmentListVisibility();
//        }
//    }
//
//    @FXML
//    private void onAddFile(ActionEvent event) {
//        FileChooser chooser = new FileChooser();
//        chooser.setTitle("첨부파일 선택");
//
//        chooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("모든 파일", "*.*"),
//                new FileChooser.ExtensionFilter("문서", "*.pdf", "*.doc", "*.docx", "*.hwp", "*.txt"),
//                new FileChooser.ExtensionFilter("이미지", "*.png", "*.jpg", "*.jpeg", "*.gif"),
//                new FileChooser.ExtensionFilter("스프레드시트", "*.xls", "*.xlsx", "*.csv")
//        );
//
//        Window owner = (addFileBtn != null && addFileBtn.getScene() != null) ? addFileBtn.getScene().getWindow() : null;
//        List<File> selected = chooser.showOpenMultipleDialog(owner);
//
//        if (selected != null && !selected.isEmpty()) {
//            for (File f : selected) {
//                if (attachments.stream().noneMatch(x -> x.equals(f))) {
//                    attachments.add(f);
//                }
//            }
//            attachmentList.getItems().setAll(
//                    attachments.stream().map(File::getName).toList()
//            );
//            refreshAttachmentListVisibility();
//        }
//    }
//
//    @FXML
//    private void onCancel(ActionEvent event) {
//        if (titleField != null) titleField.clear();
//        if (contentArea != null) contentArea.clear();
//
//        attachments.clear();
//        if (attachmentList != null) {
//            attachmentList.getItems().clear();
//        }
//        if (removeFileBtn != null) {
//            removeFileBtn.setDisable(true);
//        }
//        refreshAttachmentListVisibility();
//
//        if (dropZone != null) {
//            dropZone.getStyleClass().remove("error");
//        }
//    }
//
//    @FXML
//    private void onSubmit(ActionEvent event) {
//        String title = (titleField != null) ? titleField.getText() : null;
//        String content = (contentArea != null) ? contentArea.getText() : null;
//
//        if (title == null || title.isBlank()) {
//            showAlert("제목을 입력하세요.");
//            return;
//        }
//        if (content == null || content.isBlank()) {
//            showAlert("내용을 입력하세요.");
//            return;
//        }
//
//        try {
//            // Task 객체 생성
//            Task task = new Task();
//            task.setTitle(title);
//            task.setDescription(content);
//            task.setTaskType(TaskType.TASK); // 기본 타입
//            task.setStatus(TaskStatus.TODO); // 기본 상태
//            task.setAssignedBy(1L); // TODO: 실제 사용자 ID 사용
////            task.setAssignedTo(1L); // TODO: 실제 할당 대상 ID 사용
//
//            // 서버에 Task 생성 요청
//            taskDto createdTask = apiClient.createTask(task);
//
//            if (createdTask != null) {
//                showAlert("작업이 성공적으로 생성되었습니다.");
//                // 제출 후 초기화
//                onCancel(null);
//            } else {
//                showAlert("작업 생성에 실패했습니다.");
//            }
//        } catch (Exception e) {
//            System.err.println("작업 생성 중 오류 발생: " + e.getMessage());
//            showAlert("서버와의 통신 중 오류가 발생했습니다.");
//        }
//    }
//
//    private void refreshAttachmentListVisibility() {
//        boolean hasItems = attachmentList != null && !attachmentList.getItems().isEmpty();
//        if (attachmentList != null) {
//            attachmentList.setVisible(hasItems);
//            attachmentList.setManaged(hasItems);
//        }
//    }
//
//    private void showAlert(String msg) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
//        alert.showAndWait();
//    }
//}