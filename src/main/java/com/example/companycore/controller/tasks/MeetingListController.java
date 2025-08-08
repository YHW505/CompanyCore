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
import java.util.Optional;
import com.example.companycore.model.dto.MeetingItem;
import java.util.stream.Collectors;
import com.example.companycore.service.MeetingApiClient;
import com.example.companycore.service.ApiClient;
import com.example.companycore.util.FileUtil;
import java.io.File;

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
    @FXML private TableColumn<MeetingItem, String> colAttachment;  // 첨부파일 컬럼
    @FXML private TableColumn<MeetingItem, String> colAction;      // 액션 컬럼 (상세보기 버튼)

    /** 검색 관련 UI 컴포넌트 */
    @FXML private TextField searchField;        // 검색어 입력 필드
    @FXML private ComboBox<String> searchComboBox; // 검색 조건 선택 콤보박스
    @FXML private ComboBox<String> departmentFilterComboBox; // 부서 필터 콤보박스
    @FXML private Pagination pagination;        // 페이지네이션 UI
    @FXML private ProgressIndicator loadingIndicator; // 로딩 인디케이터

    // ==================== 데이터 관리 ====================
    
    /** 전체 데이터 저장소 (검색 시 원본 데이터 유지) */
    private final ObservableList<MeetingItem> fullData = FXCollections.observableArrayList();
    
    /** 현재 뷰에 보여지는 데이터 (검색 결과 또는 전체 데이터) */
    private ObservableList<MeetingItem> viewData = FXCollections.observableArrayList();

    /** 한 페이지에 보여질 데이터 수 */
    private int visibleRowCount = 10;
    
    /** 테스트 모드 플래그 (더미 데이터 사용 여부) */
    private static final boolean TEST_MODE = false;
    
    /** API 클라이언트 */
    private MeetingApiClient meetingApiClient;

    // ==================== 초기화 메서드 ====================
    
    /**
     * FXML 로드 후 자동 호출되는 초기화 메서드
     * 테이블 설정, 데이터 로드, 이벤트 핸들러 등록을 수행
     */
    @FXML
    public void initialize() {
        // API 클라이언트 초기화
        meetingApiClient = MeetingApiClient.getInstance();
        
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
        meetingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        meetingTable.setFixedCellSize(40); // 행 높이를 40px로 설정
        
        // 헤더 높이를 30px로 고정하고 테이블 높이 설정
        meetingTable.setStyle("-fx-table-header-height: 30px; -fx-scroll-bar-policy: never; -fx-pref-height: 427px; -fx-max-height: 427px; -fx-min-height: 427px; -fx-table-header-background: #f0f0f0;");
        
        // 테이블이 정확히 10개 행을 표시할 수 있도록 설정 (40px * 10행 + 헤더 높이 27px)
        meetingTable.setPrefHeight(427); // 40px * 10행 + 헤더 높이 27px = 427px
        meetingTable.setMaxHeight(427);
        
        // 각 컬럼의 헤더 높이도 고정
        colNumber.setStyle("-fx-table-header-height: 30px;");
        colTitle.setStyle("-fx-table-header-height: 30px;");
        colDepartment.setStyle("-fx-table-header-height: 30px;");
        colAuthor.setStyle("-fx-table-header-height: 30px;");
        colDate.setStyle("-fx-table-header-height: 30px;");
        colAttachment.setStyle("-fx-table-header-height: 30px;");
        colAction.setStyle("-fx-table-header-height: 30px;");
        
        // 고정된 행 수 설정
        visibleRowCount = 10;

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
        colNumber.setStyle("-fx-alignment: center;");

        // 나머지 컬럼은 MeetingItem의 프로퍼티를 바인딩
        colTitle.setCellValueFactory(cd -> cd.getValue().titleProperty());
        colTitle.setStyle("-fx-alignment: center-left;");
        colDepartment.setCellValueFactory(cd -> cd.getValue().departmentProperty());
        colDepartment.setStyle("-fx-alignment: center;");
        colAuthor.setCellValueFactory(cd -> cd.getValue().authorProperty());
        colAuthor.setStyle("-fx-alignment: center;");
        colDate.setCellValueFactory(cd -> cd.getValue().dateProperty());
        colDate.setStyle("-fx-alignment: center;");
        
        // 첨부파일 컬럼: 첨부파일 존재 여부 표시
        colAttachment.setCellValueFactory(cellData -> {
            MeetingItem item = cellData.getValue();
            String attachmentFilename = item.getAttachmentFilename();
            if (attachmentFilename != null && !attachmentFilename.isEmpty()) {
                return new ReadOnlyStringWrapper("📎");
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });
        colAttachment.setStyle("-fx-alignment: center;");
        
        // 첨부파일 컬럼에 툴팁 추가
        colAttachment.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setTooltip(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item);
                    MeetingItem meetingItem = getTableView().getItems().get(getIndex());
                    if (meetingItem != null && meetingItem.getAttachmentFilename() != null) {
                        String tooltipText = String.format("첨부파일: %s\n크기: %s\n타입: %s\n클릭하여 다운로드",
                            meetingItem.getAttachmentFilename(),
                            FileUtil.formatFileSize(meetingItem.getAttachmentSize() != null ? meetingItem.getAttachmentSize() : 0),
                            meetingItem.getAttachmentContentType() != null ? meetingItem.getAttachmentContentType() : "알 수 없음"
                        );
                        setTooltip(new Tooltip(tooltipText));
                        
                        // 클릭 시 다운로드 기능 추가
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) { // 더블클릭
                                downloadAttachment(meetingItem);
                            }
                        });
                        
                        // 마우스 커서 변경
                        setStyle("-fx-cursor: hand; -fx-alignment: center;");
                    }
                }
            }
        });
        
        // 액션 컬럼은 빈 문자열로 설정 (버튼이 표시되므로)
        colAction.setCellValueFactory(cd -> new ReadOnlyStringWrapper(""));
        colAction.setStyle("-fx-alignment: center;");
        colAction.setPrefWidth(120); // 버튼들이 들어갈 공간 확보
    }

         /**
      * 액션 컬럼에 상세보기, 수정 버튼을 설정
      */
     private void setupActionColumn() {
         colAction.setCellFactory(col -> new TableCell<>() {
             private final HBox buttonContainer = new HBox(5);
             private final Button detailBtn = new Button("상세보기");
             private final Button editBtn = new Button("수정");

             {
                 // 버튼 컨테이너를 중앙정렬로 설정
                 buttonContainer.setAlignment(Pos.CENTER);
                 
                 // 버튼 크기 및 스타일 설정
                 detailBtn.setPrefWidth(60);
                 detailBtn.setPrefHeight(25);
                 detailBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 2 4; -fx-font-size: 11px;");
                 
                 editBtn.setPrefWidth(50);
                 editBtn.setPrefHeight(25);
                 editBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 2 4; -fx-font-size: 11px;");
                 
                 detailBtn.setOnAction(e -> {
                     MeetingItem item = getTableView().getItems().get(getIndex());
                     showMeetingDetail(item); // 상세보기 팝업 호출
                 });
                 
                 editBtn.setOnAction(e -> {
                     MeetingItem item = getTableView().getItems().get(getIndex());
                     showMeetingEdit(item); // 수정 팝업 호출
                 });
                 
                 buttonContainer.getChildren().addAll(detailBtn, editBtn);
             }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // 비어있으면 버튼 없음
                } else {
                    setGraphic(buttonContainer); // 버튼들 표시
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
        searchComboBox.setValue("전체");
        
        // 부서 필터 콤보박스 초기화
        departmentFilterComboBox.getItems().addAll("전체", "인사팀", "개발팀", "마케팅팀", "영업팀");
        departmentFilterComboBox.setValue("전체");
    }

    // ==================== 데이터 관리 메서드 ====================
    
    /**
     * 데이터베이스에서 회의 목록을 로드합니다.
     */
    public void loadMeetingsFromDatabase() {
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

    /**
     * 서버에서 실제 데이터를 로드합니다.
     */
    private void loadDataFromServer() {
        // 로딩 인디케이터 표시
        showLoading(true);
        
        // 백그라운드에서 데이터 로드
        javafx.concurrent.Task<Void> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                MeetingApiClient meetingApiClient = MeetingApiClient.getInstance();
                // 간단한 API로 데이터 로드 (성능 최적화)
                List<MeetingApiClient.MeetingDto> meetings = meetingApiClient.getAllMeetingsSimple();
                
                // UI 업데이트는 Platform.runLater에서 수행
                javafx.application.Platform.runLater(() -> {
                    fullData.clear();
                    viewData.clear();
                    
                    if (meetings != null) {
                        for (MeetingApiClient.MeetingDto meetingDto : meetings) {
                            // MeetingDto에서 사용 가능한 정보만 추출
                            String title = meetingDto.getTitle() != null ? meetingDto.getTitle() : "제목 없음";
                            String department = meetingDto.getDepartment() != null ? meetingDto.getDepartment() : "Unknown";
                            String author = meetingDto.getAuthor() != null ? meetingDto.getAuthor() : "Unknown";
                            String date = meetingDto.getStartTime() != null ? 
                                meetingDto.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Unknown";
                            
                            // 첨부파일 정보 추출
                            String attachmentContent = meetingDto.getAttachmentContent() != null ? meetingDto.getAttachmentContent() : "";
                            String attachmentPath = meetingDto.getAttachmentPath() != null ? meetingDto.getAttachmentPath() : "";
                            Long attachmentSize = meetingDto.getAttachmentSize();
                            String attachmentFilename = meetingDto.getAttachmentFilename() != null ? meetingDto.getAttachmentFilename() : "";
                            String attachmentContentType = meetingDto.getAttachmentContentType() != null ? meetingDto.getAttachmentContentType() : "";
                            
                            MeetingItem item = new MeetingItem(
                                meetingDto.getMeetingId(),
                                title,
                                department,
                                author,
                                date,
                                meetingDto.getDescription() != null ? meetingDto.getDescription() : "",
                                meetingDto.getLocation() != null ? meetingDto.getLocation() : "",
                                attachmentContent,
                                attachmentPath,
                                attachmentSize,
                                attachmentFilename,
                                attachmentContentType
                            );
                            fullData.add(item);
                        }
                    }
                    
                    viewData.addAll(fullData);
                    System.out.println("서버에서 회의 데이터 로드 완료: " + fullData.size() + "개");
                    updatePagination();
                    showLoading(false);
                });
                
                return null;
            }
        };
        
        // 에러 처리
        loadTask.setOnFailed(event -> {
            System.err.println("회의 데이터 로드 실패: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
            fullData.clear();
            viewData.clear();
            updatePagination();
            showLoading(false);
        });
        
        // 백그라운드 스레드에서 실행
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    /**
     * 로딩 인디케이터 표시/숨김
     */
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
            loadingIndicator.setManaged(show);
            meetingTable.setVisible(!show);
            meetingTable.setManaged(!show);
        }
    }



    // ==================== 페이지네이션 메서드 ====================
    
    /**
     * 페이지네이션 UI를 구성하고 설정
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
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        pagination.setPageCount(Math.max(1, totalPages));
        
        // 현재 페이지가 총 페이지 수를 초과하면 마지막 페이지로 설정
        if (pagination.getCurrentPageIndex() >= totalPages) {
            pagination.setCurrentPageIndex(Math.max(0, totalPages - 1));
        }
        
        // 첫 페이지 데이터 로드
        if (viewData.size() > 0) {
            int startIndex = 0;
            int endIndex = Math.min(visibleRowCount, viewData.size());
            List<MeetingItem> pageData = viewData.subList(startIndex, endIndex);
            meetingTable.setItems(FXCollections.observableArrayList(pageData));
            System.out.println("페이지네이션 업데이트: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        } else {
            meetingTable.setItems(FXCollections.observableArrayList());
            System.out.println("페이지네이션 업데이트: 빈 목록");
        }
    }
    
    /**
     * 페이지 생성
     */
    private Region createPage(int pageIndex) {
        // 페이지 인덱스가 유효한지 확인
        int totalPages = (int) Math.ceil((double) viewData.size() / visibleRowCount);
        if (pageIndex >= totalPages) {
            return new Region();
        }
        
        // 현재 페이지의 데이터 계산
        int startIndex = pageIndex * visibleRowCount;
        int endIndex = Math.min(startIndex + visibleRowCount, viewData.size());
        
        // 현재 페이지의 데이터만 테이블에 설정
        List<MeetingItem> pageData = viewData.subList(startIndex, endIndex);
        meetingTable.setItems(FXCollections.observableArrayList(pageData));
        
        System.out.println("페이지 " + (pageIndex + 1) + " 로드: " + pageData.size() + "개 항목 (전체: " + viewData.size() + "개)");
        
        return new Region();
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

        // 페이지네이션 업데이트 및 첫 페이지로 리셋
        updatePagination();
        pagination.setCurrentPageIndex(0);
    }

    /**
     * 삭제 버튼 클릭 시 호출되는 메서드
     * 확인 다이얼로그를 표시하고 삭제 작업을 수행
     */
    @FXML
    public void handleDelete() {
        MeetingItem selected = meetingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "삭제할 회의를 선택하세요.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("회의 삭제");
        alert.setHeaderText("회의 삭제 확인");
        alert.setContentText("정말로 '" + selected.getTitle() + "' 회의를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 서버에서 회의 삭제
                boolean success = meetingApiClient.deleteMeeting(selected.getId());
                
                if (success) {
                    // 성공 시 목록에서 제거
                    fullData.remove(selected);
                    viewData.remove(selected);
                    
                    // 성공 메시지 표시
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("삭제 완료");
                    successAlert.setHeaderText("회의 삭제 완료");
                    successAlert.setContentText("회의가 성공적으로 삭제되었습니다.");
                    successAlert.showAndWait();
                    
                    // 테이블 새로고침
                    updatePagination();
                } else {
                    // 실패 메시지 표시
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("삭제 실패");
                    errorAlert.setHeaderText("회의 삭제 실패");
                    errorAlert.setContentText("회의 삭제 중 오류가 발생했습니다.");
                    errorAlert.showAndWait();
                }
            } catch (Exception e) {
                System.err.println("회의 삭제 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("삭제 실패");
                errorAlert.setHeaderText("회의 삭제 실패");
                errorAlert.setContentText("회의 삭제 중 오류가 발생했습니다: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
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

            // 컨트롤러 가져오기
            MeetingFormController formController = loader.getController();
            formController.setParentController(this);

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/meetingDetailDialog.fxml"));
            Parent root = loader.load();

            MeetingDetailController controller = loader.getController();
            controller.setMeetingItem(item);

            Stage stage = new Stage();
            stage.setTitle("회의 상세보기");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("오류");
            alert.setHeaderText(null);
            alert.setContentText("상세보기 창을 열 수 없습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 회의 삭제 기능
     */
    private void deleteMeeting(MeetingItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("회의 삭제");
        alert.setHeaderText("회의 삭제 확인");
        alert.setContentText("정말로 '" + item.getTitle() + "' 회의를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 서버에서 회의 삭제
                boolean success = meetingApiClient.deleteMeeting(item.getId());
                
                if (success) {
                    // 성공 시 목록에서 제거
                    fullData.remove(item);
                    viewData.remove(item);
                    
                    // 성공 메시지 표시
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("삭제 완료");
                    successAlert.setHeaderText("회의 삭제 완료");
                    successAlert.setContentText("회의가 성공적으로 삭제되었습니다.");
                    successAlert.showAndWait();
                    
                    // 테이블 새로고침
                    updatePagination();
                } else {
                    // 실패 메시지 표시
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("삭제 실패");
                    errorAlert.setHeaderText("회의 삭제 실패");
                    errorAlert.setContentText("회의 삭제 중 오류가 발생했습니다.");
                    errorAlert.showAndWait();
                }
            } catch (Exception e) {
                System.err.println("회의 삭제 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("삭제 실패");
                errorAlert.setHeaderText("회의 삭제 실패");
                errorAlert.setContentText("회의 삭제 중 오류가 발생했습니다: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * 회의 수정 팝업을 표시
     * 
     * @param item 수정할 회의 아이템
     */
    private void showMeetingEdit(MeetingItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/content/tasks/meetingEdit.fxml"));
            Parent root = loader.load();

            MeetingEditController editController = loader.getController();
            editController.setParentController(this);
            editController.setMeetingData(item); // 수정할 회의 아이템 설정

            Stage stage = new Stage();
            stage.setTitle("회의록 수정");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("오류");
            alert.setHeaderText(null);
            alert.setContentText("수정 창을 열 수 없습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 첨부파일을 다운로드합니다.
     * 
     * @param item 다운로드할 회의 아이템
     */
    private void downloadAttachment(MeetingItem item) {
        if (item.getAttachmentContent() == null || item.getAttachmentFilename() == null) {
            new Alert(Alert.AlertType.WARNING, "다운로드할 첨부파일이 없습니다.").showAndWait();
            return;
        }

        try {
            javafx.stage.DirectoryChooser directoryChooser = new javafx.stage.DirectoryChooser();
            directoryChooser.setTitle("다운로드 위치 선택");
            File selectedDirectory = directoryChooser.showDialog(getStage());

            if (selectedDirectory != null) {
                String outputPath = selectedDirectory.getAbsolutePath() + File.separator + item.getAttachmentFilename();
                FileUtil.saveBase64ToFile(item.getAttachmentContent(), outputPath);
                
                new Alert(Alert.AlertType.INFORMATION, 
                    "첨부파일이 성공적으로 다운로드되었습니다.\n위치: " + outputPath).showAndWait();
            }
        } catch (Exception e) {
            System.err.println("첨부파일 다운로드 실패: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("오류");
            alert.setHeaderText(null);
            alert.setContentText("첨부파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 현재 Stage를 가져옵니다.
     * 
     * @return 현재 Stage
     */
    private Stage getStage() {
        return (Stage) meetingTable.getScene().getWindow();
    }
}