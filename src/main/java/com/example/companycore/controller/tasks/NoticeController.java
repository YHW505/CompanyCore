package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 공지사항 목록을 관리하는 컨트롤러
 *
 * 주요 기능:
 * - 공지 테이블 표시
 * - 페이지네이션
 * - 공지 등록 / 수정 / 삭제 기능
 * - 검색 및 필터링
 * - 중요 공지사항 표시
 *
 * @author Company Core Team
 * @version 2.0
 */
public class NoticeController {

    // ==================== FXML UI 컴포넌트 ====================

    @FXML private TableView<NoticeItem> tableView;

    @FXML private TableColumn<NoticeItem, String> titleColumn;
    @FXML private TableColumn<NoticeItem, String> departmentColumn;
    @FXML private TableColumn<NoticeItem, String> authorColumn;
    @FXML private TableColumn<NoticeItem, LocalDate> dateColumn;
    @FXML private TableColumn<NoticeItem, Boolean> selectColumn;

    @FXML private Pagination pagination;
    
    // 검색 관련 UI
    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> searchConditionComboBox;
    @FXML private Button searchButton;
    
    // 필터 관련 UI
    @FXML private ComboBox<String> departmentFilterComboBox;
    
    // 액션 버튼들
    @FXML private Button deleteButton;
    @FXML private Button registerButton;
    
    // 로딩 인디케이터
    @FXML private ProgressIndicator loadingIndicator;

    // ==================== 내부 데이터 저장소 ====================

    /** 전체 공지 목록 데이터 */
    private final ObservableList<NoticeItem> allItems = FXCollections.observableArrayList();
    
    /** 필터링된 공지 목록 데이터 */
    private final ObservableList<NoticeItem> filteredItems = FXCollections.observableArrayList();

    /** 한 페이지에 보여줄 행 수 */
    private static final int ROWS_PER_PAGE = 10;
    
    /** API 클라이언트 */
    private ApiClient apiClient;

    // ==================== 초기화 메서드 ====================

    /**
     * FXML 로드 후 자동 실행되는 초기화 메서드
     */
    @FXML
    public void initialize() {
        apiClient = ApiClient.getInstance();
        setupTable();         // 테이블 컬럼 설정
        setupSearchFunctionality(); // 검색 기능 설정
        setupFilterFunctionality(); // 필터 기능 설정
        setupButtons();       // 버튼 이벤트 설정
        loadNoticesFromDatabase();  // 데이터베이스에서 데이터 로드
        setupPagination();    // 페이지네이션 설정
    }

    /**
     * 버튼 이벤트 설정
     */
    private void setupButtons() {
        // 삭제 버튼 이벤트
        deleteButton.setOnAction(event -> handleDelete(event));
        
        // 등록 버튼 이벤트
        registerButton.setOnAction(event -> handleRegister(event));
    }

    // ==================== 테이블 설정 메서드 ====================

    /**
     * 테이블 컬럼 설정 및 셀 팩토리 바인딩
     */
    private void setupTable() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setFixedCellSize(40); // 행 높이를 40px로 설정
        
        // 헤더 높이를 30px로 고정하고 테이블 높이 설정
        tableView.setStyle("-fx-table-header-height: 30px; -fx-scroll-bar-policy: never; -fx-pref-height: 427px; -fx-max-height: 427px; -fx-min-height: 427px; -fx-table-header-background: #f0f0f0;");
        
        // 테이블이 정확히 10개 행을 표시할 수 있도록 설정 (40px * 10행 + 헤더 높이 27px)
        tableView.setPrefHeight(427); // 40px * 10행 + 헤더 높이 27px = 427px
        tableView.setMaxHeight(427);
        
