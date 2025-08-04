package com.example.companycore.controller.mail;

import com.example.companycore.model.dto.MessageDto;
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
import java.util.List;

public class MailController {

    // 우측 컨텐츠 영역 (메일 미리보기나 작성 폼이 들어갈 자리)
    @FXML
    private StackPane rightContentContainer;

    // 10개의 메일 제목 Label과 각각의 행 (Row)
    @FXML private Label mailTitle1, mailTitle2, mailTitle3, mailTitle4, mailTitle5, mailTitle6, mailTitle7, mailTitle8, mailTitle9, mailTitle10;
    @FXML private HBox mailRow1, mailRow2, mailRow3, mailRow4, mailRow5, mailRow6, mailRow7, mailRow8, mailRow9, mailRow10;

    // 페이지 네비게이션 버튼 및 페이지 정보 Label
    @FXML private Button prevPageButton, nextPageButton;
    @FXML private Label pageInfoLabel;

    private int currentPage = 1; // 현재 페이지
    private final int itemsPerPage = 10; // 페이지당 표시할 메일 수

    private final MessageApiClient messageApiClient = MessageApiClient.getInstance(); // 메시지 API 클라이언트
    private List<MessageDto> receivedMessages = new ArrayList<>(); // 받은 메일 목록

    // UI 업데이트를 위한 제목 Label들과 행(HBox)들을 리스트로 정리
    private List<Label> mailTitleLabels;
    private List<HBox> mailRows;

    // 컨트롤러 초기화 시 자동 호출됨
    @FXML
    public void initialize() {
        // Label들과 Row들을 순서대로 리스트에 저장
        mailTitleLabels = List.of(mailTitle1, mailTitle2, mailTitle3, mailTitle4, mailTitle5, mailTitle6, mailTitle7, mailTitle8, mailTitle9, mailTitle10);
        mailRows = List.of(mailRow1, mailRow2, mailRow3, mailRow4, mailRow5, mailRow6, mailRow7, mailRow8, mailRow9, mailRow10);

        loadReceivedMessages(); // 받은 메일 불러오기
    }

    // 현재 로그인한 사용자의 받은 메일을 서버에서 조회
    private void loadReceivedMessages() {
        long currentUserId = ApiClient.getInstance().getCurrentUser().getUserId();

        // 받은 메시지 중 이메일 유형만 가져옴 (필터 조건 있음)
        receivedMessages = messageApiClient.getAllMessages(currentUserId, "EMAIL", null);

        updateInboxUI(); // UI에 반영
    }

    // 받은 메일 리스트를 현재 페이지 기준으로 UI에 표시
    private void updateInboxUI() {
        // 우선 전체 행 숨김 처리
        mailRows.forEach(row -> row.setVisible(false));

        // 현재 페이지에 해당하는 인덱스 범위 계산
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, receivedMessages.size());

        // 각 행에 메일 제목을 표시하고 행 보이게 하기
        for (int i = startIndex; i < endIndex; i++) {
            MessageDto message = receivedMessages.get(i);
            int displayIndex = i - startIndex;

            mailTitleLabels.get(displayIndex).setText(message.getTitle());
            mailRows.get(displayIndex).setVisible(true);
        }

