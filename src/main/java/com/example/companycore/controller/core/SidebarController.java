package com.example.companycore.controller.core;

import com.example.companycore.controller.core.MainController;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

/**
 * 사이드바 메뉴를 관리하는 컨트롤러 클래스
 *
 * 주요 기능:
 * - 메인 메뉴 선택 및 스타일 관리
 * - 하위 메뉴 토글 및 스타일 관리
 * - 메뉴 클릭 시 해당 콘텐츠 로드
 * - 메인 컨트롤러와의 통신
 *
 * @author Company Core Team
 * @version 1.0
 */
public class SidebarController {

    // ==================== 메인 메뉴 UI 컴포넌트 ====================

    /**
     * 홈 메뉴
     */
    @FXML
    private HBox homeMenu;

    /**
     * 근태관리 메뉴
     */
    @FXML
    private HBox attendanceMenu;

    /**
     * 메일 메뉴
     */
    @FXML
    private HBox mailMenu;

    /**
     * 업무 메뉴
     */
    @FXML
    private HBox tasksMenu;

    /**
     * 캘린더 메뉴
     */
    @FXML
    private HBox calendarMenu;

    /**
     * 인사관리 메뉴
     */
    @FXML
    private HBox hrManagementMenu;

    /**
     * 프로필 메뉴
     */
    @FXML
    private HBox profileMenu;

    // ==================== 로고 이미지 ====================

    /**
     * 로고 이미지
     */
    @FXML
    private ImageView logoImage;

    /**
     * 로고 텍스트
     */
    @FXML
    private ImageView logoText;

    // ==================== 하위 메뉴 관련 ====================

    /**
     * 근태관리 하위 메뉴
     */
    @FXML
    private VBox attendanceSubMenu;

    /**
     * 휴가승인 하위 메뉴
     */
    @FXML
    private HBox leaveApprovalSubMenu;

    /**
     * 메일 하위 메뉴
     */
    @FXML
    private VBox mailSubMenu;

    /**
     * 업무 하위 메뉴
     */
    @FXML
    private VBox tasksSubMenu;

    /**
     * 결재승인 하위 메뉴
     */
    @FXML
    private HBox approvalApprovalSubMenu;

    /**
     * 하위 메뉴 화살표 표시
     */
    @FXML
    private Label attendanceArrow;
    @FXML
    private Label mailArrow;
    @FXML
    private Label tasksArrow;

    // ==================== 상태 관리 ====================

    /**
     * 현재 선택된 메뉴
     */
    private HBox currentSelectedMenu;

    // ==================== 초기화 메서드 ====================

    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 초기 상태로 홈 메뉴를 선택된 상태로 설정
     */
    @FXML
    public void initialize() {
        // 초기 상태로 홈 메뉴를 선택된 상태로 설정
        setSelectedMenu(homeMenu);
        Platform.runLater(this::setupRoleBasedUI);
    }

    private void setupRoleBasedUI() {
        ApiClient apiClient = ApiClient.getInstance();
        User currentUser = apiClient.getCurrentUser();

        if (currentUser != null) {
            int positionId = currentUser.getPositionId();
//            System.out.printf("---=-==--=-=-=-=-=-==--=-=-=-=-=-=-=-=%d", positionId);
            if (positionId != 1) {
                // 메뉴를 비활성화하고 흐리게 표시
                leaveApprovalSubMenu.setDisable(true);
                leaveApprovalSubMenu.setOpacity(0.5);
                approvalApprovalSubMenu.setDisable(true);
                approvalApprovalSubMenu.setOpacity(0.5);
            }
        }
        if(currentUser != null) {
            int departmentId = currentUser.getDepartmentId();
            if (departmentId != 1){
                //메뉴를 비활성화하고 흐리게 표시
                hrManagementMenu.setDisable(true);
                hrManagementMenu.setOpacity(0.5);
            }
        }
    }

    // ==================== 메뉴 스타일 관리 메서드 ====================

