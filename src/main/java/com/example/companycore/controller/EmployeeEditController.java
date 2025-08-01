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
    
    private Employee employee;
    private boolean isPasswordChange = false;
    
    @FXML
    public void initialize() {
        setupButtons();
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
        loadEmployeeData();
    }
    
    private void loadEmployeeData() {
        if (employee != null) {
            // 사원 정보를 폼에 로드
            employeeNameLabel.setText(employee.getName());
            employeeRoleLabel.setText("사원");
            
            nameTextField.setText(employee.getName());
            employeeIdTextField.setText(employee.getEmployeeId());
            departmentTextField.setText(employee.getDepartment());
            addressTextField.setText(employee.getAddress());
            phoneNumberTextField.setText(employee.getPhoneNumber());
            emailTextField.setText(employee.getEmail());
            positionTextField.setText(employee.getPosition());
            
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
                passwordField.setPromptText("새 비밀번호를 입력하세요");
                confirmPasswordField.setPromptText("새 비밀번호를 다시 입력하세요");
            } else {
                modifyPasswordButton.setText("비밀번호 수정");
                passwordField.clear();
                confirmPasswordField.clear();
                passwordField.setPromptText("비밀번호 변경입니다?");
                confirmPasswordField.setPromptText("비밀번호 변경입니다? 2");
            }
        });
        
        // 저장 버튼
        saveButton.setOnAction(event -> {
            if (validateInput()) {
                saveEmployeeData();
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
        
        // 비밀번호 변경 시 검증
        if (isPasswordChange) {
            if (passwordField.getText().trim().isEmpty()) {
                showAlert("입력 오류", "새 비밀번호를 입력해주세요.", Alert.AlertType.ERROR);
                return false;
            }
            
            if (confirmPasswordField.getText().trim().isEmpty()) {
                showAlert("입력 오류", "비밀번호 확인을 입력해주세요.", Alert.AlertType.ERROR);
                return false;
            }
            
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("입력 오류", "비밀번호가 일치하지 않습니다.", Alert.AlertType.ERROR);
                return false;
            }
        }
        
        return true;
    }
    
    private void saveEmployeeData() {
        if (employee != null) {
            // 사원 정보 업데이트
            employee.setName(nameTextField.getText().trim());
            employee.setEmployeeId(employeeIdTextField.getText().trim());
            employee.setDepartment(departmentTextField.getText().trim());
            employee.setAddress(addressTextField.getText().trim());
            employee.setPhoneNumber(phoneNumberTextField.getText().trim());
            employee.setEmail(emailTextField.getText().trim());
            employee.setPosition(positionTextField.getText().trim());
            
            // 비밀번호 변경 시
            if (isPasswordChange) {
                employee.setPassword(passwordField.getText());
            }
            
            showAlert("성공", "사원 정보가 수정되었습니다.", Alert.AlertType.INFORMATION);
        }
    }
    
    private void closeDialog() {
        if (contentArea != null && contentArea.getScene() != null) {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
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