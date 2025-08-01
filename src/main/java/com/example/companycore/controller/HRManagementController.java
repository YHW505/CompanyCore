package com.example.companycore.controller;

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

public class HRManagementController {
    
    @FXML
    private TableView<Employee> employeeTable;
    
    @FXML
    private TableColumn<Employee, Integer> idColumn;
    
    @FXML
    private TableColumn<Employee, String> nameColumn;
    
    @FXML
    private TableColumn<Employee, String> departmentColumn;
    
    @FXML
    private TableColumn<Employee, String> employeeIdColumn;
    
    @FXML
    private TableColumn<Employee, String> emailColumn;
    
    @FXML
    private TableColumn<Employee, Void> editColumn;
    

    
    @FXML
    private ComboBox<String> searchConditionComboBox;
    
    @FXML
    private TextField searchTextField;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private VBox contentArea;
    
    private ObservableList<Employee> allEmployees;
    private ObservableList<Employee> filteredEmployees;
    private static final int ITEMS_PER_PAGE = 10;
    
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
    
    private void initializeData() {
        // 샘플 데이터 생성
        allEmployees = FXCollections.observableArrayList();
        filteredEmployees = FXCollections.observableArrayList();
        
        // 샘플 사원 데이터 추가
        allEmployees.addAll(
            new Employee(1, "한교동", "2534001", "개발", "한강물 미역길", "010-1234-5678", "fish@companycore.kr", "용왕", "password123"),
            new Employee(2, "케로케로피", "2534002", "개발", "개구리 마을", "010-2345-6789", "kero@companycore.kr", "개구리", "password456"),
            new Employee(3, "김철수", "2534003", "인사", "서울시 강남구", "010-3456-7890", "kim@companycore.kr", "대리", "password789"),
            new Employee(4, "이영희", "2534004", "마케팅", "서울시 서초구", "010-4567-8901", "lee@companycore.kr", "과장", "password012"),
            new Employee(5, "박민수", "2534005", "영업", "서울시 마포구", "010-5678-9012", "park@companycore.kr", "사원", "password345"),
            new Employee(6, "정수진", "2534006", "개발", "서울시 성동구", "010-6789-0123", "jung@companycore.kr", "팀장", "password678"),
            new Employee(7, "최동욱", "2534007", "인사", "서울시 용산구", "010-7890-1234", "choi@companycore.kr", "부장", "password901"),
            new Employee(8, "윤서연", "2534008", "마케팅", "서울시 중구", "010-8901-2345", "yoon@companycore.kr", "대리", "password234"),
            new Employee(9, "임태호", "2534009", "영업", "서울시 종로구", "010-9012-3456", "lim@companycore.kr", "사원", "password567"),
            new Employee(10, "강미영", "2534010", "개발", "서울시 송파구", "010-0123-4567", "kang@companycore.kr", "과장", "password890"),
            new Employee(11, "송재현", "2534011", "인사", "서울시 강북구", "010-1234-5678", "song@companycore.kr", "대리", "password123"),
            new Employee(12, "조은영", "2534012", "마케팅", "서울시 노원구", "010-2345-6789", "jo@companycore.kr", "사원", "password456")
        );
        
        filteredEmployees.addAll(allEmployees);
        employeeTable.setItems(filteredEmployees);
    }
    
    private void setupTableColumns() {
        // 테이블 설정 (결재승인 창과 동일)
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        
        // 수정 버튼 컬럼 설정
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
    
    private void setupSearchFunctionality() {
        // 검색 조건 콤보박스 설정
        searchConditionComboBox.getItems().addAll("이름", "부서", "사번", "이메일");
        searchConditionComboBox.setValue("이름");
        
        // 검색 버튼 이벤트 설정
        searchButton.setOnAction(event -> {
            filterEmployees();
        });
        
        // 검색 텍스트 필드 엔터키 이벤트 설정
        searchTextField.setOnAction(event -> {
            filterEmployees();
        });
    }
    
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
    
    private void setupPagination() {
        updatePagination();
    }
    
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
    
    @FXML
    private void handleSearch() {
        filterEmployees();
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 