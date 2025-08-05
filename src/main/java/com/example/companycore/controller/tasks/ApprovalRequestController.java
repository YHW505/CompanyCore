package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.ApprovalItem;
import com.example.companycore.model.dto.ApprovalDto;
import com.example.companycore.service.ApiClient;
import com.example.companycore.service.ApprovalApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApprovalRequestController {

    @FXML private TableView<ApprovalItem> approvalRequestTable;
    @FXML private TableColumn<ApprovalItem, String> colNumber;
    @FXML private TableColumn<ApprovalItem, String> colTitle;
    @FXML private TableColumn<ApprovalItem, String> colDepartment;
    @FXML private TableColumn<ApprovalItem, String> colAuthor;
    @FXML private TableColumn<ApprovalItem, String> colDate;
    @FXML private TableColumn<ApprovalItem, String> colStatus;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Pagination pagination;

    private final ObservableList<ApprovalItem> fullData = FXCollections.observableArrayList();
    private ObservableList<ApprovalItem> viewData = FXCollections.observableArrayList();

    private int visibleRowCount = 10;
    private static final boolean TEST_MODE = false;

    @FXML
    public void initialize() {
        setupTable();
        setupPagination();
        if (TEST_MODE) {
            loadApprovalRequestsFromDatabase();
        } else {
            loadDataFromServer();
        }
    }

    private void setupTable() {
        approvalRequestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        approvalRequestTable.setFixedCellSize(40);
        approvalRequestTable.setPrefHeight(427);
        approvalRequestTable.setMaxHeight(427);
        approvalRequestTable.setStyle("-fx-table-header-height: 30px; -fx-scroll-bar-policy: never; -fx-pref-height: 427px; -fx-max-height: 427px; -fx-min-height: 427px; -fx-table-header-background: #f0f0f0;");

        colNumber.setCellValueFactory(cellData -> {
            ApprovalItem item = cellData.getValue();
            int index = viewData.indexOf(item) + 1 + pagination.getCurrentPageIndex() * visibleRowCount;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());
        colStatus.setCellValueFactory(cd -> {
            ApprovalItem item = cd.getValue();
            return new ReadOnlyStringWrapper(item.getStatusKorean());
        });

        // 상태 컬럼 스타일 설정
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label label = new Label(item);
                    switch (item) {
                        case "승인됨":
                            label.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            break;
                        case "거부됨":
                            label.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            break;
                        case "대기중":
                        default:
                            label.setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                            break;
                    }
                    setGraphic(label);
                    setText(null);
                }
            }
        });

        // 컬럼별 스타일 설정
        colNumber.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colTitle.setStyle("-fx-table-header-height: 30px; -fx-alignment: center-left;");
        colDepartment.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colAuthor.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colDate.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colStatus.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");

        approvalRequestTable.setPlaceholder(new Label("데이터가 없습니다."));

        approvalRequestTable.setRowFactory(tv -> {
            TableRow<ApprovalItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ApprovalItem rowData = row.getItem();
                    showApprovalDetail(rowData);
                }
            });
            return row;
        });

        // ComboBox 초기값 및 아이템 세팅
        searchComboBox.getSelectionModel().select("전체");
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createPage);
        pagination.setVisible(true);
        updatePagination();
    }

    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) fullData.size() / visibleRowCount);
        pagination.setPageCount(Math.max(1, totalPages));
        pagination.setVisible(true);
        
        // 현재 페이지 데이터 로드
        int currentPage = pagination.getCurrentPageIndex();
        int startIndex = currentPage * visibleRowCount;
        int endIndex = Math.min(startIndex + visibleRowCount, fullData.size());
        
        viewData.clear();
        if (startIndex < fullData.size()) {
            viewData.addAll(fullData.subList(startIndex, endIndex));
        }
        
        approvalRequestTable.setItems(viewData);
    }

    /**
     * 데이터베이스에서 내 결재 요청 목록을 로드합니다.
     */
    private void loadApprovalRequestsFromDatabase() {
        if (TEST_MODE) {
            // 테스트 모드일 때만 더미 데이터 로드
            loadTestData();
        } else {
            // 실제 서버에서 데이터 로드
            loadDataFromServer();
        }
    }

    /**
     * 테스트 데이터를 로드합니다.
     */
    private void loadTestData() {
        fullData.clear();
        viewData.clear();
        System.out.println("테스트 데이터 로드 완료: 0개");
        updatePagination();
    }



    private Node createPage(int pageIndex) {
        updatePagination();
        return new Region();
    }

    private void loadDataFromServer() {
        try {
            ApiClient apiClient = ApiClient.getInstance();
            List<ApprovalDto> approvals = apiClient.getMyRequests();
            
            fullData.clear();
            viewData.clear();
            
            if (approvals != null) {
                for (ApprovalDto approvalDto : approvals) {
                    // 통합 DTO를 사용하여 변환
                    ApprovalItem item = ApprovalItem.fromApprovalDto(approvalDto);
                    fullData.add(item);
                }
            }
            
            viewData.addAll(fullData);
            System.out.println("서버에서 결재 요청 데이터 로드 완료: " + fullData.size() + "개");
            updatePagination();
            
        } catch (Exception e) {
            System.err.println("결재 요청 데이터 로드 실패: " + e.getMessage());
            e.printStackTrace();
            fullData.clear();
            viewData.clear();
            updatePagination();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = (searchField.getText() == null ? "" : searchField.getText())
                .trim().toLowerCase();

        String selectedColumn = searchComboBox.getSelectionModel().getSelectedItem();
        if (selectedColumn == null) selectedColumn = "전체";

        if (keyword.isEmpty()) {
            viewData = FXCollections.observableArrayList(fullData);
        } else {
            switch (selectedColumn) {
                case "제목":
                    viewData = fullData.filtered(item ->
                            item.getTitle() != null && item.getTitle().toLowerCase().contains(keyword));
                    break;
                case "부서":
                    viewData = fullData.filtered(item ->
                            item.getDepartment() != null && item.getDepartment().toLowerCase().contains(keyword));
                    break;
                case "작성자":
                    viewData = fullData.filtered(item ->
                            item.getAuthor() != null && item.getAuthor().toLowerCase().contains(keyword));
                    break;
                case "전체":
                default:
                    viewData = fullData.filtered(item ->
                            (item.getTitle() != null && item.getTitle().toLowerCase().contains(keyword)) ||
                                    (item.getDepartment() != null && item.getDepartment().toLowerCase().contains(keyword)) ||
                                    (item.getAuthor() != null && item.getAuthor().toLowerCase().contains(keyword))
                    );
                    break;
            }
        }

        updatePagination();

        approvalRequestTable.setPlaceholder(
                new Label(viewData.isEmpty() ? "검색 결과가 없습니다." : "데이터가 없습니다.")
        );
    }

    @FXML
    public void handleDelete() {
        ApprovalItem selected = approvalRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "삭제할 항목을 선택하세요.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "선택한 항목을 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    ApiClient apiClient = ApiClient.getInstance();
                    // TODO: 실제 결재 ID를 사용하여 삭제
                    // 현재는 제목으로 식별하지만, 실제로는 고유 ID를 사용해야 함
                    boolean success = apiClient.deleteApproval(1L); // 임시로 1L 사용
                    
                    if (success) {
                        fullData.remove(selected);
                        updatePagination();
                        new Alert(Alert.AlertType.INFORMATION, "삭제되었습니다.").showAndWait();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "삭제에 실패했습니다.").showAndWait();
                    }
                } catch (Exception e) {
                    System.err.println("결재 삭제 실패: " + e.getMessage());
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "삭제 중 오류가 발생했습니다: " + e.getMessage()).showAndWait();
                }
            }
        });
    }

    @FXML
    public void handleNewRequest() {
        try {
            // 기존 결재 요청 페이지로 이동
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/approvalRequestForm.fxml"));
            Parent root = loader.load();

            ApprovalRequestFormController formController = loader.getController();
            formController.setParentController(this); // 부모 컨트롤러 설정

            Stage stage = new Stage();
            stage.setTitle("결재 요청");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "결재 요청 페이지 열기 실패").showAndWait();
        }
    }

    /**
     * 결재 요청 목록을 새로고침합니다.
     */
    public void refreshApprovalRequests() {
        System.out.println("🔄 결재 요청 목록 새로고침 시작");
        loadDataFromServer();
    }

    private void showApprovalDetail(ApprovalItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/approvalDetailDialog.fxml"));
            Parent root = loader.load();

            ApprovalDetailController controller = loader.getController();
            controller.setApprovalItem(item);

            Stage stage = new Stage();
            stage.setTitle("결재 상세보기");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

//            if(!controller.getIsProgress()) {
//            }

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "상세보기 창 열기 실패").showAndWait();
        }
    }
} 