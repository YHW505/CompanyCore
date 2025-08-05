package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.ApprovalItem;
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
    private static final boolean TEST_MODE = true;

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
        colStatus.setCellValueFactory(cd -> cd.getValue().statusProperty());

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
                        case "승인":
                            label.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                            break;
                        case "거부":
                            label.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            break;
                        case "대기":
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
        // 현재는 빈 데이터로 초기화 (API 연동 예정)
        fullData.clear();
        updatePagination();
        System.out.println("내 결재 요청 데이터 로드 완료: 0개");
    }

    private Node createPage(int pageIndex) {
        updatePagination();
        return new Region();
    }

    private void loadDataFromServer() {
        Task<ObservableList<ApprovalItem>> task = new Task<>() {
            @Override
            protected ObservableList<ApprovalItem> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                // 내가 요청한 결재 목록을 가져오기 위해 my-requests 엔드포인트 사용
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/approvals/my-requests/1")) // userId = 1로 가정
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(
                        request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

                if (response.statusCode() != 200) {
                    throw new IllegalStateException("서버 오류: " + response.statusCode());
                }

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                List<ApprovalItem> dtoList = mapper.readValue(response.body(), new TypeReference<>() {});
                var items = dtoList.stream().map(dto -> new ApprovalItem(
                        dto.getId(), dto.getTitle(), dto.getDepartment(), dto.getAuthor(),
                        dto.getDate(), null, dto.getStatus()
                )).toList();

                return FXCollections.observableArrayList(items);
            }
        };

        task.setOnSucceeded(e -> {
            fullData.setAll(task.getValue());
            updatePagination();
        });

        task.setOnFailed(e -> Platform.runLater(() -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "데이터 로드 실패: " + ex.getMessage()).showAndWait();
        }));

        new Thread(task).start();
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
                fullData.remove(selected);
                updatePagination();
                new Alert(Alert.AlertType.INFORMATION, "삭제되었습니다.").showAndWait();
            }
        });
    }

    @FXML
    public void handleNewRequest() {
        try {
            // 기존 결재 요청 페이지로 이동
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/approvalRequestForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("결재 요청");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "결재 요청 페이지 열기 실패").showAndWait();
        }
    }

    private void showApprovalDetail(ApprovalItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/approvalDetail.fxml"));
            Parent root = loader.load();

            ApprovalDetailController controller = loader.getController();
            controller.setApprovalItem(item);

            Stage stage = new Stage();
            stage.setTitle("결재 상세보기");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "상세보기 창 열기 실패").showAndWait();
        }
    }
} 