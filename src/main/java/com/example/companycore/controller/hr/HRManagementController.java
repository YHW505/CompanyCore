package com.example.companycore.controller.hr;

import com.example.companycore.model.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Pagination;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.example.companycore.service.ApiClient;
import com.example.companycore.model.entity.Department;
import com.example.companycore.model.entity.Position;
import java.util.HashSet;
import java.util.Set;

/**
 * 인사관리 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 사원 목록 테이블 표시 및 관리
 * - 사원 검색 및 필터링
 * - 사원 등록/수정 다이얼로그 호출
 * - 페이지네이션
 * - 사원 정보 CRUD 작업
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class HRManagementController {
    
    // ==================== FXML UI 컴포넌트 ====================
    
    /** 사원 목록 테이블 */
    @FXML private TableView<User> employeeTable;
    
    /** 테이블 컬럼들 */
    @FXML private TableColumn<User, Integer> idColumn;           // 번호 컬럼
    @FXML private TableColumn<User, String> nameColumn;          // 이름 컬럼
    @FXML private TableColumn<User, String> employeeIdColumn;    // 사번 컬럼
    @FXML private TableColumn<User, String> departmentColumn;    // 부서 컬럼
    @FXML private TableColumn<User, String> positionColumn;     // 직급 컬럼
    @FXML private TableColumn<User, String> emailColumn;         // 이메일 컬럼
    @FXML private TableColumn<User, String> phoneColumn;         // 연락처 컬럼
    @FXML private TableColumn<User, String> addressColumn;       // 주소 컬럼
    @FXML private TableColumn<User, Void> editColumn;            // 관리 버튼 컬럼
    
    /** 검색 관련 UI 컴포넌트 */
    @FXML private ComboBox<String> searchConditionComboBox;  // 검색 조건 선택
    @FXML private TextField searchTextField;                 // 검색어 입력
    @FXML private Button searchButton;                       // 검색 버튼
    
    /** 액션 버튼들 */
    @FXML private Button deleteButton;       // 삭제 버튼
    @FXML private Button registerButton;     // 사원 등록 버튼
    
    /** 페이지네이션 및 레이아웃 */
    @FXML private Pagination pagination;     // 페이지네이션
    @FXML private VBox contentArea;          // 콘텐츠 영역
    
    // ==================== 데이터 관리 ====================
    
    /** 전체 사원 데이터 */
    private ObservableList<User> allEmployees;
    
    /** 필터링된 사원 데이터 (검색 결과) */
    private ObservableList<User> filteredEmployees;
    
    /** 한 페이지에 표시할 항목 수 */
    private static final int ITEMS_PER_PAGE = 10;
    
    // API 클라이언트
    private ApiClient apiClient;
    
    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 데이터 초기화, 테이블 설정, 이벤트 핸들러 등록을 수행
     */
    @FXML
    public void initialize() {
        apiClient = ApiClient.getInstance();
        
        initializeData();
        setupTableColumns();
        setupSearchFunctionality();
        setupPagination();
        setupButtons();
        
        // 테이블 높이 변경 리스너 (결재승인 창과 동일)
        employeeTable.heightProperty().addListener((obs, oldVal, newVal) -> {
            // 테이블 높이가 변경될 때 필요한 처리
        });
    }
    
    /**
     * 데이터 초기화
     */
    private void initializeData() {
        allEmployees = FXCollections.observableArrayList();
        filteredEmployees = FXCollections.observableArrayList();
        
        // 실제 데이터베이스에서 사용자 목록을 가져옴
        loadUsersFromDatabase();
        
        // loadUsersFromDatabase() 내부에서 이미 filteredEmployees에 데이터를 추가하므로
        // 여기서는 추가로 addAll을 호출하지 않음
    }
    
    /**
     * 데이터베이스에서 사용자 목록을 로드합니다.
     */
    private void loadUsersFromDatabase() {
        try {
            // 기존 데이터 클리어
            allEmployees.clear();
            filteredEmployees.clear();
            employeeTable.getItems().clear();
            
            // API를 통해 사용자 데이터 가져오기
            List<User> users = apiClient.getUsers();
            
            if (users != null && !users.isEmpty()) {
                // 중복 제거를 위해 Set 사용
                Set<Long> addedUserIds = new HashSet<>();
                List<User> uniqueUsers = new ArrayList<>();
                
                for (User user : users) {
                    if (user.getUserId() != null && !addedUserIds.contains(user.getUserId())) {
                        uniqueUsers.add(user);
                        addedUserIds.add(user.getUserId());
                    }
                }
                
                allEmployees.addAll(uniqueUsers);
                // 필터링된 데이터에 한 번만 추가
                filteredEmployees.addAll(uniqueUsers);
                System.out.println("사용자 데이터 로드 완료: " + uniqueUsers.size() + "명");
            } else {
                // API에서 데이터를 가져올 수 없는 경우 빈 목록으로 설정
                System.out.println("API에서 사용자 데이터를 가져올 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("사용자 데이터 로드 중 오류 발생: " + e.getMessage());
            // API 오류 시 빈 목록으로 설정
            System.out.println("API 오류로 인해 빈 목록으로 설정되었습니다.");
        }
        
        // 이미 위에서 filteredEmployees에 데이터를 추가했으므로 여기서는 추가하지 않음
    }
    
    /**
     * 테이블 컬럼 설정
     */
    private void setupTableColumns() {
        // 테이블 기본 설정
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        employeeTable.setFixedCellSize(40); // 행 높이를 40px로 설정
        
        // 헤더 높이를 30px로 고정하고 테이블 높이 설정
        employeeTable.setStyle("-fx-table-header-height: 30px; -fx-scroll-bar-policy: never; -fx-pref-height: 427px; -fx-max-height: 427px; -fx-min-height: 427px; -fx-table-header-background: #f0f0f0;");
        
        // 테이블이 정확히 10개 행을 표시할 수 있도록 설정 (40px * 10행 + 헤더 높이 27px)
        employeeTable.setPrefHeight(427); // 40px * 10행 + 헤더 높이 27px = 427px
        employeeTable.setMaxHeight(427);
        
        // 각 컬럼의 헤더 높이도 고정
        idColumn.setStyle("-fx-table-header-height: 30px;");
        nameColumn.setStyle("-fx-table-header-height: 30px;");
        employeeIdColumn.setStyle("-fx-table-header-height: 30px;");
        departmentColumn.setStyle("-fx-table-header-height: 30px;");
        positionColumn.setStyle("-fx-table-header-height: 30px;");
        emailColumn.setStyle("-fx-table-header-height: 30px;");
        phoneColumn.setStyle("-fx-table-header-height: 30px;");
        addressColumn.setStyle("-fx-table-header-height: 30px;");
        editColumn.setStyle("-fx-table-header-height: 30px;");
        
        // 컬럼별 데이터 바인딩
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idColumn.setStyle("-fx-alignment: center;");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setStyle("-fx-alignment: center;");
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        employeeIdColumn.setStyle("-fx-alignment: center;");
        
        // 부서 컬럼 - Department 객체에서 부서명 가져오기
        departmentColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user.getDepartment() != null && user.getDepartment().getDepartmentName() != null) {
                return new javafx.beans.property.SimpleStringProperty(user.getDepartment().getDepartmentName());
            } else if (user.getDepartmentName() != null) {
                return new javafx.beans.property.SimpleStringProperty(user.getDepartmentName());
            } else {
                return new javafx.beans.property.SimpleStringProperty("미지정");
            }
        });
        departmentColumn.setStyle("-fx-alignment: center;");
        
        // 직급 컬럼 - Position 객체에서 직급명 가져오기
        positionColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            if (user.getPosition() != null && user.getPosition().getPositionName() != null) {
                return new javafx.beans.property.SimpleStringProperty(user.getPosition().getPositionName());
            } else if (user.getPositionName() != null) {
                return new javafx.beans.property.SimpleStringProperty(user.getPositionName());
            } else {
                return new javafx.beans.property.SimpleStringProperty("미지정");
            }
        });
        positionColumn.setStyle("-fx-alignment: center;");
        
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setStyle("-fx-alignment: center;");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setStyle("-fx-alignment: center;");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setStyle("-fx-alignment: center;");
        
        // 수정 버튼 컬럼 설정
        editColumn.setText("수정");
        setupEditColumn();
    }
    
    /**
     * 수정 버튼 컬럼 설정
     */
    private void setupEditColumn() {
        editColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("수정");
            
            {
                editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-cursor: hand; -fx-background-radius: 3;");
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    openEmployeeEditDialog(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    
    /**
     * 검색 기능 설정
     */
    private void setupSearchFunctionality() {
        // 검색 조건 콤보박스 초기화
        searchConditionComboBox.getItems().addAll("전체", "이름", "사번", "부서", "직급", "이메일", "연락처");
        searchConditionComboBox.setValue("전체");
        
        // 검색어 입력 시 실시간 필터링
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees();
        });
    }
    
    /**
     * 사원 데이터 필터링
     */
    private void filterEmployees() {
        String searchText = searchTextField.getText().toLowerCase();
        String searchCondition = searchConditionComboBox.getValue();
        
        List<User> filtered = allEmployees.stream()
            .filter(user -> {
                if (searchText.isEmpty()) return true;
                
                switch (searchCondition) {
                    case "이름":
                        return user.getUsername() != null && user.getUsername().toLowerCase().contains(searchText);
                    case "사번":
                        return user.getEmployeeCode() != null && user.getEmployeeCode().toLowerCase().contains(searchText);
                    case "부서":
                        return user.getDepartment() != null && 
                               user.getDepartment().getDepartmentName() != null &&
                               user.getDepartment().getDepartmentName().toLowerCase().contains(searchText);
                    case "직급":
                        return user.getPosition() != null && 
                               user.getPosition().getPositionName() != null &&
                               user.getPosition().getPositionName().toLowerCase().contains(searchText);
                    case "이메일":
                        return user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText);
                    case "연락처":
                        return user.getPhone() != null && user.getPhone().toLowerCase().contains(searchText);
                    default: // "전체"
                        return (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchText)) ||
                               (user.getEmployeeCode() != null && user.getEmployeeCode().toLowerCase().contains(searchText)) ||
                               (user.getDepartment() != null && user.getDepartment().getDepartmentName() != null && 
                                user.getDepartment().getDepartmentName().toLowerCase().contains(searchText)) ||
                               (user.getPosition() != null && user.getPosition().getPositionName() != null && 
                                user.getPosition().getPositionName().toLowerCase().contains(searchText)) ||
                               (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText)) ||
                               (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchText));
                }
            })
            .collect(Collectors.toList());
        
        filteredEmployees.clear();
        filteredEmployees.addAll(filtered);
        updatePagination();
    }
    
    /**
     * 페이지네이션 설정
     */
    private void setupPagination() {
        // 페이지네이션을 다시 활성화
        pagination.setVisible(true);
        
        // 페이지 변경 이벤트 리스너 추가
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            updatePagination();
        });
        
        updatePagination();
    }
    
    /**
     * 페이지네이션 업데이트
     */
    private void updatePagination() {
        int totalItems = filteredEmployees.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
        
        if (totalPages == 0) totalPages = 1;
        
        pagination.setPageCount(totalPages);
        
        // 현재 페이지의 데이터만 테이블에 표시
        int currentPage = pagination.getCurrentPageIndex();
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
        
        // 테이블 데이터 클리어 후 현재 페이지 데이터만 추가
        employeeTable.getItems().clear();
        
        if (startIndex < totalItems) {
            List<User> pageData = filteredEmployees.subList(startIndex, endIndex);
            employeeTable.getItems().addAll(pageData);
        }
    }
    
    /**
     * 버튼 이벤트 핸들러 설정
     */
    private void setupButtons() {
        // 사원 등록 버튼
        registerButton.setOnAction(event -> openEmployeeRegisterDialog());
        
        // 삭제 버튼
        deleteButton.setOnAction(event -> {
            User selectedUser = employeeTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                deleteEmployee(selectedUser);
            } else {
                showAlert("알림", "삭제할 사원을 선택해주세요.", Alert.AlertType.WARNING);
            }
        });
        
        // 검색 버튼
        searchButton.setOnAction(event -> handleSearch());
    }
    
    /**
     * 사원 삭제 처리
     */
    private void deleteEmployee(User user) {
        // 삭제 확인 다이얼로그
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText("선택된 사원을 삭제하시겠습니까?");
        alert.setContentText("사원명: " + user.getUsername() + "\n사번: " + user.getEmployeeCode() + "\n\n이 작업은 되돌릴 수 없습니다.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 실제 API 호출로 사용자 삭제
                    boolean deleteSuccess = apiClient.deleteUser(user.getUserId());
                    
                    if (deleteSuccess) {
                        allEmployees.remove(user);
                        filterEmployees();
                        showAlert("완료", "사원이 성공적으로 삭제되었습니다.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("오류", "사원 삭제에 실패했습니다.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    // API 오류 시 로컬에서만 삭제
                    allEmployees.remove(user);
                    filterEmployees();
                    showAlert("완료", "사원이 삭제되었습니다. (로컬 처리)", Alert.AlertType.INFORMATION);
                }
            }
        });
    }
    
    /**
     * 사원 수정 다이얼로그 열기
     */
    private void openEmployeeEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/hr/employeeEditDialog.fxml"));
            Parent root = loader.load();
            
            EmployeeEditController controller = loader.getController();
            controller.setUser(user);
            
            Stage stage = new Stage();
            stage.setTitle("사원 정보 수정");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // 다이얼로그가 닫힌 후 테이블 새로고침
            refreshEmployeeList();
            
        } catch (Exception e) {
            showAlert("오류", "사원 수정 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 사원 등록 다이얼로그 열기
     */
    private void openEmployeeRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/hr/employeeRegisterDialog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("새 사원 등록");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // 다이얼로그가 닫힌 후 테이블 새로고침
            refreshEmployeeList();
            
        } catch (Exception e) {
            showAlert("오류", "사원 등록 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 사원 목록 새로고침
     */
    private void refreshEmployeeList() {
        // 기존 데이터 클리어
        allEmployees.clear();
        filteredEmployees.clear();
        employeeTable.getItems().clear();
        
        // 데이터베이스에서 새로 로드
        loadUsersFromDatabase();
        
        // 페이지네이션 업데이트
        updatePagination();
    }
    
    /**
     * 검색 버튼 클릭 이벤트
     */
    @FXML
    private void handleSearch() {
        filterEmployees();
    }
    
    /**
     * 알림 다이얼로그 표시
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 