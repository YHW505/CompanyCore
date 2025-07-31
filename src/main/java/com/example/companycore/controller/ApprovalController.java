package com.example.companycore.controller;

import com.example.companycore.DTO.ApprovalItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApprovalController {

    @FXML private TableView<ApprovalItem> approvalTable;
    @FXML private TableColumn<ApprovalItem, String> colNumber;       // 순번 컬럼 추가
    @FXML private TableColumn<ApprovalItem, String> colTitle;
    @FXML private TableColumn<ApprovalItem, String> colDepartment;
    @FXML private TableColumn<ApprovalItem, String> colAuthor;
    @FXML private TableColumn<ApprovalItem, String> colDate;
    @FXML private TableColumn<ApprovalItem, String> colAction;
    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    private final ObservableList<ApprovalItem> fullData = FXCollections.observableArrayList();
    private ObservableList<ApprovalItem> viewData = FXCollections.observableArrayList();

    private int visibleRowCount = 10;  // 화면에 보이는 행 개수 (초기값)

    private static final boolean TEST_MODE = true;

    @FXML
    public void initialize() {
        // TableView 행 높이 고정 (UI에 맞게 조절하세요)
        approvalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        approvalTable.setFixedCellSize(40);

        // visibleRowCount 초기 계산 (초기 TableView 높이 기준)
        visibleRowCount = (int) (approvalTable.getHeight() / approvalTable.getFixedCellSize());
        if (visibleRowCount == 0) visibleRowCount = 10; // 초기 0 방지

        // TableView 높이 변동 시 visibleRowCount 재계산 후 페이징 재설정
        approvalTable.heightProperty().addListener((obs, oldVal, newVal) -> {
            int newCount = (int) (newVal.doubleValue() / approvalTable.getFixedCellSize());
            if (newCount != visibleRowCount && newCount > 0) {
                visibleRowCount = newCount;
                resetPagingToFirstPage();
            }
        });

        // 컬럼별 셀값 설정
        colNumber.setCellValueFactory(cellData -> {
            ApprovalItem item = cellData.getValue();
            int index = approvalTable.getItems().indexOf(item) + 1
                    + pagination.getCurrentPageIndex() * visibleRowCount;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());
        colAction.setCellValueFactory(cd -> cd.getValue().actionProperty());

        // 컬럼 정렬 스타일
        colNumber.setStyle("-fx-alignment: CENTER;");
        colTitle.setStyle("-fx-alignment: CENTER;");
        colDepartment.setStyle("-fx-alignment: CENTER;");
        colAuthor.setStyle("-fx-alignment: CENTER;");
        colDate.setStyle("-fx-alignment: CENTER;");
        colAction.setStyle("-fx-alignment: CENTER;");

        approvalTable.setPlaceholder(new Label("데이터가 없습니다."));

        if (TEST_MODE) {
            fillDummyData(37);
            viewData = FXCollections.observableArrayList(fullData);
            resetPagingToFirstPage();
        } else {
            loadDataFromServer();
        }

        pagination.setPageFactory(this::createPage);
    }

    private void fillDummyData(int count) {
        String[] departments = {"인사부", "총무부", "개발1팀", "개발2팀", "영업부"};
        String[] authors = {"한교동", "김다빈", "케로케로피", "김다번", "김다분"};

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i <= count; i++) {
            fullData.add(new ApprovalItem(
                    String.valueOf(i),
                    "기안서",
                    departments[i % departments.length],
                    authors[i % authors.length],
                    LocalDate.now().minusDays(i).format(fmt),
                    null,
                    "승인/거부"
            ));
        }
    }

    private void resetPagingToFirstPage() {
        int count = Math.max((viewData.size() + visibleRowCount - 1) / visibleRowCount, 1);
        pagination.setPageCount(count);
        pagination.setCurrentPageIndex(0);
        applyPageItems(0);
    }

    private void applyPageItems(int pageIndex) {
        if (viewData.isEmpty()) {
            approvalTable.setItems(FXCollections.observableArrayList());
            return;
        }
        int from = pageIndex * visibleRowCount;
        int to = Math.min(from + visibleRowCount, viewData.size());
        approvalTable.setItems(FXCollections.observableArrayList(viewData.subList(from, to)));
    }

    private Node createPage(int pageIndex) {
        applyPageItems(pageIndex);
        return new Region();  // 빈 노드 반환
    }

    private void loadDataFromServer() {
        Task<ObservableList<ApprovalItem>> task = new Task<>() {
            @Override
            protected ObservableList<ApprovalItem> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/approvals"))
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
                        dto.getDate(),
                        null,
                        dto.getAction()
                )).toList();

                return FXCollections.observableArrayList(items);
            }
        };

        task.setOnSucceeded(e -> {
            fullData.setAll(task.getValue());
            viewData = FXCollections.observableArrayList(fullData);
            resetPagingToFirstPage();
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

        if (keyword.isEmpty()) {
            viewData = FXCollections.observableArrayList(fullData);
        } else {
            viewData = fullData.filtered(item ->
                    (item.getTitle() != null && item.getTitle().toLowerCase().contains(keyword)) ||
                            (item.getDepartment() != null && item.getDepartment().toLowerCase().contains(keyword)) ||
                            (item.getAuthor() != null && item.getAuthor().toLowerCase().contains(keyword))
            );
        }

        resetPagingToFirstPage();

        approvalTable.setPlaceholder(
                new Label(viewData.isEmpty() ? "검색 결과가 없습니다." : "데이터가 없습니다.")
        );
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        ApprovalItem selected = approvalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "삭제할 항목을 선택하세요.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "선택한 항목을 삭제하시겠습니까?", ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if (!TEST_MODE) {
                            HttpClient client = HttpClient.newHttpClient();
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/api/approvals/" + selected.getId()))
                                    .DELETE()
                                    .build();

                            HttpResponse<String> resp = client.send(request,
                                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                            if (resp.statusCode() != 200) {
                                throw new RuntimeException("삭제 실패: " + resp.body());
                            }
                        }
                        return null;
                    }
                };

                task.setOnSucceeded(e -> Platform.runLater(() -> {
                    fullData.remove(selected);
                    viewData.remove(selected);
                    int current = pagination.getCurrentPageIndex();
                    int totalPages = Math.max((viewData.size() + visibleRowCount - 1) / visibleRowCount, 1);
                    pagination.setPageCount(totalPages);
                    pagination.setCurrentPageIndex(Math.min(current, totalPages - 1));
                    applyPageItems(pagination.getCurrentPageIndex());
                    new Alert(Alert.AlertType.INFORMATION, "삭제되었습니다.").showAndWait();
                }));

                task.setOnFailed(e -> Platform.runLater(() -> {
                    Throwable ex = task.getException();
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "삭제 중 오류: " + ex.getMessage()).showAndWait();
                }));

                new Thread(task).start();
            }
        });
    }
}