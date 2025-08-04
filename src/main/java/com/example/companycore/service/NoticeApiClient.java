
package com.example.companycore.service;

import com.example.companycore.model.dto.NoticeItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공지사항 관련 API 클라이언트
 * 공지사항 조회, 생성, 수정, 삭제 등의 기능을 제공
 */
public class NoticeApiClient extends BaseApiClient {
    private static NoticeApiClient instance;

    private NoticeApiClient() {
        super();
    }

    public static NoticeApiClient getInstance() {
        if (instance == null) {
            synchronized (NoticeApiClient.class) {
                if (instance == null) {
                    instance = new NoticeApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 모든 공지사항 조회
     * @return 공지사항 목록
     */
    public List<NoticeItem> getAllNotices() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "모든 공지사항 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // API 응답 구조에 맞게 파싱
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if (rootNode.has("data")) {
                        JsonNode dataNode = rootNode.get("data");
                        List<NoticeItem> notices = new ArrayList<>();
                        
                        for (JsonNode noticeNode : dataNode) {
                            NoticeItem notice = new NoticeItem();
                            
                            // API 응답 필드에 맞게 매핑
                            if (noticeNode.has("id")) {
                                notice.setNoticeId(noticeNode.get("id").asLong());
                            }
                            if (noticeNode.has("title")) {
                                notice.setTitle(noticeNode.get("title").asText());
                            }
                            if (noticeNode.has("content")) {
                                notice.setContent(noticeNode.get("content").asText());
                            }
                            if (noticeNode.has("authorDepartment")) {
                                notice.setDepartment(noticeNode.get("authorDepartment").asText());
                            }
                            if (noticeNode.has("authorName")) {
                                notice.setAuthor(noticeNode.get("authorName").asText());
                            }
                            if (noticeNode.has("createdAt")) {
                                String createdAtStr = noticeNode.get("createdAt").asText();
                                try {
                                    LocalDateTime createdAt = LocalDateTime.parse(createdAtStr.replace("Z", ""));
                                    notice.setCreatedAt(createdAt);
                                    notice.setDate(createdAt.toLocalDate());
                                } catch (Exception e) {
                                    System.out.println("날짜 파싱 오류: " + createdAtStr);
                                    notice.setDate(LocalDate.now());
                                }
                            }
                            if (noticeNode.has("updatedAt")) {
                                String updatedAtStr = noticeNode.get("updatedAt").asText();
                                try {
                                    LocalDateTime updatedAt = LocalDateTime.parse(updatedAtStr.replace("Z", ""));
                                    notice.setUpdatedAt(updatedAt);
                                } catch (Exception e) {
                                    System.out.println("업데이트 날짜 파싱 오류: " + updatedAtStr);
                                }
                            }
                            
                            // 첨부파일 정보 매핑
                            if (noticeNode.has("hasAttachments") && noticeNode.get("hasAttachments").asBoolean()) {
                                notice.setHasAttachments(true);
                                if (noticeNode.has("attachmentFilename")) {
                                    notice.setAttachmentFilename(noticeNode.get("attachmentFilename").asText());
                                }
                                if (noticeNode.has("attachmentContentType")) {
                                    notice.setAttachmentContentType(noticeNode.get("attachmentContentType").asText());
                                }
                                if (noticeNode.has("attachmentSize")) {
                                    notice.setAttachmentSize(noticeNode.get("attachmentSize").asLong());
                                }
                                // 🆕 첨부파일 내용 파싱
                                if (noticeNode.has("attachmentContent")) {
                                    notice.setAttachmentContent(noticeNode.get("attachmentContent").asText());
                                    System.out.println("첨부파일 내용 파싱: " + notice.getAttachmentFilename() + " (Base64 길이: " + notice.getAttachmentContent().length() + ")");
                                } else {
                                    System.out.println("⚠️ attachmentContent 필드가 없습니다. 서버 응답 필드들: " + noticeNode.fieldNames());
                                }
                            } else {
                                notice.setHasAttachments(false);
                                System.out.println("⚠️ hasAttachments가 false이거나 필드가 없습니다. 서버 응답 필드들: " + noticeNode.fieldNames());
                            }
                            
                            // 기본값 설정
                            notice.setSelected(false);
                            notice.setImportant(false); // API에서 중요도 정보가 없으므로 기본값
                            
                            notices.add(notice);
                        }
                        
                        System.out.println("공지사항 파싱 완료: " + notices.size() + "개");
                        return notices;
                    } else {
                        System.out.println("응답에 'data' 필드가 없습니다: " + responseBody);
                        return new ArrayList<>();
                    }
                } else {
                    System.out.println("빈 응답을 받았습니다.");
                    return new ArrayList<>();
                }
            } else {
                System.out.println("공지사항 조회 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("공지사항 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ID로 공지사항 조회
     * @param noticeId 공지사항 ID
     * @return 공지사항 정보
     */
    public NoticeItem getNoticeById(Long noticeId) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/" + noticeId)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "ID로 공지사항 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode noticeNode = objectMapper.readTree(responseBody);
                    NoticeItem notice = new NoticeItem();
                    
                    // API 응답 필드에 맞게 매핑
                    if (noticeNode.has("id")) {
                        notice.setNoticeId(noticeNode.get("id").asLong());
                    }
                    if (noticeNode.has("title")) {
                        notice.setTitle(noticeNode.get("title").asText());
                    }
                    if (noticeNode.has("content")) {
                        notice.setContent(noticeNode.get("content").asText());
                    }
                    if (noticeNode.has("authorDepartment")) {
                        notice.setDepartment(noticeNode.get("authorDepartment").asText());
                    }
                    if (noticeNode.has("authorName")) {
                        notice.setAuthor(noticeNode.get("authorName").asText());
                    }
                    if (noticeNode.has("createdAt")) {
                        String createdAtStr = noticeNode.get("createdAt").asText();
                        try {
                            LocalDateTime createdAt = LocalDateTime.parse(createdAtStr.replace("Z", ""));
                            notice.setCreatedAt(createdAt);
                            notice.setDate(createdAt.toLocalDate());
                        } catch (Exception e) {
                            System.out.println("날짜 파싱 오류: " + createdAtStr);
                            notice.setDate(LocalDate.now());
                        }
                    }
                    if (noticeNode.has("updatedAt")) {
                        String updatedAtStr = noticeNode.get("updatedAt").asText();
                        try {
                            LocalDateTime updatedAt = LocalDateTime.parse(updatedAtStr.replace("Z", ""));
                            notice.setUpdatedAt(updatedAt);
                        } catch (Exception e) {
                            System.out.println("업데이트 날짜 파싱 오류: " + updatedAtStr);
                        }
                    }
                    
                    // 기본값 설정
                    notice.setSelected(false);
                    notice.setImportant(false);
                    
                    return notice;
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "ID로 공지사항 조회");
        }
        return null;
    }

    /**
     * 공지사항 생성
     * @param notice 공지사항 정보
     * @return 생성된 공지사항 정보
     */
    public NoticeItem createNotice(NoticeItem notice) {
        try {
            // API 요청 구조에 맞게 JSON 생성
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("title", notice.getTitle());
            requestNode.put("content", notice.getContent());
            requestNode.put("authorDepartment", notice.getDepartment());
            requestNode.put("authorName", notice.getAuthor());
            
            // 첨부파일 정보 추가
            if (notice.hasAttachments()) {
                requestNode.put("attachmentFilename", notice.getAttachmentFilename());
                requestNode.put("attachmentContentType", notice.getAttachmentContentType());
                requestNode.put("attachmentSize", notice.getAttachmentSize());
                requestNode.put("hasAttachments", true);
                if (notice.getAttachmentContent() != null && !notice.getAttachmentContent().isEmpty()) {
                    requestNode.put("attachmentContent", notice.getAttachmentContent());
                    System.out.println("첨부파일 내용 추가: " + notice.getAttachmentFilename() + " (Base64)");
                }
                System.out.println("첨부파일 정보 추가: " + notice.getAttachmentFilename());
            } else {
                requestNode.put("hasAttachments", false);
            }
            
            // 현재 로그인한 사용자의 ID를 authorId로 설정
            // JWT 토큰에서 사용자 ID 추출
            if (hasValidToken()) {
                try {
                    String[] tokenParts = authToken.split("\\.");
                    if (tokenParts.length == 3) {
                        String payload = tokenParts[1];
                        // Base64 디코딩 (패딩 추가)
                        while (payload.length() % 4 != 0) {
                            payload += "=";
                        }
                        String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));
                        JsonNode payloadNode = objectMapper.readTree(decodedPayload);
                        
                        if (payloadNode.has("userId")) {
                            Long userId = payloadNode.get("userId").asLong();
                            requestNode.put("authorId", userId);
                            System.out.println("작성자 ID 설정: " + userId);
                        } else {
                            System.out.println("JWT 토큰에 userId가 없습니다.");
                            requestNode.put("authorId", 1L); // 기본값
                        }
                    } else {
                        System.out.println("JWT 토큰 형식이 올바르지 않습니다.");
                        requestNode.put("authorId", 1L); // 기본값
                    }
                } catch (Exception e) {
                    System.out.println("JWT 토큰 파싱 오류: " + e.getMessage());
                    requestNode.put("authorId", 1L); // 기본값
                }
            } else {
                System.out.println("인증 토큰이 없습니다.");
                requestNode.put("authorId", 1L); // 기본값
            }
            
            String requestBody = objectMapper.writeValueAsString(requestNode);
            System.out.println("공지사항 생성 요청: " + requestBody);
            
            HttpRequest request = createAuthenticatedRequestBuilder("/notices")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "공지사항 생성");

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        JsonNode dataNode = jsonNode.get("data");
                        NoticeItem createdNotice = new NoticeItem();
                        