    /**
     * 메뉴를 선택된 상태로 설정하고 스타일을 변경
     *
     * @param menu 선택할 메뉴
     */
    private void setSelectedMenu(HBox menu) {
        // 이전 선택된 메뉴의 스타일을 초기화
        if (currentSelectedMenu != null) {
            resetMenuStyle(currentSelectedMenu);
        }

        // 새로운 메뉴를 선택된 상태로 설정
        currentSelectedMenu = menu;
        setMenuSelectedStyle(menu);
    }

    /**
     * 메뉴의 스타일을 기본 상태로 초기화
     *
     * @param menu 초기화할 메뉴
     */
    private void resetMenuStyle(HBox menu) {
        menu.setStyle("-fx-background-color: transparent; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        // 모든 라벨의 텍스트 색상을 기본 색상으로 변경
        menu.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (!label.getText().contains("🏠") && !label.getText().contains("👤") &&
                        !label.getText().contains("✉") && !label.getText().contains("📄") &&
                        !label.getText().contains("📅") && !label.getText().contains("▶")) {
                    label.setStyle("-fx-text-fill: #9197B3;");
                }
            }
        });
    }

    /**
     * 메뉴를 선택된 상태의 스타일로 변경
     *
     * @param menu 선택된 메뉴
     */
    private void setMenuSelectedStyle(HBox menu) {
        menu.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        // 모든 라벨의 텍스트 색상을 굵은 글씨로 변경
        menu.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (!label.getText().contains("🏠") && !label.getText().contains("👤") &&
                        !label.getText().contains("📧") && !label.getText().contains("📄") &&
                        !label.getText().contains("📅") && !label.getText().contains("▶")) {
                    label.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 14px;");
                }
            }
        });
    }

    /**
     * 하위 메뉴를 선택된 상태의 스타일로 변경
     *
     * @param subMenu 선택된 하위 메뉴
     */
    private void setSubMenuSelectedStyle(HBox subMenu) {
        // 하위 메뉴 선택 시 굵은 글씨로 표시
        subMenu.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (!label.getText().contains("📊") && !label.getText().contains("📝") &&
                        !label.getText().contains("✅") && !label.getText().contains("📬") &&
                        !label.getText().contains("📥") && !label.getText().contains("📤") &&
                        !label.getText().contains("📋") && !label.getText().contains("📢")) {
                    label.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 13px;");
                }
            }
        });
    }

    /**
     * 하위 메뉴의 스타일을 기본 상태로 초기화
     *
     * @param subMenu 초기화할 하위 메뉴
     */
    private void resetSubMenuStyle(HBox subMenu) {
        // 하위 메뉴 스타일 초기화
        subMenu.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (!label.getText().contains("📊") && !label.getText().contains("📝") &&
                        !label.getText().contains("✅") && !label.getText().contains("📬") &&
                        !label.getText().contains("📥") && !label.getText().contains("📤") &&
                        !label.getText().contains("📋") && !label.getText().contains("📢")) {
                    label.setStyle("-fx-text-fill: #9197B3; -fx-font-weight: normal; -fx-font-size: 12px;");
                }
            }
        });
    }

    // ==================== 메인 메뉴 이벤트 핸들러 ====================

    /**
     * 홈 메뉴 클릭 시 호출되는 메서드
     * 홈 콘텐츠를 로드하고 모든 하위 메뉴를 닫음
     */
    @FXML
    public void handleHomeClick() {
        setSelectedMenu(homeMenu);

        // 모든 하위 메뉴 닫기
        closeAllSubMenus();

        // 메인 컨트롤러에 홈 화면 로드 요청
        Platform.runLater(() -> {
            loadHomeContent();
        });
    }

    /**
     * 근태관리 메뉴 클릭 시 호출되는 메서드
     * 근태관리 하위 메뉴를 토글하고 첫 번째 하위 메뉴 콘텐츠를 로드
     */
    @FXML
    public void handleAttendanceClick() {
        setSelectedMenu(attendanceMenu);

        // 하위 메뉴 토글
        if (attendanceSubMenu.isVisible()) {
            attendanceSubMenu.setVisible(false);
            attendanceSubMenu.setManaged(false);
            attendanceArrow.setText("▶");
        } else {
            // 다른 하위 메뉴들 닫기
            mailSubMenu.setVisible(false);
            mailSubMenu.setManaged(false);
            mailArrow.setText("▶");
            tasksSubMenu.setVisible(false);
            tasksSubMenu.setManaged(false);
            tasksArrow.setText("▶");

            // 근태관리 하위 메뉴 열기
            attendanceSubMenu.setVisible(true);
            attendanceSubMenu.setManaged(true);
            attendanceArrow.setText("▼");
        }

        // 첫 번째 하위 메뉴로 이동
        Platform.runLater(() -> {
            loadAttendanceRecordContent();
        });
        // 첫 번째 하위 메뉴 굵게 표시
        resetAllSubMenuStyles();
        setSubMenuSelectedStyle((HBox) attendanceSubMenu.getChildren().get(0));
    }

    /**
     * 메일 메뉴 클릭 시 호출되는 메서드
     * 메일 하위 메뉴를 토글하고 첫 번째 하위 메뉴 콘텐츠를 로드
     */
    @FXML
    public void handleMailClick() {
        setSelectedMenu(mailMenu);

        // 하위 메뉴 토글
        if (mailSubMenu.isVisible()) {
            mailSubMenu.setVisible(false);
            mailSubMenu.setManaged(false);
            mailArrow.setText("▶");
        } else {
            // 다른 하위 메뉴들 닫기
            attendanceSubMenu.setVisible(false);
            attendanceSubMenu.setManaged(false);
            attendanceArrow.setText("▶");
            tasksSubMenu.setVisible(false);
            tasksSubMenu.setManaged(false);
            tasksArrow.setText("▶");

            // 메일 하위 메뉴 열기
            mailSubMenu.setVisible(true);
            mailSubMenu.setManaged(true);
            mailArrow.setText("▼");
        }

        // 첫 번째 하위 메뉴로 이동
        Platform.runLater(() -> {
            loadAllMailboxContent();
        });
        // 첫 번째 하위 메뉴 굵게 표시
        resetAllSubMenuStyles();
        setSubMenuSelectedStyle((HBox) mailSubMenu.getChildren().get(0));
    }

    /**
     * 업무 메뉴 클릭 시 호출되는 메서드
     * 업무 하위 메뉴를 토글하고 첫 번째 하위 메뉴(회의 목록) 콘텐츠를 로드
     */
    @FXML
    public void handleTasksClick() {
        setSelectedMenu(tasksMenu);

        // 하위 메뉴 토글
        if (tasksSubMenu.isVisible()) {
            tasksSubMenu.setVisible(false);
            tasksSubMenu.setManaged(false);
            tasksArrow.setText("▶");
        } else {
            // 다른 하위 메뉴들 닫기
            attendanceSubMenu.setVisible(false);
            attendanceSubMenu.setManaged(false);
            attendanceArrow.setText("▶");
            mailSubMenu.setVisible(false);
            mailSubMenu.setManaged(false);
            mailArrow.setText("▶");

            // 업무 하위 메뉴 열기
            tasksSubMenu.setVisible(true);
            tasksSubMenu.setManaged(true);
            tasksArrow.setText("▼");
        }

        // 첫 번째 하위 메뉴로 이동 (회의 목록)
        Platform.runLater(() -> {
            loadMeetingListContent();
        });
        // 첫 번째 하위 메뉴 굵게 표시
        resetAllSubMenuStyles();
        setSubMenuSelectedStyle((HBox) tasksSubMenu.getChildren().get(0));
    }

    /**
     * 캘린더 메뉴 클릭 시 호출되는 메서드
     * 캘린더 콘텐츠를 로드하고 모든 하위 메뉴를 닫음
     */
    @FXML
    public void handleCalendarClick() {
        setSelectedMenu(calendarMenu);

        // 모든 하위 메뉴 닫기
        closeAllSubMenus();

        // 메인 컨트롤러에 캘린더 화면 로드 요청
        Platform.runLater(() -> {
            loadCalendarContent();
        });
    }

    /**
     * 인사관리 메뉴 클릭 시 호출되는 메서드
     * 인사관리 콘텐츠를 로드하고 모든 하위 메뉴를 닫음
     */
    @FXML
    public void handleHRManagementClick() {
        setSelectedMenu(hrManagementMenu);

        // 모든 하위 메뉴 닫기
        closeAllSubMenus();

        // 메인 컨트롤러에 인사관리 화면 로드 요청
        Platform.runLater(() -> {
            loadHRManagementContent();
        });
    }

    /**
     * 프로필 메뉴 클릭 시 호출되는 메서드
     * 프로필 콘텐츠를 로드하고 모든 하위 메뉴를 닫음
     */
    @FXML
    public void handleProfileClick() {
        setSelectedMenu(profileMenu);

        // 모든 하위 메뉴 닫기
        closeAllSubMenus();

        // 메인 컨트롤러에 프로필 화면 로드 요청
        Platform.runLater(() -> {
            loadProfileContent();
        });
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 모든 하위 메뉴를 닫는 메서드
     */
    private void closeAllSubMenus() {
        attendanceSubMenu.setVisible(false);
        attendanceSubMenu.setManaged(false);
        attendanceArrow.setText("▶");

        mailSubMenu.setVisible(false);
        mailSubMenu.setManaged(false);
        mailArrow.setText("▶");

        tasksSubMenu.setVisible(false);
        tasksSubMenu.setManaged(false);
        tasksArrow.setText("▶");
    }

    /**
     * 모든 하위 메뉴의 스타일을 초기화하는 메서드
     */
    private void resetAllSubMenuStyles() {
        // 모든 하위 메뉴 스타일 초기화
        attendanceSubMenu.getChildren().forEach(subMenu -> {
            if (subMenu instanceof HBox) {
                resetSubMenuStyle((HBox) subMenu);
            }
        });
        mailSubMenu.getChildren().forEach(subMenu -> {
            if (subMenu instanceof HBox) {
                resetSubMenuStyle((HBox) subMenu);
            }
        });
        tasksSubMenu.getChildren().forEach(subMenu -> {
            if (subMenu instanceof HBox) {
                resetSubMenuStyle((HBox) subMenu);
            }
        });
    }

    // ==================== 하위 메뉴 이벤트 핸들러 ====================

    /**
     * 출근기록부 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleAttendanceRecordClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) attendanceSubMenu.getChildren().get(0));
        Platform.runLater(() -> {
            loadAttendanceRecordContent();
        });
    }

    /**
     * 휴가신청 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleLeaveApplicationClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) attendanceSubMenu.getChildren().get(1));
        Platform.runLater(() -> {
            loadLeaveApplicationContent();
        });
    }

    /**
     * 휴가승인 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleLeaveApprovalClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) attendanceSubMenu.getChildren().get(2));
        Platform.runLater(() -> {
            loadLeaveApprovalContent();
        });
    }

    /**
     * 전체 메일함 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleAllMailboxClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) mailSubMenu.getChildren().get(0));
        Platform.runLater(() -> {
            loadAllMailboxContent();
        });
    }

    /**
     * 받은 메일함 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleInboxClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) mailSubMenu.getChildren().get(1));
        Platform.runLater(() -> {
            loadInboxContent();
        });
    }

    /**
     * 보낸 메일함 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleSentMailboxClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) mailSubMenu.getChildren().get(2));
        Platform.runLater(() -> {
            loadSentMailboxContent();
        });
    }

    /**
     * 결재 요청 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleApprovalRequestClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) tasksSubMenu.getChildren().get(1));
        Platform.runLater(() -> {
            loadApprovalRequestContent();
        });
    }

    /**
     * 결재 승인 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleApprovalApprovalClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) tasksSubMenu.getChildren().get(2));
        Platform.runLater(() -> {
            loadApprovalApprovalContent();
        });
    }

    /**
     * 회의 목록 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleMeetingListClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) tasksSubMenu.getChildren().get(0));
        Platform.runLater(() -> {
            loadMeetingListContent();
        });
    }

    /**
     * 공지사항 하위 메뉴 클릭 시 호출되는 메서드
     */
    @FXML
    public void handleAnnouncementsClick() {
        // 모든 하위 메뉴 스타일 초기화
        resetAllSubMenuStyles();
        // 선택된 하위 메뉴 굵게 표시
        setSubMenuSelectedStyle((HBox) tasksSubMenu.getChildren().get(3));
        Platform.runLater(() -> {
            loadAnnouncementsContent();
        });
    }

    // ==================== 메인 컨트롤러 통신 메서드 ====================

    /**
     * 홈 콘텐츠를 로드하는 메서드
     * 메인 컨트롤러와 통신하여 홈 화면을 표시
     */
    private void loadHomeContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadHomeContent();
            }
        } catch (Exception e) {
            System.err.println("홈 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 근태관리 콘텐츠를 로드하는 메서드
     */
    private void loadAttendanceContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadAttendanceContent();
            }
        } catch (Exception e) {
            System.err.println("근태관리 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 메일 콘텐츠를 로드하는 메서드
     */
    private void loadMailContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadMailContent();
            }
        } catch (Exception e) {
            System.err.println("메일 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 업무 콘텐츠를 로드하는 메서드
     */
    private void loadTasksContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadTasksContent();
            }
        } catch (Exception e) {
            System.err.println("업무 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 캘린더 콘텐츠를 로드하는 메서드
     */
    private void loadCalendarContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadCalendarContent();
            }
        } catch (Exception e) {
            System.err.println("캘린더 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 프로필 콘텐츠를 로드하는 메서드
     */
    private void loadProfileContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadProfileContent();
            }
        } catch (Exception e) {
            System.err.println("프로필 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 인사관리 콘텐츠를 로드하는 메서드
     */
    private void loadHRManagementContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadHRManagementContent();
            }
        } catch (Exception e) {
            System.err.println("인사관리 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    // ==================== 하위 메뉴 콘텐츠 로드 메서드 ====================

    /**
     * 출근기록부 콘텐츠를 로드하는 메서드
     */
    private void loadAttendanceRecordContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("attendanceRecord");
            }
        } catch (Exception e) {
            System.err.println("출근기록부 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 휴가신청 콘텐츠를 로드하는 메서드
     */
    private void loadLeaveApplicationContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("leaveApplication");
            }
        } catch (Exception e) {
            System.err.println("휴가신청 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 휴가승인 콘텐츠를 로드하는 메서드
     */
    private void loadLeaveApprovalContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("leaveApproval");
            }
        } catch (Exception e) {
            System.err.println("휴가승인 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 전체 메일함 콘텐츠를 로드하는 메서드
     */
    private void loadAllMailboxContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("allMailbox");
            }
        } catch (Exception e) {
            System.err.println("전체 메일함 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 받은 메일함 콘텐츠를 로드하는 메서드
     */
    private void loadInboxContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("inbox");
            }
        } catch (Exception e) {
            System.err.println("받은 메일함 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 보낸 메일함 콘텐츠를 로드하는 메서드
     */
    private void loadSentMailboxContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("sentMailbox");
            }
        } catch (Exception e) {
            System.err.println("보낸 메일함 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 결재 요청 콘텐츠를 로드하는 메서드
     */
    private void loadApprovalRequestContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("approvalRequest");
            }
        } catch (Exception e) {
            System.err.println("결재 요청 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 결재 승인 콘텐츠를 로드하는 메서드
     */
    private void loadApprovalApprovalContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("approvalApproval");
            }
        } catch (Exception e) {
            System.err.println("결재 승인 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 회의 목록 콘텐츠를 로드하는 메서드
     */
    private void loadMeetingListContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("meetingList");
            }
        } catch (Exception e) {
            System.err.println("회의 목록 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }

    /**
     * 공지사항 콘텐츠를 로드하는 메서드
     */
    private void loadAnnouncementsContent() {
        try {
            MainController mainController = (MainController) homeMenu.getScene().getUserData();
            if (mainController != null) {
                mainController.loadContent("announcements");
            }
        } catch (Exception e) {
            System.err.println("공지사항 콘텐츠 로드 중 오류: " + e.getMessage());
        }
    }
}
