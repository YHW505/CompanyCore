package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

/**
 * 메인 애플리케이션 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 애플리케이션의 메인 화면 관리
 * - 사이드바와 콘텐츠 영역 간의 통신 중계
 * - 다양한 콘텐츠 화면 로드 및 전환
 * - 네비게이션 이벤트 처리
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class MainController {
    
    // ==================== FXML UI 컴포넌트 ====================
    
    /** 로고 이미지 */
    @FXML private ImageView logoImage;
    
    /** 로고 텍스트 */
    @FXML private ImageView logoText;
    
    /** 메인 콘텐츠 영역 */
    @FXML private VBox contentArea;
    
    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 애플리케이션 시작 시 필요한 설정을 수행
     */
    @FXML
    public void initialize() {
        // 초기화 작업
        setupNavigationHandlers();
        
        // 초기 콘텐츠 로드 (홈 화면)
        Platform.runLater(() -> {
            loadHomeContent();
            // Scene이 완전히 설정된 후 사이드바 통신 설정
            setupSidebarCommunication();
        });
    }
    
    // ==================== 설정 메서드 ====================
    
    /**
     * 네비게이션 이벤트 핸들러 설정
     * 각 메뉴 클릭 시 호출될 이벤트 핸들러를 등록
     */
    private void setupNavigationHandlers() {
        // 네비게이션 메뉴 클릭 이벤트 처리
        // 실제 구현에서는 각 메뉴에 대한 이벤트 핸들러를 추가
    }
    
    /**
     * 사이드바 컨트롤러와의 통신 설정
     * 현재 씬의 사용자 데이터에 메인 컨트롤러 참조를 저장하여
     * 사이드바에서 메인 컨트롤러의 메서드를 호출할 수 있도록 함
     */
    private void setupSidebarCommunication() {
        // 사이드바 컨트롤러와의 통신 설정
        // 현재 씬의 사용자 데이터에 메인 컨트롤러 참조 저장
        if (contentArea != null && contentArea.getScene() != null) {
            contentArea.getScene().setUserData(this);
        }
    }
    
    // ==================== 콘텐츠 로드 메서드 ====================
    
    /**
     * 홈 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadHomeContent() {
        loadContent("home");
    }
    
    /**
     * 근태관리 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadAttendanceContent() {
        loadContent("attendance");
    }
    
    /**
     * 메일 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadMailContent() {
        // 메일 화면 콘텐츠를 메인 영역에 로드
        loadContent("mail");
    }
    
    /**
     * 업무 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadTasksContent() {
        loadContent("tasks");
    }
    
    /**
     * 캘린더 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadCalendarContent() {
        loadContent("calendar");
    }
    
    /**
     * 프로필 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadProfileContent() {
        loadContent("profile");
    }
    
    /**
     * 인사관리 콘텐츠를 로드하는 메서드
     * 사이드바에서 호출됨
     */
    public void loadHRManagementContent() {
        loadContent("hrManagement");
    }
    
    // ==================== 하위 메뉴 콘텐츠 로드 메서드 ====================
    
    /**
     * 콘텐츠 타입에 따라 해당 FXML을 로드하여 메인 영역에 표시
     * 
     * @param contentType 로드할 콘텐츠 타입
     */
    public void loadContent(String contentType) {
        // 콘텐츠 타입에 따라 메인 영역의 콘텐츠를 변경
        try {
            String fxmlPath = "";
            switch (contentType) {
                case "home":
                    fxmlPath = "/com/example/companycore/view/content/homeContent.fxml";
                    break;
                case "attendance":
                    fxmlPath = "/com/example/companycore/view/content/attendanceContent.fxml";
                    break;
                case "mail":
                    fxmlPath = "/com/example/companycore/view/content/mailContent.fxml";
                    break;
                case "tasks":
                    fxmlPath = "/com/example/companycore/view/content/tasksContent.fxml";
                    break;
                case "calendar":
                    fxmlPath = "/com/example/companycore/view/content/calendar/calendarContent.fxml";
                    break;
                case "profile":
                    fxmlPath = "/com/example/companycore/view/content/profileContent.fxml";
                    break;
                case "hrManagement":
                    fxmlPath = "/com/example/companycore/view/content/hrManagementContent.fxml";
                    break;
                // 하위 메뉴 콘텐츠들
                case "attendanceRecord":
                    fxmlPath = "/com/example/companycore/view/content/attendance/attendanceRecordContent.fxml";
                    break;
                case "leaveApplication":
                    fxmlPath = "/com/example/companycore/view/content/attendance/leaveApplicationContent.fxml";
                    break;
                case "leaveApproval":
                    fxmlPath = "/com/example/companycore/view/content/attendance/leaveApprovalContent.fxml";
                    break;
                case "allMailbox":
                    fxmlPath = "/com/example/companycore/view/content/mail/allMailboxContent.fxml";
                    break;
                case "inbox":
                    fxmlPath = "/com/example/companycore/view/content/mail/inboxContent.fxml";
                    break;
                case "sentMailbox":
                    fxmlPath = "/com/example/companycore/view/content/mail/sentMailboxContent.fxml";
                    break;
                case "approvalRequest":
                    fxmlPath = "/com/example/companycore/view/content/tasks/approvalRequestContent.fxml";
                    break;
                case "approvalApproval":
                    fxmlPath = "/com/example/companycore/view/content/tasks/approvalApprovalContent.fxml";
                    break;
                case "meetingList":
                    fxmlPath = "/com/example/companycore/view/content/tasks/meetingListContent.fxml";
                    break;
                case "announcements":
                    fxmlPath = "/com/example/companycore/view/content/tasks/announcementsContent.fxml";
                    break;
                default:
                    System.err.println("알 수 없는 콘텐츠 타입: " + contentType);
                    return;
            }
            
            // FXML 로더를 사용하여 콘텐츠 로드
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent content = loader.load();
            
            // 기존 콘텐츠 제거 후 새 콘텐츠 추가
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            
        } catch (Exception e) {
            System.err.println("콘텐츠 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

