package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;
    @FXML
    private void initialize() {
        // Enter 키로 로그인 가능하도록
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String employeeId = usernameField.getText().trim();
        String password = passwordField.getText();

        // 입력 검증
        if (employeeId.isEmpty()) {
            showMessage("직원 ID를 입력해주세요.", "#dc2626");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showMessage("비밀번호를 입력해주세요.", "#dc2626");
            passwordField.requestFocus();
            return;
        }

        // 로그인 처리 (임시)
        if ("admin".equals(employeeId) && "1234".equals(password)) {
            showMessage("로그인 성공!", "#16a34a");
            // TODO: 메인 화면으로 이동
            loadMainDashboard();
        } else {
            showMessage("잘못된 직원 ID 또는 비밀번호입니다.", "#dc2626");
            passwordField.clear();
            usernameField.selectAll();
            usernameField.requestFocus();
        }
    }

    private void showMessage(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
    }

    private void loadMainDashboard() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/mainDashBoardView.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            currentStage.setScene(scene);
            currentStage.setTitle("CompanyCore - 직원 관리 시스템");
            currentStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("화면 전환 중 오류가 발생했습니다.", "#dc2626");
        }
    }
}


