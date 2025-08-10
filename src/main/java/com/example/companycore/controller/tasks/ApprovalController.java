package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.ApprovalItem;
import com.example.companycore.service.ApiClient;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ApprovalController {

    @FXML private TableView<ApprovalItem> approvalTable;
    @FXML private TableColumn<ApprovalItem, String> colNumber;
    @FXML private TableColumn<ApprovalItem, String> colTitle;
    @FXML private TableColumn<ApprovalItem, String> colDepartment;
    @FXML private TableColumn<ApprovalItem, String> colAuthor;
    @FXML private TableColumn<ApprovalItem, String> colDate;
    @FXML private TableColumn<ApprovalItem, String> colAction;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchComboBox;  // 여기 추가
    @FXML private Pagination pagination;

    private final ObservableList<ApprovalItem> fullData = FXCollections.observableArrayList();
    private ObservableList<ApprovalItem> viewData = FXCollections.observableArrayList();

    private int visibleRowCount = 10;

    @FXML
    public void initialize() {
        setupTable();
        setupPagination();
        loadDataFromServer();
    }

    private void setupTable() {
        approvalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        approvalTable.setFixedCellSize(40);
        approvalTable.setPrefHeight(427);
        approvalTable.setMaxHeight(427);
        approvalTable.setStyle("-fx-table-header-height: 30px; -fx-scroll-bar-policy: never; -fx-pref-height: 427px; -fx-max-height: 427px; -fx-min-height: 427px; -fx-table-header-background: #f0f0f0;");

        colNumber.setCellValueFactory(cellData -> {
            ApprovalItem item = cellData.getValue();
            int index = viewData.indexOf(item) + 1 + pagination.getCurrentPageIndex() * visibleRowCount;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("승인");
            private final Button rejectBtn = new Button("거부");
            private final HBox container = new HBox(10, approveBtn, rejectBtn);

            {
                container.setAlignment(Pos.CENTER);
                approveBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                approveBtn.setOnAction(e -> {
                    ApprovalItem item = getTableView().getItems().get(getIndex());
                    // API 호출을 통해 승인 처리
                    try {
                        ApiClient.getInstance().getApprovalApiClient().approveApproval(item.getServerId());
                        Platform.runLater(() -> {
                            item.setStatus("APPROVED"); // 백엔드와 일치하는 상태 값 사용
                            getTableView().refresh();
                            new Alert(Alert.AlertType.INFORMATION, "결재가 승인되었습니다.").showAndWait();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.ERROR, "결재 승인 실패: " + ex.getMessage()).showAndWait();
                        });
                    }
                });

                rejectBtn.setOnAction(e -> {
                    ApprovalItem item = getTableView().getItems().get(getIndex());

                    // 거부 사유 입력 다이얼로그
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("결재 거부");
                    dialog.setHeaderText("결재를 거부하시겠습니까?");
                    dialog.setContentText("거부 사유를 입력하세요:");
                    ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("확인");
                    ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("취소");

                    dialog.showAndWait().ifPresent(rejectionReason -> {
                        if (rejectionReason.trim().isEmpty()) {
                            Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, "거부 사유를 입력해야 합니다.").showAndWait());
                            return;
                        }
                        // API 호출을 통해 거부 처리
                        try {
                            ApiClient.getInstance().getApprovalApiClient().rejectApproval(item.getServerId(), rejectionReason);
                            Platform.runLater(() -> {
                                item.setStatus("REJECTED"); // 백엔드와 일치하는 상태 값 사용
                                getTableView().refresh();
                                new Alert(Alert.AlertType.INFORMATION, "결재가 거부되었습니다.").showAndWait();
                            });
                        } catch (Exception ex) {
                            Platform.runLater(() -> {
                                new Alert(Alert.AlertType.ERROR, "결재 거부 실패: " + ex.getMessage()).showAndWait();
                            });
                        }
                    });
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ApprovalItem approvalItem = getTableView().getItems().get(getIndex());
                    String status = approvalItem.getStatus();
                    if ("APPROVED".equals(status)) {
                        Label label = new Label("승인됨");
                        label.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                        setGraphic(label);
                    } else if ("REJECTED".equals(status)) {
                        Label label = new Label("거부됨");
                        label.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                        setGraphic(label);
                    } else {
                        setGraphic(container);
                        setText(null);
                    }
                }
            }
        });

        // 컬럼별 스타일 설정
        colNumber.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colTitle.setStyle("-fx-table-header-height: 30px; -fx-alignment: center-left;");
        colDepartment.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colAuthor.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colDate.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");
        colAction.setStyle("-fx-table-header-height: 30px; -fx-alignment: center;");

        approvalTable.setPlaceholder(new Label("데이터가 없습니다."));

        approvalTable.setRowFactory(tv -> {
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
        
        approvalTable.setItems(viewData);
    }



    /**
     * 데이터베이스에서 결재 목록을 로드합니다.
     */
    private void loadApprovalsFromDatabase() {
        // 현재는 빈 데이터로 초기화 (API 연동 예정)
        fullData.clear();
        updatePagination();
        System.out.println("결재 데이터 로드 완료: 0개");
    }

    private Node createPage(int pageIndex) {
        updatePagination();
        return new Region();
    }

    private void loadDataFromServer() {
        Task<ObservableList<ApprovalItem>> task = new Task<>() {
            @Override
            protected ObservableList<ApprovalItem> call() throws Exception {
                // 현재 사용자 정보 가져오기
                var currentUser = ApiClient.getInstance().getCurrentUser();
                if (currentUser == null || currentUser.getDepartmentId() == null) {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "사용자 정보 또는 부서 정보를 가져올 수 없습니다.").showAndWait());
                    return FXCollections.emptyObservableList();
                }

                // departmentId는 Integer 타입이므로 변환
                Integer departmentId = currentUser.getDepartmentId();

                // ApprovalApiClient를 통해 페이지네이션된 대기중인 결재 목록 가져오기
                // 여기서는 첫 페이지, 10개씩, requestDate 기준으로 내림차순 정렬을 기본으로 합니다.
                Map<String, Object> responseMap = ApiClient.getInstance().getApprovalApiClient()
                        .getMyPendingWithPagination(departmentId, 0, 10, "requestDate", "desc");

                if (responseMap == null || !responseMap.containsKey("content")) {
                    throw new IllegalStateException("서버 응답이 유효하지 않습니다.");
                }

                // "content" 키의 값을 List<Map<String, Object>>로 변환
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) responseMap.get("content");

                // Map 리스트를 ApprovalItem 리스트로 변환
                List<ApprovalItem> approvalItems = new ArrayList<>();
                for (Map<String, Object> itemMap : contentList) {
                    // 필요한 필드들을 추출하여 ApprovalItem 생성
                    Long serverId = ((Number) itemMap.get("id")).longValue();
                    String title = (String) itemMap.get("title");
                    String content = (String) itemMap.get("content");
                    String status = (String) itemMap.get("status");
                    String requestDateStr = (String) itemMap.get("requestDate");
                    
                    // requester 정보 추출
                    Map<String, Object> requesterMap = (Map<String, Object>) itemMap.get("requester");
                    String author = (requesterMap != null) ? (String) requesterMap.get("username") : "알 수 없음";
                    String department = (requesterMap != null) ? (String) requesterMap.get("department") : "알 수 없음";

                    // 날짜 파싱 (ISO 8601 형식)
                    LocalDate requestDate = null;
                    if (requestDateStr != null) {
                        try {
                            requestDate = LocalDateTime.parse(requestDateStr).toLocalDate();
                        } catch (Exception e) {
                            System.err.println("날짜 파싱 오류: " + requestDateStr + " - " + e.getMessage());
                        }
                    }

                    approvalItems.add(new ApprovalItem(
                            serverId, title, department, author,
                            (requestDate != null) ? requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "날짜 없음",
                            content, status
                    ));
                }
                return FXCollections.observableArrayList(approvalItems);
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

        approvalTable.setPlaceholder(
                new Label(viewData.isEmpty() ? "검색 결과가 없습니다." : "데이터가 없습니다.")
        );
    }

    @FXML
    public void handleDelete() {
        ApprovalItem selected = approvalTable.getSelectionModel().getSelectedItem();
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