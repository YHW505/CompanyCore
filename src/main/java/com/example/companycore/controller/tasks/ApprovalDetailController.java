package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.ApprovalItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

/**
 * 결재 상세 정보를 표시하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 결재 상세 정보 표시
 * - 결재 내용 및 첨부파일 관리
 * - 다이얼로그 창 제어
 * - 결재 데이터 바인딩
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class ApprovalDetailController {

    // ==================== FXML UI 컴포넌트 ====================
    
    /** 결재 제목 라벨 */
    @FXML private Label titleLabel;
    
    /** 작성자 라벨 */
    @FXML private Label writerLabel;
    
    /** 날짜 라벨 */
    @FXML private Label dateLabel;
    
    /** 부서 라벨 */
    @FXML private Label departmentLabel;
    
    /** 결재 내용 텍스트 영역 */
    @FXML private TextArea contentArea;
    
    /** 첨부파일 리스트뷰 */
    @FXML private ListView<String> attachmentListView;

    // ==================== 상태 관리 ====================
    
    /** 다이얼로그 스테이지 참조 */
    private Stage dialogStage;

    // ==================== 설정 메서드 ====================
    
    /**
     * 다이얼로그 스테이지를 설정
     * 다이얼로그 창을 제어하기 위한 Stage 참조를 저장
     * 
     * @param stage 다이얼로그 스테이지
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * 결재 아이템의 데이터를 UI 컴포넌트에 바인딩
     * 결재의 모든 정보(제목, 작성자, 날짜, 부서, 내용, 첨부파일)를 화면에 표시
     * 
     * @param item 표시할 결재 아이템
     */
    public void setApprovalItem(ApprovalItem item) {
        // 기본 정보 설정 (null 체크 포함)
        titleLabel.setText(item.getTitle() != null ? item.getTitle() : "");
        writerLabel.setText(item.getAuthor() != null ? item.getAuthor() : "");
        dateLabel.setText(item.getDate() != null ? item.getDate() : "");
        departmentLabel.setText(item.getDepartment() != null ? item.getDepartment() : "");
        contentArea.setText(item.getContent() != null ? item.getContent() : "");

        // 첨부파일 설정
        setupAttachments(item.getAttachments());
    }
    
    /**
     * 첨부파일 목록을 설정
     * 첨부파일이 있으면 리스트뷰에 표시하고, 없으면 빈 목록으로 설정
     * 
     * @param attachments 첨부파일 목록
     */
    private void setupAttachments(List<String> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            // 첨부파일이 있으면 ObservableList로 변환하여 리스트뷰에 설정
            attachmentListView.setItems(FXCollections.observableArrayList(attachments));
        } else {
            // 첨부파일이 없으면 리스트뷰를 비움
            attachmentListView.getItems().clear();
        }
    }

    // ==================== 이벤트 핸들러 메서드 ====================
    
    /**
     * 닫기 버튼 클릭 시 호출되는 메서드
     * 다이얼로그 창을 닫는 기능을 수행
     * FXML에서 onAction으로 연결됨
     */
    @FXML
    private void onClose() {
        if (dialogStage != null) {
            // 다이얼로그 스테이지가 설정되어 있으면 해당 스테이지를 닫음
            dialogStage.close();
        } else {
            // 다이얼로그 스테이지가 설정되어 있지 않으면 현재 씬의 윈도우를 숨김
            contentArea.getScene().getWindow().hide();
        }
    }
}