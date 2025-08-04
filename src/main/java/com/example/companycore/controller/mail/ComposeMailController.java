package com.example.companycore.controller.mail;

import com.example.companycore.model.dto.MessageDto;
import com.example.companycore.service.MessageApiClient;
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
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String content = contentArea.getText();
            String attachmentName = attachmentLabel.getText();

            if (attachmentName == null || attachmentName.isEmpty()) {
                attachmentName = "";
            }

            // ✅ 1. 서버에 메시지 전송
            MessageDto message = new MessageDto();
            message.setReceiverName(recipient);  // 받는 사람
            message.setTitle(subject);            // 메일 제목 (수정된 부분)
            message.setContent(content);          // 내용

            // senderId는 로그인한 사용자 정보로부터 얻는 게 일반적
            Long senderId = 1L; // 예시

            MessageApiClient client = MessageApiClient.getInstance();
            MessageDto sent = client.sendMessage(message, senderId);

            if (sent != null) {
                System.out.println("✅ 서버에 메시지 전송 완료");
            } else {
                System.out.println("❌ 서버 메시지 전송 실패");
            }

            // ✅ 2. 로컬 보낸메일함에 저장
            if (parentController != null) {
                parentController.addSentMail(recipient, subject, content, attachmentName);
            }

            // ✅ 3. 입력 폼 초기화 및 기본 뷰 복귀
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