        updatePaginationUI(); // 페이지 번호 및 버튼 상태 갱신
    }

    // 페이지 정보 업데이트 (예: 1 / 3 페이지)
    private void updatePaginationUI() {
        int totalPages = Math.max(1, (int) Math.ceil((double) receivedMessages.size() / itemsPerPage));
        pageInfoLabel.setText(currentPage + " / " + totalPages);

        // 페이지에 따라 이전/다음 버튼 활성화 여부 설정
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);
    }

    // 특정 메일을 선택했을 때 호출되는 미리보기 로딩 함수
    private void handleMailPreview(int mailIndex) {
        int actualIndex = (currentPage - 1) * itemsPerPage + mailIndex;

        if (actualIndex < receivedMessages.size()) {
            MessageDto selectedMessage = receivedMessages.get(actualIndex);
            loadMailPreviewPanel(selectedMessage);
        }
    }

    // 메일 제목 클릭 이벤트 핸들러들 (1~10번째 행 각각 연결됨)
    @FXML public void handleMailPreview1() { handleMailPreview(0); }
    @FXML public void handleMailPreview2() { handleMailPreview(1); }
    @FXML public void handleMailPreview3() { handleMailPreview(2); }
    @FXML public void handleMailPreview4() { handleMailPreview(3); }
    @FXML public void handleMailPreview5() { handleMailPreview(4); }
    @FXML public void handleMailPreview6() { handleMailPreview(5); }
    @FXML public void handleMailPreview7() { handleMailPreview(6); }
    @FXML public void handleMailPreview8() { handleMailPreview(7); }
    @FXML public void handleMailPreview9() { handleMailPreview(8); }
    @FXML public void handleMailPreview10() { handleMailPreview(9); }

    // 우측 미리보기 패널 로드
    private void loadMailPreviewPanel(MessageDto message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/mail/dynamicMailPreviewPanel.fxml"));
            Node mailPreviewPanel = loader.load();

            DynamicMailPreviewController previewController = loader.getController();
            previewController.setMailData(
                    message.getSenderName(),
                    message.getReceiverName(),
                    message.getTitle(),
                    message.getContent(),
                    message.getCreatedAt().toString(),
                    "" // 첨부파일 등 추가 필요 시 확장 가능
            );

            rightContentContainer.getChildren().clear();
            rightContentContainer.getChildren().add(mailPreviewPanel);

        } catch (IOException e) {
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
                // 필요시 composeController.setParentController(this);
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
            updateInboxUI();
        }
    }

    // 다음 페이지 버튼 클릭 시
    @FXML
    public void handleNextPage() {
        int totalPages = (int) Math.ceil((double) receivedMessages.size() / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updateInboxUI();
        }
    }

    // 외부에서 현재 메일함 상태 설정 (예: "inbox", "sent" 등)
    public void setCurrentMailbox(String mailbox) {
        if ("inbox".equals(mailbox)) {
            loadReceivedMessages(); // 받은 편지함이면 받은 메일 다시 불러옴
        } else {
            // 나중에 보낸 메일함, 휴지통 등 다른 메일함 추가 가능
        }
    }

    // Alert 창 간편 호출 메서드
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 메일 전송 후 후처리 (메일 추가, 새로고침 등)
    public void addSentMail(String recipient, String subject, String content, String attachmentName) {
        long senderId = ApiClient.getInstance().getCurrentUser().getUserId();

        MessageDto newMessage = new MessageDto(recipient, subject, content, "EMAIL");

        // TODO: 첨부파일 처리 로직 필요시 여기에 추가

        MessageDto sentMessage = messageApiClient.sendMessage(newMessage, senderId);

        if (sentMessage != null) {
            showAlert("성공", "메일이 성공적으로 전송되었습니다.", Alert.AlertType.INFORMATION);
            loadReceivedMessages(); // 받은 편지함 새로고침
            showDefaultView(); // 기본 화면으로 돌아가기
        } else {
            showAlert("실패", "메일 전송에 실패했습니다.", Alert.AlertType.ERROR);
        }
    }

    // 메일을 선택하지 않았을 때 보이는 기본 뷰
    public void showDefaultView() {
        rightContentContainer.getChildren().clear();
        VBox defaultView = new VBox();
        defaultView.setAlignment(javafx.geometry.Pos.CENTER);
        defaultView.setSpacing(20);

        Label iconLabel = new Label("📧");
        iconLabel.setStyle("-fx-font-size: 48px;");

        Label messageLabel = new Label("메일을 선택하거나 새 메일을 작성하세요");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");

        defaultView.getChildren().addAll(iconLabel, messageLabel);
        rightContentContainer.getChildren().add(defaultView);
    }
}