package com.example.companycore.controller.core;

import com.example.companycore.model.dto.UserDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.UserApiClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchCodeDialogController implements Initializable {

    @FXML
    private TextField emailTxtField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Button backButton;

    @FXML
    private Label resultLabel;
    @FXML
    private Label initPw;

    @FXML
    private HBox resultField;
    @FXML
    private HBox pwBox;

    private String result = null;
    private Stage stage;
    private UserApiClient apiClient;
    private User user;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.apiClient = UserApiClient.getInstance();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleOkAction() {
        result = emailTxtField.getText();
        user = apiClient.getUserByEmailAsUser(result);
        resultField.setVisible(true);
        try {
            String userName = user.getUsername();
            String empCode = user.getEmployeeCode();
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            backButton.setVisible(true);
            pwBox.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: black;");
            resultLabel.setText(userName + "님의 사원번호는 " + empCode + "입니다.");
            initPw.setText("초기 비밀번호는 1234 입니다");
        } catch (Exception e) {
            resultLabel.setText("존재하지 않는 이메일 입니다.");
            resultLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancelAction() {
        result = null;
        stage.close();
    }

    @FXML
    private void handleBackAction() {
        result = null;
        stage.close();
    }

    public String getResult() {
        return result;
    }

    // 텍스트 필드에 기본값 설정하는 메서드 (선택사항)
//    public void setDefaultText(String defaultText) {
//        textField.setText(defaultText);
//    }
}
