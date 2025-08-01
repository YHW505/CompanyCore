package com.example.companycore.controller.mail;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ComposeMailController {
    
    @FXML
    private TextField recipientField;
    
    @FXML
    private TextField subjectField;
    
    @FXML
    private TextArea contentArea;
    
    @FXML
    private Label attachmentLabel;
    
    private File selectedFile;
    private MailController parentController;
    
    @FXML
    public void handleSendMail() {
        if (validateMailForm()) {
            // 메일을 보낸메일함에 저장
            if (parentController != null) {
                String attachmentName = attachmentLabel.getText();
                if (attachmentName == null || attachmentName.isEmpty()) {
                    attachmentName = "";
                }
                parentController.addSentMail(
                    recipientField.getText(),
                    subjectField.getText(),
                    contentArea.getText(),
                    attachmentName
                );
            }
            
            clearMailForm();
            if (parentController != null) {
                parentController.showDefaultView();
            }
        }
    }
    
    @FXML
    public void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("확인");
        alert.setHeaderText("메일쓰기 취소");
        alert.setContentText("작성 중인 메일이 있습니다. 정말 취소하시겠습니까?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearMailForm();
                if (parentController != null) {
                    parentController.showDefaultView();
                }
            }
        });
    }
    
    @FXML
    public void handleAddAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("모든 파일", "*.*"),
            new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("이미지 파일", "*.jpg", "*.png", "*.gif")
        );
        
        Stage stage = (Stage) recipientField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            attachmentLabel.setText(selectedFile.getName());
        }
    }
    
    private boolean validateMailForm() {
        if (recipientField == null || recipientField.getText().trim().isEmpty()) {
            showAlert("오류", "받는 사람을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        if (subjectField == null || subjectField.getText().trim().isEmpty()) {
            showAlert("오류", "제목을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        if (contentArea == null || contentArea.getText().trim().isEmpty()) {
            showAlert("오류", "내용을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    private void clearMailForm() {
        if (recipientField != null) recipientField.clear();
        if (subjectField != null) subjectField.clear();
        if (contentArea != null) contentArea.clear();
        if (attachmentLabel != null) attachmentLabel.setText("");
        selectedFile = null;
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // 부모 컨트롤러 설정
    public void setParentController(MailController parentController) {
        this.parentController = parentController;
    }
} 