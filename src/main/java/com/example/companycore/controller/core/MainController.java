package com.example.companycore.controller.core;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

public class MainController {

    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView logoText;
    @FXML
    private VBox contentArea;
    
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
    
    private void setupNavigationHandlers() {
        // 네비게이션 메뉴 클릭 이벤트 처리
        // 실제 구현에서는 각 메뉴에 대한 이벤트 핸들러를 추가
    }
    
    private void setupSidebarCommunication() {
        // 사이드바 컨트롤러와의 통신 설정
        // 현재 씬의 사용자 데이터에 메인 컨트롤러 참조 저장
        if (contentArea != null && contentArea.getScene() != null) {
            contentArea.getScene().setUserData(this);
        }
    }
    
    // 사이드바에서 호출되는 콘텐츠 로드 메서드들
    public void loadHomeContent() {
        loadContent("home");
    }
    
    public void loadAttendanceContent() {
        loadContent("attendance");
    }
    
    public void loadMailContent() {
        // 메일 화면 콘텐츠를 메인 영역에 로드
        loadContent("mail");
    }
    
    public void loadTasksContent() {
        loadContent("tasks");
    }
    
    public void loadCalendarContent() {
        loadContent("calendar");
    }
    
    public void loadProfileContent() {
        loadContent("profile");
    }
    
    public void loadHRManagementContent() {
        loadContent("hrManagement");
    }
    
    public void loadContent(String contentType) {
        // 콘텐츠 타입에 따라 메인 영역의 콘텐츠를 변경
        String fxmlPath = "";
        try {
            switch (contentType) {
                case "home":
                    fxmlPath = "/com/example/companycore/view/content/home/homeContent.fxml";
                    break;
                case "attendance":
                    fxmlPath = "/com/example/companycore/view/content/attendanceContent.fxml";
                    break;
                case "mail":
                    fxmlPath = "/com/example/companycore/view/content/mailContent.fxml";
                    break;
                case "tasks":
                    fxmlPath = "/com/example/companycore/view/content/tasks/taskListContent.fxml";
                    break;
                case "calendar":
                    fxmlPath = "/com/example/companycore/view/content/calendar/calendarContent.fxml";
                    break;
                case "profile":
                    fxmlPath = "/com/example/companycore/view/content/profile/profileContent.fxml";
                    break;
                case "hrManagement":
                    fxmlPath = "/com/example/companycore/view/content/hr/hrManagementContent.fxml";
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
                case "taskList":
                    fxmlPath = "/com/example/companycore/view/content/tasks/taskListContent.fxml";
                    break;
                case "approvalRequest":
                    fxmlPath = "/com/example/companycore/view/content/tasks/approvalRequestContent.fxml";
                    break;
                case "meetingList":
                    fxmlPath = "/com/example/companycore/view/content/tasks/meetingListContent.fxml";
                    break;
                case "approvalApproval":
                    fxmlPath = "/com/example/companycore/view/content/tasks/approvalApprovalContent.fxml";
                    break;
                case "announcements":
                    fxmlPath = "/com/example/companycore/view/content/tasks/announcementsContent.fxml";
                    break;
                default:
                    fxmlPath = "/com/example/companycore/view/content/home/homeContent.fxml";
                    break;
            }
            
            // FXML 로더를 사용하여 콘텐츠 로드
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent content = loader.load();
            
            // 메일함 컨트롤러에 메일함 타입 설정
            if (contentType.equals("allMailbox") || contentType.equals("inbox") || contentType.equals("sentMailbox")) {
                try {
                    com.example.companycore.controller.mail.MailController mailController = loader.getController();
                    if (mailController != null) {
                        mailController.setCurrentMailbox(contentType);
                    }
                } catch (Exception e) {
                    // 컨트롤러가 MailController가 아닌 경우 무시
                }
            }
            
            // 메인 영역의 콘텐츠를 교체
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            System.err.println("콘텐츠 로드 중 오류 발생: " + e.getMessage());
            System.err.println("FXML 경로: " + fxmlPath);
            e.printStackTrace();
            
            // 오류 발생 시 기본 홈 콘텐츠를 로드
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/companycore/view/content/home/homeContent.fxml"));
                javafx.scene.Parent content = loader.load();
                if (contentArea != null) {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(content);
                }
            } catch (Exception recoveryException) {
                System.err.println("복구 시도도 실패: " + recoveryException.getMessage());
                recoveryException.printStackTrace();
            }
        }
    }
}

