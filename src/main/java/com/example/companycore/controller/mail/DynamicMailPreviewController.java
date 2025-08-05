package com.example.companycore.controller.mail;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;

public class DynamicMailPreviewController {

    @FXML
    private Label senderLabel;

    @FXML
    private Label recipientLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label attachmentLabel;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Button forwardButton;

    /**
     * 메일 데이터를 받아서 UI에 표시
     */

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
        } else {
            attachmentLabel.setText("첨부파일 없음");
        }
    }

    /**
     * 보낸 메일 데이터를 받아서 UI에 표시 (보낸메일함용)
     */
    public void setSentMailData(String recipient, String subject, String content, String date, String attachment) {
        senderLabel.setText("나");
        recipientLabel.setText(recipient != null ? recipient : "수신자");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");

        if (attachment != null && !attachment.isEmpty()) {
            attachmentLabel.setText(attachment);
        } else {
            attachmentLabel.setText("첨부파일 없음");
        }
    }

    /**
     * 받은 메일 데이터를 받아서 UI에 표시 (받은메일함용)
     */
    public void setReceivedMailData(String sender, String subject, String content, String date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
        recipientLabel.setText("나");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");

        if (attachment != null && !attachment.isEmpty()) {
            attachmentLabel.setText(attachment);
        } else {
            attachmentLabel.setText("첨부파일 없음");
        }
    }

    @FXML
    public void handleForwardMail() {
        try {
            // 1. 메일쓰기 FXML 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();

            // 2. ComposeMailController 가져오기
            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                // 3. 보낸사람 이름을 수신자 필드에 세팅
                composeController.setRecipientEmail(senderLabel.getText());
            }

            // 4. 오른쪽 컨테이너(StackPane) 찾아서 교체
            // (이 컨트롤러의 부모 StackPane을 찾음)
            Node node = senderLabel;
            while (node != null && !(node.getParent() instanceof javafx.scene.layout.StackPane)) {
                node = node.getParent();
            }
            if (node != null && node.getParent() instanceof javafx.scene.layout.StackPane) {
                javafx.scene.layout.StackPane rightContentContainer = (javafx.scene.layout.StackPane) node.getParent();
                rightContentContainer.getChildren().clear();
                rightContentContainer.getChildren().add(composeMailPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}