# API 클라이언트 분리 가이드

## 개요

기존의 단일 `ApiClient.java` 파일을 기능별로 분리하여 더 체계적이고 유지보수하기 쉬운 구조로 개선했습니다.

## 새로운 구조

### 1. BaseApiClient (기본 클래스)
- 모든 API 클라이언트의 공통 기능 제공
- HTTP 클라이언트, ObjectMapper 설정
- 인증 토큰 관리
- JWT 토큰 분석
- 로그인/인증 메서드

### 2. UserApiClient (사용자 관련)
- 사용자 조회, 생성, 수정
- 공지사항 조회
- 비밀번호 변경
- 현재 사용자 정보 조회

### 3. TaskApiClient (작업 관련)
- 작업 목록 조회
- 작업 생성, 수정, 삭제
- 작업 검색 및 필터링
- 사용자별 작업 조회

### 4. AttendanceApiClient (출근 관련)
- 출근/퇴근 체크
- 출근 기록 조회
- 출근 통계
- 출근 기록 관리

### 5. LeaveApiClient (휴가 관련)
- 휴가 신청, 조회
- 휴가 승인/반려
- 휴가 수정/취소
- 휴가 검색 및 필터링

### 6. MessageApiClient (메시지 관련)
- 메시지 전송, 조회
- 메시지 상태 변경 (읽음/삭제)
- 메시지 답장
- 대화 내역 조회
- 메시지 대시보드

### 7. ApiClient (통합 관리자)
- 각 API 클라이언트들을 통합 관리
- 기존 코드와의 호환성 유지
- 토큰 공유 및 동기화

## 사용법

### 기본 사용법 (기존과 동일)

```java
// 기존 방식 그대로 사용 가능
ApiClient apiClient = ApiClient.getInstance();

// 로그인
boolean success = apiClient.authenticate("emp001", "password123");

// 사용자 정보 조회
User currentUser = apiClient.getCurrentUser();

// 작업 목록 조회
List<Task> tasks = apiClient.getTasks();

// 출근 체크
boolean checkedIn = apiClient.checkIn(1L);

// 휴가 신청
LeaveRequestDto leaveRequest = new LeaveRequestDto(1L, "ANNUAL", LocalDate.now(), LocalDate.now().plusDays(2), "가족 여행");
LeaveRequestDto createdLeave = apiClient.createLeaveRequest(leaveRequest);

// 메시지 전송
MessageDto message = new MessageDto(2L, "회의 일정 안내", "내일 오후 2시에 회의실 A에서 팀 미팅이 있습니다.", "MESSAGE");
MessageDto sentMessage = apiClient.sendMessage(message, 1L);
```

### 개별 API 클라이언트 직접 사용

```java
// 사용자 관련 API만 사용
UserApiClient userClient = UserApiClient.getInstance();
List<User> users = userClient.getUsers();
User currentUser = userClient.getCurrentUser();

// 작업 관련 API만 사용
TaskApiClient taskClient = TaskApiClient.getInstance();
List<Task> tasks = taskClient.getTasks();
Task newTask = taskClient.createTask(task);

// 출근 관련 API만 사용
AttendanceApiClient attendanceClient = AttendanceApiClient.getInstance();
boolean checkedIn = attendanceClient.checkIn(1L);
List<Attendance> records = attendanceClient.getUserAttendance(1L);

// 휴가 관련 API만 사용
LeaveApiClient leaveClient = LeaveApiClient.getInstance();
List<LeaveRequestDto> leaveRequests = leaveClient.getAllLeaveRequests();
LeaveRequestDto newLeave = leaveClient.createLeaveRequest(leaveRequest);

// 메시지 관련 API만 사용
MessageApiClient messageClient = MessageApiClient.getInstance();
List<MessageDto> messages = messageClient.getReceivedMessages(1L, null, null, false);
MessageDto newMessage = messageClient.sendMessage(message, 1L);
```