                        // 응답 데이터 매핑
                        if (dataNode.has("id")) {
                            createdNotice.setNoticeId(dataNode.get("id").asLong());
                        }
                        if (dataNode.has("title")) {
                            createdNotice.setTitle(dataNode.get("title").asText());
                        }
                        if (dataNode.has("content")) {
                            createdNotice.setContent(dataNode.get("content").asText());
                        }
                        if (dataNode.has("authorDepartment")) {
                            createdNotice.setDepartment(dataNode.get("authorDepartment").asText());
                        }
                        if (dataNode.has("authorName")) {
                            createdNotice.setAuthor(dataNode.get("authorName").asText());
                        }
                        if (dataNode.has("createdAt")) {
                            String createdAtStr = dataNode.get("createdAt").asText();
                            try {
                                LocalDateTime createdAt = LocalDateTime.parse(createdAtStr.replace("Z", ""));
                                createdNotice.setCreatedAt(createdAt);
                                createdNotice.setDate(createdAt.toLocalDate());
                            } catch (Exception e) {
                                System.out.println("날짜 파싱 오류: " + createdAtStr);
                                createdNotice.setDate(LocalDate.now());
                            }
                        }
                        
                        // 첨부파일 정보 매핑
                        if (dataNode.has("hasAttachments") && dataNode.get("hasAttachments").asBoolean()) {
                            createdNotice.setHasAttachments(true);
                            if (dataNode.has("attachmentFilename")) {
                                createdNotice.setAttachmentFilename(dataNode.get("attachmentFilename").asText());
                            }
                            if (dataNode.has("attachmentContentType")) {
                                createdNotice.setAttachmentContentType(dataNode.get("attachmentContentType").asText());
                            }
                            if (dataNode.has("attachmentSize")) {
                                createdNotice.setAttachmentSize(dataNode.get("attachmentSize").asLong());
                            }
                        } else {
                            createdNotice.setHasAttachments(false);
                        }
                        
