# 휴가 API 사용 예시

## 개요

휴가 관련 API를 사용하는 다양한 예시를 제공합니다.

## 휴가 유형 (LeaveType)

- `ANNUAL` - 연차
- `HALF_DAY` - 반차
- `SICK` - 병가
- `PERSONAL` - 개인사유
- `MATERNITY` - 출산휴가
- `PATERNITY` - 육아휴가
- `SPECIAL` - 특별휴가

## 휴가 상태 (LeaveStatus)

- `PENDING` - 대기중
- `APPROVED` - 승인됨
- `REJECTED` - 거부됨
- `CANCELLED` - 취소됨

## 기본 사용법

### 1. 휴가 신청

```java
import com.example.companycore.model.dto.LeaveRequestDto;
import com.example.companycore.service.ApiClient;
import java.time.LocalDate;

ApiClient apiClient = ApiClient.getInstance();

// 휴가 신청 생성
LeaveRequestDto leaveRequest = new LeaveRequestDto(
    1L,                    // userId
    "ANNUAL",              // leaveType
    LocalDate.now(),       // startDate
    LocalDate.now().plusDays(2), // endDate
    "가족 여행"             // reason
);

LeaveRequestDto createdLeave = apiClient.createLeaveRequest(leaveRequest);
if (createdLeave != null) {
    System.out.println("휴가 신청 성공! ID: " + createdLeave.getLeaveId());
}
```

### 2. 휴가 신청 조회

```java
// 모든 휴가 신청 조회
List<LeaveRequestDto> allLeaves = apiClient.getAllLeaveRequests();

// 특정 휴가 신청 조회
LeaveRequestDto leave = apiClient.getLeaveRequestById(1L);

// 사용자별 휴가 신청 조회
List<LeaveRequestDto> userLeaves = apiClient.getLeaveRequestsByUserId(1L);
```

### 3. 휴가 승인/반려

```java
// 휴가 승인 (관리자)
boolean approved = apiClient.approveLeaveRequest(1L, 2L); // leaveId, approvedBy

// 휴가 반려 (관리자)
boolean rejected = apiClient.rejectLeaveRequest(1L, 2L, "업무 일정상 해당 기간 휴가 불가");

// 휴가 취소 (본인만)
boolean cancelled = apiClient.cancelLeaveRequest(1L, 1L); // leaveId, userId
```

### 4. 휴가 신청 수정

```java
// 기존 휴가 신청 수정 (PENDING 상태만 가능)
LeaveRequestDto updateRequest = new LeaveRequestDto(
    1L,                    // userId
    "SICK",                // leaveType (병가로 변경)
    LocalDate.now().plusDays(1), // startDate
    LocalDate.now().plusDays(1), // endDate
    "몸살감기로 인한 병가"   // reason
);

boolean updated = apiClient.updateLeaveRequest(1L, updateRequest);
```

### 5. 휴가 검색 및 필터링

```java
// 대기중인 연차만 조회
List<LeaveRequestDto> pendingAnnual = apiClient.searchLeaveRequests(
    "PENDING",     // status
    "ANNUAL",      // type
    1L,            // userId
    null,          // startDate
    null           // endDate
);

// 특정 기간의 휴가 조회
List<LeaveRequestDto> periodLeaves = apiClient.searchLeaveRequests(
    null,          // status
    null,          // type
    null,          // userId
    "2025-08-01", // startDate
    "2025-08-31"  // endDate
);
```

## 개별 클라이언트 사용법

```java
import com.example.companycore.service.LeaveApiClient;

LeaveApiClient leaveClient = LeaveApiClient.getInstance();

// 휴가 신청
LeaveRequestDto newLeave = new LeaveRequestDto(1L, "HALF_DAY", LocalDate.now(), LocalDate.now(), "오후 반차");
LeaveRequestDto created = leaveClient.createLeaveRequest(newLeave);

// 승인된 휴가만 조회
List<LeaveRequestDto> approvedLeaves = leaveClient.searchLeaveRequests("APPROVED", null, null, null, null);
```

## 실제 사용 시나리오

### 시나리오 1: 직원 휴가 신청

```java
public class LeaveApplicationService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public boolean applyForLeave(Long userId, String leaveType, LocalDate startDate, 
                                LocalDate endDate, String reason) {
        try {
            LeaveRequestDto leaveRequest = new LeaveRequestDto(userId, leaveType, startDate, endDate, reason);
            LeaveRequestDto created = apiClient.createLeaveRequest(leaveRequest);
            return created != null;
        } catch (Exception e) {
            System.err.println("휴가 신청 실패: " + e.getMessage());
            return false;
        }
    }
}
```

### 시나리오 2: 관리자 휴가 승인/반려

```java
public class LeaveApprovalService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public boolean approveLeave(Long leaveId, Long managerId) {
        return apiClient.approveLeaveRequest(leaveId, managerId);
    }
    
    public boolean rejectLeave(Long leaveId, Long managerId, String reason) {
        return apiClient.rejectLeaveRequest(leaveId, managerId, reason);
    }
    
    public List<LeaveRequestDto> getPendingLeaves() {
        return apiClient.searchLeaveRequests("PENDING", null, null, null, null);
    }
}
```

### 시나리오 3: 휴가 통계 및 조회

```java
public class LeaveStatisticsService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public List<LeaveRequestDto> getUserLeaveHistory(Long userId) {
        return apiClient.getLeaveRequestsByUserId(userId);
    }
    
    public List<LeaveRequestDto> getApprovedLeavesByType(String leaveType) {
        return apiClient.searchLeaveRequests("APPROVED", leaveType, null, null, null);
    }
    
    public List<LeaveRequestDto> getLeavesByPeriod(LocalDate startDate, LocalDate endDate) {
        return apiClient.searchLeaveRequests(null, null, null, 
                                          startDate.toString(), endDate.toString());
    }
}
```

## 오류 처리

```java
public class LeaveErrorHandling {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public LeaveRequestDto safeCreateLeave(LeaveRequestDto leaveRequest) {
        try {
            return apiClient.createLeaveRequest(leaveRequest);
        } catch (Exception e) {
            System.err.println("휴가 신청 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
    
    public boolean safeApproveLeave(Long leaveId, Long managerId) {
        try {
            return apiClient.approveLeaveRequest(leaveId, managerId);
        } catch (Exception e) {
            System.err.println("휴가 승인 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
}
```

## 주의사항

1. **상태 제한**: 휴가 수정은 `PENDING` 상태에서만 가능합니다.
2. **권한 확인**: 휴가 취소는 본인만 가능합니다.
3. **날짜 형식**: 날짜는 `YYYY-MM-DD` 형식으로 전송됩니다.
4. **토큰 인증**: 모든 API 호출에는 유효한 인증 토큰이 필요합니다.
5. **예외 처리**: 네트워크 오류나 서버 오류에 대한 적절한 예외 처리가 필요합니다. 