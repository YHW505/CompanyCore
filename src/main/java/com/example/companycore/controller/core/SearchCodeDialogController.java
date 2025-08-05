package com.example.companycore.controller.core;

import com.example.companycore.model.dto.UserDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.UserApiClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
    private Label initPwLabel;

    @FXML
    private VBox resultField;
    @FXML
    private HBox btnBox;
//    @FXML private HBox resultButtonBox;

    private String result = null;
    private Stage stage;
    private UserApiClient apiClient;
    private User user;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.apiClient = UserApiClient.getInstance();
        createInitialButtons();
        setupEnterKeyHandlers();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setupEnterKeyHandlers() {
        // 사용자명 필드에서 Enter 키 처리
        emailTxtField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleOkAction();
            }
        });
    }

    private void createInitialButtons() {
        btnBox.getChildren().clear();

        okButton = new Button("확인");
        okButton.setOnAction(e -> handleOkAction());

        cancelButton = new Button("취소");
        cancelButton.setOnAction(e -> handleCancelAction());

        btnBox.getChildren().addAll(okButton, cancelButton);
    }

    private void createBackButton() {
        btnBox.getChildren().clear();
        btnBox.setAlignment(Pos.BOTTOM_CENTER);

        backButton = new Button("뒤로가기");
        backButton.setOnAction(e -> handleBackAction());

        btnBox.getChildren().add(backButton);
    }

    @FXML
    private void handleOkAction() {
        result = emailTxtField.getText();
        user = apiClient.getUserByEmailAsUser(result);
        resultField.setVisible(true);
        if (result.isEmpty()) {
            resultLabel.setText("이메일을 입력해주세요");
            resultLabel.setStyle("-fx-text-fill: red;");
        } else {
            try {
                String userName = user.getUsername();
                String empCode = user.getEmployeeCode();
                createBackButton();
//            okButton.setVisible(false);
//            cancelButton.setVisible(false);
//            backButton.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: black;");
                resultLabel.setText(userName + "님의 사원번호는 " + empCode + "입니다.");
                initPwLabel.setText("초기 비밀번호는 1234 입니다");
            } catch (Exception e) {
                resultLabel.setText("존재하지 않는 이메일 입니다.");
                resultLabel.setStyle("-fx-text-fill: red;");
            }
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
