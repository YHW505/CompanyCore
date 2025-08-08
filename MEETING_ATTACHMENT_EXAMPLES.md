# 회의록 첨부파일 기능 사용 예제

## 개요
회의록 작성 시 첨부파일을 추가할 수 있는 기능이 구현되었습니다. 이 기능을 통해 회의 자료, 발표 자료, 회의록 등을 첨부할 수 있습니다.

## 주요 기능

### 1. 첨부파일 지원 형식
- **문서**: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT
- **이미지**: JPG, JPEG, PNG, GIF
- **압축파일**: ZIP, RAR
- **기타**: 모든 파일 형식 지원 (기본 MIME 타입: application/octet-stream)

### 2. 파일 크기 제한
- 기본 최대 크기: 10MB
- 설정 가능한 크기 제한

### 3. 보안 기능
- 파일명 유효성 검사
- 위험한 문자 필터링
- 파일 크기 검증

## 클라이언트 측 사용 예제

### 1. 회의록 생성 시 첨부파일 추가

```java
import com.example.companycore.service.MeetingApiClient;
import com.example.companycore.util.FileUtil;
import java.io.File;
import java.time.LocalDateTime;

public class MeetingAttachmentExample {
    
    public static void createMeetingWithAttachment() {
        try {
            MeetingApiClient client = MeetingApiClient.getInstance();
            
            // 첨부할 파일
            File attachmentFile = new File("meeting_materials.pdf");
            
            // 파일을 Base64로 인코딩
            String attachmentContent = FileUtil.encodeFileToBase64(attachmentFile);
            
            // MIME 타입 자동 감지
            String contentType = FileUtil.getMimeType(attachmentFile.getName());
            
            // 회의록 생성
            MeetingApiClient.MeetingDto meeting = client.createMeetingWithAttachment(
                "2024년 1분기 실적 회의",                    // 제목
                "1분기 실적 검토 및 2분기 계획 수립",         // 설명
                LocalDateTime.now().plusDays(1),             // 시작 시간
                LocalDateTime.now().plusDays(1).plusHours(2), // 종료 시간
                "대회의실",                                   // 회의실
                "경영기획팀",                                 // 부서
                "김철수",                                     // 작성자
                attachmentFile.getName(),                     // 파일명
                contentType,                                  // MIME 타입
                attachmentFile.length(),                      // 파일 크기
                attachmentContent                             // Base64 인코딩된 내용
            );
            
            System.out.println("회의록이 성공적으로 생성되었습니다: " + meeting.getMeetingId());
            
        } catch (Exception e) {
            System.err.println("회의록 생성 실패: " + e.getMessage());
        }
    }
}
```

### 2. 첨부파일 다운로드

```java
public static void downloadMeetingAttachment(Long meetingId) {
    try {
        MeetingApiClient client = MeetingApiClient.getInstance();
        
        // 첨부파일 정보 다운로드
        MeetingApiClient.MeetingDto meeting = client.downloadMeetingAttachment(meetingId);
        
        if (meeting != null && meeting.getAttachmentContent() != null) {
            // Base64 문자열을 파일로 저장
            String outputPath = "downloaded_" + meeting.getAttachmentFilename();
            FileUtil.saveBase64ToFile(meeting.getAttachmentContent(), outputPath);
            
            System.out.println("첨부파일이 다운로드되었습니다: " + outputPath);
        } else {
            System.out.println("첨부파일이 없습니다.");
        }
        
    } catch (Exception e) {
        System.err.println("첨부파일 다운로드 실패: " + e.getMessage());
    }
}
```

### 3. 기존 회의록에 첨부파일 추가

