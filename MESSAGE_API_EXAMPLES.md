# 메시지 API 사용 예시

## 개요

메시지 관련 API를 사용하는 다양한 예시를 제공합니다.

## 메시지 유형 (MessageType)

- `MESSAGE` - 일반 메시지
- `EMAIL` - 이메일
- `NOTICE` - 공지사항

## 조회 타입 (Query Parameter용)

- `RECEIVED` - 받은메시지
- `SENT` - 보낸메시지

## 읽음 상태 (Query Parameter용)

- `READ` - 읽음
- `UNREAD` - 안읽음

## 기본 사용법

### 1. 메시지 전송

```java
import com.example.companycore.model.dto.MessageDto;
import com.example.companycore.service.ApiClient;

ApiClient apiClient = ApiClient.getInstance();

// 일반 메시지 전송
MessageDto message = new MessageDto(
    2L,                                    // receiverId
    "회의 일정 안내",                        // title
    "내일 오후 2시에 회의실 A에서 팀 미팅이 있습니다.", // content
    "MESSAGE"                              // messageType
);

MessageDto sentMessage = apiClient.sendMessage(message, 1L); // senderId
if (sentMessage != null) {
    System.out.println("메시지 전송 성공! ID: " + sentMessage.getMessageId());
}

// 공지사항 전송
MessageDto notice = new MessageDto(
    0L,                                    // receiverId (0 = 전체 공지)
    "시스템 점검 안내",                      // title
    "오늘 밤 12시부터 2시간 동안 시스템 점검이 예정되어 있습니다.", // content
    "NOTICE"                               // messageType
);

MessageDto sentNotice = apiClient.sendMessage(notice, 1L);
```

### 2. 메시지 조회

```java
// 받은 메시지 조회
List<MessageDto> receivedMessages = apiClient.getReceivedMessages(1L, null, null, false);

// 보낸 메시지 조회
List<MessageDto> sentMessages = apiClient.getSentMessages(1L, null, null);

// 읽지 않은 메시지만 조회
List<MessageDto> unreadMessages = apiClient.getUnreadMessages(1L);

// 특정 유형의 메시지만 조회
List<MessageDto> noticeMessages = apiClient.getReceivedMessages(1L, "NOTICE", null, false);

// 키워드로 검색
List<MessageDto> searchResults = apiClient.getMessages(1L, "received", null, "회의", false);
```

### 3. 메시지 상세 조회

```java
// 특정 메시지 조회
MessageDto message = apiClient.getMessageById(123L, 1L);
if (message != null) {
    System.out.println("제목: " + message.getTitle());
    System.out.println("내용: " + message.getContent());
    System.out.println("보낸 사람: " + message.getSenderName());
    System.out.println("읽음 여부: " + message.getIsRead());
}
```

### 4. 메시지 상태 변경

```java
// 메시지 읽음 처리
boolean readSuccess = apiClient.updateMessageStatus(123L, 1L, "read");

// 메시지 삭제
boolean deleteSuccess = apiClient.updateMessageStatus(123L, 1L, "delete");

// 여러 메시지 일괄 처리
List<Long> messageIds = Arrays.asList(123L, 124L, 125L);
boolean bulkReadSuccess = apiClient.bulkUpdateMessages(1L, messageIds, "read");
boolean bulkDeleteSuccess = apiClient.bulkUpdateMessages(1L, messageIds, "delete");
```

### 5. 메시지 답장

```java
// 메시지에 답장
MessageDto reply = apiClient.replyToMessage(
    123L,                          // 원본 메시지 ID
    1L,                           // 답장하는 사용자 ID
    "Re: 회의 일정 안내",           // 제목
    "참석하겠습니다. 감사합니다."     // 내용
);
```

### 6. 대화 내역 조회

```java
// 특정 사용자와의 대화 내역 조회
List<MessageDto> conversation = apiClient.getConversation(1L, 2L);
for (MessageDto msg : conversation) {
    System.out.println(msg.getSenderName() + ": " + msg.getContent());
}
```

### 7. 메시지 대시보드

```java
// 메시지 대시보드 정보 조회
JsonNode dashboard = apiClient.getMessageDashboard(1L);
if (dashboard != null) {
    System.out.println("읽지 않은 메시지: " + dashboard.get("unreadCount").asInt());
    System.out.println("받은 메시지 총 개수: " + dashboard.get("totalReceivedCount").asInt());
    System.out.println("보낸 메시지 총 개수: " + dashboard.get("totalSentCount").asInt());
}
```

## 개별 클라이언트 사용법

```java
import com.example.companycore.service.MessageApiClient;

MessageApiClient messageClient = MessageApiClient.getInstance();

// 메시지 전송
MessageDto newMessage = new MessageDto(2L, "안녕하세요", "반갑습니다.", "MESSAGE");
MessageDto sent = messageClient.sendMessage(newMessage, 1L);

// 받은 메시지 조회
List<MessageDto> received = messageClient.getReceivedMessages(1L, null, null, false);
```

## 실제 사용 시나리오

### 시나리오 1: 일반적인 메시지 전송 및 조회