                        // 기본값 설정
                        createdNotice.setSelected(false);
                        createdNotice.setImportant(false);
                        
                        return createdNotice;
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "공지사항 생성");
        }
        return null;
    }

    /**
     * 공지사항 수정
     * @param noticeId 공지사항 ID
     * @param notice 수정할 공지사항 정보
     * @return 수정된 공지사항 정보
     */
    public NoticeItem updateNotice(Long noticeId, NoticeItem notice) {
        try {
            // API 요청 구조에 맞게 JSON 생성
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("title", notice.getTitle());
            requestNode.put("content", notice.getContent());
            requestNode.put("authorDepartment", notice.getDepartment());
            requestNode.put("authorName", notice.getAuthor());
            
            // 현재 로그인한 사용자의 ID를 authorId로 설정
            if (hasValidToken()) {
                try {
                    String[] tokenParts = authToken.split("\\.");
                    if (tokenParts.length == 3) {
                        String payload = tokenParts[1];
                        // Base64 디코딩 (패딩 추가)
                        while (payload.length() % 4 != 0) {
                            payload += "=";
                        }
                        String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));
                        JsonNode payloadNode = objectMapper.readTree(decodedPayload);
                        
                        if (payloadNode.has("userId")) {
                            Long userId = payloadNode.get("userId").asLong();
                            requestNode.put("authorId", userId);
                            System.out.println("수정 작성자 ID 설정: " + userId);
                        } else {
                            System.out.println("JWT 토큰에 userId가 없습니다.");
                            requestNode.put("authorId", 1L); // 기본값
                        }
                    } else {
                        System.out.println("JWT 토큰 형식이 올바르지 않습니다.");
                        requestNode.put("authorId", 1L); // 기본값
                    }
                } catch (Exception e) {
                    System.out.println("JWT 토큰 파싱 오류: " + e.getMessage());
                    requestNode.put("authorId", 1L); // 기본값
                }
            } else {
                System.out.println("인증 토큰이 없습니다.");
                requestNode.put("authorId", 1L); // 기본값
            }
            
            String requestBody = objectMapper.writeValueAsString(requestNode);
            System.out.println("공지사항 수정 요청: " + requestBody);
            
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/" + noticeId)
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "공지사항 수정");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        JsonNode dataNode = jsonNode.get("data");
                        NoticeItem updatedNotice = new NoticeItem();
                        
                        // 응답 데이터 매핑
                        if (dataNode.has("id")) {
                            updatedNotice.setNoticeId(dataNode.get("id").asLong());
                        }
                        if (dataNode.has("title")) {
                            updatedNotice.setTitle(dataNode.get("title").asText());
                        }
                        if (dataNode.has("content")) {
                            updatedNotice.setContent(dataNode.get("content").asText());
                        }
                        if (dataNode.has("authorDepartment")) {
                            updatedNotice.setDepartment(dataNode.get("authorDepartment").asText());
                        }
                        if (dataNode.has("authorName")) {
                            updatedNotice.setAuthor(dataNode.get("authorName").asText());
                        }
                        if (dataNode.has("updatedAt")) {
                            String updatedAtStr = dataNode.get("updatedAt").asText();
                            try {
                                LocalDateTime updatedAt = LocalDateTime.parse(updatedAtStr.replace("Z", ""));
                                updatedNotice.setUpdatedAt(updatedAt);
                                updatedNotice.setDate(updatedAt.toLocalDate());
                            } catch (Exception e) {
                                System.out.println("날짜 파싱 오류: " + updatedAtStr);
                                updatedNotice.setDate(LocalDate.now());
                            }
                        }
                        
                        // 기본값 설정
                        updatedNotice.setSelected(false);
                        updatedNotice.setImportant(false);
                        
                        return updatedNotice;
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "공지사항 수정");
        }
        return null;
    }

    /**
     * 공지사항 삭제
     * @param noticeId 공지사항 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteNotice(Long noticeId) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/" + noticeId)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "공지사항 삭제");

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            handleChunkedTransferError(e, "공지사항 삭제");
        }
        return false;
    }

    /**
     * 제목으로 공지사항 검색
     * @param title 검색할 제목
     * @return 검색된 공지사항 목록
     */
    public List<NoticeItem> searchNoticesByTitle(String title) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/search?title=" + title)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "제목으로 공지사항 검색");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if (rootNode.has("data")) {
                        JsonNode dataNode = rootNode.get("data");
                        return objectMapper.readValue(dataNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class));
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "제목으로 공지사항 검색");
        }
        return new ArrayList<>();
    }

    /**
     * 부서별 공지사항 조회
     * @param department 부서명
     * @return 해당 부서의 공지사항 목록
     */
    public List<NoticeItem> getNoticesByDepartment(String department) {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/department/" + department)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "부서별 공지사항 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if (rootNode.has("data")) {
                        JsonNode dataNode = rootNode.get("data");
                        return objectMapper.readValue(dataNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class));
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "부서별 공지사항 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 중요 공지사항 조회
     * @return 중요 공지사항 목록
     */
    public List<NoticeItem> getImportantNotices() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices/important")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "중요 공지사항 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if (rootNode.has("data")) {
                        JsonNode dataNode = rootNode.get("data");
                        return objectMapper.readValue(dataNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class));
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "중요 공지사항 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 복합 조건으로 공지사항 검색
     * @param department 부서명 (선택사항)
     * @param isImportant 중요 여부 (선택사항)
     * @param page 페이지 번호 (선택사항)
     * @param size 페이지 크기 (선택사항)
     * @return 검색된 공지사항 목록
     */
    public List<NoticeItem> filterNotices(String department, Boolean isImportant, Integer page, Integer size) {
        try {
            StringBuilder urlBuilder = new StringBuilder("/notices/filter?");
            if (department != null) urlBuilder.append("department=").append(department).append("&");
            if (isImportant != null) urlBuilder.append("isImportant=").append(isImportant).append("&");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (size != null) urlBuilder.append("size=").append(size).append("&");
            
            String url = urlBuilder.toString();
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }

            HttpRequest request = createAuthenticatedRequestBuilder(url)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "복합 조건 공지사항 검색");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if (rootNode.has("data") && rootNode.get("data").has("content")) {
                        JsonNode contentNode = rootNode.get("data").get("content");
                        return objectMapper.readValue(contentNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class));
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "복합 조건 공지사항 검색");
        }
        return new ArrayList<>();
    }
} 