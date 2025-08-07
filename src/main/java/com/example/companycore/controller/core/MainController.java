package com.example.companycore.controller.core;

import com.example.companycore.controller.mail.MailController;
import com.example.companycore.service.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML
    private VBox contentArea;

    private Object currentContentController; // 현재 로드된 콘텐츠의 컨트롤러
    private Stage mainStage;
    private ApiClient apiClient;

    public void setStage(Stage stage) {
        this.mainStage = stage;
        setupCloseRequestHandler();
    }

    @FXML
    public void initialize() {
        this.apiClient = ApiClient.getInstance();
        // 초기화 작업
        setupNavigationHandlers();

        // 초기 콘텐츠 로드 (홈 화면)
        Platform.runLater(() -> {
            loadHomeContent();
            // Scene이 완전히 설정된 후 사이드바 통신 설정
            setupSidebarCommunication();
        });
    }

    private void setupCloseRequestHandler() {
        mainStage.setOnCloseRequest(event -> {
            event.consume(); // 기본 닫기 작업을 막습니다.

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("종료 확인");
            alert.setHeaderText("애플리케이션을 종료하거나 로그아웃할 수 있습니다.");
            alert.setContentText("수행할 작업을 선택하세요.");

            ButtonType exitButton = new ButtonType("종료");
            ButtonType logoutButton = new ButtonType("로그아웃");
            ButtonType cancelButton = new ButtonType("취소", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(exitButton, logoutButton, cancelButton);

            // 스타일시트 적용
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/com/example/companycore/view/styles.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");


            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == exitButton) {
                    Platform.exit(); // 애플리케이션 종료
                } else if (result.get() == logoutButton) {
                    handleLogout(); // 로그아웃 처리
                }
            }
        });
        
        // 전체화면 지원 설정
        setupFullscreenSupport();
    }
    
    private void setupFullscreenSupport() {
        if (mainStage != null && mainStage.getScene() != null) {
            Scene scene = mainStage.getScene();
            
            // F11 키로 전체화면 토글
            scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case F11:
                        toggleFullscreen();
                        break;
                    case ESCAPE:
                        if (mainStage.isFullScreen()) {
                            mainStage.setFullScreen(false);
                        }
                        break;
                }
            });
            
            // 창 크기 변경 시 리사이징 처리
            mainStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                handleWindowResize();
            });
            
            mainStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                handleWindowResize();
            });
        }
    }
    
    @FXML
    private void toggleFullscreen() {
        if (mainStage != null) {
            if (mainStage.isFullScreen()) {
                mainStage.setFullScreen(false);
            } else {
                mainStage.setFullScreen(true);
            }
        }
    }
    
    private void handleWindowResize() {
        if (mainStage != null && mainStage.getScene() != null) {
            double width = mainStage.getWidth();
            double height = mainStage.getHeight();
            
            // 최소 크기 보장
            if (width < 800) width = 800;
            if (height < 600) height = 600;
            
            // 루트 컨테이너 크기 조정
            if (mainStage.getScene().getRoot() != null) {
                Parent root = mainStage.getScene().getRoot();
                if (root instanceof javafx.scene.layout.Region) {
                    javafx.scene.layout.Region region = (javafx.scene.layout.Region) root;
                    region.setPrefSize(width, height);
                    region.setMinSize(800, 600);
                }
            }
        }
    }

    private void handleLogout() {
        // ApiClient에서 토큰 등 인증 정보 제거
        apiClient.clearToken();

        try {
            // 로그인 화면으로 돌아가기
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/login/loginView.fxml"));
            Parent loginRoot = loader.load();
            Scene scene = new Scene(loginRoot, 1400, 800);

            mainStage.setScene(scene);
            mainStage.setTitle("CompanyCore - 로그인");
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // 로그인 화면 로드 실패 시 오류 처리
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("오류");
            errorAlert.setHeaderText("로그아웃 중 오류가 발생했습니다.");
            errorAlert.setContentText("로그인 화면을 불러올 수 없습니다.");
            errorAlert.showAndWait();
        }
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