```java
public class MessageService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public boolean sendMessageToUser(Long senderId, Long receiverId, String title, String content) {
        try {
            MessageDto message = new MessageDto(receiverId, title, content, "MESSAGE");
            MessageDto sent = apiClient.sendMessage(message, senderId);
            return sent != null;
        } catch (Exception e) {
            System.err.println("메시지 전송 실패: " + e.getMessage());
            return false;
        }
    }
    
    public List<MessageDto> getUserInbox(Long userId) {
        return apiClient.getReceivedMessages(userId, null, null, false);
    }
    
    public List<MessageDto> getUserUnreadMessages(Long userId) {
        return apiClient.getUnreadMessages(userId);
    }
}
```

### 시나리오 2: 공지사항 관리

```java
public class NoticeService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public boolean sendNoticeToAll(Long senderId, String title, String content) {
        try {
            MessageDto notice = new MessageDto(0L, title, content, "NOTICE");
            MessageDto sent = apiClient.sendMessage(notice, senderId);
            return sent != null;
        } catch (Exception e) {
            System.err.println("공지사항 전송 실패: " + e.getMessage());
            return false;
        }
    }
    
    public List<MessageDto> getNotices(Long userId) {
        return apiClient.getReceivedMessages(userId, "NOTICE", null, false);
    }
}
```

### 시나리오 3: 메시지 관리 및 정리

```java
public class MessageManagementService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public boolean markAllAsRead(Long userId) {
        try {
            List<MessageDto> unreadMessages = apiClient.getUnreadMessages(userId);
            List<Long> messageIds = unreadMessages.stream()
                .map(MessageDto::getMessageId)
                .collect(Collectors.toList());
            
            if (!messageIds.isEmpty()) {
                return apiClient.bulkUpdateMessages(userId, messageIds, "read");
            }
            return true;
        } catch (Exception e) {
            System.err.println("일괄 읽음 처리 실패: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteOldMessages(Long userId, int daysOld) {
        try {
            List<MessageDto> allMessages = apiClient.getAllMessages(userId, null, null);
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            
            List<Long> oldMessageIds = allMessages.stream()
                .filter(msg -> msg.getCreatedAt() != null && 
                              msg.getCreatedAt().isBefore(cutoffDate))
                .map(MessageDto::getMessageId)
                .collect(Collectors.toList());
            
            if (!oldMessageIds.isEmpty()) {
                return apiClient.bulkUpdateMessages(userId, oldMessageIds, "delete");
            }
            return true;
        } catch (Exception e) {
            System.err.println("오래된 메시지 삭제 실패: " + e.getMessage());
            return false;
        }
    }
}
```

### 시나리오 4: 대화 관리

```java
public class ConversationService {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public List<MessageDto> getConversationWithUser(Long userId, Long otherUserId) {
        return apiClient.getConversation(userId, otherUserId);
    }
    
    public boolean replyToMessage(Long messageId, Long userId, String replyContent) {
        try {
            MessageDto originalMessage = apiClient.getMessageById(messageId, userId);
            if (originalMessage != null) {
                String replyTitle = "Re: " + originalMessage.getTitle();
                MessageDto reply = apiClient.replyToMessage(messageId, userId, replyTitle, replyContent);
                return reply != null;
            }
            return false;
        } catch (Exception e) {
            System.err.println("답장 전송 실패: " + e.getMessage());
            return false;
        }
    }
}
```

## 오류 처리

```java
public class MessageErrorHandling {
    private ApiClient apiClient = ApiClient.getInstance();
    
    public MessageDto safeSendMessage(MessageDto message, Long senderId) {
        try {
            return apiClient.sendMessage(message, senderId);
        } catch (Exception e) {
            System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
    
    public boolean safeUpdateMessageStatus(Long messageId, Long userId, String action) {
        try {
            return apiClient.updateMessageStatus(messageId, userId, action);
        } catch (Exception e) {
            System.err.println("메시지 상태 변경 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
    
    public List<MessageDto> safeGetMessages(Long userId, String type, String messageType, 
                                          String keyword, Boolean unreadOnly) {
        try {
            return apiClient.getMessages(userId, type, messageType, keyword, unreadOnly);
        } catch (Exception e) {
            System.err.println("메시지 조회 중 오류 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
```

## 주의사항

1. **User-Id 헤더**: 모든 메시지 API 호출에는 `User-Id` 헤더가 필요합니다.
2. **메시지 유형**: `MESSAGE`, `EMAIL`, `NOTICE` 중 하나를 사용해야 합니다.
3. **읽음 상태**: 메시지 조회 시 읽음 상태를 확인할 수 있습니다.
4. **일괄 처리**: 여러 메시지를 한 번에 처리할 수 있습니다.
5. **대화 내역**: 특정 사용자와의 모든 대화를 조회할 수 있습니다.
6. **대시보드**: 메시지 통계와 최근 메시지를 한 번에 조회할 수 있습니다.
7. **토큰 인증**: 모든 API 호출에는 유효한 인증 토큰이 필요합니다.
8. **예외 처리**: 네트워크 오류나 서버 오류에 대한 적절한 예외 처리가 필요합니다. 