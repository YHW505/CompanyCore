package com.example.companycore.service;

import com.example.companycore.model.dto.MessageDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 메시지 관련 API 클라이언트
 * 메시지 전송, 조회, 상태 변경, 답장 등
 */
public class MessageApiClient extends BaseApiClient {
    private static MessageApiClient instance;

    private MessageApiClient() {
        super();
    }

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

    /**
     * 새로운 메시지를 전송합니다.
     */
    public MessageDto sendMessage(MessageDto message, Long senderId) {
        try {
            // 1. 메시지 객체를 JSON으로 변환
            String json = objectMapper.writeValueAsString(message);
            HttpRequest request = createAuthenticatedRequestBuilder("/messages")
                    .header("Content-Type", "application/json")
                    .header("User-Id", senderId.toString())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // 3. 요청 전송 및 응답 수신
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode == 200 || statusCode == 201) {
                if (responseBody != null && !responseBody.isBlank()) {
                    try {
                        MessageDto sentMessage = objectMapper.readValue(responseBody, MessageDto.class);
                        System.out.println("✅ 메시지 전송 성공!");
                        return sentMessage;
                    } catch (Exception parseEx) {
                        System.out.println("❌ 전송된 메시지 파싱 실패: " + parseEx.getMessage());
                        parseEx.printStackTrace();
                    }
                } else {
                    System.out.println("⚠️ 응답 본문이 비어 있습니다.");
                }
            } else {
                System.out.println("❌ 메시지 전송 실패 - 상태 코드: " + statusCode);
                System.out.println("오류 응답 내용: " + responseBody);
            }
        } catch (Exception e) {
            System.out.println("❌ 메시지 전송 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 메시지 목록을 조회합니다.
     */
    public List<MessageDto> getMessages(Long userId, String type, String messageType, 
                                      String keyword, Boolean unreadOnly) {
        try {
            StringBuilder endpoint = new StringBuilder("/messages?");
            
            if (type != null) endpoint.append("type=").append(type).append("&");
            if (messageType != null) endpoint.append("messageType=").append(messageType).append("&");
            if (keyword != null) endpoint.append("keyword=").append(keyword).append("&");
            if (unreadOnly != null) endpoint.append("unreadOnly=").append(unreadOnly).append("&");

            // 마지막 & 제거
            if (endpoint.charAt(endpoint.length() - 1) == '&') {
                endpoint.setLength(endpoint.length() - 1);
            }

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint.toString())
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<MessageDto> messages = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
                    return messages;
                } catch (Exception e) {
                    System.out.println("메시지 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("메시지 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("메시지 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 특정 메시지를 조회합니다.
     */
    public MessageDto getMessageById(Long messageId, Long userId) {
        try {
            String endpoint = "/messages/" + messageId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    MessageDto message = objectMapper.readValue(response.body(), MessageDto.class);
                    return message;
                } catch (Exception e) {
                    System.out.println("메시지 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("메시지 조회 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("메시지 조회 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 메시지 상태를 변경합니다 (읽음 처리 또는 삭제).
     */
    public boolean updateMessageStatus(Long messageId, Long userId, String action) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("action", action);

            String json = objectMapper.writeValueAsString(requestBody);
            String endpoint = "/messages/" + messageId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("메시지 상태 변경 성공!");
                return true;
            } else {
                System.out.println("메시지 상태 변경 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("메시지 상태 변경 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 여러 메시지를 일괄 처리합니다.
     */
    public boolean bulkUpdateMessages(Long userId, List<Long> messageIds, String action) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("action", action);
            requestBody.set("messageIds", objectMapper.valueToTree(messageIds));

            String json = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = createAuthenticatedRequestBuilder("/messages/bulk")
                    .header("User-Id", userId.toString())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("메시지 일괄 처리 성공!");
                return true;
            } else {
                System.out.println("메시지 일괄 처리 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("메시지 일괄 처리 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 메시지에 답장합니다.
     */
    public MessageDto replyToMessage(Long messageId, Long userId, String title, String content) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("title", title);
            requestBody.put("content", content);

            String json = objectMapper.writeValueAsString(requestBody);
            String endpoint = "/messages/" + messageId + "/reply";
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                try {
                    MessageDto replyMessage = objectMapper.readValue(response.body(), MessageDto.class);
                    System.out.println("메시지 답장 성공!");
                    return replyMessage;
                } catch (Exception e) {
                    System.out.println("답장 메시지 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("메시지 답장 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.out.println("메시지 답장 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 특정 사용자와의 대화 내역을 조회합니다.
     */
    public List<MessageDto> getConversation(Long userId, Long otherUserId) {
        try {
            String endpoint = "/messages/conversation/" + otherUserId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<MessageDto> messages = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDto.class));
                    return messages;
                } catch (Exception e) {
                    System.out.println("대화 내역 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("대화 내역 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("대화 내역 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 메시지 대시보드 정보를 가져옵니다.
     */
    public JsonNode getMessageDashboard(Long userId) {
        try {
            String endpoint = "/messages/dashboard";
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .header("User-Id", userId.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    JsonNode dashboard = objectMapper.readTree(response.body());
                    return dashboard;
                } catch (Exception e) {
                    System.out.println("대시보드 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("대시보드 요청 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("대시보드 요청 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 받은 메시지를 조회합니다.
     */
    public List<MessageDto> getReceivedMessages(Long userId, String messageType, String keyword, Boolean unreadOnly) {
        return getMessages(userId, "received", messageType, keyword, unreadOnly);
    }

    /**
     * 보낸 메시지를 조회합니다.
     */
    public List<MessageDto> getSentMessages(Long userId, String messageType, String keyword) {
        return getMessages(userId, "sent", messageType, keyword, null);
    }

    /**
     * 모든 메시지를 조회합니다.
     */
    public List<MessageDto> getAllMessages(Long userId, String messageType, String keyword) {
        return getMessages(userId, "all", messageType, keyword, null);
    }

    /**
     * 읽지 않은 메시지만 조회합니다.
     */
    public List<MessageDto> getUnreadMessages(Long userId) {
        return getMessages(userId, "received", null, null, true);
    }
} 