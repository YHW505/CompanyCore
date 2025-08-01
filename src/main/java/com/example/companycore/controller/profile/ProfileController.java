package com.example.companycore.controller.profile;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    
    @FXML
    private PasswordField currentPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button changePasswordButton;
    
    @FXML
    private Button saveButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 이벤트 핸들러를 프로그래밍 방식으로 연결
        saveButton.setOnAction(event -> handleSave());
    }
    
    @FXML
    public void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // 입력 검증
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("오류", "모든 필드를 입력해주세요.", Alert.AlertType.ERROR);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showAlert("오류", "새 비밀번호가 일치하지 않습니다.", Alert.AlertType.ERROR);
            return;
        }
        
        if (newPassword.length() < 6) {
            showAlert("오류", "새 비밀번호는 최소 6자 이상이어야 합니다.", Alert.AlertType.ERROR);
            return;
        }
        
        // TODO: 실제 비밀번호 변경 로직 구현 (데이터베이스 연동)
        // 현재는 임시로 성공 메시지만 표시
        
        // 비밀번호 변경 성공
        showAlert("성공", "비밀번호가 성공적으로 변경되었습니다.", Alert.AlertType.INFORMATION);
        
        // 입력 필드 초기화
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    public void handleSave() {
        // TODO: 프로필 정보 저장 로직 구현 (데이터베이스 연동)
        // 현재는 임시로 성공 메시지만 표시
        
        showAlert("성공", "프로필 정보가 성공적으로 저장되었습니다.", Alert.AlertType.INFORMATION);
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 