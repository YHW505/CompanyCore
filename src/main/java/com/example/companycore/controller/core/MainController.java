package com.example.companycore.controller.core;

import com.example.companycore.controller.mail.MailController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

public class MainController {

    @FXML
    private VBox contentArea;

    private Object currentContentController; // 현재 로드된 콘텐츠의 컨트롤러

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
    }

    private void setupSidebarCommunication() {
        // 사이드바 컨트롤러와의 통신 설정
        if (contentArea != null && contentArea.getScene() != null) {
            contentArea.getScene().setUserData(this);
        }
    }

    // 콘텐츠 로드 메서드들
    public void loadHomeContent() { loadContent("home"); }
    public void loadAttendanceContent() { loadContent("attendance"); }
    public void loadMailContent() { loadContent("mail"); }
    public void loadTasksContent() { loadContent("tasks"); }
    public void loadCalendarContent() { loadContent("calendar"); }
    public void loadProfileContent() { loadContent("profile"); }
    public void loadHRManagementContent() { loadContent("hrManagement"); }

    public void loadContent(String contentType) {
        // 이전에 로드된 컨트롤러가 MailController의 인스턴스이면 shutdown() 호출
        if (currentContentController instanceof MailController) {
            ((MailController) currentContentController).shutdown();
        }

        String fxmlPath = getFxmlPath(contentType);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            currentContentController = loader.getController(); // 새 컨트롤러 저장

            // 메일함 컨트롤러에 메일함 타입 설정
            if (currentContentController instanceof MailController) {
                ((MailController) currentContentController).setCurrentMailbox(contentType);
            }

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (Exception e) {
            handleFxmlLoadError(fxmlPath, e);
        }
    }

    private String getFxmlPath(String contentType) {
        switch (contentType) {
            case "home": return "/com/example/companycore/view/content/home/homeContent.fxml";
            case "attendance": return "/com/example/companycore/view/content/attendanceContent.fxml";
            case "mail": return "/com/example/companycore/view/content/mail/allMailboxContent.fxml";
            case "tasks": return "/com/example/companycore/view/content/tasks/taskListContent.fxml";
            case "calendar": return "/com/example/companycore/view/content/calendar/calendarContent.fxml";
            case "profile": return "/com/example/companycore/view/content/profile/profileContent.fxml";
            case "hrManagement": return "/com/example/companycore/view/content/hr/hrManagementContent.fxml";
            case "attendanceRecord": return "/com/example/companycore/view/content/attendance/attendanceRecordContent.fxml";
            case "leaveApplication": return "/com/example/companycore/view/content/attendance/leaveApplicationContent.fxml";
            case "leaveApproval": return "/com/example/companycore/view/content/attendance/leaveApprovalContent.fxml";
            case "allMailbox": return "/com/example/companycore/view/content/mail/allMailboxContent.fxml";
            case "inbox": return "/com/example/companycore/view/content/mail/inboxContent.fxml";
            case "sentMailbox": return "/com/example/companycore/view/content/mail/sentMailboxContent.fxml";
            case "taskList": return "/com/example/companycore/view/content/tasks/taskListContent.fxml";
            case "approvalRequest": return "/com/example/companycore/view/content/tasks/approvalRequestContent.fxml";
            case "meetingList": return "/com/example/companycore/view/content/tasks/meetingListContent.fxml";
            case "approvalApproval": return "/com/example/companycore/view/content/tasks/approvalApprovalContent.fxml";
            case "announcements": return "/com/example/companycore/view/content/tasks/announcementsContent.fxml";
            default: return "/com/example/companycore/view/content/home/homeContent.fxml";
        }
    }

    private void handleFxmlLoadError(String fxmlPath, Exception e) {
        System.err.println("콘텐츠 로드 중 오류 발생: " + e.getMessage());
        System.err.println("FXML 경로: " + fxmlPath);
        e.printStackTrace();

        // 오류 발생 시 기본 홈 콘텐츠를 로드
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/home/homeContent.fxml"));
            Parent content = loader.load();
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

