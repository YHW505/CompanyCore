package com.example.companycore.controller.attendance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.companycore.model.dto.LeaveRequestDto;
import com.example.companycore.service.ApiClient;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class LeaveApprovalController implements Initializable {
    
    @FXML private VBox tableData;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private Button deleteButton;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button page1Button, page2Button, page3Button, page4Button, page5Button;
    @FXML private Button page6Button, page7Button, page8Button, page9Button, page10Button, page20Button;
    
    private int currentPage = 1;
    private int totalPages = 20;
    private List<LeaveRequestDto> leaveRequests = new ArrayList<>();
    private ObservableList<CheckBox> rowCheckBoxes = FXCollections.observableArrayList();
    private ApiClient apiClient = ApiClient.getInstance();
    

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEventHandlers();
        setupInitialData();
        loadPage(1);
    }
    
    private void setupEventHandlers() {
        // 전체 선택 체크박스
        selectAllCheckBox.setOnAction(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            for (CheckBox checkBox : rowCheckBoxes) {
                checkBox.setSelected(selected);
            }
        });
        
        // 삭제 버튼
        deleteButton.setOnAction(e -> handleDelete());
        
        // 페이지네이션 버튼들
        setupPaginationHandlers();
    }
    
    private void setupPaginationHandlers() {
        prevButton.setOnAction(e -> loadPage(Math.max(1, currentPage - 1)));
        nextButton.setOnAction(e -> loadPage(Math.min(totalPages, currentPage + 1)));
        
        page1Button.setOnAction(e -> loadPage(1));
        page2Button.setOnAction(e -> loadPage(2));
        page3Button.setOnAction(e -> loadPage(3));
        page4Button.setOnAction(e -> loadPage(4));
        page5Button.setOnAction(e -> loadPage(5));
        page6Button.setOnAction(e -> loadPage(6));
        page7Button.setOnAction(e -> loadPage(7));
        page8Button.setOnAction(e -> loadPage(8));
        page9Button.setOnAction(e -> loadPage(9));
        page10Button.setOnAction(e -> loadPage(10));
        page20Button.setOnAction(e -> loadPage(20));
    }
    
    private void setupInitialData() {
        // 서버에서 휴가 신청 데이터를 가져옴
        loadLeaveRequestsFromServer();
    }
    
    private void loadLeaveRequestsFromServer() {
        try {
            // 서버에서 모든 휴가 신청을 가져옴
            leaveRequests = apiClient.getAllLeaveRequests();
            System.out.println("서버에서 " + leaveRequests.size() + "개의 휴가 신청을 가져왔습니다.");
        } catch (Exception e) {
            System.err.println("서버에서 휴가 신청 데이터를 가져오는 중 오류 발생: " + e.getMessage());
            showAlert("오류", "서버에서 데이터를 가져오는 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
        }
    }
    
    private void loadPage(int page) {
        currentPage = page;
        loadPageData();
        updatePaginationUI();
    }
    
    private void loadPageData() {
        tableData.getChildren().clear();
        rowCheckBoxes.clear();
        
        // 현재 페이지의 데이터만 표시 (실제로는 데이터베이스에서 가져옴)
        List<LeaveRequestDto> pageData = getPageData(currentPage);
        
        for (int i = 0; i < 10; i++) {
            if (i < pageData.size()) {
                addTableRow(pageData.get(i), i);
            } else {
                addEmptyRow(i);
            }
        }
    }
    
    private List<LeaveRequestDto> getPageData(int page) {
        // 페이지네이션 로직 구현
        int pageSize = 10;
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, leaveRequests.size());
        
        if (startIndex >= leaveRequests.size()) {
            return new ArrayList<>();
        }
        
        return leaveRequests.subList(startIndex, endIndex);
    }
    
    private void addTableRow(LeaveRequestDto request, int rowIndex) {
        HBox row = new HBox(0);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 체크박스
        VBox checkBoxContainer = new VBox();
        checkBoxContainer.setAlignment(javafx.geometry.Pos.CENTER);
        checkBoxContainer.setStyle("-fx-min-width: 50; -fx-pref-width: 50; -fx-min-height: 50; -fx-pref-height: 50;");
        CheckBox checkBox = new CheckBox();
        checkBoxContainer.getChildren().add(checkBox);
        rowCheckBoxes.add(checkBox);
        
        // 휴가 종류
        VBox leaveTypeContainer = new VBox();
        leaveTypeContainer.setAlignment(javafx.geometry.Pos.CENTER);
        leaveTypeContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-min-height: 50; -fx-pref-height: 50;");
        Label leaveTypeLabel = new Label(request.getLeaveType());
        leaveTypeContainer.getChildren().add(leaveTypeLabel);
        
        // 사번
        VBox employeeIdContainer = new VBox();
        employeeIdContainer.setAlignment(javafx.geometry.Pos.CENTER);
        employeeIdContainer.setStyle("-fx-min-width: 150; -fx-pref-width: 150; -fx-min-height: 50; -fx-pref-height: 50;");
        Label employeeIdLabel = new Label(request.getEmployeeId());
        employeeIdContainer.getChildren().add(employeeIdLabel);
        
        // 사원이름
        VBox employeeNameContainer = new VBox();
        employeeNameContainer.setAlignment(javafx.geometry.Pos.CENTER);
        employeeNameContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-min-height: 50; -fx-pref-height: 50;");
        Label employeeNameLabel = new Label(request.getEmployeeName());
        employeeNameContainer.getChildren().add(employeeNameLabel);
        
        // 일자
        VBox dateContainer = new VBox();
        dateContainer.setAlignment(javafx.geometry.Pos.CENTER);
        dateContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-min-height: 50; -fx-pref-height: 50;");
        Label dateLabel = new Label(request.getStartDate() + " ~ " + request.getEndDate());
        dateContainer.getChildren().add(dateLabel);
        
        // 거부/승인 버튼들
        HBox actionButtons = createActionButtons(request, rowIndex);
        actionButtons.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-alignment: CENTER;");
        
        row.getChildren().addAll(checkBoxContainer, leaveTypeContainer, employeeIdContainer, employeeNameContainer, dateContainer, actionButtons);
        tableData.getChildren().add(row);
    }
    
    private HBox createActionButtons(LeaveRequestDto request, int rowIndex) {
        HBox buttonContainer = new HBox(5);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        buttonContainer.setStyle("-fx-alignment: CENTER;");
        
        if ("PENDING".equals(request.getStatus())) {
            // 대기중인 경우: 거부/승인 버튼 표시
            Button rejectButton = new Button("거부");
            rejectButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
            rejectButton.setOnAction(e -> handleReject(rowIndex));
            
            Button approveButton = new Button("승인");
            approveButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
            approveButton.setOnAction(e -> handleApprove(rowIndex));
            
            buttonContainer.getChildren().addAll(rejectButton, approveButton);
        } else if ("APPROVED".equals(request.getStatus())) {
            // 승인된 경우: 승인 버튼만 표시 (비활성화)
            Button approvedButton = new Button("승인");
            approvedButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 3;");
            approvedButton.setDisable(true);
            buttonContainer.getChildren().add(approvedButton);
        } else if ("REJECTED".equals(request.getStatus())) {
            // 거부된 경우: 거부 버튼만 표시 (비활성화)
            Button rejectedButton = new Button("거부");
            rejectedButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 3;");
            rejectedButton.setDisable(true);
            buttonContainer.getChildren().add(rejectedButton);
        }
        
        return buttonContainer;
    }
    
    private void addEmptyRow(int rowIndex) {
        HBox row = new HBox(0);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 빈 체크박스
        VBox checkBoxContainer = new VBox();
        checkBoxContainer.setAlignment(javafx.geometry.Pos.CENTER);
        checkBoxContainer.setStyle("-fx-min-width: 50; -fx-pref-width: 50; -fx-min-height: 50; -fx-pref-height: 50;");
        CheckBox checkBox = new CheckBox();
        checkBox.setDisable(true);
        checkBoxContainer.getChildren().add(checkBox);
        rowCheckBoxes.add(checkBox);
        
        // 빈 라벨들
        Label emptyLabel1 = new Label("");
        emptyLabel1.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-alignment: CENTER;");
        Label emptyLabel2 = new Label("");
        emptyLabel2.setStyle("-fx-min-width: 150; -fx-pref-width: 150; -fx-alignment: CENTER;");
        Label emptyLabel3 = new Label("");
        emptyLabel3.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-alignment: CENTER;");
        Label emptyLabel4 = new Label("");
        emptyLabel4.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-alignment: CENTER;");
        Label emptyLabel5 = new Label("");
        emptyLabel5.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-alignment: CENTER;");
        
        row.getChildren().addAll(checkBoxContainer, emptyLabel1, emptyLabel2, emptyLabel3, emptyLabel4, emptyLabel5);
        tableData.getChildren().add(row);
    }
    
    private void handleApprove(int rowIndex) {
        if (rowIndex < leaveRequests.size()) {
            LeaveRequestDto request = leaveRequests.get(rowIndex);
            try {
                // 서버에 승인 요청
                boolean success = apiClient.approveLeaveRequest(request.getLeaveId(), 1L); // TODO: 실제 승인자 ID 사용
                if (success) {
                    request.setStatus("APPROVED");
                    loadPageData(); // 테이블 새로고침
                    showAlert("성공", "휴가 신청이 승인되었습니다.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("오류", "휴가 신청 승인에 실패했습니다.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                System.err.println("휴가 승인 중 오류 발생: " + e.getMessage());
                showAlert("오류", "서버와의 통신 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
            }
        }
    }
    
    private void handleReject(int rowIndex) {
        if (rowIndex < leaveRequests.size()) {
            LeaveRequestDto request = leaveRequests.get(rowIndex);
            try {
                // 서버에 거부 요청
                boolean success = apiClient.rejectLeaveRequest(request.getLeaveId(), 1L, "관리자에 의해 거부됨"); // TODO: 실제 거부자 ID 사용
                if (success) {
                    request.setStatus("REJECTED");
                    loadPageData(); // 테이블 새로고침
                    showAlert("성공", "휴가 신청이 거부되었습니다.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("오류", "휴가 신청 거부에 실패했습니다.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                System.err.println("휴가 거부 중 오류 발생: " + e.getMessage());
                showAlert("오류", "서버와의 통신 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
            }
        }
    }
    
    private void handleDelete() {
        // 선택된 항목들 찾기
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < rowCheckBoxes.size(); i++) {
            if (rowCheckBoxes.get(i).isSelected()) {
                selectedIndices.add(i);
            }
        }
        
        if (selectedIndices.isEmpty()) {
            showAlert("알림", "삭제할 항목을 선택해주세요.", Alert.AlertType.WARNING);
            return;
        }
        
        // 확인 다이얼로그
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText("선택된 휴가신청을 삭제하시겠습니까?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 선택된 항목들 삭제 (실제로는 데이터베이스에서 삭제)
                for (int i = selectedIndices.size() - 1; i >= 0; i--) {
                    int index = selectedIndices.get(i);
                    if (index < leaveRequests.size()) {
                        leaveRequests.remove(index);
                    }
                }
                
                loadPageData(); // 테이블 새로고침
                selectAllCheckBox.setSelected(false);
                showAlert("완료", "선택된 항목이 삭제되었습니다.", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    private void updatePaginationUI() {
        // 모든 페이지 버튼 스타일 초기화
        Button[] pageButtons = {page1Button, page2Button, page3Button, page4Button, page5Button,
                               page6Button, page7Button, page8Button, page9Button, page10Button, page20Button};
        
        for (Button button : pageButtons) {
            button.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #495057; -fx-font-weight: bold; -fx-min-width: 35; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 5;");
        }
        
        // 현재 페이지 버튼 하이라이트
        if (currentPage >= 1 && currentPage <= 10) {
            pageButtons[currentPage - 1].setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 5;");
        } else if (currentPage == 20) {
            page20Button.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 5;");
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