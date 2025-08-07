package com.example.companycore.controller.attendance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Pagination;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.companycore.model.dto.LeaveRequestDto;
import com.example.companycore.model.entity.User;
import com.example.companycore.service.ApiClient;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class LeaveApprovalController implements Initializable {
    
    @FXML private VBox tableData;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private Button deleteButton;
    @FXML private Pagination pagination;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private int visibleRowCount = 10;
    private List<LeaveRequestDto> leaveRequests = new ArrayList<>();
    private ObservableList<LeaveRequestDto> viewData = FXCollections.observableArrayList();
    private ObservableList<CheckBox> rowCheckBoxes = FXCollections.observableArrayList();
    private ApiClient apiClient = ApiClient.getInstance();
    private Map<Long, User> userCache = new HashMap<>(); // 사용자 정보 캐시

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== LeaveApprovalController 초기화 시작 ===");
        setupEventHandlers();
        setupPagination();
        setupInitialData();
        updatePagination();
        System.out.println("=== LeaveApprovalController 초기화 완료 ===");
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
    }
    
    /**
     * 페이지네이션 UI를 구성하고 설정
     */
    private void setupPagination() {
        System.out.println("=== 페이지네이션 설정 시작 ===");
        pagination.setVisible(true);
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
        System.out.println("페이지네이션 설정 완료 - visible: " + pagination.isVisible() + ", pageCount: " + pagination.getPageCount());
        updatePagination();
    }
    
    /**
     * 페이지네이션 업데이트
     */
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(Math.max(1, totalPages));
        
        // 페이지네이션을 명시적으로 보이도록 설정
        pagination.setVisible(true);
        
        // 현재 페이지가 총 페이지 수를 초과하면 마지막 페이지로 설정
        if (pagination.getCurrentPageIndex() >= totalPages) {
            pagination.setCurrentPageIndex(Math.max(0, totalPages - 1));
        }
        
        // 첫 페이지 데이터 로드
        if (viewData.size() > 0) {
            int startIndex = 0;
            int endIndex = Math.min(visibleRowCount, viewData.size());
            List<LeaveRequestDto> pageData = viewData.subList(startIndex, endIndex);
            loadPageData(pageData);
            System.out.println("페이지네이션 업데이트: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        } else {
            tableData.getChildren().clear();
            System.out.println("페이지네이션 업데이트: 빈 목록");
        }
    }
    
    /**
     * 페이지 생성
     */
    private Region createPage(int pageIndex) {
        // 페이지 인덱스가 유효한지 확인
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        if (pageIndex >= totalPages) {
            return new Region();
        }
        
        // 현재 페이지의 데이터 계산
        int startIndex = pageIndex * visibleRowCount;
        int endIndex = Math.min(startIndex + visibleRowCount, viewData.size());
        
        // 현재 페이지의 데이터만 테이블에 설정
        List<LeaveRequestDto> pageData = viewData.subList(startIndex, endIndex);
        loadPageData(pageData);
        
        System.out.println("페이지 " + (pageIndex + 1) + " 로드: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        
        return new Region();
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
            
            viewData.clear();
            viewData.addAll(leaveRequests);
            
            // 사용자 정보 캐시 로드
            loadUserCache();
            
        } catch (Exception e) {
            System.err.println("서버에서 휴가 신청 데이터를 가져오는 중 오류 발생: " + e.getMessage());
            showAlert("오류", "서버에서 데이터를 가져오는 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 사용자 정보를 캐시에 로드합니다.
     */
    private void loadUserCache() {
        try {
            System.out.println("=== 사용자 정보 캐시 로드 시작 ===");
            
            // 모든 사용자 정보를 가져와서 캐시에 저장
            List<User> allUsers = apiClient.getUsers();
            System.out.println("API에서 가져온 사용자 수: " + allUsers.size());
            
            // 사용자 정보 상세 출력
            for (User user : allUsers) {
                System.out.println("사용자 정보 - ID: " + user.getUserId() + 
                                 ", 이름: " + user.getUsername() + 
                                 ", 사번: " + user.getEmployeeCode());
            }
            
            // 사용자 ID를 키로 하는 맵으로 변환
            for (User user : allUsers) {
                if (user.getUserId() != null) {
                    userCache.put(user.getUserId(), user);
                    System.out.println("캐시에 추가됨 - ID: " + user.getUserId() + ", 이름: " + user.getUsername());
                }
            }
            
            System.out.println("사용자 정보 캐시 로드 완료: " + userCache.size() + "명");
            System.out.println("캐시된 사용자 ID들: " + userCache.keySet());
            
        } catch (Exception e) {
            System.err.println("사용자 정보 캐시 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
            // 오류 발생 시 임시 사용자 정보 생성
            createTemporaryUserCache();
        }
    }
    
    /**
     * 임시 사용자 정보를 생성합니다 (API 오류 시 사용)
     */
    private void createTemporaryUserCache() {
        try {
            System.out.println("=== 임시 사용자 정보 생성 시작 ===");
            
            // 모든 휴가신청에서 사용자 ID를 수집
            List<Long> userIds = new ArrayList<>();
            for (LeaveRequestDto request : leaveRequests) {
                if (request.getUserId() != null && !userIds.contains(request.getUserId())) {
                    userIds.add(request.getUserId());
                }
            }
            
            System.out.println("휴가신청에서 발견된 사용자 ID들: " + userIds);
            
            // 임시 사용자 정보 생성
            for (Long userId : userIds) {
                User user = new User();
                user.setUserId(userId);
                user.setUsername("사원" + userId);
                user.setEmployeeCode("EMP" + String.format("%03d", userId));
                userCache.put(userId, user);
                System.out.println("임시 사용자 생성 - ID: " + userId + ", 이름: " + user.getUsername());
            }
            
            System.out.println("임시 사용자 정보 캐시 생성 완료: " + userCache.size() + "명");
            
        } catch (Exception e) {
            System.err.println("임시 사용자 정보 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadPageData(List<LeaveRequestDto> pageData) {
        // 테이블 데이터를 클리어
        tableData.getChildren().clear();
        rowCheckBoxes.clear();
        
        // 현재 페이지의 데이터만 표시
        for (int i = 0; i < pageData.size(); i++) {
            addTableRow(pageData.get(i), i);
        }
        
        // 빈 행으로 채우기 (10개 행 고정)
        int remainingRows = visibleRowCount - pageData.size();
        for (int i = 0; i < remainingRows; i++) {
            addEmptyRow(pageData.size() + i);
        }
    }
    
    /**
     * 휴가종류를 한글로 변환합니다.
     */
    private String getLeaveTypeDisplayName(String leaveType) {
        switch (leaveType) {
            case "ANNUAL": return "연차";
            case "HALF_DAY": return "반차";
            case "SICK": return "병가";
            case "PERSONAL": return "개인사유";
            case "OFFICIAL": return "공가";
            case "MATERNITY": return "출산휴가";
            case "PATERNITY": return "육아휴가";
            case "SPECIAL": return "특별휴가";
            default: return leaveType;
        }
    }
    
    /**
     * 사용자 정보를 가져옵니다.
     */
    private User getUserById(Long userId) {
        User user = userCache.get(userId);
        System.out.println("사용자 조회 - ID: " + userId + 
                         ", 캐시에서 찾은 사용자: " + (user != null ? user.getUsername() : "null"));
        return user;
    }
    
    private void addTableRow(LeaveRequestDto request, int rowIndex) {
        HBox row = new HBox(0);
        row.setAlignment(javafx.geometry.Pos.CENTER);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 체크박스
        VBox checkBoxContainer = new VBox();
        checkBoxContainer.setAlignment(javafx.geometry.Pos.CENTER);
        checkBoxContainer.setStyle("-fx-min-width: 50; -fx-pref-width: 50; -fx-min-height: 50; -fx-pref-height: 50;");
        CheckBox checkBox = new CheckBox();
        checkBoxContainer.getChildren().add(checkBox);
        rowCheckBoxes.add(checkBox);
        
        // 휴가 종류 (한글로 표시)
        VBox leaveTypeContainer = new VBox();
        leaveTypeContainer.setAlignment(javafx.geometry.Pos.CENTER);
        leaveTypeContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-min-height: 50; -fx-pref-height: 50;");
        Label leaveTypeLabel = new Label(getLeaveTypeDisplayName(request.getLeaveType()));
        leaveTypeContainer.getChildren().add(leaveTypeLabel);
        
        // 사번 (실제 사번 표시)
        VBox employeeIdContainer = new VBox();
        employeeIdContainer.setAlignment(javafx.geometry.Pos.CENTER);
        employeeIdContainer.setStyle("-fx-min-width: 150; -fx-pref-width: 150; -fx-min-height: 50; -fx-pref-height: 50;");
        String employeeId = "사번 없음";
        if (request.getUserId() != null) {
            User user = getUserById(request.getUserId());
            if (user != null && user.getEmployeeCode() != null) {
                employeeId = user.getEmployeeCode();
            } else {
                employeeId = request.getUserId().toString();
            }
        }
        Label employeeIdLabel = new Label(employeeId);
        employeeIdContainer.getChildren().add(employeeIdLabel);
        
        // 사원이름 (실제 사용자 이름 표시)
        VBox employeeNameContainer = new VBox();
        employeeNameContainer.setAlignment(javafx.geometry.Pos.CENTER);
        employeeNameContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200; -fx-min-height: 50; -fx-pref-height: 50;");
        String employeeName = "사원";
        if (request.getUserId() != null) {
            System.out.println("=== 사원이름 처리 시작 ===");
            System.out.println("휴가신청 사용자 ID: " + request.getUserId());
            User user = getUserById(request.getUserId());
            System.out.println("찾은 사용자: " + (user != null ? user.getUsername() : "null"));
            if (user != null && user.getUsername() != null) {
                employeeName = user.getUsername();
                System.out.println("최종 사원이름: " + employeeName);
            } else {
                System.out.println("사용자 정보가 없거나 이름이 null입니다.");
            }
        } else {
            System.out.println("휴가신청의 사용자 ID가 null입니다.");
        }
        Label employeeNameLabel = new Label(employeeName);
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
        row.setAlignment(javafx.geometry.Pos.CENTER);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 빈 체크박스 (숨김)
        VBox checkBoxContainer = new VBox();
        checkBoxContainer.setAlignment(javafx.geometry.Pos.CENTER);
        checkBoxContainer.setStyle("-fx-min-width: 50; -fx-pref-width: 50; -fx-min-height: 50; -fx-pref-height: 50;");
        CheckBox checkBox = new CheckBox();
        checkBox.setDisable(true);
        checkBox.setVisible(false); // 체크박스 숨김
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
        if (rowIndex < viewData.size()) {
            LeaveRequestDto request = viewData.get(rowIndex);
            try {
                // 서버에 승인 요청
                boolean success = apiClient.approveLeaveRequest(request.getLeaveId(), 1L); // TODO: 실제 승인자 ID 사용
                if (success) {
                    request.setStatus("APPROVED");
                    updatePagination(); // 테이블 새로고침
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
        if (rowIndex < viewData.size()) {
            LeaveRequestDto request = viewData.get(rowIndex);
            try {
                // 서버에 거부 요청
                boolean success = apiClient.rejectLeaveRequest(request.getLeaveId(), 1L, "관리자에 의해 거부됨"); // TODO: 실제 거부자 ID 사용
                if (success) {
                    request.setStatus("REJECTED");
                    updatePagination(); // 테이블 새로고침
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
                    if (index < viewData.size()) {
                        viewData.remove(index);
                    }
                }
                
                updatePagination(); // 테이블 새로고침
                selectAllCheckBox.setSelected(false);
                showAlert("완료", "선택된 항목이 삭제되었습니다.", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 