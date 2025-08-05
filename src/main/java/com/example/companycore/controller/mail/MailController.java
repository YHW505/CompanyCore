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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailController {

    // 우측 컨텐츠 영역 (메일 미리보기나 작성 폼이 들어갈 자리)
    @FXML
    private StackPane rightContentContainer;

    @FXML private Label selectedLabel;
    // 10개의 메일 제목 Label과 각각의 행 (Row)
    @FXML private Label mailTitle1, mailTitle2, mailTitle3, mailTitle4, mailTitle5, mailTitle6, mailTitle7, mailTitle8, mailTitle9, mailTitle10;
    @FXML private HBox mailRow1, mailRow2, mailRow3, mailRow4, mailRow5, mailRow6, mailRow7, mailRow8, mailRow9, mailRow10;

    // 페이지 네비게이션 버튼 및 페이지 정보 Label
    @FXML private Button prevPageButton, nextPageButton;
    @FXML private Label pageInfoLabel;

    private int currentPage = 1; // 현재 페이지
    private final int itemsPerPage = 10; // 페이지당 표시할 메일 수

    private final MessageApiClient messageApiClient = MessageApiClient.getInstance(); // 메시지 API 클라이언트
    private List<MessageDto> messages = new ArrayList<>(); // 현재 메일함의 메일 목록

    // UI 업데이트를 위한 제목 Label들과 행(HBox)들을 리스트로 정리
    private List<Label> mailTitleLabels;
    private List<HBox> mailRows;

    // 현재 메일함 타입 추적
    private String currentMailboxType = "allMailbox"; // 기본값: 전체 메일함

    // 컨트롤러 초기화 시 자동 호출됨
    @FXML
    public void initialize() {
        // Label들과 Row들을 순서대로 리스트에 저장
        mailTitleLabels = List.of(mailTitle1, mailTitle2, mailTitle3, mailTitle4, mailTitle5, mailTitle6, mailTitle7, mailTitle8, mailTitle9, mailTitle10);
        mailRows = List.of(mailRow1, mailRow2, mailRow3, mailRow4, mailRow5, mailRow6, mailRow7, mailRow8, mailRow9, mailRow10);

        // 기본적으로 전체 메일함 로드
        loadMessages();
    }

    // 현재 메일함 타입에 따라 메일 목록을 서버에서 조회
    private void loadMessages() {
        long currentUserId = ApiClient.getInstance().getCurrentUser().getUserId();

        switch (currentMailboxType) {
            case "inbox":
                // 받은 메일함
                messages = messageApiClient.getReceiveMessagesById(currentUserId);
                break;
            case "sentMailbox":
                // 보낸 메일함
                messages = messageApiClient.getSentMessagesById(currentUserId);
                break;
            case "allMailbox":
            default:
                // 전체 메일함: 받은 메일 + 보낸 메일 합치기
                List<MessageDto> received = messageApiClient.getReceiveMessagesById(currentUserId);
                List<MessageDto> sent = messageApiClient.getSentMessagesById(currentUserId);

                // 두 리스트를 하나로 병합
                messages = new ArrayList<>();
                messages.addAll(received);
                messages.addAll(sent);
                break;
        }

        // 페이지를 1로 리셋하고 UI 업데이트
        currentPage = 1;
        updateMailListUI();
    }

    // 메일 리스트를 현재 페이지 기준으로 UI에 표시
    private void updateMailListUI() {
        // 우선 전체 행 숨김 처리
        mailRows.forEach(row -> row.setVisible(false));

        // 현재 페이지에 해당하는 인덱스 범위 계산
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, messages.size());

        // 각 행에 메일 제목을 표시하고 행 보이게 하기
        for (int i = startIndex; i < endIndex; i++) {
            MessageDto message = messages.get(i);
            int displayIndex = i - startIndex;

            mailTitleLabels.get(displayIndex).setText(message.getTitle());
            mailRows.get(displayIndex).setVisible(true);
        }

        updatePaginationUI(); // 페이지 번호 및 버튼 상태 갱신
    }

    // 페이지 정보 업데이트 (예: 1 / 3 페이지)
    private void updatePaginationUI() {
        int totalPages = Math.max(1, (int) Math.ceil((double) messages.size() / itemsPerPage));
        pageInfoLabel.setText(currentPage + " / " + totalPages);

        // 페이지에 따라 이전/다음 버튼 활성화 여부 설정
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);
    }

    private void highlightSelectedLabel(Label clickedLabel) {
        if (selectedLabel != null) {
            selectedLabel.getStyleClass().remove("selected-mail-label");
        }

        // ✅ 항상 스타일 재적용
        clickedLabel.getStyleClass().remove("selected-mail-label");
        clickedLabel.getStyleClass().add("selected-mail-label");

        selectedLabel = clickedLabel;
    }

    // 특정 메일을 선택했을 때 호출되는 미리보기 로딩 함수
    private void handleMailPreview(int mailIndex, Label clickedLabel) {
        int actualIndex = (currentPage - 1) * itemsPerPage + mailIndex;

        if (actualIndex < messages.size()) {
            MessageDto selectedMessage = messages.get(actualIndex);

            // ✅ 이전과 같은 제목이더라도 항상 메일 미리보기 새로 로드
            loadMailPreviewPanel(selectedMessage);

            // ✅ 스타일도 항상 새로 적용 (같은 Label이라도)
            highlightSelectedLabel(clickedLabel);
        }
    }

    // 메일 제목 클릭 이벤트 핸들러들 (1~10번째 행 각각 연결됨)
    @FXML
    public void handleMailPreview1() {
        System.out.println("메일 1 클릭됨");
        { handleMailPreview(0, mailTitle1); }
    }
    @FXML public void handleMailPreview2() { System.out.println("메일 2 클릭됨"); handleMailPreview(1, mailTitle2); }
    @FXML public void handleMailPreview3() { System.out.println("메일 3 클릭됨"); handleMailPreview(2, mailTitle3); }
    @FXML public void handleMailPreview4() { System.out.println("메일 4 클릭됨"); handleMailPreview(3, mailTitle4); }
    @FXML public void handleMailPreview5() { System.out.println("메일 5 클릭됨"); handleMailPreview(4, mailTitle5); }
    @FXML public void handleMailPreview6() { System.out.println("메일 6 클릭됨"); handleMailPreview(5, mailTitle6); }
    @FXML public void handleMailPreview7() { System.out.println("메일 7 클릭됨"); handleMailPreview(6, mailTitle7); }
    @FXML public void handleMailPreview8() { System.out.println("메일 8 클릭됨"); handleMailPreview(7, mailTitle8); }
    @FXML public void handleMailPreview9() { System.out.println("메일 9 클릭됨"); handleMailPreview(8, mailTitle9); }
    @FXML public void handleMailPreview10() { System.out.println("메일 10 클릭됨"); handleMailPreview(9, mailTitle10); }

    /**
     * 메일 미리보기 패널을 로드하고 우측 컨테이너에 표시하는 메서드
     *
     * @param message 미리보기할 메일 데이터 (MessageDto)
     */
    private void loadMailPreviewPanel(MessageDto message) {
        try {
            // 1. FXML 파일을 로드하기 위한 FXMLLoader 생성
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/companycore/view/content/mail/dynamicMailPreviewPanel.fxml"));

            // 2. FXML 파일을 실제 노드로 로드
            Node mailPreviewPanel = loader.load();

            // 3. 해당 FXML의 컨트롤러를 가져옴
            DynamicMailPreviewController previewController = loader.getController();
            previewController.setMailController(this);
            previewController.setSelectedMessage(message);

            // 4. 현재 로그인한 사용자 정보 불러오기
            ApiClient apiClient = ApiClient.getInstance();
            User user = apiClient.getCurrentUser();

            // 1. 특정 메시지 ID에 해당하는 메시지 조회
            MessageDto selectedMessage = messageApiClient.getMessageById(
                    message.getMessageId(), user.getUserId());

            // 6. 컨트롤러에 메일 데이터를 전달하여 화면에 표시되도록 함
            previewController.setMailData(
                    selectedMessage.getSenderEmail(),           // ✅ 발신자: 실제 보낸 사람
                    selectedMessage.getReceiverEmail(),         // 수신자: 현재 사용자
                    selectedMessage.getTitle(),                 // 제목
                    selectedMessage.getContent(),               // 내용
                    selectedMessage.getSentAt(),                // 날짜
                    ""                                          // 첨부파일 (추후 확장)
            );
            System.out.println("CreatedAt: " + selectedMessage.getSentAt());

            // 7. 기존 우측 컨테이너의 내용을 비우고 새로운 미리보기 패널을 추가
            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(mailPreviewPanel);

        } catch (IOException e) {
            // 8. 로드 도중 에러 발생 시 경고창을 띄우고 로그 출력
            showAlert("오류", "메일 미리보기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // 메일쓰기 버튼 클릭 시 호출됨
    @FXML
    public void handleComposeMail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();

            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                // 부모 컨트롤러 설정 (메일 전송 후 리다이렉트를 위해)
                composeController.setParentController(this);
            }

            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(composeMailPanel);

        } catch (IOException e) {
            showAlert("오류", "메일쓰기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // 이전 페이지 버튼 클릭 시
    @FXML
    public void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateMailListUI();
        }
    }

    // 다음 페이지 버튼 클릭 시
    @FXML
    public void handleNextPage() {
        int totalPages = (int) Math.ceil((double) messages.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updateMailListUI();
        }
    }

    // 외부에서 현재 메일함 상태 설정 (예: "inbox", "sentMailbox", "allMailbox" 등)
    public void setCurrentMailbox(String mailbox) {
        currentMailboxType = mailbox;
        loadMessages(); // 메일함 타입에 따라 메일 목록 다시 불러옴
        showDefaultView(); // 기본 화면으로 돌아가기
    }

    // Alert 창 간편 호출 메서드
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 메일 전송 후 후처리 (메일 목록 새로고침)
    public void addSentMail(String recipient, String subject, String content, String attachmentName) {
        // 메일은 이미 ComposeMailController에서 전송되었으므로
        // 여기서는 단순히 메일 목록을 새로고침만 함
        loadMessages();
    }

    // 메일을 선택하지 않았을 때 보이는 기본 뷰
    public void showDefaultView() {
        rightContentContainer.getChildren().clear();
        VBox defaultView = new VBox();
        defaultView.setAlignment(javafx.geometry.Pos.CENTER);
        defaultView.setSpacing(20);

        Label iconLabel = new Label("📧");
        iconLabel.setStyle("-fx-font-size: 48px;");

        String messageText = "메일을 선택하거나 새 메일을 작성하세요";
        switch (currentMailboxType) {
            case "inbox":
                messageText = "받은 메일함 - 메일을 선택하거나 새 메일을 작성하세요";
                break;
            case "sentMailbox":
                messageText = "보낸 메일함 - 메일을 선택하거나 새 메일을 작성하세요";
                break;
            case "allMailbox":
            default:
                messageText = "전체 메일함 - 메일을 선택하거나 새 메일을 작성하세요";
                break;
        }

        Label messageLabel = new Label(messageText);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");

        defaultView.getChildren().addAll(iconLabel, messageLabel);
        rightContentContainer.getChildren().add(defaultView);
    }

    /**
     * 메일 목록으로 돌아가는 메서드
     * 메일 전송 후 호출되어 메일 목록을 새로고침하고 기본 뷰를 표시
     */
    public void returnToMailList() {
        // 현재 메일함의 메일 목록을 새로고침
        loadMessages();
        // 기본 뷰 표시
        showDefaultView();
    }

    /**
     * 전달 기능: 메일쓰기 창을 열고 수신자 필드에 이메일을 자동 입력
     */
    public void forwardMessageToCompose(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/composeMailPanel.fxml"));
            Node composeMailPanel = loader.load();

            ComposeMailController composeController = loader.getController();
            if (composeController != null) {
                composeController.setParentController(this);
                composeController.setRecipientEmail(email);
            }

            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(composeMailPanel);
        } catch (IOException e) {
            showAlert("오류", "메일쓰기를 로드할 수 없습니다.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}