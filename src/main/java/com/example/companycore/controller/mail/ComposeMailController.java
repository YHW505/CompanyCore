package com.example.companycore.controller.mail;

import com.example.companycore.model.dto.MessageDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.MessageApiClient;

import javafx.fxml.FXML;
import javafx.concurrent.Task;
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
        if (validateMailForm()) {
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String content = contentArea.getText();
            String attachmentName = attachmentLabel.getText();

            if (attachmentName == null || attachmentName.isEmpty()) {
                attachmentName = "";
            }

            apiClient = ApiClient.getInstance();
            User user = apiClient.getCurrentUser();

            if (user == null) {
                System.out.println("❌ 로그인한 사용자 정보를 찾을 수 없습니다.");
                showAlert("오류", "로그인한 사용자 정보를 찾을 수 없습니다.", Alert.AlertType.ERROR);
                return;
            }

            Long senderId = user.getUserId();

            MessageDto message = new MessageDto();
            message.setReceiverEmail(recipient);
            message.setTitle(subject);
            message.setContent(content);
            message.setSenderId(senderId);
            message.setMessageType("EMAIL");

            if (selectedFile != null) {
                try {
                    String sanitizedFilename = sanitizeFilename(selectedFile.getName());
                    String encodedFile = com.example.companycore.util.FileUtil.encodeFileToBase64(selectedFile);
                    message.setAttachmentContent(encodedFile);
                    message.setAttachmentFilename(sanitizedFilename);
                    message.setAttachmentSize(selectedFile.length());
                    message.setAttachmentContentType(com.example.companycore.util.FileUtil.getMimeType(selectedFile.getName()));
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    showAlert("오류", "파일을 처리하는 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                System.out.println("handleSendMail - selectedFile is null. No attachment to send.");
            }

            Task<MessageDto> sendMailTask = new Task<>() {
                @Override
                protected MessageDto call() throws Exception {
                    MessageApiClient client = MessageApiClient.getInstance();
                    return client.sendMessage(message, senderId);
                }
            };

            String finalAttachmentName = attachmentName;
            sendMailTask.setOnSucceeded(event -> {
                MessageDto sent = sendMailTask.getValue();
                if (sent != null) {
                    System.out.println("✅ 서버에 메시지 전송 완료");
                    showAlert("성공", "메일이 성공적으로 전송되었습니다.", Alert.AlertType.INFORMATION);
                    if (parentController != null) {
                        parentController.addSentMail(recipient, subject, content, finalAttachmentName);
                    }
                    clearMailForm();
                } else {
                    System.out.println("❌ 서버 메시지 전송 실패");
                    showAlert("실패", "메일 전송에 실패했습니다. 다시 시도해주세요.", Alert.AlertType.ERROR);
                }
            });

            sendMailTask.setOnFailed(event -> {
                Throwable e = sendMailTask.getException();
                System.err.println("메일 전송 중 예외 발생: " + e.getMessage());
                e.printStackTrace();
                showAlert("오류", "메일 전송 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
            });

            new Thread(sendMailTask).start();
        }
    }

    /**
     * 파일명을 안전한 문자로만 구성되도록 처리합니다.
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return "attachment";
        // 한글, 영문, 숫자, 그리고 특정 특수문자(._-)만 허용하고 나머지는 제거합니다.
        return filename.replaceAll("[^a-zA-Z0-9가-힣._-]", "");
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
            String filePath = selectedFile.getAbsolutePath(); // ✅ 전체 경로 얻기
            System.out.println("파일 경로: " + filePath);
            System.out.println("handleAddAttachment - selectedFile: " + selectedFile.getName());
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
     * 파일 이름에서 확장자를 제외한 부분을 반환합니다.
     */
    private String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName; // 확장자가 없는 경우
        }
        return fileName.substring(0, dotIndex);
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

    /**
     * 전달 기능: 수신자 이메일을 외부에서 세팅
     */
    public void setRecipientEmail(String email) {
        if (recipientField != null) {
            recipientField.setText(email);
        }
    }
    public void setRecipientContent(String content) {
        if (contentArea != null) {
            contentArea.setText(content);
        }
    }
}