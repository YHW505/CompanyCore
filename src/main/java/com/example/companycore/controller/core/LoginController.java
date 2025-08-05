package com.example.companycore.controller.core;

import com.example.companycore.service.ApiClient;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Label clickableText;

    private ApiClient apiClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ApiClient 인스턴스 가져오기
        this.apiClient = ApiClient.getInstance();

        // 초기화 로직
        loadingIndicator.setVisible(false);

        // Enter 키 이벤트 핸들러 추가
        setupEnterKeyHandlers();
    }

    /**
     * Enter 키 이벤트 핸들러 설정
     * 사용자명 필드와 비밀번호 필드에서 Enter 키를 누르면 로그인 버튼이 클릭됨
     */
    private void setupEnterKeyHandlers() {
        // 사용자명 필드에서 Enter 키 처리
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        // 비밀번호 필드에서 Enter 키 처리
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String employeeCode = usernameField.getText().trim();
        String password = passwordField.getText();

        if (employeeCode.isEmpty() || password.isEmpty()) {
            statusLabel.setText("사번과 비밀번호를 입력하세요.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // UI 상태 변경 (로딩 시작)
        loadingIndicator.setVisible(true);
        loginButton.setDisable(true);
        statusLabel.setText("로그인 중...");
        statusLabel.setStyle("-fx-text-fill: blue;");

        // 백그라운드에서 로그인 처리
        Task<Boolean> loginTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return apiClient.authenticate(employeeCode, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            loginButton.setDisable(false);

            Boolean loginSuccess = loginTask.getValue();
            if (loginSuccess) {
                statusLabel.setText("로그인 성공!");
                statusLabel.setStyle("-fx-text-fill: green;");
                navigateToMainPage();
            } else {
                statusLabel.setText("로그인 실패. 사번 또는 비밀번호를 확인하세요.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        loginTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            loginButton.setDisable(false);

            Throwable exception = loginTask.getException();
            statusLabel.setText("로그인 처리 중 오류가 발생했습니다: " + exception.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void navigateToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/mainDashBoardView.fxml"));
            Parent root = loader.load();

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

    @FXML
    public String handleEmployeeIdInquiry() {
//        statusLabel.setText("사번 조회 기능은 준비 중입니다.");
//        statusLabel.setStyle("-fx-text-fill: orange;");
        try {
            // FXML 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/login/searchCodeDialog.fxml"));
            Parent root = loader.load();

            // Controller 가져오기
            SearchCodeDialogController controller = loader.getController();

            // Stage 생성 및 설정
            Stage popupStage = new Stage();
            popupStage.setTitle("사번 조회");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);

            // Controller에 Stage 전달
            controller.setStage(popupStage);

            // Scene 생성 및 Stage에 설정
            Scene scene = new Scene(root, 300, 400);
            popupStage.setScene(scene);

            // 팝업 표시하고 결과 대기
            popupStage.showAndWait();

            // 결과 반환
            return controller.getResult();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
