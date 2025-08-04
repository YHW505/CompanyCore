package com.example.companycore.controller.mail;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * DynamicMailPreviewController는 메일 미리보기 UI에 데이터를 동적으로 표시하는 컨트롤러입니다.
 * 받은 메일, 보낸 메일, 전체 메일 등의 정보를 상황에 맞게 구분하여 UI 구성 요소(Label, TextArea)에 출력합니다.
 */
public class DynamicMailPreviewController {

    // =======================
    // FXML UI 요소 정의
    // =======================

    /** 발신자 라벨 */
    @FXML
    private Label senderLabel;

    /** 수신자 라벨 */
    @FXML
    private Label recipientLabel;

    /** 메일 제목 라벨 */
    @FXML
    private Label subjectLabel;

    /** 날짜 라벨 */
    @FXML
    private Label dateLabel;

    /** 첨부파일 라벨 */
    @FXML
    private Label attachmentLabel;

    /** 메일 본문 표시 영역 */
    @FXML
    private TextArea contentTextArea;

    // ==================================================
    // 공통 메일 표시 메서드 (메일의 일반 정보 전달용)
    // ==================================================

    /**
     * 모든 유형의 메일에 대해 공통적으로 UI에 데이터를 표시합니다.
     *
     * @param sender     발신자 이름
     * @param recipient  수신자 이름
     * @param subject    메일 제목
     * @param content    메일 본문
     * @param date       발신/수신 날짜
     * @param attachment 첨부파일 이름
     */
    public void setMailData(String sender, String recipient, String subject, String content, String date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
        recipientLabel.setText(recipient != null ? recipient : "수신자");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");
        attachmentLabel.setText((attachment != null && !attachment.isEmpty()) ? attachment : "첨부파일 없음");
    }

    // ==================================================
    // 보낸 메일 표시 메서드 (나 → 다른 사람)
    // ==================================================

    /**
     * 보낸 메일함에서 사용할 데이터 설정 메서드입니다.
     * 발신자는 항상 "나"로 표시되며, 수신자 정보가 중요합니다.
     *
     * @param recipient  수신자 이름
     * @param subject    메일 제목
     * @param content    메일 본문
     * @param date       보낸 날짜
     * @param attachment 첨부파일 이름
     */
    public void setSentMailData(String recipient, String subject, String content, String date, String attachment) {
        senderLabel.setText("나");
        recipientLabel.setText(recipient != null ? recipient : "수신자");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");
        attachmentLabel.setText((attachment != null && !attachment.isEmpty()) ? attachment : "첨부파일 없음");
    }

    // ==================================================
    // 받은 메일 표시 메서드 (다른 사람 → 나)
    // ==================================================

    /**
     * 받은 메일함에서 사용할 데이터 설정 메서드입니다.
     * 수신자는 항상 "나"로 표시되며, 발신자 정보가 중요합니다.
     *
     * @param sender     발신자 이름
     * @param subject    메일 제목
     * @param content    메일 본문
     * @param date       받은 날짜
     * @param attachment 첨부파일 이름
     */
    public void setReceivedMailData(String sender, String subject, String content, String date, String attachment) {
        senderLabel.setText(sender != null ? sender : "발신자");
        recipientLabel.setText("나");
        subjectLabel.setText(subject != null ? subject : "제목 없음");
        contentTextArea.setText(content != null ? content : "");
        dateLabel.setText(date != null ? date : "");
        attachmentLabel.setText((attachment != null && !attachment.isEmpty()) ? attachment : "첨부파일 없음");
    }
}