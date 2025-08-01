package com.example.companycore.controller.tasks;

import com.example.companycore.model.dto.NoticeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

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
 * - 공지 등록 / 삭제 기능
 *
 * @author
 * @version 1.0
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

    // ==================== 내부 데이터 저장소 ====================

    /** 전체 공지 목록 데이터 */
    private final ObservableList<NoticeItem> allItems = FXCollections.observableArrayList();

    /** 한 페이지에 보여줄 행 수 */
    private static final int ROWS_PER_PAGE = 10;

    // ==================== 초기화 메서드 ====================

    /**
     * FXML 로드 후 자동 실행되는 초기화 메서드
     */
    @FXML
    public void initialize() {
        setupTable();         // 테이블 컬럼 설정
        generateDummyData();  // 더미 데이터 추가
        setupPagination();    // 페이지네이션 설정
    }

    // ==================== 테이블 설정 메서드 ====================

    /**
     * 테이블 컬럼 설정 및 셀 팩토리 바인딩
     */
    private void setupTable() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setFixedCellSize(40);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // 선택 컬럼은 체크박스 셀로 표시
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
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

                // 데이터가 없거나 비어 있는 경우: 체크박스 제거
                if (empty || item == null || item.getTitle() == null || item.getTitle().isEmpty()) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(selected != null && selected);
                    setGraphic(checkBox);
                }
            }
        });
    }

    // ==================== 페이지네이션 관련 ====================

    /**
     * 페이지네이션 설정 및 초기 페이지 구성
     */
    private void setupPagination() {
        int totalPages = Math.max(1, (int) Math.ceil((double) allItems.size() / ROWS_PER_PAGE));
        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    /**
     * 해당 페이지 인덱스에 맞는 데이터를 테이블에 표시
     *
     * @param pageIndex 페이지 인덱스 (0부터 시작)
     * @return 빈 Region (UI 구성 필요 없음)
     */
    private Region createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allItems.size());

        ObservableList<NoticeItem> currentPageData = FXCollections.observableArrayList();

        if (fromIndex < toIndex) {
            currentPageData.addAll(allItems.subList(fromIndex, toIndex));
        }

        // 남은 행은 빈 NoticeItem으로 채움 → 테이블 모양 유지
        while (currentPageData.size() < ROWS_PER_PAGE) {
            currentPageData.add(new NoticeItem("", "", "", null, false));
        }

        tableView.setItems(currentPageData);
        return new Region();
    }

    // ==================== 데이터 생성 ====================

    /**
     * 테스트용 더미 데이터를 생성하여 allItems에 추가
     */
    private void generateDummyData() {
        IntStream.rangeClosed(1, 16).forEach(i -> {
            allItems.add(new NoticeItem(
                    "공지 제목 " + i,
                    "부서 " + (i % 3 + 1),
                    "작성자" + i,
                    LocalDate.now().minusDays(i),
                    false
            ));
        });
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

        allItems.removeAll(toRemove);

        // 페이지 수 재계산
        int totalPages = Math.max(1, (int) Math.ceil((double) allItems.size() / ROWS_PER_PAGE));
        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(Math.min(pagination.getCurrentPageIndex(), totalPages - 1));
        pagination.setPageFactory(this::createPage);
    }

    /**
     * 등록 버튼 클릭 시 호출되는 메서드
     * (기능은 향후 구현)
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        System.out.println("등록 버튼 클릭됨 (기능 구현 필요)");
    }
}