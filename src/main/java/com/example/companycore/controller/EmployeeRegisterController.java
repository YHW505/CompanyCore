package com.example.companycore.controller;

import com.example.companycore.model.entity.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeRegisterController {
    
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
    private Button registerButton;
    
    @FXML
    private Button cancelButton;
    
    private static int nextEmployeeId = 2534013; // 다음 사번
    
    @FXML
    public void initialize() {
        setupForm();
        setupButtons();
    }
    
    private void setupForm() {
        // 사번 필드를 빈칸으로 시작 (직접 입력)
        employeeIdTextField.clear();
        employeeIdTextField.setDisable(false); // 수정 가능하도록 활성화
        
        // 비밀번호 필드에 초기 비밀번호 설정
        passwordField.setText("초기 비밀번호");
        passwordField.setDisable(true); // 초기 비밀번호는 수정 불가
        
        // 사원 이름과 역할 설정 (새 사원 등록이므로 기본값)
        employeeNameLabel.setText("새 사원");
        employeeRoleLabel.setText("사원");
    }
    
    private void setupButtons() {
        // 등록 버튼
        registerButton.setOnAction(event -> {
            System.out.println("등록 버튼 클릭됨");
            if (validateInput()) {
                registerEmployee();
                closeDialog();
            }
        });
        
        // 취소 버튼
        cancelButton.setOnAction(event -> {
            System.out.println("취소 버튼 클릭됨 (setupButtons에서)");
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
    
    private void registerEmployee() {
        try {
            // 새 사원 객체 생성
            Employee newEmployee = new Employee(
                nextEmployeeId,
                nameTextField.getText().trim(),
                employeeIdTextField.getText().trim(),
                departmentTextField.getText().trim(),
                addressTextField.getText().trim(),
                phoneNumberTextField.getText().trim(),
                emailTextField.getText().trim(),
                positionTextField.getText().trim(),
                passwordField.getText()
            );
            
            // 여기서 실제 데이터베이스에 저장하는 로직을 구현
            // 현재는 메모리에만 저장하는 예시
            
            // 다음 사번 증가
            nextEmployeeId++;
            
            showAlert("성공", "사원이 성공적으로 등록되었습니다.", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            showAlert("오류", "사원 등록 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void closeDialog() {
        System.out.println("closeDialog() 호출됨 (EmployeeRegister)");
        
        // 여러 방법으로 Stage를 찾아보기
        Stage stage = null;
        
        // 방법 1: contentArea를 통해 찾기
        if (contentArea != null && contentArea.getScene() != null) {
            stage = (Stage) contentArea.getScene().getWindow();
            System.out.println("contentArea를 통해 Stage 찾음: " + (stage != null));
        }
        
        // 방법 2: 다른 컨트롤을 통해 찾기
        if (stage == null && cancelButton != null && cancelButton.getScene() != null) {
            stage = (Stage) cancelButton.getScene().getWindow();
            System.out.println("cancelButton을 통해 Stage 찾음: " + (stage != null));
        }
        
        // 방법 3: registerButton을 통해 찾기
        if (stage == null && registerButton != null && registerButton.getScene() != null) {
            stage = (Stage) registerButton.getScene().getWindow();
            System.out.println("registerButton을 통해 Stage 찾음: " + (stage != null));
        }
        
        if (stage != null) {
            System.out.println("Stage 닫기 시도");
            stage.close();
        } else {
            System.out.println("Stage를 찾을 수 없음");
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