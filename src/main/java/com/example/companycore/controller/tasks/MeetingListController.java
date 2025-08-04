package com.example.companycore.controller.tasks;

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
import com.example.companycore.model.dto.MeetingItem;
import java.util.stream.Collectors;
import com.example.companycore.service.MeetingApiClient;

/**
 * 회의 목록을 관리하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 회의 목록 테이블 표시
 * - 검색 및 필터링
 * - 페이지네이션
 * - 회의 상세보기
 * - 회의 등록 폼 호출
 * 
 * @author Company Core Team
 * @version 1.0
 */
public class MeetingListController {

    // ==================== FXML UI 컴포넌트 ====================
    
    /** 회의 목록 테이블 */
    @FXML private TableView<MeetingItem> meetingTable;
    
    /** 테이블 컬럼들 */
    @FXML private TableColumn<MeetingItem, String> colNumber;      // 번호 컬럼
    @FXML private TableColumn<MeetingItem, String> colTitle;       // 제목 컬럼
    @FXML private TableColumn<MeetingItem, String> colDepartment;  // 부서 컬럼
    @FXML private TableColumn<MeetingItem, String> colAuthor;      // 작성자 컬럼
    @FXML private TableColumn<MeetingItem, String> colDate;        // 날짜 컬럼
    @FXML private TableColumn<MeetingItem, String> colAction;      // 액션 컬럼 (상세보기 버튼)

    /** 검색 관련 UI 컴포넌트 */
    @FXML private TextField searchField;        // 검색어 입력 필드
    @FXML private ComboBox<String> searchComboBox; // 검색 조건 선택 콤보박스
    @FXML private Pagination pagination;        // 페이지네이션 UI

    // ==================== 데이터 관리 ====================
    
    /** 전체 데이터 저장소 (검색 시 원본 데이터 유지) */
    private final ObservableList<MeetingItem> fullData = FXCollections.observableArrayList();
    
    /** 현재 뷰에 보여지는 데이터 (검색 결과 또는 전체 데이터) */
    private ObservableList<MeetingItem> viewData = FXCollections.observableArrayList();

    /** 한 페이지에 보여질 데이터 수 */
    private int visibleRowCount = 10;
    
    /** 테스트 모드 플래그 (더미 데이터 사용 여부) */
    private static final boolean TEST_MODE = false;

    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 테이블 설정, 데이터 로드, 이벤트 핸들러 등록을 수행
     */
    @FXML
    public void initialize() {
        // 테이블 기본 설정
        setupTable();
        
        // 검색 기능 초기화
        setupSearch();
        
        // 데이터베이스에서 회의 데이터 로드
        loadMeetingsFromDatabase();
        
        // 페이지네이션 설정
        setupPagination();
    }

    /**
     * 테이블의 기본 설정을 구성
     * - 컬럼 바인딩
     * - 행 높이 설정
     * - 동적 행 수 계산
     * - 상세보기 버튼 설정
     */
    private void setupTable() {
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

        // 컬럼 바인딩 설정
        setupColumnBindings();
        
        // 상세보기 버튼 설정
        setupActionColumn();
    }

    /**
     * 테이블 컬럼들을 데이터와 바인딩
     */
    private void setupColumnBindings() {
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
    }

    /**
     * 액션 컬럼에 상세보기 버튼을 설정
     */
    private void setupActionColumn() {
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
    }

    /**
     * 검색 기능 초기화
     */
    private void setupSearch() {
        // 검색 콤보박스 초기화
        searchComboBox.getItems().addAll("전체", "제목", "부서", "작성자");
        searchComboBox.setValue(null);
    }

    // ==================== 데이터 관리 메서드 ====================
    
    /**
     * 데이터베이스에서 회의 목록을 로드합니다.
     */
    private void loadMeetingsFromDatabase() {
        // 현재는 빈 데이터로 초기화 (API 연동 예정)
        fullData.clear();
        viewData.clear();
        viewData.addAll(fullData);
        System.out.println("회의 데이터 로드 완료: 0개");
    }

    /**
     * 랜덤 부서명을 반환
     * 
     * @return 랜덤 선택된 부서명
     */
    private String getRandomDepartment() {
        String[] departments = {"개발팀", "인사팀", "마케팅팀", "영업팀", "총무팀"};
        return departments[(int) (Math.random() * departments.length)];
    }

    /**
     * 랜덤 작성자명을 반환
     * 
     * @return 랜덤 선택된 작성자명
     */
    private String getRandomAuthor() {
        String[] authors = {"김철수", "이영희", "박민수", "정수진", "최동욱"};
        return authors[(int) (Math.random() * authors.length)];
    }

    // ==================== 페이지네이션 메서드 ====================
    
    /**
     * 페이지네이션 UI를 구성하고 설정
     */
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

    /**
     * 페이지네이션을 첫 페이지로 리셋
     */
    private void resetPagingToFirstPage() {
        pagination.setCurrentPageIndex(0);
        applyPageItems(0);
    }

    /**
     * 해당 페이지의 데이터만 추려서 테이블에 적용
     * 
     * @param pageIndex 페이지 인덱스 (0부터 시작)
     */
    private void applyPageItems(int pageIndex) {
        int fromIndex = pageIndex * visibleRowCount;
        int toIndex = Math.min(fromIndex + visibleRowCount, viewData.size());

        if (fromIndex < viewData.size()) {
            List<MeetingItem> pageData = viewData.subList(fromIndex, toIndex);
            meetingTable.setItems(FXCollections.observableArrayList(pageData));
        }
    }

    // ==================== 이벤트 핸들러 메서드 ====================
    
    /**
     * 검색 버튼 클릭 시 호출되는 메서드
     * 검색 조건에 따라 데이터를 필터링하고 결과를 표시
     */
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

    /**
     * 삭제 버튼 클릭 시 호출되는 메서드
     * 확인 다이얼로그를 표시하고 삭제 작업을 수행
     */
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

    /**
     * 회의 등록 버튼 클릭 시 호출되는 메서드
     * 회의 등록 폼을 모달 창으로 열기
     * 
     * @param actionEvent 버튼 클릭 이벤트
     */
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

    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 회의 상세보기 팝업을 표시
     * 
     * @param item 상세보기할 회의 아이템
     */
    private void showMeetingDetail(MeetingItem item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("회의 상세보기");
        alert.setHeaderText(item.getTitle());
        alert.setContentText("부서: " + item.getDepartment() + "\n" +
                "작성자: " + item.getAuthor() + "\n" +
                "일자: " + item.getDate());
        alert.showAndWait();
    }

}