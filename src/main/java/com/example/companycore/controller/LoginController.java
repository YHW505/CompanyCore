package com.example.companycore.controller;

import com.example.companycore.service.ApiClient;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 로그인 화면을 관리하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 사용자 로그인 인증 처리
 * - 로그인 상태 UI 관리
 * - 백그라운드 로그인 처리
 * - 메인 페이지로의 네비게이션
 * - 사번 조회 기능 (준비 중)
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class LoginController implements Initializable {

    // ==================== FXML UI 컴포넌트 ====================
    
    /** 사용자명(사번) 입력 필드 */
    @FXML private TextField usernameField;
    
    /** 비밀번호 입력 필드 */
    @FXML private PasswordField passwordField;
    
    /** 로그인 버튼 */
    @FXML private Button loginButton;
    
    /** 상태 메시지 표시 라벨 */
    @FXML private Label statusLabel;
    
    /** 로딩 인디케이터 */
    @FXML private ProgressIndicator loadingIndicator;
    
    /** 클릭 가능한 텍스트 (사번 조회 등) */
    @FXML private Label clickableText;

    // ==================== 서비스 및 상태 관리 ====================
    
    /** API 클라이언트 인스턴스 */
    private ApiClient apiClient;

    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * UI 컴포넌트 초기화 및 서비스 설정을 수행
     * 
     * @param location FXML 파일의 위치
     * @param resources 리소스 번들
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ApiClient 인스턴스 가져오기
        this.apiClient = ApiClient.getInstance();

        // 초기화 로직
        loadingIndicator.setVisible(false);
    }

    // ==================== 이벤트 핸들러 메서드 ====================
    
    /**
     * 로그인 버튼 클릭 시 호출되는 메서드
     * 사용자 입력 검증 후 백그라운드에서 로그인 처리를 수행
     */
    @FXML
    private void handleLogin() {
        String employeeCode = usernameField.getText().trim();
        String password = passwordField.getText();

        // 입력값 검증
        if (employeeCode.isEmpty() || password.isEmpty()) {
            statusLabel.setText("사번과 비밀번호를 입력하세요.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // UI 상태 변경 (로딩 시작)
        setLoadingState(true);

        // 백그라운드에서 로그인 처리
        Task<Boolean> loginTask = createLoginTask(employeeCode, password);
        
        // 로그인 성공 시 처리
        loginTask.setOnSucceeded(e -> {
            setLoadingState(false);
            handleLoginResult(loginTask.getValue());
        });

        // 로그인 실패 시 처리
        loginTask.setOnFailed(e -> {
            setLoadingState(false);
            handleLoginError(loginTask.getException());
        });

        // 백그라운드 스레드에서 로그인 작업 실행
        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * 사번 조회 버튼 클릭 시 호출되는 메서드
     * 현재는 준비 중 메시지만 표시
     */
    @FXML
    private void handleEmployeeIdInquiry() {
        statusLabel.setText("사번 조회 기능은 준비 중입니다.");
        statusLabel.setStyle("-fx-text-fill: orange;");
    }

    // ==================== 로그인 처리 메서드 ====================
    
    /**
     * 로그인 작업을 생성하는 메서드
     * 
     * @param employeeCode 사번
     * @param password 비밀번호
     * @return 로그인 작업 Task
     */
    private Task<Boolean> createLoginTask(String employeeCode, String password) {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return apiClient.authenticate(employeeCode, password);
            }
        };
    }
    
    /**
     * 로그인 결과를 처리하는 메서드
     * 
     * @param loginSuccess 로그인 성공 여부
     */
    private void handleLoginResult(Boolean loginSuccess) {
        if (loginSuccess) {
            statusLabel.setText("로그인 성공!");
            statusLabel.setStyle("-fx-text-fill: green;");
            navigateToMainPage();
        } else {
            statusLabel.setText("로그인 실패. 사번 또는 비밀번호를 확인하세요.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    /**
     * 로그인 오류를 처리하는 메서드
     * 
     * @param exception 발생한 예외
     */
    private void handleLoginError(Throwable exception) {
        statusLabel.setText("로그인 처리 중 오류가 발생했습니다: " + exception.getMessage());
        statusLabel.setStyle("-fx-text-fill: red;");
    }

    // ==================== UI 상태 관리 메서드 ====================
    
    /**
     * 로딩 상태를 설정하는 메서드
     * 
     * @param isLoading 로딩 중 여부
     */
    private void setLoadingState(boolean isLoading) {
        loadingIndicator.setVisible(isLoading);
        loginButton.setDisable(isLoading);
        
        if (isLoading) {
            statusLabel.setText("로그인 중...");
            statusLabel.setStyle("-fx-text-fill: blue;");
        }
    }

    // ==================== 네비게이션 메서드 ====================
    
    /**
     * 로그인 성공 시 메인 페이지로 이동하는 메서드
     * 메인 대시보드 화면을 로드하고 현재 창을 교체
     */
    private void navigateToMainPage() {
        try {
            // 메인 대시보드 FXML 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/mainDashBoardView.fxml"));
            Parent root = loader.load();

            // 현재 창의 씬을 메인 대시보드로 교체
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1400, 800);

            stage.setScene(scene);
            stage.setTitle("CompanyCore - 메인");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("메인 페이지 로드 중 오류가 발생했습니다.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