```java
public static void addAttachmentToExistingMeeting(Long meetingId, File attachmentFile) {
    try {
        MeetingApiClient client = MeetingApiClient.getInstance();
        
        // 파일을 Base64로 인코딩
        String attachmentContent = FileUtil.encodeFileToBase64(attachmentFile);
        String contentType = FileUtil.getMimeType(attachmentFile.getName());
        
        // 첨부파일 업로드
        boolean success = client.uploadMeetingAttachment(
            meetingId,
            attachmentFile.getName(),
            contentType,
            attachmentFile.length(),
            attachmentContent
        );
        
        if (success) {
            System.out.println("첨부파일이 성공적으로 추가되었습니다.");
        } else {
            System.out.println("첨부파일 추가에 실패했습니다.");
        }
        
    } catch (Exception e) {
        System.err.println("첨부파일 추가 실패: " + e.getMessage());
    }
}
```

### 4. 파일 유효성 검사

```java
public static boolean validateAttachmentFile(File file) {
    // 파일명 유효성 검사
    if (!FileUtil.isValidFilename(file.getName())) {
        System.err.println("유효하지 않은 파일명입니다.");
        return false;
    }
    
    // 파일 크기 검사 (10MB 제한)
    if (!FileUtil.isFileSizeValid(file, 10)) {
        System.err.println("파일 크기가 너무 큽니다. (최대 10MB)");
        return false;
    }
    
    // 파일 존재 여부 검사
    if (!file.exists()) {
        System.err.println("파일이 존재하지 않습니다.");
        return false;
    }
    
    System.out.println("파일 검증이 완료되었습니다.");
    System.out.println("파일명: " + file.getName());
    System.out.println("파일 크기: " + FileUtil.formatFileSize(file.length()));
    System.out.println("MIME 타입: " + FileUtil.getMimeType(file.getName()));
    
    return true;
}
```

## 서버 측 API 엔드포인트

### 1. 회의록 생성 (첨부파일 포함)
```
POST /api/meetings
Content-Type: application/json

{
  "title": "회의 제목",
  "description": "회의 설명",
  "startTime": "2024-01-15T10:00:00",
  "endTime": "2024-01-15T12:00:00",
  "location": "회의실",
  "department": "부서명",
  "author": "작성자",
  "attachmentFilename": "meeting_materials.pdf",
  "attachmentContentType": "application/pdf",
  "attachmentSize": 1048576,
  "attachmentContent": "Base64인코딩된파일내용..."
}
```

### 2. 첨부파일 다운로드
```
GET /api/meetings/{id}/attachment
```

### 3. 첨부파일 업로드
```
PUT /api/meetings/{id}/attachment
Content-Type: application/json

{
  "attachmentFilename": "new_attachment.pdf",
  "attachmentContentType": "application/pdf",
  "attachmentSize": 1048576,
  "attachmentContent": "Base64인코딩된파일내용..."
}
```

### 4. 첨부파일 삭제
```
DELETE /api/meetings/{id}/attachment
```

## 주의사항

1. **파일 크기 제한**: 기본적으로 10MB 이하의 파일만 업로드 가능합니다.
2. **파일명 제한**: 위험한 문자(`<>:"/\|?*`)가 포함된 파일명은 사용할 수 없습니다.
3. **Base64 인코딩**: 모든 파일은 Base64로 인코딩되어 전송됩니다.
4. **메모리 사용량**: 큰 파일의 경우 메모리 사용량에 주의하세요.

## 에러 처리

```java
try {
    // 첨부파일 관련 작업
} catch (IOException e) {
    System.err.println("파일 처리 오류: " + e.getMessage());
} catch (IllegalArgumentException e) {
    System.err.println("잘못된 파라미터: " + e.getMessage());
} catch (Exception e) {
    System.err.println("예상치 못한 오류: " + e.getMessage());
}
```

## 성능 최적화 팁

1. **대용량 파일**: 큰 파일의 경우 청크 단위로 업로드하는 것을 고려하세요.
2. **캐싱**: 자주 다운로드하는 파일은 로컬에 캐시하세요.
3. **비동기 처리**: 파일 업로드/다운로드는 비동기로 처리하는 것이 좋습니다.
4. **압축**: 가능한 경우 파일을 압축하여 전송하세요. 