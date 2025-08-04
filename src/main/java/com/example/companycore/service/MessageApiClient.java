package com.example.companycore.service;

import com.example.companycore.model.dto.MessageDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 📦 메시지 관련 API 클라이언트 클래스
 * - 메시지 전송
 * - 메시지 단건/목록 조회
 * - 메시지 상태 변경 (읽음, 삭제 등)
 * - 답장
 * - 대화 내역 조회
 * - 대시보드 정보 조회
 *
 * 이 클래스는 싱글턴 패턴으로 구현되어 있어,
 * 어디서든 동일 인스턴스로 접근 가능합니다.
 */
public class MessageApiClient extends BaseApiClient {

    // 싱글턴 인스턴스
    private static MessageApiClient instance;

    // 생성자 비공개 (외부 생성 방지)
    private MessageApiClient() {
        super(); // BaseApiClient (공통 인증/요청 기능)
    }

    // 인스턴스 생성 및 반환 (싱글턴)
    public static MessageApiClient getInstance() {
        if (instance == null) {
            synchronized (MessageApiClient.class) {
                if (instance == null) {
                    instance = new MessageApiClient();
                }
            }
        }
        return instance;
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 메시지를 전송합니다 (POST /messages).
     * @param message 전송할 메시지 객체
     * @param senderId 보낸 사람의 사용자 ID (Header)
     * @return 성공 시 서버 응답 메시지 객체, 실패 시 null
     */
    public MessageDto sendMessage(MessageDto message, Long senderId) {
        try {
            String json = objectMapper.writeValueAsString(message); // 메시지를 JSON 문자열로 변환

            HttpRequest request = createAuthenticatedRequestBuilder("/messages")
                    .header("Content-Type", "application/json")
                    .header("User-Id", senderId.toString())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                if (response.body() != null && !response.body().isBlank()) {
                    return objectMapper.readValue(response.body(), MessageDto.class);
                } else {
                    System.out.println("⚠️ 응답 본문이 비어 있습니다.");
                }
            } else {
                System.out.println("❌ 메시지 전송 실패 - 상태 코드: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ 메시지 전송 중 예외 발생: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 메시지 목록을 조회합니다 (GET /messages).
     * @param userId 사용자 ID (조회 기준)
     * @param type sent, received, all 중 하나
     * @param messageType optional - 일반/공지 등 메시지 분류
     * @param keyword 검색 키워드
     * @param unreadOnly true면 안 읽은 메시지만 조회
     * @return 메시지 DTO 리스트
     */
    public List<MessageDto> getMessages(Long userId, String type, String messageType,
                                        String keyword, Boolean unreadOnly) {
        try {
            StringBuilder endpoint = new StringBuilder("/messages?");
            if (type != null) endpoint.append("type=").append(type).append("&");
            if (messageType != null) endpoint.append("messageType=").append(messageType).append("&");
            if (keyword != null) endpoint.append("keyword=").append(keyword).append("&");
            if (unreadOnly != null) endpoint.append("unreadOnly=").append(unreadOnly).append("&");

            // 끝에 남은 & 제거
            if (endpoint.charAt(endpoint.length() - 1) == '&') {
                endpoint.setLength(endpoint.length() - 1);
            }

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint.toString())
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().trim().isEmpty()) {
                return objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("메시지 목록 요청 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

  public List<MessageDto> getReceiveMessagesById(Long userId) {
        try {
//            StringBuilder endpoint = new StringBuilder("/messages?");
//            String type = "received";
            String endpoint = "/messages?type=received";


            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            if (response.statusCode() == 200 && response.body() != null && !response.body().trim().isEmpty()) {
                return objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("메시지 목록 요청 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<MessageDto> getSentMessagesById(Long userId) {
        try {
            String endpoint = "/messages?type=sent";

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().trim().isEmpty()) {
                return objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("메시지 목록 요청 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // ------------------------------------------------------------------------

    /**
     * ✅ 단일 메시지를 ID로 조회합니다 (GET /messages/{id}).
     * @param messageId 메시지 ID
     * @param userId 사용자 ID (헤더에 포함)
     * @return 메시지 DTO 객체
     */
    public MessageDto getMessageById(Long messageId, Long userId) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/messages/" + messageId)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), MessageDto.class);
            } else {
                System.out.println("❌ 메시지 조회 실패 - 상태 코드: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ 메시지 조회 중 예외 발생: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 메시지 상태 업데이트 (PUT /messages/{id}).
     * 예: 읽음 처리, 삭제 등
     */
    public boolean updateMessageStatus(Long messageId, Long userId, String action) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("action", action); // 예: "read", "delete"

            HttpRequest request = createAuthenticatedRequestBuilder("/messages/" + messageId)
                    .header("User-Id", userId.toString())
                    .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println("❌ 메시지 상태 변경 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 메시지 여러 개를 일괄 업데이트합니다 (PUT /messages/bulk).
     * 예: 다중 삭제, 읽음 처리
     */
    public boolean bulkUpdateMessages(Long userId, List<Long> messageIds, String action) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("action", action);
            body.set("messageIds", objectMapper.valueToTree(messageIds));

            HttpRequest request = createAuthenticatedRequestBuilder("/messages/bulk")
                    .header("User-Id", userId.toString())
                    .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println("❌ 메시지 일괄 처리 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 특정 메시지에 답장합니다 (POST /messages/{id}/reply).
     */
    public MessageDto replyToMessage(Long messageId, Long userId, String title, String content) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("title", title);
            body.put("content", content);

            HttpRequest request = createAuthenticatedRequestBuilder("/messages/" + messageId + "/reply")
                    .header("User-Id", userId.toString())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), MessageDto.class);
            } else {
                System.out.println("❌ 답장 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ 메시지 답장 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 특정 사용자와의 대화 내역을 조회합니다.
     */
    public List<MessageDto> getConversation(Long userId, Long otherUserId) {
        try {
            String endpoint = "/messages/conversation/" + otherUserId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
                return objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
            }
        } catch (Exception e) {
            System.out.println("❌ 대화 내역 조회 중 예외 발생: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // ------------------------------------------------------------------------

    /**
     * ✅ 메시지 대시보드 정보 조회 (GET /messages/dashboard).
     */
    public JsonNode getMessageDashboard(Long userId) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/messages/dashboard")
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readTree(response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ 대시보드 요청 중 예외 발생: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------------------------------------

    // 아래는 getMessages()를 특정 목적에 맞게 래핑한 메서드들

    public List<MessageDto> getReceivedMessages(Long userId, String messageType, String keyword, Boolean unreadOnly) {
        return getMessages(userId, "received", messageType, keyword, unreadOnly);
    }

    public List<MessageDto> getSentMessages(Long userId, String messageType, String keyword) {
        return getMessages(userId, "sent", messageType, keyword, null);
    }

    public List<MessageDto> getAllMessages(Long userId, String messageType, String keyword) {
        return getMessages(userId, "all", messageType, keyword, null);
    }

    public List<MessageDto> getUnreadMessages(Long userId) {
        return getMessages(userId, "received", null, null, true);
    }
}