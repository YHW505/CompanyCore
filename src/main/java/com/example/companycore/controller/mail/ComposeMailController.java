package com.example.companycore.controller.mail;

import com.example.companycore.model.dto.MessageDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
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

/**
 * 메일 작성 화면의 컨트롤러 클래스
 * 사용자가 수신자, 제목, 내용을 입력하고 메일을 전송하거나 취소할 수 있음.
 */
public class ComposeMailController {

    // FXML로 연결된 UI 요소들
    @FXML private TextField recipientField;   // 수신자 이메일 입력 필드
    @FXML private TextField subjectField;     // 메일 제목 입력 필드
    @FXML private TextArea contentArea;       // 메일 본문 입력 필드
    @FXML private Label attachmentLabel;      // 첨부파일 이름 표시 Label

    // 첨부파일로 선택된 파일
    private File selectedFile;

    // 메일 화면 전체를 제어하는 부모 컨트롤러 (보낸 메일 목록 등에 접근)
    private MailController parentController;

    // 현재 로그인된 사용자 정보를 얻기 위한 API 클라이언트
    private ApiClient apiClient;

    /**
     * [전송] 버튼 클릭 시 호출되는 메서드
     * 유효성 검사 → 서버 전송 → 로컬 저장 → 화면 초기화
     */
    @FXML
    public void handleSendMail() {
        // 입력 필드 유효성 검사
        if (validateMailForm()) {
            // 사용자 입력값 가져오기
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String content = contentArea.getText();
            String attachmentName = attachmentLabel.getText();

            // 첨부파일 없을 경우 빈 문자열 처리
            if (attachmentName == null || attachmentName.isEmpty()) {
                attachmentName = "";
            }

            // 현재 로그인한 사용자 정보 가져오기
            apiClient = ApiClient.getInstance();
            User user = apiClient.getCurrentUser();

            // 사용자 정보가 없을 경우 처리 중단
            if (user == null) {
                System.out.println("❌ 로그인한 사용자 정보를 찾을 수 없습니다.");
                return;
            }

            // 발신자 ID
            Long senderId = user.getUserId();

            // 메시지 객체 생성 및 정보 설정
            MessageDto message = new MessageDto();
            message.setReceiverEmail(recipient); // 받는 사람 이메일
            message.setTitle(subject);           // 제목
            message.setContent(content);         // 본문
            message.setSenderId(senderId);       // 보내는 사람 ID

            // 메시지 API 클라이언트를 통해 서버로 메시지 전송
            MessageApiClient client = MessageApiClient.getInstance();
            MessageDto sent = client.sendMessage(message, senderId); // 전송 결과를 받음

            if (sent != null) {
                System.out.println("✅ 서버에 메시지 전송 완료");
                
                // 성공 메시지 표시
                showAlert("성공", "메일이 성공적으로 전송되었습니다.", Alert.AlertType.INFORMATION);
                
                // 로컬 보낸 메일함에 추가
                if (parentController != null) {
                    parentController.addSentMail(recipient, subject, content, attachmentName);
                }

                // 폼 초기화 및 메일 목록으로 돌아감
                clearMailForm();
                if (parentController != null) {
                    parentController.returnToMailList();
                }
            } else {
                System.out.println("❌ 서버 메시지 전송 실패");
                System.out.println(message.getReceiverEmail());
                System.out.println(message.getSenderId());
                
                // 실패 메시지 표시
                showAlert("실패", "메일 전송에 실패했습니다. 다시 시도해주세요.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * [취소] 버튼 클릭 시 호출되는 메서드
     * 작성 중인 메일을 초기화하고 기본 화면으로 돌아감
     */
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

    /**
     * [첨부파일] 버튼 클릭 시 호출되는 메서드
     * 사용자가 로컬에서 파일을 선택할 수 있도록 함
     */
    @FXML
    public void handleAddAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");

        // 파일 선택 필터 설정
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("모든 파일", "*.*"),
                new FileChooser.ExtensionFilter("문서 파일", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("이미지 파일", "*.jpg", "*.png", "*.gif")
        );

        // 현재 창에서 파일 선택 대화상자 열기
        Stage stage = (Stage) recipientField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        // 선택한 파일이 있을 경우 이름 표시
        if (selectedFile != null) {
            attachmentLabel.setText(selectedFile.getName());
        }
    }

    /**
     * 입력 폼 유효성 검사
     */
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

    /**
     * 입력 폼 초기화
     */
    private void clearMailForm() {
        if (recipientField != null) recipientField.clear();
        if (subjectField != null) subjectField.clear();
        if (contentArea != null) contentArea.clear();
        if (attachmentLabel != null) attachmentLabel.setText("");
        selectedFile = null;
    }

    /**
     * 경고창 띄우기
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 부모 컨트롤러 설정 (메일 작성 후 리스트 갱신을 위해)
     */
    public void setParentController(MailController parentController) {
        this.parentController = parentController;
    }
}