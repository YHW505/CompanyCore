package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
    
    /**
     * 메일 데이터를 받아서 UI에 표시
     */
    public void setMailData(String sender, String recipient, String subject, String content, String date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
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
} 