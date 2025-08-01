package com.example.companycore.controller.hr;

import com.example.companycore.model.entity.Employee;
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
    @FXML private TableView<Employee> employeeTable;
    
    /** 테이블 컬럼들 */
    @FXML private TableColumn<Employee, Integer> idColumn;           // 순서 컬럼
    @FXML private TableColumn<Employee, String> nameColumn;          // 이름 컬럼
    @FXML private TableColumn<Employee, String> departmentColumn;    // 부서 컬럼
    @FXML private TableColumn<Employee, String> employeeIdColumn;    // 사번 컬럼
    @FXML private TableColumn<Employee, String> emailColumn;         // 이메일 컬럼
    @FXML private TableColumn<Employee, Void> editColumn;            // 수정 버튼 컬럼
    
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
    private ObservableList<Employee> allEmployees;
    
    /** 필터링된 사원 데이터 (검색 결과) */
    private ObservableList<Employee> filteredEmployees;
    
    /** 한 페이지에 표시할 항목 수 */
    private static final int ITEMS_PER_PAGE = 10;
    
    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 데이터 초기화, 테이블 설정, 이벤트 핸들러 등록을 수행
     */
    @FXML
    public void initialize() {
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
     * 데이터베이스에서 사원 데이터를 초기화
     * 실제 구현에서는 데이터베이스에서 데이터를 로드
     */
    private void initializeData() {
        // 데이터베이스에서 사원 데이터 로드
        allEmployees = FXCollections.observableArrayList();
        filteredEmployees = FXCollections.observableArrayList();
        
        // TODO: 데이터베이스에서 사원 목록을 로드하는 로직 구현
        // loadEmployeesFromDatabase();
        
        filteredEmployees.addAll(allEmployees);
        employeeTable.setItems(filteredEmployees);
    }
    
    // ==================== 테이블 설정 메서드 ====================
    
    /**
     * 테이블 컬럼들을 설정하고 바인딩
     * 컬럼 너비, 정렬, 이벤트 핸들러 등을 설정
     */
    private void setupTableColumns() {
        // 테이블 설정 (결재승인 창과 동일)
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        employeeTable.setFixedCellSize(40);
        
        // 기본 컬럼 설정
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // 컬럼 너비 설정 (결재승인 창과 동일)
        idColumn.setPrefWidth(100);
        nameColumn.setPrefWidth(200);
        departmentColumn.setPrefWidth(120);
        employeeIdColumn.setPrefWidth(120);
        emailColumn.setPrefWidth(140);
        editColumn.setPrefWidth(160);
        
        // 모든 컬럼 중앙 정렬 설정
        idColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");
        departmentColumn.setStyle("-fx-alignment: CENTER;");
        employeeIdColumn.setStyle("-fx-alignment: CENTER;");
        emailColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setStyle("-fx-alignment: CENTER;");
        
        // 수정 버튼 컬럼 설정
        setupEditColumn();
    }
    
    /**
     * 수정 버튼이 있는 컬럼을 설정
     * 각 행에 수정 버튼을 추가하고 클릭 시 사원 수정 다이얼로그를 열음
     */
    private void setupEditColumn() {
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            
            {
                editButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
                editButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    openEmployeeEditDialog(employee);
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
    
    // ==================== 검색 기능 메서드 ====================
    
    /**
     * 검색 기능을 초기화
     * 검색 조건 콤보박스 설정 및 기본값 설정
     */
    private void setupSearchFunctionality() {
        // 검색 조건 콤보박스 설정
        searchConditionComboBox.getItems().addAll("이름", "부서", "사번", "이메일");
        searchConditionComboBox.setValue(null);
        
        // 검색 버튼 이벤트 설정
        searchButton.setOnAction(event -> {
            filterEmployees();
        });
        
        // 검색 텍스트 필드 엔터키 이벤트 설정
        searchTextField.setOnAction(event -> {
            filterEmployees();
        });
    }
    
    /**
     * 검색 조건에 따라 사원 목록을 필터링
     * 검색어가 비어있으면 전체 목록을 표시
     */
    private void filterEmployees() {
        String searchText = searchTextField.getText().toLowerCase();
        String searchCondition = searchConditionComboBox.getValue();
        
        List<Employee> filtered = allEmployees.stream()
            .filter(employee -> {
                if (searchText.isEmpty()) {
                    return true;
                }
                
                switch (searchCondition) {
                    case "이름":
                        return employee.getName().toLowerCase().contains(searchText);
                    case "부서":
                        return employee.getDepartment().toLowerCase().contains(searchText);
                    case "사번":
                        return employee.getEmployeeId().toLowerCase().contains(searchText);
                    case "이메일":
                        return employee.getEmail().toLowerCase().contains(searchText);
                    default:
                        return true;
                }
            })
            .collect(Collectors.toList());
        
        filteredEmployees.clear();
        filteredEmployees.addAll(filtered);
        
        // 페이지네이션 업데이트
        updatePagination();
    }
    
    // ==================== 페이지네이션 메서드 ====================
    
    /**
     * 페이지네이션을 설정
     * 페이지 변경 시 해당 페이지의 데이터만 표시
     */
    private void setupPagination() {
        updatePagination();
    }
    
    /**
     * 페이지네이션을 업데이트
     * 필터링된 데이터에 따라 페이지 수를 재계산
     */
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) filteredEmployees.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(totalPages);
        
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredEmployees.size());
            
            List<Employee> pageData = filteredEmployees.subList(fromIndex, toIndex);
            ObservableList<Employee> pageObservableList = FXCollections.observableArrayList(pageData);
            
            // 테이블 데이터 업데이트
            employeeTable.setItems(pageObservableList);
            
            return new Region(); // 페이지네이션은 테이블을 직접 업데이트하므로 빈 Region 반환
        });
        
        // 첫 페이지 데이터 설정
        if (!filteredEmployees.isEmpty()) {
            int endIndex = Math.min(ITEMS_PER_PAGE, filteredEmployees.size());
            List<Employee> firstPageData = filteredEmployees.subList(0, endIndex);
            employeeTable.setItems(FXCollections.observableArrayList(firstPageData));
        }
    }
    
    // ==================== 버튼 설정 메서드 ====================
    
    /**
     * 버튼들의 이벤트 핸들러를 설정
     * 삭제, 등록 버튼의 클릭 이벤트를 등록
     */
    private void setupButtons() {
        // 삭제 버튼 설정
        deleteButton.setOnAction(event -> {
            // 선택된 사원 삭제 로직
            showAlert("삭제", "선택된 사원을 삭제하시겠습니까?", Alert.AlertType.CONFIRMATION);
        });
        
        // 등록 버튼 설정
        registerButton.setOnAction(event -> {
            openEmployeeRegisterDialog();
        });
    }
    
    // ==================== 다이얼로그 메서드 ====================
    
    /**
     * 사원 수정 다이얼로그를 열기
     * 
     * @param employee 수정할 사원 객체
     */
    private void openEmployeeEditDialog(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/hr/employeeEditDialog.fxml"));
            Parent root = loader.load();
            
            EmployeeEditController controller = loader.getController();
            controller.setEmployee(employee);
            
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("사원 정보 수정");
            dialog.setScene(new Scene(root));
            
            dialog.setOnCloseRequest(event -> {
                // 다이얼로그가 닫힐 때 테이블 새로고침
                filterEmployees();
            });
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "사원 정보 수정 창을 열 수 없습니다.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 사원 등록 다이얼로그를 열기
     */
    private void openEmployeeRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/hr/employeeRegisterDialog.fxml"));
            Parent root = loader.load();
            
            EmployeeRegisterController controller = loader.getController();
            
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("사원 등록");
            dialog.setScene(new Scene(root));
            
            dialog.setOnCloseRequest(event -> {
                // 다이얼로그가 닫힐 때 테이블 새로고침
                filterEmployees();
            });
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("오류", "사원 등록 창을 열 수 없습니다.", Alert.AlertType.ERROR);
        }
    }
    
    // ==================== 이벤트 핸들러 메서드 ====================
    
    /**
     * 검색 버튼 클릭 시 호출되는 메서드
     * FXML에서 onAction으로 연결됨
     */
    @FXML
    private void handleSearch() {
        filterEmployees();
    }
    
    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 알림 다이얼로그를 표시
     * 
     * @param title 알림 제목
     * @param content 알림 내용
     * @param alertType 알림 타입 (ERROR, WARNING, INFORMATION, CONFIRMATION)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 