### 토큰 공유

```java
// 한 클라이언트에서 로그인하면 모든 클라이언트에 토큰 공유
UserApiClient userClient = UserApiClient.getInstance();
boolean success = userClient.authenticate("emp001", "password123");

// 다른 클라이언트들도 같은 토큰 사용 가능
TaskApiClient taskClient = TaskApiClient.getInstance();
AttendanceApiClient attendanceClient = AttendanceApiClient.getInstance();
LeaveApiClient leaveClient = LeaveApiClient.getInstance();
MessageApiClient messageClient = MessageApiClient.getInstance();

// 토큰이 자동으로 공유됨
List<Task> tasks = taskClient.getTasks(); // 인증됨
List<Attendance> records = attendanceClient.getUserAttendance(1L); // 인증됨
List<LeaveRequestDto> leaves = leaveClient.getAllLeaveRequests(); // 인증됨
List<MessageDto> messages = messageClient.getReceivedMessages(1L, null, null, false); // 인증됨
```

## 장점

### 1. 코드 분리
- 각 기능별로 독립적인 클래스
- 파일 크기 감소
- 가독성 향상

### 2. 유지보수성
- 특정 기능만 수정 가능
- 버그 추적 용이
- 테스트 작성 편리

### 3. 확장성
- 새로운 API 기능 추가 시 해당 클라이언트만 수정
- 기존 코드 영향 최소화

### 4. 재사용성
- 필요한 기능만 선택적으로 사용 가능
- 다른 프로젝트에서도 개별 클라이언트 재사용 가능

## 마이그레이션 가이드

### 기존 코드에서 변경할 점

**변경 전:**
```java
ApiClient apiClient = ApiClient.getInstance();
// 모든 메서드가 하나의 클래스에 있었음
```

**변경 후:**
```java
// 기존 코드는 그대로 작동
ApiClient apiClient = ApiClient.getInstance();

// 또는 개별 클라이언트 사용
UserApiClient userClient = UserApiClient.getInstance();
TaskApiClient taskClient = TaskApiClient.getInstance();
AttendanceApiClient attendanceClient = AttendanceApiClient.getInstance();
LeaveApiClient leaveClient = LeaveApiClient.getInstance();
MessageApiClient messageClient = MessageApiClient.getInstance();
```

### 메서드 시그니처
- 모든 메서드 시그니처는 동일하게 유지
- 기존 코드 수정 불필요
- 점진적 마이그레이션 가능

## 파일 구조

```
src/main/java/com/example/companycore/service/
├── BaseApiClient.java          # 기본 클래스
├── UserApiClient.java          # 사용자 관련 API
├── TaskApiClient.java          # 작업 관련 API
├── AttendanceApiClient.java    # 출근 관련 API
├── LeaveApiClient.java         # 휴가 관련 API
├── MessageApiClient.java       # 메시지 관련 API
└── ApiClient.java             # 통합 관리자 (기존 호환성)
```

## 주의사항

1. **토큰 동기화**: 한 클라이언트에서 로그인하면 다른 클라이언트들도 자동으로 토큰을 공유받습니다.

2. **싱글톤 패턴**: 모든 클라이언트는 싱글톤 패턴을 사용하여 인스턴스를 관리합니다.

3. **예외 처리**: 각 클라이언트는 독립적으로 예외를 처리합니다.

4. **로깅**: 디버그 로그는 각 클라이언트에서 개별적으로 출력됩니다.

## 향후 개선 계획

1. **로깅 프레임워크 도입**: System.out.println 대신 SLF4J + Logback 사용
2. **설정 파일 분리**: API URL, 타임아웃 등을 설정 파일로 관리
3. **캐싱 기능**: 자주 사용되는 데이터에 대한 캐싱 구현
4. **재시도 로직**: 네트워크 오류 시 자동 재시도 기능
5. **메트릭 수집**: API 호출 성능 모니터링 