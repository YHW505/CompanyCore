package com.example.companycore.controller.hr; 

import com.example.companycore.model.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class EmployeeEditController {
    
    @FXML
    private VBox contentArea;
    
    @FXML
    private Label employeeNameLabel;
    
    @FXML
    private Label employeeRoleLabel;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private TextField employeeIdTextField;
    
    @FXML
    private TextField departmentTextField;
    
    @FXML
    private TextField addressTextField;
    
    @FXML
    private TextField phoneNumberTextField;
    
    @FXML
    private TextField emailTextField;
    
    @FXML
    private TextField positionTextField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button modifyPasswordButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private User user;
    private boolean isPasswordChange = false;
    
    @FXML
    public void initialize() {
        setupButtons();
    }
    
    public void setUser(User user) {
        this.user = user;
        loadUserData();
    }
    
    private void loadUserData() {
        if (user != null) {
            // 사원 정보를 폼에 로드
            employeeNameLabel.setText(user.getUsername());
            employeeRoleLabel.setText("사원");
            
            nameTextField.setText(user.getUsername());
            employeeIdTextField.setText(user.getEmployeeCode());
            departmentTextField.setText(user.getDepartment() != null ? user.getDepartment().getDepartmentName() : "");
            addressTextField.setText(""); // User Entity에는 address 필드가 없음
            phoneNumberTextField.setText(user.getPhone());
            emailTextField.setText(user.getEmail());
            positionTextField.setText(user.getPosition() != null ? user.getPosition().getPositionName() : "");
            
            // 비밀번호 필드는 비워둠 (보안상)
            passwordField.clear();
            confirmPasswordField.clear();
        }
    }
    
    private void setupButtons() {
        // 비밀번호 수정 버튼
        modifyPasswordButton.setOnAction(event -> {
            isPasswordChange = !isPasswordChange;
            passwordField.setDisable(!isPasswordChange);
            confirmPasswordField.setDisable(!isPasswordChange);
            
            if (isPasswordChange) {
                modifyPasswordButton.setText("비밀번호 수정 취소");
                passwordField.setPromptText("새 비밀번호 입력");
                confirmPasswordField.setPromptText("새 비밀번호 확인");
            } else {
                modifyPasswordButton.setText("비밀번호 수정");
                passwordField.clear();
                confirmPasswordField.clear();
                passwordField.setPromptText("");
                confirmPasswordField.setPromptText("");
            }
        });
        
        // 저장 버튼
        saveButton.setOnAction(event -> {
            if (validateInput()) {
                saveUserData();
                closeDialog();
            }
        });
        
        // 취소 버튼
        cancelButton.setOnAction(event -> {
            closeDialog();
        });
    }
    
    private boolean validateInput() {
        // 필수 필드 검증
        if (nameTextField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이름을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        if (employeeIdTextField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "사번을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        if (departmentTextField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "부서를 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        if (emailTextField.getText().trim().isEmpty()) {
            showAlert("입력 오류", "이메일을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        // 이메일 형식 검증
        if (!isValidEmail(emailTextField.getText().trim())) {
            showAlert("입력 오류", "올바른 이메일 형식을 입력해주세요.", Alert.AlertType.ERROR);
            return false;
        }
        
        // 전화번호 형식 검증 (선택사항)
        if (!phoneNumberTextField.getText().trim().isEmpty()) {
            if (!isValidPhoneNumber(phoneNumberTextField.getText().trim())) {
                showAlert("입력 오류", "올바른 전화번호 형식을 입력해주세요. (예: 010-1234-5678)", Alert.AlertType.ERROR);
                return false;
            }
        }
        
        // 비밀번호 변경 시 검증
        if (isPasswordChange) {
            if (passwordField.getText().trim().isEmpty()) {
                showAlert("입력 오류", "새 비밀번호를 입력해주세요.", Alert.AlertType.ERROR);
                return false;
            }
            
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("입력 오류", "비밀번호가 일치하지 않습니다.", Alert.AlertType.ERROR);
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {    
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^01[0-9]-[0-9]{4}-[0-9]{4}$";
        return phoneNumber.matches(phoneRegex);
    }
    
    private void saveUserData() {
        try {
            // 사원 정보 업데이트
            user.setUsername(nameTextField.getText().trim());
            user.setEmployeeCode(employeeIdTextField.getText().trim());
            user.setEmail(emailTextField.getText().trim());
            user.setPhone(phoneNumberTextField.getText().trim());
            
            // 비밀번호 변경 시
            if (isPasswordChange) {
                user.setPassword(passwordField.getText());
            }
            
            // 여기서 실제 데이터베이스에 저장하는 로직을 구현
            // 현재는 메모리에만 저장하는 예시
            
            showAlert("성공", "사원 정보가 성공적으로 수정되었습니다.", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            showAlert("오류", "사원 정보 수정 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void closeDialog() {
        // 여러 방법으로 Stage를 찾아보기
        Stage stage = null;
        
        // 방법 1: contentArea를 통해 찾기
        if (contentArea != null && contentArea.getScene() != null) {
            stage = (Stage) contentArea.getScene().getWindow();
        }
        
        // 방법 2: 다른 컨트롤을 통해 찾기
        if (stage == null && cancelButton != null && cancelButton.getScene() != null) {
            stage = (Stage) cancelButton.getScene().getWindow();
        }
        
        // 방법 3: saveButton을 통해 찾기
        if (stage == null && saveButton != null && saveButton.getScene() != null) {
            stage = (Stage) saveButton.getScene().getWindow();
        }
        
        if (stage != null) {
            stage.close();
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 