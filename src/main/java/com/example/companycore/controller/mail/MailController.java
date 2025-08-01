package com.example.companycore.controller.mail;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MailController {
    
    @FXML
    private StackPane rightContentContainer;
    
    // 메일 제목 라벨들
    @FXML
    private javafx.scene.control.Label mailTitle1;
    @FXML
    private javafx.scene.control.Label mailTitle2;
    @FXML
    private javafx.scene.control.Label mailTitle3;
    @FXML
    private javafx.scene.control.Label mailTitle4;
    @FXML
    private javafx.scene.control.Label mailTitle5;
    @FXML
    private javafx.scene.control.Label mailTitle6;
    @FXML
    private javafx.scene.control.Label mailTitle7;
    @FXML
    private javafx.scene.control.Label mailTitle8;
    @FXML
    private javafx.scene.control.Label mailTitle9;
    @FXML
    private javafx.scene.control.Label mailTitle10;
    
    // 메일 행 컨테이너들 (배경색 변경용)
    @FXML
    private javafx.scene.layout.HBox mailRow1;
    @FXML
    private javafx.scene.layout.HBox mailRow2;
    @FXML
    private javafx.scene.layout.HBox mailRow3;
    @FXML
    private javafx.scene.layout.HBox mailRow4;
    @FXML
    private javafx.scene.layout.HBox mailRow5;
    @FXML
    private javafx.scene.layout.HBox mailRow6;
    @FXML
    private javafx.scene.layout.HBox mailRow7;
    @FXML
    private javafx.scene.layout.HBox mailRow8;
    @FXML
    private javafx.scene.layout.HBox mailRow9;
    @FXML
    private javafx.scene.layout.HBox mailRow10;
    
    // 체크박스들
    @FXML
    private javafx.scene.control.CheckBox checkBox1;
    @FXML
    private javafx.scene.control.CheckBox checkBox2;
    @FXML
    private javafx.scene.control.CheckBox checkBox3;
    @FXML
    private javafx.scene.control.CheckBox checkBox4;
    @FXML
    private javafx.scene.control.CheckBox checkBox5;
    @FXML
    private javafx.scene.control.CheckBox checkBox6;
    @FXML
    private javafx.scene.control.CheckBox checkBox7;
    @FXML
    private javafx.scene.control.CheckBox checkBox8;
    @FXML
    private javafx.scene.control.CheckBox checkBox9;
    @FXML
    private javafx.scene.control.CheckBox checkBox10;
    
    // 페이지네이션 관련
    @FXML
    private javafx.scene.control.Button prevPageButton;
    @FXML
    private javafx.scene.control.Button nextPageButton;
    @FXML
    private javafx.scene.control.Label pageInfoLabel;
    
    private int currentPage = 1;
    private int itemsPerPage = 10;
    
    private String currentMailbox = "allMailbox"; // 기본값은 전체메일함
    
    // 메일 읽음 상태 관리 (메일 인덱스별로 읽음 여부 저장)
    private boolean[] mailReadStatus = new boolean[20]; // 최대 20개 메일 (페이지당 10개)
    
    // 보낸 메일 데이터 관리 (임시 메모리 저장)
    private java.util.List<SentMail> sentMails = new java.util.ArrayList<>();
    
    // 보낸 메일 클래스
    public static class SentMail {
        private String recipient;
        private String subject;
        private String content;
        private String date;
        private String attachment;
        
        public SentMail(String recipient, String subject, String content, String attachment) {
            this.recipient = recipient;
            this.subject = subject;
            this.content = content;
            this.attachment = attachment;
            this.date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"));
        }
        
        // Getters
        public String getRecipient() { return recipient; }
        public String getSubject() { return subject; }
        public String getContent() { return content; }
        public String getDate() { return date; }
        public String getAttachment() { return attachment; }
    }
    

    
    @FXML
    public void handleMailPreview() {
        // 기본적으로 첫 번째 메일 미리보기 로드
        loadMailPreviewByMailbox("", 1);
    }
    
    @FXML
    public void handleMailPreview1() {
        loadMailPreviewByMailbox("", 1);
        markMailAsRead(1);
    }
    
    @FXML
    public void handleMailPreview2() {
        loadMailPreviewByMailbox("", 2);
        markMailAsRead(2);
    }
    
    @FXML
    public void handleMailPreview3() {
        loadMailPreviewByMailbox("", 3);
        markMailAsRead(3);
    }
    
    @FXML
    public void handleMailPreview4() {
        loadMailPreviewByMailbox("", 4);
        markMailAsRead(4);
    }
    
    @FXML
    public void handleMailPreview5() {
        loadMailPreviewByMailbox("", 5);
        markMailAsRead(5);
    }
    
    @FXML
    public void handleMailPreview6() {
        loadMailPreviewByMailbox("", 6);
        markMailAsRead(6);
    }
    
    @FXML
    public void handleMailPreview7() {
        loadMailPreviewByMailbox("", 7);
        markMailAsRead(7);
    }
    
    @FXML
    public void handleMailPreview8() {
        loadMailPreviewByMailbox("", 8);
        markMailAsRead(8);
    }
    
    @FXML
    public void handleMailPreview9() {
        loadMailPreviewByMailbox("", 9);
        markMailAsRead(9);
    }
    
    @FXML
    public void handleMailPreview10() {
        loadMailPreviewByMailbox("", 10);
        markMailAsRead(10);
    }
    

    
    private void loadMailPreview(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node mailPreviewPanel = loader.load();
            
            // 기존 내용을 지우고 새 패널 추가
            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(mailPreviewPanel);
            
        } catch (IOException e) {
            showAlert("오류", "메일 미리보기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // 메일함별로 다른 미리보기 패널 로드
    private void loadMailPreviewByMailbox(String mailTitle, int mailIndex) {
        String fxmlPath = "/com/example/companycore/view/content/mail/dynamicMailPreviewPanel.fxml";
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node mailPreviewPanel = loader.load();
            
            // 컨트롤러 가져오기
            DynamicMailPreviewController previewController = loader.getController();
            
            // 메일함별로 다른 데이터 설정
            switch (currentMailbox) {
                case "allMailbox":
                    setAllMailboxData(previewController, mailIndex);
                    break;
                case "inbox":
                    setInboxData(previewController, mailIndex);
                    break;
                case "sentMailbox":
                    setSentMailboxData(previewController, mailIndex);
                    break;
                default:
                    setAllMailboxData(previewController, mailIndex);
                    break;
            }
            
            // 기존 내용을 지우고 새 패널 추가
            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(mailPreviewPanel);
            
        } catch (IOException e) {
            showAlert("오류", "메일 미리보기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // 전체메일함 데이터 설정
    private void setAllMailboxData(DynamicMailPreviewController controller, int mailIndex) {
        // 실제 보낸 메일이 있는지 확인 (전체메일함에서는 보낸 메일도 포함)
        if (!sentMails.isEmpty() && mailIndex <= sentMails.size()) {
            // 실제 보낸 메일 데이터 사용
            SentMail sentMail = sentMails.get(mailIndex - 1);
            controller.setMailData(
                "나", // 보낸 사람은 "나"
                sentMail.getRecipient(),
                sentMail.getSubject(),
                sentMail.getContent(),
                sentMail.getDate(),
                sentMail.getAttachment()
            );
        } else {
            // 메일이 없으면 빈 데이터 표시
            controller.setMailData("", "", "메일이 없습니다", "", "", "");
        }
    }
    
    // 받은메일함 데이터 설정
    private void setInboxData(DynamicMailPreviewController controller, int mailIndex) {
        // 받은메일함은 전체메일함과 동일한 데이터 사용
        setAllMailboxData(controller, mailIndex);
    }
    
    // 보낸메일함 데이터 설정
    private void setSentMailboxData(DynamicMailPreviewController controller, int mailIndex) {
        // 실제 보낸 메일이 있는지 확인
        if (!sentMails.isEmpty() && mailIndex <= sentMails.size()) {
            // 실제 보낸 메일 데이터 사용
            SentMail sentMail = sentMails.get(mailIndex - 1);
            controller.setSentMailData(
                sentMail.getRecipient(),
                sentMail.getSubject(),
                sentMail.getContent(),
                sentMail.getDate(),
                sentMail.getAttachment()
            );
        } else {
            // 보낸 메일이 없으면 빈 데이터 표시
            controller.setSentMailData("", "보낸 메일이 없습니다", "", "", "");
        }
    }
    
    @FXML
    public void handleComposeMail() {
        try {
            // 선택된 메일의 배경 초기화
            clearAllMailRowBackgrounds();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();
            
            // 컨트롤러 참조 가져오기
            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                composeController.setParentController(this);
            }
            
            // 기존 내용을 지우고 새 패널 추가
            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(composeMailPanel);
            
        } catch (IOException e) {
            showAlert("오류", "메일쓰기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            // 현재 메일함에 따라 적절한 UI 업데이트 메서드 호출
            switch (currentMailbox) {
                case "sentMailbox":
                    updateSentMailboxUI();
                    break;
                case "allMailbox":
                    updateAllMailboxUI();
                    break;
                case "inbox":
                    updateInboxUI();
                    break;
            }
            updatePaginationUI();
        }
    }
    
    @FXML
    public void handleNextPage() {
        int totalPages = (int) Math.ceil((double) sentMails.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            // 현재 메일함에 따라 적절한 UI 업데이트 메서드 호출
            switch (currentMailbox) {
                case "sentMailbox":
                    updateSentMailboxUI();
                    break;
                case "allMailbox":
                    updateAllMailboxUI();
                    break;
                case "inbox":
                    updateInboxUI();
                    break;
            }
            updatePaginationUI();
        }
    }
    

    
    public void showDefaultView() {
        rightContentContainer.getChildren().clear();
        VBox defaultView = new VBox();
        defaultView.setAlignment(javafx.geometry.Pos.CENTER);
        defaultView.setSpacing(20);
        
        javafx.scene.control.Label iconLabel = new javafx.scene.control.Label("📧");
        iconLabel.setStyle("-fx-font-size: 48px;");
        
        String message = "";
        switch (currentMailbox) {
            case "allMailbox":
                message = "메일을 선택하거나 새 메일을 작성하세요";
                break;
            case "inbox":
                message = "받은 메일을 선택하거나 새 메일을 작성하세요";
                break;
            case "sentMailbox":
                message = "보낸 메일을 선택하거나 새 메일을 작성하세요";
                break;
            default:
                message = "메일을 선택하거나 새 메일을 작성하세요";
                break;
        }
        
        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        
        defaultView.getChildren().addAll(iconLabel, messageLabel);
        rightContentContainer.getChildren().add(defaultView);
    }
    
    // 메일함 설정
    public void setCurrentMailbox(String mailbox) {
        this.currentMailbox = mailbox;
        updateAllMailTitleStyles(); // 메일함 변경 시 스타일 업데이트
        
        // 메일함별로 UI 업데이트
        switch (mailbox) {
            case "sentMailbox":
                updateSentMailboxUI();
                break;
            case "allMailbox":
                updateAllMailboxUI();
                break;
            case "inbox":
                updateInboxUI();
                break;
        }
    }
    
    // 메일 읽음 상태 초기화 (모든 메일을 안 읽음 상태로)
    private void initializeMailReadStatus() {
        for (int i = 0; i < mailReadStatus.length; i++) {
            mailReadStatus[i] = false; // 모든 메일을 안 읽음 상태로 초기화
        }
    }
    
    // 보낸메일함 초기화 (임시 메모리 저장)
    public void initializeSentMailbox() {
        // TODO: 나중에 데이터베이스에서 로드하도록 수정
        if ("sentMailbox".equals(currentMailbox)) {
            updateSentMailboxUI();
        }
    }
    
    // 메일을 보낸메일함에 저장
    public void addSentMail(String recipient, String subject, String content, String attachment) {
        SentMail sentMail = new SentMail(recipient, subject, content, attachment);
        sentMails.add(0, sentMail); // 최신 메일을 맨 위에 추가
        
        // TODO: 나중에 데이터베이스에 저장하도록 수정
        
        // 새 메일 추가 시 읽음 상태 초기화 (기존 메일들의 읽음 상태를 한 칸씩 뒤로 밀기)
        for (int i = mailReadStatus.length - 1; i > 0; i--) {
            mailReadStatus[i] = mailReadStatus[i - 1];
        }
        mailReadStatus[0] = false; // 새 메일은 읽지 않은 상태로 설정
        
        // 현재 메일함에 따라 UI 업데이트
        if ("sentMailbox".equals(currentMailbox)) {
            updateSentMailboxUI();
        } else if ("allMailbox".equals(currentMailbox)) {
            updateAllMailboxUI();
        }
        
        // 성공 메시지 표시
        showAlert("성공", "메일이 성공적으로 전송되었습니다.", Alert.AlertType.INFORMATION);
    }
    
    // 전체메일함 UI 업데이트
    private void updateAllMailboxUI() {
        // 모든 메일 제목과 체크박스 초기화
        for (int i = 1; i <= 10; i++) {
            updateMailTitle(i, "");
            updateCheckBoxVisibility(i, false);
        }
        
        if (sentMails.isEmpty()) {
            updatePaginationUI();
            return;
        }
        
        // 현재 페이지의 메일들 표시
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sentMails.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            SentMail mail = sentMails.get(i);
            int displayIndex = i - startIndex + 1;
            updateMailTitle(displayIndex, mail.getSubject());
            updateCheckBoxVisibility(displayIndex, true);
        }
        
        updatePaginationUI();
    }
    
    // 메일 제목 업데이트 (전체메일함용)
    private void updateMailTitle(int index, String subject) {
        javafx.scene.control.Label titleLabel = null;
        
        switch (index) {
            case 1:
                titleLabel = mailTitle1;
                break;
            case 2:
                titleLabel = mailTitle2;
                break;
            case 3:
                titleLabel = mailTitle3;
                break;
            case 4:
                titleLabel = mailTitle4;
                break;
            case 5:
                titleLabel = mailTitle5;
                break;
        }
        
        if (titleLabel != null) {
            titleLabel.setText(subject);
        }
    }
    
    // 받은메일함 UI 업데이트
    private void updateInboxUI() {
        // 모든 메일 제목과 체크박스 초기화
        for (int i = 1; i <= 10; i++) {
            updateMailTitle(i, "");
            updateCheckBoxVisibility(i, false);
        }
        
        if (sentMails.isEmpty()) {
            updatePaginationUI();
            return;
        }
        
        // 현재 페이지의 메일들 표시
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sentMails.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            SentMail mail = sentMails.get(i);
            int displayIndex = i - startIndex + 1;
            updateMailTitle(displayIndex, mail.getSubject());
            updateCheckBoxVisibility(displayIndex, true);
        }
        
        updatePaginationUI();
    }
    
    // 보낸메일함 UI 업데이트
    private void updateSentMailboxUI() {
        // 모든 메일 제목 초기화
        for (int i = 1; i <= 10; i++) {
            updateSentMailTitle(i, "", "", false);
            updateCheckBoxVisibility(i, false);
        }
        
        if (sentMails.isEmpty()) {
            updatePaginationUI();
            return;
        }
        
        // 현재 페이지의 메일들 표시
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sentMails.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            SentMail mail = sentMails.get(i);
            int displayIndex = i - startIndex + 1;
            updateSentMailTitle(displayIndex, mail.getSubject(), mail.getDate(), !mail.getAttachment().isEmpty());
            updateCheckBoxVisibility(displayIndex, true);
        }
        
        updatePaginationUI();
    }
    
    // 페이지네이션 UI 업데이트
    private void updatePaginationUI() {
        int totalPages = Math.max(1, (int) Math.ceil((double) sentMails.size() / itemsPerPage));
        
        if (pageInfoLabel != null) {
            pageInfoLabel.setText(currentPage + " / " + totalPages);
        }
        
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 1);
        }
        
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }
    }
    
    // 보낸메일 제목 업데이트
    private void updateSentMailTitle(int index, String subject, String date, boolean hasAttachment) {
        javafx.scene.control.Label titleLabel = null;
        javafx.scene.control.Label dateLabel = null;
        javafx.scene.control.Label attachmentLabel = null;
        
        switch (index) {
            case 1:
                titleLabel = mailTitle1;
                // FXML에서 날짜와 첨부파일 라벨을 가져오기 위해 부모 HBox의 자식들 확인
                if (mailRow1 != null && mailRow1.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow1.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow1.getChildren().get(3);
                }
                break;
            case 2:
                titleLabel = mailTitle2;
                if (mailRow2 != null && mailRow2.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow2.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow2.getChildren().get(3);
                }
                break;
            case 3:
                titleLabel = mailTitle3;
                if (mailRow3 != null && mailRow3.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow3.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow3.getChildren().get(3);
                }
                break;
            case 4:
                titleLabel = mailTitle4;
                if (mailRow4 != null && mailRow4.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow4.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow4.getChildren().get(3);
                }
                break;
            case 5:
                titleLabel = mailTitle5;
                if (mailRow5 != null && mailRow5.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow5.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow5.getChildren().get(3);
                }
                break;
            case 6:
                titleLabel = mailTitle6;
                if (mailRow6 != null && mailRow6.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow6.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow6.getChildren().get(3);
                }
                break;
            case 7:
                titleLabel = mailTitle7;
                if (mailRow7 != null && mailRow7.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow7.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow7.getChildren().get(3);
                }
                break;
            case 8:
                titleLabel = mailTitle8;
                if (mailRow8 != null && mailRow8.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow8.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow8.getChildren().get(3);
                }
                break;
            case 9:
                titleLabel = mailTitle9;
                if (mailRow9 != null && mailRow9.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow9.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow9.getChildren().get(3);
                }
                break;
            case 10:
                titleLabel = mailTitle10;
                if (mailRow10 != null && mailRow10.getChildren().size() >= 4) {
                    dateLabel = (javafx.scene.control.Label) mailRow10.getChildren().get(2);
                    attachmentLabel = (javafx.scene.control.Label) mailRow10.getChildren().get(3);
                }
                break;
        }
        
        if (titleLabel != null) {
            titleLabel.setText(subject);
        }
        
        if (dateLabel != null) {
            dateLabel.setText(date);
        }
        
        if (attachmentLabel != null) {
            attachmentLabel.setText(hasAttachment ? "📎" : "");
        }
    }
    
    // 실제 보낸 메일을 동적으로 표시하는 메서드
    public void showSentMailPreview(SentMail sentMail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/dynamicMailPreviewPanel.fxml"));
            Node mailPreviewPanel = loader.load();
            
            DynamicMailPreviewController previewController = loader.getController();
            previewController.setSentMailData(
                sentMail.getRecipient(),
                sentMail.getSubject(),
                sentMail.getContent(),
                sentMail.getDate(),
                sentMail.getAttachment()
            );
            
            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(mailPreviewPanel);
            
        } catch (IOException e) {
            showAlert("오류", "보낸 메일 미리보기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    // 모든 메일 제목 스타일 업데이트
    private void updateAllMailTitleStyles() {
        for (int i = 1; i <= 10; i++) {
            updateMailTitleStyle(i, isMailRead(i));
        }
    }
    
    // 메일을 읽음 상태로 표시
    private void markMailAsRead(int mailIndex) {
        if (mailIndex >= 0 && mailIndex < mailReadStatus.length) {
            // 모든 메일 행의 배경색 초기화
            for (int i = 1; i <= 10; i++) {
                updateMailRowBackground(i, false);
            }
            
            mailReadStatus[mailIndex - 1] = true; // 인덱스는 0부터 시작하므로 -1
            updateMailTitleStyle(mailIndex, true); // 스타일 업데이트
            updateMailRowBackground(mailIndex, true); // 선택된 메일 배경색 설정
        }
    }
    
    // 메일 행 배경색 업데이트
    private void updateMailRowBackground(int mailIndex, boolean isSelected) {
        javafx.scene.layout.HBox mailRow = null;
        
        switch (mailIndex) {
            case 1:
                mailRow = mailRow1;
                break;
            case 2:
                mailRow = mailRow2;
                break;
            case 3:
                mailRow = mailRow3;
                break;
            case 4:
                mailRow = mailRow4;
                break;
            case 5:
                mailRow = mailRow5;
                break;
            case 6:
                mailRow = mailRow6;
                break;
            case 7:
                mailRow = mailRow7;
                break;
            case 8:
                mailRow = mailRow8;
                break;
            case 9:
                mailRow = mailRow9;
                break;
            case 10:
                mailRow = mailRow10;
                break;
        }
        
        if (mailRow != null) {
            if (isSelected) {
                // 선택된 메일: 회색 배경
                mailRow.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-background-color: #f8f9fa;");
            } else {
                // 선택되지 않은 메일: 투명 배경
                mailRow.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");
            }
        }
    }
    
    // 메일 읽음 상태 확인
    public boolean isMailRead(int mailIndex) {
        if (mailIndex >= 0 && mailIndex < mailReadStatus.length) {
            return mailReadStatus[mailIndex - 1];
        }
        return false;
    }
    
    // 메일 제목 스타일 업데이트
    private void updateMailTitleStyle(int mailIndex, boolean isRead) {
        javafx.scene.control.Label titleLabel = null;
        
        switch (mailIndex) {
            case 1:
                titleLabel = mailTitle1;
                break;
            case 2:
                titleLabel = mailTitle2;
                break;
            case 3:
                titleLabel = mailTitle3;
                break;
            case 4:
                titleLabel = mailTitle4;
                break;
            case 5:
                titleLabel = mailTitle5;
                break;
            case 6:
                titleLabel = mailTitle6;
                break;
            case 7:
                titleLabel = mailTitle7;
                break;
            case 8:
                titleLabel = mailTitle8;
                break;
            case 9:
                titleLabel = mailTitle9;
                break;
            case 10:
                titleLabel = mailTitle10;
                break;
        }
        
        if (titleLabel != null) {
            if (isRead) {
                // 읽은 메일: 회색 글씨, 일반 글씨
                titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 14px; -fx-text-fill: #6c757d;");
            } else {
                // 안 읽은 메일: 검은 글씨, 굵은 글씨
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #000000;");
            }
        }
    }
    
    // 모든 메일 행의 배경 초기화
    private void clearAllMailRowBackgrounds() {
        for (int i = 1; i <= 10; i++) {
            updateMailRowBackground(i, false);
        }
    }
    
    // 체크박스 가시성 업데이트
    private void updateCheckBoxVisibility(int index, boolean visible) {
        javafx.scene.control.CheckBox checkBox = null;
        
        switch (index) {
            case 1:
                checkBox = checkBox1;
                break;
            case 2:
                checkBox = checkBox2;
                break;
            case 3:
                checkBox = checkBox3;
                break;
            case 4:
                checkBox = checkBox4;
                break;
            case 5:
                checkBox = checkBox5;
                break;
            case 6:
                checkBox = checkBox6;
                break;
            case 7:
                checkBox = checkBox7;
                break;
            case 8:
                checkBox = checkBox8;
                break;
            case 9:
                checkBox = checkBox9;
                break;
            case 10:
                checkBox = checkBox10;
                break;
        }
        
        if (checkBox != null) {
            checkBox.setVisible(visible);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    

} 