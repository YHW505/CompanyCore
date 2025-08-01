package com.example.companycore.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MeetingListController {

    @FXML private TableView<MeetingItem> meetingTable;
    @FXML private TableColumn<MeetingItem, String> colNumber;
    @FXML private TableColumn<MeetingItem, String> colTitle;
    @FXML private TableColumn<MeetingItem, String> colDepartment;
    @FXML private TableColumn<MeetingItem, String> colAuthor;
    @FXML private TableColumn<MeetingItem, String> colDate;
    @FXML private TableColumn<MeetingItem, String> colAction;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Pagination pagination;

    private final ObservableList<MeetingItem> fullData = FXCollections.observableArrayList();
    private ObservableList<MeetingItem> viewData = FXCollections.observableArrayList();

    private int visibleRowCount = 10;
    private static final boolean TEST_MODE = true;

    @FXML
    public void initialize() {
        meetingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        meetingTable.setFixedCellSize(40);

        visibleRowCount = (int) (meetingTable.getHeight() / meetingTable.getFixedCellSize());
        if (visibleRowCount == 0) visibleRowCount = 10;

        meetingTable.heightProperty().addListener((obs, oldVal, newVal) -> {
            int newCount = (int) (newVal.doubleValue() / meetingTable.getFixedCellSize());
            if (newCount != visibleRowCount && newCount > 0) {
                visibleRowCount = newCount;
                resetPagingToFirstPage();
            }
        });

        colNumber.setCellValueFactory(cellData -> {
            MeetingItem item = cellData.getValue();
            int index = meetingTable.getItems().indexOf(item) + 1
                    + pagination.getCurrentPageIndex() * visibleRowCount;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button detailBtn = new Button("상세보기");

            {
                detailBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 8;");
                detailBtn.setOnAction(e -> {
                    MeetingItem item = getTableView().getItems().get(getIndex());
                    showMeetingDetail(item);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailBtn);
                }
            }
        });

        // 검색 콤보박스 초기화
        searchComboBox.getItems().addAll("전체", "제목", "부서", "작성자");
        searchComboBox.setValue("전체");

        // 샘플 데이터 로드
        if (TEST_MODE) {
            fillDummyData(20);
        }

        // 페이지네이션 설정
        setupPagination();
    }

    private void fillDummyData(int count) {
        fullData.clear();
        for (int i = 1; i <= count; i++) {
            String title = "회의 " + i;
            String department = getRandomDepartment();
            String author = getRandomAuthor();
            String date = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            fullData.add(new MeetingItem(title, department, author, date));
        }
        viewData.addAll(fullData);
        meetingTable.setItems(viewData);
    }

    private String getRandomDepartment() {
        String[] departments = {"개발팀", "인사팀", "마케팅팀", "영업팀", "총무팀"};
        return departments[(int) (Math.random() * departments.length)];
    }

    private String getRandomAuthor() {
        String[] authors = {"김철수", "이영희", "박민수", "정수진", "최동욱"};
        return authors[(int) (Math.random() * authors.length)];
    }

    private void setupPagination() {
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(totalPages);
        
        pagination.setPageFactory(pageIndex -> {
            applyPageItems(pageIndex);
            return new Region();
        });
        
        // 첫 페이지 데이터 설정
        if (!viewData.isEmpty()) {
            applyPageItems(0);
        }
    }

    private void resetPagingToFirstPage() {
        pagination.setCurrentPageIndex(0);
        applyPageItems(0);
    }

    private void applyPageItems(int pageIndex) {
        int fromIndex = pageIndex * visibleRowCount;
        int toIndex = Math.min(fromIndex + visibleRowCount, viewData.size());
        
        if (fromIndex < viewData.size()) {
            List<MeetingItem> pageData = viewData.subList(fromIndex, toIndex);
            meetingTable.setItems(FXCollections.observableArrayList(pageData));
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = searchComboBox.getValue();
        
        List<MeetingItem> filtered = fullData.stream()
            .filter(item -> {
                if (searchText.isEmpty()) return true;
                
                switch (searchType) {
                    case "제목":
                        return item.getTitle().toLowerCase().contains(searchText);
                    case "부서":
                        return item.getDepartment().toLowerCase().contains(searchText);
                    case "작성자":
                        return item.getAuthor().toLowerCase().contains(searchText);
                    default: // "전체"
                        return item.getTitle().toLowerCase().contains(searchText) ||
                               item.getDepartment().toLowerCase().contains(searchText) ||
                               item.getAuthor().toLowerCase().contains(searchText);
                }
            })
            .collect(Collectors.toList());
        
        viewData.clear();
        viewData.addAll(filtered);
        
        // 페이지네이션 재설정
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(0);
        
        if (!viewData.isEmpty()) {
            applyPageItems(0);
        } else {
            meetingTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    public void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText(null);
        alert.setContentText("선택된 회의를 삭제하시겠습니까?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 삭제 로직 구현
                System.out.println("회의 삭제됨");
            }
        });
    }

    private void showMeetingDetail(MeetingItem item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("회의 상세보기");
        alert.setHeaderText(item.getTitle());
        alert.setContentText("부서: " + item.getDepartment() + "\n" +
                           "작성자: " + item.getAuthor() + "\n" +
                           "일자: " + item.getDate());
        alert.showAndWait();
    }

    // MeetingItem 내부 클래스
    public static class MeetingItem {
        private final javafx.beans.property.StringProperty title;
        private final javafx.beans.property.StringProperty department;
        private final javafx.beans.property.StringProperty author;
        private final javafx.beans.property.StringProperty date;

        public MeetingItem(String title, String department, String author, String date) {
            this.title = new javafx.beans.property.SimpleStringProperty(title);
            this.department = new javafx.beans.property.SimpleStringProperty(department);
            this.author = new javafx.beans.property.SimpleStringProperty(author);
            this.date = new javafx.beans.property.SimpleStringProperty(date);
        }

        public String getTitle() { return title.get(); }
        public void setTitle(String title) { this.title.set(title); }
        public javafx.beans.property.StringProperty titleProperty() { return title; }

        public String getDepartment() { return department.get(); }
        public void setDepartment(String department) { this.department.set(department); }
        public javafx.beans.property.StringProperty departmentProperty() { return department; }

        public String getAuthor() { return author.get(); }
        public void setAuthor(String author) { this.author.set(author); }
        public javafx.beans.property.StringProperty authorProperty() { return author; }

        public String getDate() { return date.get(); }
        public void setDate(String date) { this.date.set(date); }
        public javafx.beans.property.StringProperty dateProperty() { return date; }
    }
} 