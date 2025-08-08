package com.example.companycore.controller.mail;

import com.example.companycore.model.dto.MessageDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MessageApiClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DynamicMailPreviewController {

    @FXML private Label senderLabel;
    @FXML private Label recipientLabel;
    @FXML private Label subjectLabel;
    @FXML private Label dateLabel;
    @FXML private Label attachmentLabel;
    @FXML private Button downloadButton;
    @FXML private TextArea contentTextArea;
    @FXML private Button forwardButton;

    private ApiClient apiClient = ApiClient.getInstance();

    private MessageDto selectedMessage;
    private MailController mailController;

    public void setMailController(MailController mailController) {
        this.mailController = mailController;
    }

    public void setSelectedMessage(MessageDto selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public void setMailData(String sender, String recipient, String subject, String content, LocalDateTime date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
        recipientLabel.setText(recipient != null ? recipient : "수신자");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");

        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateLabel.setText(date.format(formatter));
        } else {
            dateLabel.setText("날짜 없음");
        }

        if (attachment != null && !attachment.isEmpty()) {
            attachmentLabel.setText(attachment);
            downloadButton.setVisible(true);
        } else {
            attachmentLabel.setText("첨부파일 없음");
            downloadButton.setVisible(false);
        }
    }

    public void setSentMailData(String recipient, String subject, String content, String date, String attachment) {
        senderLabel.setText("나");
        recipientLabel.setText(recipient != null ? recipient : "수신자");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");
        if (attachment != null && !attachment.isEmpty()) {
            attachmentLabel.setText(attachment);
            downloadButton.setVisible(true);
        } else {
            attachmentLabel.setText("첨부파일 없음");
            downloadButton.setVisible(false);
        }
    }

    public void setReceivedMailData(String sender, String subject, String content, String date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
        recipientLabel.setText("나");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");
        if (attachment != null && !attachment.isEmpty()) {
            attachmentLabel.setText(attachment);
            downloadButton.setVisible(true);
        } else {
            attachmentLabel.setText("첨부파일 없음");
            downloadButton.setVisible(false);
        }
    }


    @FXML
    public void handleForwardMail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();

            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                composeController.setRecipientEmail(senderLabel.getText());
            }

            Node node = senderLabel;
            while (node != null && !(node.getParent() instanceof StackPane)) {
                node = node.getParent();
            }
            if (node != null && node.getParent() instanceof StackPane) {
                StackPane rightContentContainer = (StackPane) node.getParent();
                rightContentContainer.getChildren().clear();
                rightContentContainer.getChildren().add(composeMailPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleForwardContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();

            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                composeController.setRecipientContent(contentTextArea.getText());
            }

            Node node = contentTextArea;
            while (node != null && !(node.getParent() instanceof StackPane)) {
                node = node.getParent();
            }
            if (node != null && node.getParent() instanceof StackPane) {
                StackPane rightContentContainer = (StackPane) node.getParent();
                rightContentContainer.getChildren().clear();
                rightContentContainer.getChildren().add(composeMailPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ 메시지 삭제 메서드
    @FXML
    public void handleDelete() {
        if (selectedMessage == null) {
            showAlert("삭제할 메시지가 선택되지 않았습니다.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("메일 삭제 확인");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("정말로 이 메일을 삭제하시겠습니까?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Integer messageId = selectedMessage.getMessageId();
            User user = apiClient.getCurrentUser();
            Long userId = user.getUserId();

            boolean isDeleted = MessageApiClient.getInstance().deleteMessageById(messageId, userId);

            if (isDeleted) {
                showAlert("메시지가 성공적으로 삭제되었습니다.", Alert.AlertType.INFORMATION);
                if (mailController != null) {
                    mailController.returnToMailList(); // MailController의 메서드를 호출하여 목록 새로고침
                }
            } else {
                showAlert("메시지 삭제에 실패했습니다.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDownloadAttachment() {
        if (selectedMessage == null || selectedMessage.getAttachmentFilename() == null || selectedMessage.getAttachmentFilename().isEmpty()) {
            showAlert("다운로드할 첨부파일이 없습니다.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 저장");
        fileChooser.setInitialFileName(selectedMessage.getAttachmentFilename());
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            byte[] attachmentData = MessageApiClient.getInstance().downloadAttachment(selectedMessage.getMessageId(), apiClient.getCurrentUser().getUserId());
            if (attachmentData != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(attachmentData);
                    showAlert("첨부파일이 성공적으로 저장되었습니다.", Alert.AlertType.INFORMATION);
                } catch (IOException e) {
                    showAlert("첨부파일 저장 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            } else {
                showAlert("첨부파일 다운로드에 실패했습니다.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