        // 각 컬럼의 헤더 높이도 고정
        titleColumn.setStyle("-fx-table-header-height: 30px;");
        departmentColumn.setStyle("-fx-table-header-height: 30px;");
        authorColumn.setStyle("-fx-table-header-height: 30px;");
        dateColumn.setStyle("-fx-table-header-height: 30px;");
        selectColumn.setStyle("-fx-table-header-height: 30px;");
        
        // 기본 컬럼 설정
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setStyle("-fx-alignment: center-left;");
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentColumn.setStyle("-fx-alignment: center;");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setStyle("-fx-alignment: center;");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setStyle("-fx-alignment: center;");
        
        // 선택 컬럼은 체크박스 셀로 표시
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setStyle("-fx-alignment: center;");
        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setDisable(false);
                checkBox.setOnAction(e -> {
                    NoticeItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        item.setSelected(checkBox.isSelected());
                    }
                });
            }

            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);

                NoticeItem item = getTableRow() != null ? getTableRow().getItem() : null;

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(selected != null && selected);
                    setGraphic(checkBox);
                }
            }
        });
        
        // 테이블 더블클릭 이벤트 설정
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                NoticeItem selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    openNoticeDetailDialog(selectedItem);
                }
            }
        });
    }
    
    /**
     * 검색 기능 설정
     */
    private void setupSearchFunctionality() {
        searchConditionComboBox.getItems().addAll("전체", "제목", "부서", "작성자");
        searchConditionComboBox.setValue("전체");
        
        // 검색어 입력 시 실시간 필터링
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterNotices();
        });
    }
    
    /**
     * 필터 기능 설정
     */
    private void setupFilterFunctionality() {
        departmentFilterComboBox.getItems().addAll("전체", "인사팀", "개발팀", "마케팅팀", "영업팀");
        departmentFilterComboBox.setValue("전체");
        
        // 필터 변경 시 이벤트
        departmentFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterNotices();
        });
    }

    // ==================== 페이지네이션 관련 ====================

    /**
     * 페이지네이션 설정
     */
    private void setupPagination() {
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
        updatePagination();
    }

    /**
     * 페이지네이션 업데이트
     */
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) filteredItems.size() / ROWS_PER_PAGE);
        pagination.setPageCount(Math.max(1, totalPages));
        
        // 현재 페이지가 총 페이지 수를 초과하면 마지막 페이지로 설정
        if (pagination.getCurrentPageIndex() >= totalPages) {
            pagination.setCurrentPageIndex(Math.max(0, totalPages - 1));
        }
        
        // 첫 페이지 데이터 로드
        if (filteredItems.size() > 0) {
            int startIndex = 0;
            int endIndex = Math.min(ROWS_PER_PAGE, filteredItems.size());
            List<NoticeItem> pageData = filteredItems.subList(startIndex, endIndex);
            tableView.setItems(FXCollections.observableArrayList(pageData));
            System.out.println("페이지네이션 업데이트: " + pageData.size() + "개 항목 (전체: " + filteredItems.size() + "개)");
        } else {
            tableView.setItems(FXCollections.observableArrayList());
            System.out.println("페이지네이션 업데이트: 빈 목록");
        }
    }

    /**
     * 페이지 생성
     */
    private Region createPage(int pageIndex) {
        // 페이지 인덱스가 유효한지 확인
        int totalPages = (int) Math.ceil((double) filteredItems.size() / ROWS_PER_PAGE);
        if (pageIndex >= totalPages) {
            return new Region();
        }
        
        // 현재 페이지의 데이터 계산
        int startIndex = pageIndex * ROWS_PER_PAGE;
        int endIndex = Math.min(startIndex + ROWS_PER_PAGE, filteredItems.size());
        
        // 현재 페이지의 데이터만 테이블에 설정
        List<NoticeItem> pageData = filteredItems.subList(startIndex, endIndex);
        tableView.setItems(FXCollections.observableArrayList(pageData));
        
        System.out.println("페이지 " + (pageIndex + 1) + " 로드: " + pageData.size() + "개 항목 (전체: " + filteredItems.size() + "개)");
        
        return new Region();
    }



    // ==================== 데이터 로드 ====================

    /**
     * 로딩 인디케이터 표시/숨김
     */
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
            loadingIndicator.setManaged(show);
            tableView.setVisible(!show);
            tableView.setManaged(!show);
        }
    }

    /**
     * 데이터베이스에서 공지사항 데이터를 로드
     */
    private void loadNoticesFromDatabase() {
        // 로딩 인디케이터 표시
        showLoading(true);
        
        // 백그라운드에서 데이터 로드
        javafx.concurrent.Task<List<NoticeItem>> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected List<NoticeItem> call() throws Exception {
                // ApiClient를 통해 서버에서 공지사항 데이터 가져오기 (간단한 버전)
                return apiClient.getAllNoticesSimple();
            }
        };
        
        // 성공 시 처리
        loadTask.setOnSucceeded(event -> {
            List<NoticeItem> notices = loadTask.getValue();
            
            allItems.clear();
            allItems.addAll(notices);
            
            // 필터링된 데이터 초기화
            filteredItems.clear();
            filteredItems.addAll(allItems);
            
            System.out.println("공지사항 로드 완료: " + notices.size() + "개");
            
            // 필터링과 페이지네이션 업데이트
            filterNotices();
            showLoading(false);
        });
        
        // 실패 시 처리
        loadTask.setOnFailed(event -> {
            System.out.println("공지사항 로드 중 오류 발생: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
            // API 오류 시 빈 목록으로 설정
            allItems.clear();
            filteredItems.clear();
            System.out.println("API 오류로 인해 빈 목록으로 설정되었습니다.");
            
            // 빈 목록일 때도 페이지네이션 업데이트
            updatePagination();
            showLoading(false);
        });
        
        // 백그라운드 스레드에서 실행
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    /**
     * 공지사항 필터링
     */
    private void filterNotices() {
        String searchText = searchTextField.getText().toLowerCase();
        String searchCondition = searchConditionComboBox.getValue();
        String departmentFilter = departmentFilterComboBox.getValue();
        
        List<NoticeItem> filtered = allItems.stream()
            .filter(notice -> {
                // 검색 조건 필터링
                if (!searchText.isEmpty()) {
                    switch (searchCondition) {
                        case "제목":
                            if (notice.getTitle() == null || !notice.getTitle().toLowerCase().contains(searchText)) {
                                return false;
                            }
                            break;
                        case "부서":
                            if (notice.getDepartment() == null || !notice.getDepartment().toLowerCase().contains(searchText)) {
                                return false;
                            }
                            break;
                        case "작성자":
                            if (notice.getAuthor() == null || !notice.getAuthor().toLowerCase().contains(searchText)) {
                                return false;
                            }
                            break;
                        default: // "전체"
                            if ((notice.getTitle() == null || !notice.getTitle().toLowerCase().contains(searchText)) &&
                                (notice.getDepartment() == null || !notice.getDepartment().toLowerCase().contains(searchText)) &&
                                (notice.getAuthor() == null || !notice.getAuthor().toLowerCase().contains(searchText))) {
                                return false;
                            }
                            break;
                    }
                }
                
                // 부서 필터링
                if (!"전체".equals(departmentFilter) && (notice.getDepartment() == null || !departmentFilter.equals(notice.getDepartment()))) {
                    return false;
                }
                
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
        
        filteredItems.clear();
        filteredItems.addAll(filtered);
        
        // 페이지네이션 업데이트 및 첫 페이지로 리셋
        updatePagination();
        pagination.setCurrentPageIndex(0);
    }

    // ==================== 버튼 이벤트 핸들러 ====================

    /**
     * 삭제 버튼 클릭 시 선택된 항목을 삭제
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        List<NoticeItem> toRemove = new ArrayList<>();
        for (NoticeItem item : allItems) {
            if (item.isSelected()) {
                toRemove.add(item);
            }
        }

        if (toRemove.isEmpty()) {
            showAlert("알림", "삭제할 공지사항을 선택해주세요.", Alert.AlertType.WARNING);
            return;
        }

        // 삭제 확인 다이얼로그
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText("선택된 공지사항을 삭제하시겠습니까?");
        alert.setContentText("삭제할 공지사항: " + toRemove.size() + "개\n\n이 작업은 되돌릴 수 없습니다.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 실제 API 호출로 삭제
                for (NoticeItem item : toRemove) {
                    try {
                        boolean deleteSuccess = apiClient.deleteNotice(item.getNoticeId());
                        if (deleteSuccess) {
                            System.out.println("공지사항 삭제 성공: " + item.getTitle());
                        } else {
                            System.out.println("공지사항 삭제 실패: " + item.getTitle());
                        }
                    } catch (Exception e) {
                        System.out.println("공지사항 삭제 중 오류: " + e.getMessage());
                    }
                }
                
                // 로컬에서도 제거
                allItems.removeAll(toRemove);
                filterNotices();
                showAlert("완료", "선택된 공지사항이 삭제되었습니다.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * 등록 버튼 클릭 시 공지사항 등록 다이얼로그 열기
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        openNoticeEditDialog(null);
    }
    
    /**
     * 검색 버튼 클릭 이벤트
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        filterNotices();
    }
    

    
    /**
     * 공지사항 상세 보기 다이얼로그 열기
     */
    private void openNoticeDetailDialog(NoticeItem notice) {
        System.out.println("<<<<< LATEST CODE MARKER: openNoticeDetailDialog CALLED >>>>>");
        try {
            // FXML 로더 생성
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/companycore/view/content/tasks/noticeDetailDialog.fxml"));
            
            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML 파일을 찾을 수 없습니다: noticeDetailDialog.fxml");
            }
            
            // --- FIX START ---
            // 상세보기를 위해 서버로부터 완전한 공지사항 정보를 다시 가져옵니다.
            System.out.println("상세보기를 위해 ID(" + notice.getNoticeId() + ")의 전체 정보를 다시 요청합니다.");
            NoticeItem fullNoticeItem = apiClient.getNoticeById(notice.getNoticeId());

            if (fullNoticeItem == null) {
                showAlert("오류", "공지사항의 전체 정보를 불러오는 데 실패했습니다.", Alert.AlertType.ERROR);
                return;
            }
            // --- FIX END ---

            Parent root = loader.load();
            
            NoticeDetailController detailController = loader.getController();
            if (detailController == null) {
                throw new RuntimeException("NoticeDetailController를 찾을 수 없습니다.");
            }
            
            // 완전한 정보가 담긴 공지사항 객체를 컨트롤러에 설정합니다.
            detailController.setNoticeItem(fullNoticeItem);
            
            // 다이얼로그 스테이지 생성
            Stage dialogStage = new Stage();
            dialogStage.setTitle("공지사항 상세");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            System.out.println("공지사항 상세보기 다이얼로그 오류: " + e.getMessage());
            e.printStackTrace();
            showAlert("오류", "공지사항 상세 보기 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 공지사항 수정 다이얼로그 열기
     */
    private void openNoticeEditDialog(NoticeItem notice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/noticeEditDialog.fxml"));
            Parent root = loader.load();
            
            NoticeEditController editController = loader.getController();
            editController.setParentController(this);
            
            if (notice != null) {
                // 수정 모드
                editController.setEditNoticeMode(notice);
            } else {
                // 등록 모드
                editController.setNewNoticeMode();
            }
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(notice != null ? "공지사항 수정" : "공지사항 등록");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            showAlert("오류", "공지사항 다이얼로그를 열 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

    /**
     * 데이터 새로고침 (공개 메서드)
     */
    public void refreshData() {
        loadNoticesFromDatabase();
    }
}