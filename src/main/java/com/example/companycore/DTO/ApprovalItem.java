package com.example.companycore.DTO;

import javafx.beans.property.SimpleStringProperty;

/**
 * ApprovalItem 클래스
 * 역할: 승인 목록의 한 행(row)을 나타내는 데이터 모델
 *      JavaFX TableView에서 각 컬럼과 바인딩하여 사용함
 * 어떤 상황에서 사용되나?:
 *      서버(스프링)에서 받아온 승인 데이터(JSON)를 JavaFX TableView에 표시할 때 사용
 *      각 승인 항목의 제목, 부서, 작성자, 날짜, 상태, 조치 등을 관리함
 * 자세한 예시:
 *      ApprovalItem item = new ApprovalItem("프로젝트 보고서", "개발팀", "홍길동", "2025-07-31", "대기", "승인/거부");
 *      이 객체를 TableView에 추가해서 화면에 데이터 표시
 * 왜 필요하나?:
 *      JavaFX의 TableView는 데이터 모델과 UI를 바인딩할 때 프로퍼티 형태를 요구
 *      SimpleStringProperty로 감싸면 UI 변경 시 자동 반영이 가능해 편리함
 */
public class ApprovalItem {

    // final 추가해야함
    private final SimpleStringProperty id;  // 식별자용
    // 승인 제목
    private final SimpleStringProperty title;
    // 소속 부서
    private final SimpleStringProperty department;
    // 작성자 이름
    private final SimpleStringProperty author;
    // 승인 요청 날짜 (문자열 형식)
    private final SimpleStringProperty date;
    // 승인 상태 (예: 대기, 승인, 거부)
    private final SimpleStringProperty status;
    // 승인 조치 (예: 승인/거부 버튼 표시 텍스트 등)
    private final SimpleStringProperty action;

    /**
     * 생성자
     * @param title 승인 제목
     * @param department 소속 부서명
     * @param author 작성자 이름
     * @param date 요청 날짜 문자열
     * @param status 승인 상태 문자열
     * @param action 승인 조치 문자열
     */
    public ApprovalItem(String id, String title, String department, String author,
                        String date, String status, String action) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.department = new SimpleStringProperty(department);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
        this.action = new SimpleStringProperty(action);
    }

    // Getter 메서드들: 실제 문자열 값을 반환
    public String getTitle() { return title.get(); }
    public String getDepartment() { return department.get(); }
    public String getAuthor() { return author.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public String getAction() { return action.get(); }
    public String getId() { return id.get(); }

    // JavaFX 프로퍼티 반환 메서드: TableView 바인딩 시 필요
    public SimpleStringProperty titleProperty() { return title; }
    public SimpleStringProperty departmentProperty() { return department; }
    public SimpleStringProperty authorProperty() { return author; }
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty actionProperty() { return action; }

}