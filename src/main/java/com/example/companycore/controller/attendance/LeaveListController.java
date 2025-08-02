package com.example.companycore.controller.attendance;

import com.example.companycore.model.entity.User;
import com.example.companycore.model.dto.LeaveRequestDto;
import com.example.companycore.service.ApiClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LeaveListController implements Initializable {
    
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private DatePicker startDateFilter;
    @FXML private DatePicker endDateFilter;
    @FXML private VBox tableData;
    @FXML private Button prevButton, nextButton;
    @FXML private Button page1Button, page2Button, page3Button, page4Button, page5Button;
    @FXML private Label totalRequestsLabel, pendingLabel, approvedLabel, rejectedLabel;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private List<LeaveRequestDto> allLeaveRequests = new ArrayList<>();
    private List<LeaveRequestDto> filteredLeaveRequests = new ArrayList<>();
    private ApiClient apiClient;
    private User currentUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apiClient = ApiClient.getInstance();
        setupEventHandlers();
        setupFilters();
        loadCurrentUser();
    }
    
    /**
     * 현재 로그인된 사용자 정보를 로드합니다.
     */
    private void loadCurrentUser() {
        try {
            System.out.println("현재 사용자 정보 로드 시작...");
            
            // 백그라운드에서 사용자 정보 로드
            javafx.concurrent.Task<User> loadUserTask = new javafx.concurrent.Task<User>() {
                @Override
                protected User call() throws Exception {
                    return apiClient.getCurrentUser();
                }
            };
            
            loadUserTask.setOnSucceeded(e -> {
                currentUser = loadUserTask.getValue();
                if (currentUser != null) {
                    loadLeaveRequests();
                    System.out.println("사용자 정보 로드 완료: " + currentUser.getUsername());
                } else {
                    showError("오류", "사용자 정보를 불러올 수 없습니다.");
                }
            });
            
            loadUserTask.setOnFailed(e -> {
                showError("오류", "사용자 정보 로드 중 오류가 발생했습니다.");
            });
            
            new Thread(loadUserTask).start();
            
        } catch (Exception e) {
            showError("오류", "사용자 정보 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void setupEventHandlers() {
        // 새 휴가신청 버튼
        // handleNewLeaveRequest는 FXML에서 연결됨
        
        // 새로고침 버튼
        // handleRefresh는 FXML에서 연결됨
        
        // 검색 버튼
        // handleSearch는 FXML에서 연결됨
        
        // 초기화 버튼
        // handleReset는 FXML에서 연결됨
        
        // 페이지네이션 버튼들
        prevButton.setOnAction(e -> loadPage(Math.max(1, currentPage - 1)));
        nextButton.setOnAction(e -> loadPage(Math.min(totalPages, currentPage + 1)));
        page1Button.setOnAction(e -> loadPage(1));
        page2Button.setOnAction(e -> loadPage(2));
        page3Button.setOnAction(e -> loadPage(3));
        page4Button.setOnAction(e -> loadPage(4));
        page5Button.setOnAction(e -> loadPage(5));
    }
    
    private void setupFilters() {
        // 상태 필터 초기화
        statusFilterComboBox.getItems().addAll("전체", "대기중", "승인됨", "거부됨");
        statusFilterComboBox.setValue("전체");
        
        // 휴가종류 필터 초기화
        typeFilterComboBox.getItems().addAll("전체", "연차", "병가", "공가", "반차", "특별휴가");
        typeFilterComboBox.setValue("전체");
        
        // 필터 변경 이벤트
        statusFilterComboBox.setOnAction(e -> applyFilters());
        typeFilterComboBox.setOnAction(e -> applyFilters());
    }
    
    /**
     * 서버에서 휴가신청 목록을 로드합니다.
     */
    private void loadLeaveRequests() {
        try {
            System.out.println("휴가신청 목록 로드 시작...");
            
            // 백그라운드에서 휴가신청 목록 로드
            javafx.concurrent.Task<List<LeaveRequestDto>> loadTask = new javafx.concurrent.Task<List<LeaveRequestDto>>() {
                @Override
                protected List<LeaveRequestDto> call() throws Exception {
                    if (currentUser != null) {
                        return apiClient.getLeaveRequestsByUserId(currentUser.getUserId());
                    }
                    return new ArrayList<>();
                }
            };
            
            loadTask.setOnSucceeded(e -> {
                allLeaveRequests = loadTask.getValue();
                filteredLeaveRequests = new ArrayList<>(allLeaveRequests);
                updateStatistics();
                loadPage(1);
                System.out.println("휴가신청 목록 로드 완료: " + allLeaveRequests.size() + "개");
            });
            
            loadTask.setOnFailed(e -> {
                showError("오류", "휴가신청 목록 로드 중 오류가 발생했습니다: " + loadTask.getException().getMessage());
            });
            
            new Thread(loadTask).start();
            
        } catch (Exception e) {
            showError("오류", "휴가신청 목록 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 필터를 적용합니다.
     */
    private void applyFilters() {
        filteredLeaveRequests.clear();
        
        String statusFilter = statusFilterComboBox.getValue();
        String typeFilter = typeFilterComboBox.getValue();
        LocalDate startDate = startDateFilter.getValue();
        LocalDate endDate = endDateFilter.getValue();
        
        for (LeaveRequestDto request : allLeaveRequests) {
            boolean matchesStatus = "전체".equals(statusFilter) || 
                ("대기중".equals(statusFilter) && "PENDING".equals(request.getStatus())) ||
                ("승인됨".equals(statusFilter) && "APPROVED".equals(request.getStatus())) ||
                ("거부됨".equals(statusFilter) && "REJECTED".equals(request.getStatus()));
            
            boolean matchesType = "전체".equals(typeFilter) || 
                getLeaveTypeDisplayName(request.getLeaveType()).equals(typeFilter);
            
            boolean matchesDate = true;
            if (startDate != null && request.getStartDate() != null) {
                matchesDate = !request.getStartDate().isBefore(startDate);
            }
            if (endDate != null && request.getEndDate() != null) {
                matchesDate = matchesDate && !request.getEndDate().isAfter(endDate);
            }
            
            if (matchesStatus && matchesType && matchesDate) {
                filteredLeaveRequests.add(request);
            }
        }
        
        updateStatistics();
        loadPage(1);
    }
    
    /**
     * 휴가종류를 한글로 변환합니다.
     */
    private String getLeaveTypeDisplayName(String leaveType) {
        switch (leaveType) {
            case "ANNUAL": return "연차";
            case "SICK": return "병가";
            case "OFFICIAL": return "공가";
            case "HALF": return "반차";
            case "SPECIAL": return "특별휴가";
            default: return leaveType;
        }
    }
    
    /**
     * 휴가상태를 한글로 변환합니다.
     */
    private String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING": return "대기중";
            case "APPROVED": return "승인됨";
            case "REJECTED": return "거부됨";
            default: return status;
        }
    }
    
    /**
     * 통계 정보를 업데이트합니다.
     */
    private void updateStatistics() {
        int total = filteredLeaveRequests.size();
        int pending = 0;
        int approved = 0;
        int rejected = 0;
        
        for (LeaveRequestDto request : filteredLeaveRequests) {
            switch (request.getStatus()) {
                case "PENDING":
                    pending++;
                    break;
                case "APPROVED":
                    approved++;
                    break;
                case "REJECTED":
                    rejected++;
                    break;
            }
        }
        
        totalRequestsLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(pending));
        approvedLabel.setText(String.valueOf(approved));
        rejectedLabel.setText(String.valueOf(rejected));
    }
    
    private void loadPage(int page) {
        currentPage = page;
        totalPages = (int) Math.ceil((double) filteredLeaveRequests.size() / 10);
        
        loadPageData();
        updatePaginationUI();
    }
    
    private void loadPageData() {
        tableData.getChildren().clear();
        
        int pageSize = 10;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredLeaveRequests.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            addTableRow(filteredLeaveRequests.get(i), i - startIndex + 1);
        }
        
        // 빈 행 추가
        for (int i = endIndex - startIndex; i < pageSize; i++) {
            addEmptyRow();
        }
    }
    
    private void addTableRow(LeaveRequestDto request, int rowNumber) {
        HBox row = new HBox(0);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 번호
        VBox numberContainer = new VBox();
        numberContainer.setAlignment(javafx.geometry.Pos.CENTER);
        numberContainer.setStyle("-fx-min-width: 80; -fx-pref-width: 80;");
        Label numberLabel = new Label(String.valueOf(rowNumber));
        numberContainer.getChildren().add(numberLabel);
        
        // 휴가종류
        VBox typeContainer = new VBox();
        typeContainer.setAlignment(javafx.geometry.Pos.CENTER);
        typeContainer.setStyle("-fx-min-width: 120; -fx-pref-width: 120;");
        Label typeLabel = new Label(getLeaveTypeDisplayName(request.getLeaveType()));
        typeContainer.getChildren().add(typeLabel);
        
        // 시작일
        VBox startDateContainer = new VBox();
        startDateContainer.setAlignment(javafx.geometry.Pos.CENTER);
        startDateContainer.setStyle("-fx-min-width: 100; -fx-pref-width: 100;");
        Label startDateLabel = new Label(request.getStartDate() != null ? request.getStartDate().toString() : "");
        startDateContainer.getChildren().add(startDateLabel);
        
        // 종료일
        VBox endDateContainer = new VBox();
        endDateContainer.setAlignment(javafx.geometry.Pos.CENTER);
        endDateContainer.setStyle("-fx-min-width: 100; -fx-pref-width: 100;");
        Label endDateLabel = new Label(request.getEndDate() != null ? request.getEndDate().toString() : "");
        endDateContainer.getChildren().add(endDateLabel);
        
        // 일수
        VBox daysContainer = new VBox();
        daysContainer.setAlignment(javafx.geometry.Pos.CENTER);
        daysContainer.setStyle("-fx-min-width: 80; -fx-pref-width: 80;");
        long days = 0;
        if (request.getStartDate() != null && request.getEndDate() != null) {
            days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        }
        Label daysLabel = new Label(String.valueOf(days));
        daysContainer.getChildren().add(daysLabel);
        
        // 상태
        VBox statusContainer = new VBox();
        statusContainer.setAlignment(javafx.geometry.Pos.CENTER);
        statusContainer.setStyle("-fx-min-width: 120; -fx-pref-width: 120;");
        Label statusLabel = new Label(getStatusDisplayName(request.getStatus()));
        statusLabel.setStyle(getStatusStyle(request.getStatus()));
        statusContainer.getChildren().add(statusLabel);
        
        // 사유
        VBox reasonContainer = new VBox();
        reasonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        reasonContainer.setStyle("-fx-min-width: 200; -fx-pref-width: 200;");
        Label reasonLabel = new Label(request.getReason() != null ? request.getReason() : "");
        reasonLabel.setWrapText(true);
        reasonContainer.getChildren().add(reasonLabel);
        
        // 신청일
        VBox appliedDateContainer = new VBox();
        appliedDateContainer.setAlignment(javafx.geometry.Pos.CENTER);
        appliedDateContainer.setStyle("-fx-min-width: 120; -fx-pref-width: 120;");
        Label appliedDateLabel = new Label("신청일 정보 없음");
        appliedDateContainer.getChildren().add(appliedDateLabel);
        
        // 액션 버튼들
        HBox actionContainer = createActionButtons(request);
        actionContainer.setStyle("-fx-min-width: 150; -fx-pref-width: 150; -fx-alignment: CENTER;");
        
        row.getChildren().addAll(numberContainer, typeContainer, startDateContainer, endDateContainer, 
                                daysContainer, statusContainer, reasonContainer, appliedDateContainer, actionContainer);
        tableData.getChildren().add(row);
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "PENDING":
                return "-fx-text-fill: #ffc107; -fx-font-weight: bold;";
            case "APPROVED":
                return "-fx-text-fill: #28a745; -fx-font-weight: bold;";
            case "REJECTED":
                return "-fx-text-fill: #dc3545; -fx-font-weight: bold;";
            default:
                return "";
        }
    }
    
    private HBox createActionButtons(LeaveRequestDto request) {
        HBox buttonContainer = new HBox(5);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        if ("PENDING".equals(request.getStatus())) {
            // 대기중인 경우: 수정/취소 버튼 표시
            Button editButton = new Button("수정");
            editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
            editButton.setOnAction(e -> handleEdit(request));
            
            Button cancelButton = new Button("취소");
            cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
            cancelButton.setOnAction(e -> handleCancel(request));
            
            buttonContainer.getChildren().addAll(editButton, cancelButton);
        } else {
            // 승인/거부된 경우: 상세보기 버튼만 표시
            Button detailButton = new Button("상세");
            detailButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
            detailButton.setOnAction(e -> handleDetail(request));
            
            buttonContainer.getChildren().add(detailButton);
        }
        
        return buttonContainer;
    }
    
    private void addEmptyRow() {
        HBox row = new HBox(0);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0; -fx-min-height: 50; -fx-pref-height: 50;");
        
        // 빈 라벨들
        for (int i = 0; i < 9; i++) {
            Label emptyLabel = new Label("");
            emptyLabel.setStyle("-fx-min-width: 100; -fx-pref-width: 100; -fx-alignment: CENTER;");
            row.getChildren().add(emptyLabel);
        }
        
        tableData.getChildren().add(row);
    }
    
    private void updatePaginationUI() {
        // 모든 페이지 버튼 스타일 초기화
        Button[] pageButtons = {page1Button, page2Button, page3Button, page4Button, page5Button};
        
        for (Button button : pageButtons) {
            button.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #495057; -fx-font-weight: bold; -fx-min-width: 35; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 5;");
        }
        
        // 현재 페이지 버튼 하이라이트
        if (currentPage >= 1 && currentPage <= 5) {
            pageButtons[currentPage - 1].setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 5;");
        }
        
        // 이전/다음 버튼 활성화/비활성화
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }
    
    @FXML
    private void handleNewLeaveRequest() {
        // 새 휴가신청 화면으로 이동
        showInfo("새 휴가신청", "새 휴가신청 화면으로 이동합니다.");
    }
    
    @FXML
    private void handleRefresh() {
        loadLeaveRequests();
        showInfo("새로고침", "휴가신청 목록을 새로고침했습니다.");
    }
    
    @FXML
    private void handleSearch() {
        applyFilters();
        showInfo("검색", "검색 조건을 적용했습니다.");
    }
    
    @FXML
    private void handleReset() {
        statusFilterComboBox.setValue("전체");
        typeFilterComboBox.setValue("전체");
        startDateFilter.setValue(null);
        endDateFilter.setValue(null);
        applyFilters();
        showInfo("초기화", "검색 조건을 초기화했습니다.");
    }
    
    private void handleEdit(LeaveRequestDto request) {
        showInfo("수정", "휴가신청 수정 기능은 개발 중입니다.");
    }
    
    private void handleCancel(LeaveRequestDto request) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("휴가신청 취소");
        alert.setHeaderText(null);
        alert.setContentText("이 휴가신청을 취소하시겠습니까?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = apiClient.cancelLeaveRequest(request.getLeaveId(), currentUser.getUserId());
                    if (success) {
                        loadLeaveRequests(); // 목록 새로고침
                        showInfo("취소 완료", "휴가신청이 취소되었습니다.");
                    } else {
                        showError("취소 실패", "휴가신청 취소에 실패했습니다.");
                    }
                } catch (Exception e) {
                    showError("오류", "휴가신청 취소 중 오류가 발생했습니다: " + e.getMessage());
                }
            }
        });
    }
    
    private void handleDetail(LeaveRequestDto request) {
        showInfo("상세보기", "휴가신청 상세보기 기능은 개발 중입니다.");
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 