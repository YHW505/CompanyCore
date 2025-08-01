package com.example.companycore.controller;

// JavaFX 관련 라이브러리 import
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MeetingListController {

    // FXML에서 정의된 UI 컴포넌트와 연결
    @FXML private TableView<MeetingItem> meetingTable;
    @FXML private TableColumn<MeetingItem, String> colNumber;
    @FXML private TableColumn<MeetingItem, String> colTitle;
    @FXML private TableColumn<MeetingItem, String> colDepartment;
    @FXML private TableColumn<MeetingItem, String> colAuthor;
    @FXML private TableColumn<MeetingItem, String> colDate;
    @FXML private TableColumn<MeetingItem, String> colAction;

    @FXML private TextField searchField; // 검색어 입력 필드
    @FXML private ComboBox<String> searchComboBox; // 검색 조건 선택 콤보박스
    @FXML private Pagination pagination; // 페이지네이션 UI

    // 전체 데이터와 현재 뷰에 보여지는 데이터 분리
    private final ObservableList<MeetingItem> fullData = FXCollections.observableArrayList();
    private ObservableList<MeetingItem> viewData = FXCollections.observableArrayList();

    private int visibleRowCount = 10; // 한 페이지에 보여질 데이터 수
    private static final boolean TEST_MODE = true; // 테스트 모드 (더미 데이터 사용 여부)

    @FXML
    public void initialize() {
        // 테이블 기본 설정
        meetingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // 너비 자동 조정
        meetingTable.setFixedCellSize(40); // 행 높이 고정

        // 테이블의 높이에 따라 한 페이지에 표시할 행 개수 계산
        visibleRowCount = (int) (meetingTable.getHeight() / meetingTable.getFixedCellSize());
        if (visibleRowCount == 0) visibleRowCount = 10;

        // 테이블 높이가 바뀔 때마다 페이지 수 재계산
        meetingTable.heightProperty().addListener((obs, oldVal, newVal) -> {
            int newCount = (int) (newVal.doubleValue() / meetingTable.getFixedCellSize());
            if (newCount != visibleRowCount && newCount > 0) {
                visibleRowCount = newCount;
                resetPagingToFirstPage(); // 페이지 처음으로 리셋
            }
        });

        // 번호 컬럼: 행 번호를 자동 계산
        colNumber.setCellValueFactory(cellData -> {
            MeetingItem item = cellData.getValue();
            int index = meetingTable.getItems().indexOf(item) + 1
                    + pagination.getCurrentPageIndex() * visibleRowCount;
            return new ReadOnlyStringWrapper(String.valueOf(index));
        });

        // 나머지 컬럼은 MeetingItem의 프로퍼티를 바인딩
        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());

        // '상세보기' 버튼 셀 생성
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button detailBtn = new Button("상세보기");

            {
                // 버튼 스타일 지정 및 클릭 이벤트 핸들링
                detailBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 8;");
                detailBtn.setOnAction(e -> {
                    MeetingItem item = getTableView().getItems().get(getIndex());
                    showMeetingDetail(item); // 상세보기 팝업 호출
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // 비어있으면 버튼 없음
                } else {
                    setGraphic(detailBtn); // 버튼 표시
                }
            }
        });

        // 검색 콤보박스 초기화
        searchComboBox.getItems().addAll("전체", "제목", "부서", "작성자");
        searchComboBox.setValue("전체");

        // 테스트용 더미 데이터 삽입
        if (TEST_MODE) {
            fillDummyData(20);
        }

        // 페이지네이션 UI 설정
        setupPagination();
    }

    // 테스트용 더미 데이터 생성
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

    // 부서 랜덤 선택
    private String getRandomDepartment() {
        String[] departments = {"개발팀", "인사팀", "마케팅팀", "영업팀", "총무팀"};
        return departments[(int) (Math.random() * departments.length)];
    }

    // 작성자 랜덤 선택
    private String getRandomAuthor() {
        String[] authors = {"김철수", "이영희", "박민수", "정수진", "최동욱"};
        return authors[(int) (Math.random() * authors.length)];
    }

    // 페이지네이션 구성
    private void setupPagination() {
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(totalPages);

        pagination.setPageFactory(pageIndex -> {
            applyPageItems(pageIndex);
            return new Region(); // 빈 노드 반환
        });

        // 첫 페이지 초기화
        if (!viewData.isEmpty()) {
            applyPageItems(0);
        }
    }

    // 첫 페이지로 리셋
    private void resetPagingToFirstPage() {
        pagination.setCurrentPageIndex(0);
        applyPageItems(0);
    }

    // 해당 페이지 데이터만 추려서 테이블에 적용
    private void applyPageItems(int pageIndex) {
        int fromIndex = pageIndex * visibleRowCount;
        int toIndex = Math.min(fromIndex + visibleRowCount, viewData.size());

        if (fromIndex < viewData.size()) {
            List<MeetingItem> pageData = viewData.subList(fromIndex, toIndex);
            meetingTable.setItems(FXCollections.observableArrayList(pageData));
        }
    }

    // 검색 처리
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = searchComboBox.getValue();

        // 검색 조건에 맞는 데이터 필터링
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
                        default: // 전체
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

    // 삭제 버튼 클릭 시
    @FXML
    public void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText(null);
        alert.setContentText("선택된 회의를 삭제하시겠습니까?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 실제 삭제 로직은 여기에 구현
                System.out.println("회의 삭제됨");
            }
        });
    }

    // 상세보기 버튼 클릭 시 팝업 출력
    private void showMeetingDetail(MeetingItem item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("회의 상세보기");
        alert.setHeaderText(item.getTitle());
        alert.setContentText("부서: " + item.getDepartment() + "\n" +
                "작성자: " + item.getAuthor() + "\n" +
                "일자: " + item.getDate());
        alert.showAndWait();
    }


    @FXML
    public void onRegisterClick(ActionEvent actionEvent) {
        try {
            // FXML 경로 테스트
            var fxmlPath = getClass().getResource("/com/example/companycore/view/content/tasks/meetingform.fxml");
            System.out.println("FXML URL: " + fxmlPath); // null이면 경로 문제

            if (fxmlPath == null) {
                System.err.println("❌ meetingform.fxml 경로를 찾을 수 없습니다.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlPath);
            Parent formRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("회의록 등록");
            stage.setScene(new Scene(formRoot));
            stage.initModality(Modality.APPLICATION_MODAL); // 모달창으로
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 회의 데이터를 표현하는 내부 클래스
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