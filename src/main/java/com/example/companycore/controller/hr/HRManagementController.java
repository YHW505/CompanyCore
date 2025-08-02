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
    @FXML private TableColumn<User, Integer> idColumn;           // 순서 컬럼
    @FXML private TableColumn<User, String> nameColumn;          // 이름 컬럼
    @FXML private TableColumn<User, String> departmentColumn;    // 부서 컬럼
    @FXML private TableColumn<User, String> employeeIdColumn;    // 사번 컬럼
    @FXML private TableColumn<User, String> emailColumn;         // 이메일 컬럼
    @FXML private TableColumn<User, Void> editColumn;            // 수정 버튼 컬럼
    
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
     * 샘플 데이터 초기화
     */
    private void initializeData() {
        allEmployees = FXCollections.observableArrayList();
        filteredEmployees = FXCollections.observableArrayList();
        
        // 실제 데이터베이스에서 사용자 목록을 가져옴
        loadUsersFromDatabase();
        
        // 초기 필터링된 데이터 설정
        filteredEmployees.addAll(allEmployees);
    }
    
    /**
     * 데이터베이스에서 사용자 목록을 로드합니다.
     */
    private void loadUsersFromDatabase() {
        try {
            // 실제 API 호출로 사용자 목록을 가져오는 로직 구현
            List<User> users = apiClient.getUsers();
            allEmployees.addAll(users);
            
            System.out.println("사용자 목록을 데이터베이스에서 로드했습니다. 총 " + users.size() + "명");
            
        } catch (Exception e) {
            showAlert("오류", "사용자 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 테이블 컬럼 설정
     */
    private void setupTableColumns() {
        // 컬럼별 데이터 바인딩
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        departmentColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                user.getDepartment() != null ? user.getDepartment().getDepartmentName() : ""
            );
        });
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // 수정 버튼 컬럼 설정
        setupEditColumn();
    }
    
    /**
     * 수정 버튼 컬럼 설정
     */
    private void setupEditColumn() {
        editColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("✏️");
            
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
                }
            }
        });
    }
    
    /**
     * 검색 기능 설정
     */
    private void setupSearchFunctionality() {
        // 검색 조건 콤보박스 초기화
        searchConditionComboBox.getItems().addAll("전체", "이름", "사번", "부서", "이메일");
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
                        return user.getUsername().toLowerCase().contains(searchText);
                    case "사번":
                        return user.getEmployeeCode().toLowerCase().contains(searchText);
                    case "부서":
                        return user.getDepartment() != null && 
                               user.getDepartment().getDepartmentName().toLowerCase().contains(searchText);
                    case "이메일":
                        return user.getEmail().toLowerCase().contains(searchText);
                    default: // "전체"
                        return user.getUsername().toLowerCase().contains(searchText) ||
                               user.getEmployeeCode().toLowerCase().contains(searchText) ||
                               (user.getDepartment() != null && user.getDepartment().getDepartmentName().toLowerCase().contains(searchText)) ||
                               user.getEmail().toLowerCase().contains(searchText);
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
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
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
        
        List<User> pageData = filteredEmployees.subList(startIndex, endIndex);
        employeeTable.getItems().clear();
        employeeTable.getItems().addAll(pageData);
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
                // 삭제 확인 다이얼로그
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("삭제 확인");
                alert.setHeaderText("선택된 사원을 삭제하시겠습니까?");
                alert.setContentText("이 작업은 되돌릴 수 없습니다.");
                
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        allEmployees.remove(selectedUser);
                        filterEmployees();
                        showAlert("완료", "사원이 삭제되었습니다.", Alert.AlertType.INFORMATION);
                    }
                });
            } else {
                showAlert("알림", "삭제할 사원을 선택해주세요.", Alert.AlertType.WARNING);
            }
        });
    }
    
    /**
     * 사원 수정 다이얼로그 열기
     */
    private void openEmployeeEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hr/EmployeeEdit.fxml"));
            Parent root = loader.load();
            
            EmployeeEditController controller = loader.getController();
            controller.setUser(user);
            
            Stage stage = new Stage();
            stage.setTitle("사원 정보 수정");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // 다이얼로그가 닫힌 후 테이블 새로고침
            filterEmployees();
            
        } catch (Exception e) {
            showAlert("오류", "사원 수정 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 사원 등록 다이얼로그 열기
     */
    private void openEmployeeRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hr/EmployeeRegister.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("새 사원 등록");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // 다이얼로그가 닫힌 후 테이블 새로고침
            filterEmployees();
            
        } catch (Exception e) {
            showAlert("오류", "사원 등록 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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