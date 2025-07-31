package com.example.companycore.controller;


import com.example.companycore.model.dto.ApprovalItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class ApprovalDetailController {

    @FXML private Label titleLabel;
    @FXML private Label writerLabel;
    @FXML private Label dateLabel;
    @FXML private Label departmentLabel;
    @FXML private TextArea contentArea;
    @FXML private ListView<String> attachmentListView;  // 첨부파일 리스트뷰

    private Stage dialogStage;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setApprovalItem(ApprovalItem item) {
        titleLabel.setText(item.getTitle() != null ? item.getTitle() : "");
        writerLabel.setText(item.getAuthor() != null ? item.getAuthor() : "");
        dateLabel.setText(item.getDate() != null ? item.getDate() : "");
        departmentLabel.setText(item.getDepartment() != null ? item.getDepartment() : "");
        contentArea.setText(item.getContent() != null ? item.getContent() : "");

        // 첨부파일이 null이 아닐 경우 ListView에 세팅
        List<String> attachments = item.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            attachmentListView.setItems(FXCollections.observableArrayList(attachments));
        } else {
            attachmentListView.getItems().clear();
        }
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            contentArea.getScene().getWindow().hide();
        }
    }
}