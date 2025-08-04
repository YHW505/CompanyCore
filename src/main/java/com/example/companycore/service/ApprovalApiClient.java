package com.example.companycore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 결재 관련 API 클라이언트
 * 결재 요청, 승인, 거부, 조회 등의 기능을 제공
 */
public class ApprovalApiClient extends BaseApiClient {
    private static ApprovalApiClient instance;

    private ApprovalApiClient() {
        super();
    }

    public static ApprovalApiClient getInstance() {
        if (instance == null) {
            synchronized (ApprovalApiClient.class) {
                if (instance == null) {
                    instance = new ApprovalApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 결재 요청 생성
     * @param approvalDto 결재 요청 데이터
     * @return 생성된 결재 정보
     */
    public ApprovalDto createApproval(ApprovalDto approvalDto) {
        try {
            String requestBody = objectMapper.writeValueAsString(approvalDto);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 요청 생성");

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 요청 생성");
        }
        return null;
    }

    /**
     * 결재 승인
     * @param approvalId 결재 ID
     * @return 승인된 결재 정보
     */
    public ApprovalDto approveApproval(Long approvalId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId + "/approve");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 승인");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 승인");
        }
        return null;
    }

    /**
     * 결재 거부
     * @param approvalId 결재 ID
     * @param rejectionReason 거부 사유
     * @return 거부된 결재 정보
     */
    public ApprovalDto rejectApproval(Long approvalId, String rejectionReason) {
        try {
            Map<String, String> requestBody = Map.of("rejectionReason", rejectionReason);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId + "/reject");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 거부");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 거부");
        }
        return null;
    }

    /**
     * 결재 상세 조회
     * @param approvalId 결재 ID
     * @return 결재 상세 정보
     */
    public ApprovalDto getApprovalById(Long approvalId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 상세 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 상세 조회");
        }
        return null;
    }

    /**
     * 내가 요청한 결재 목록 조회
     * @return 내가 요청한 결재 목록
     */
    public List<ApprovalDto> getMyRequests() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-requests");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 요청한 결재 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.convertValue(jsonNode.get("data"), new TypeReference<List<ApprovalDto>>() {});
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 요청한 결재 목록 조회");
        }
        return null;
    }

    /**
     * 내가 결재해야 할 목록 조회
     * @return 내가 결재해야 할 목록
     */
    public List<ApprovalDto> getMyApprovals() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-approvals");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 결재해야 할 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.convertValue(jsonNode.get("data"), new TypeReference<List<ApprovalDto>>() {});
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 결재해야 할 목록 조회");
        }
        return null;
    }

    /**
     * 내가 결재해야 할 대기중인 목록 조회
     * @return 내가 결재해야 할 대기중인 목록
     */
    public List<ApprovalDto> getMyPending() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-pending");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 결재해야 할 대기중인 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.convertValue(jsonNode.get("data"), new TypeReference<List<ApprovalDto>>() {});
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 결재해야 할 대기중인 목록 조회");
        }
        return null;
    }

    /**
     * 모든 결재 목록 조회 (관리자용)
     * @return 모든 결재 목록
     */
    public List<ApprovalDto> getAllApprovals() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/all");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "모든 결재 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.convertValue(jsonNode.get("data"), new TypeReference<List<ApprovalDto>>() {});
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "모든 결재 목록 조회");
        }
        return null;
    }

    /**
     * 결재 삭제
     * @param approvalId 결재 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteApproval(Long approvalId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId);
            HttpRequest request = builder.DELETE().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 삭제");

            return response.statusCode() == 200;
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 삭제");
        }
        return false;
    }

    /**
     * 제목으로 결재 검색
     * @param title 검색할 제목
     * @return 검색된 결재 목록
     */
    public List<ApprovalDto> searchApprovalsByTitle(String title) {
        try {
            String encodedTitle = java.net.URLEncoder.encode(title, "UTF-8");
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/search?title=" + encodedTitle);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 제목 검색");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.convertValue(jsonNode.get("data"), new TypeReference<List<ApprovalDto>>() {});
                    }
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "결재 제목 검색");
        }
        return null;
    }

    /**
     * 결재 DTO 클래스
     */
    public static class ApprovalDto {
        private Long id;
        private String title;
        private String content;
        private UserDto requester;
        private UserDto approver;
        private LocalDateTime requestDate;
        private String status; // PENDING, APPROVED, REJECTED
        private String rejectionReason;
        private LocalDateTime processedDate;
        private String attachmentPath;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Constructors
        public ApprovalDto() {}

        public ApprovalDto(String title, String content, Long approverId, String attachmentPath) {
            this.title = title;
            this.content = content;
            this.approver = new UserDto();
            this.approver.setId(approverId);
            this.attachmentPath = attachmentPath;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public UserDto getRequester() { return requester; }
        public void setRequester(UserDto requester) { this.requester = requester; }

        public UserDto getApprover() { return approver; }
        public void setApprover(UserDto approver) { this.approver = approver; }

        public LocalDateTime getRequestDate() { return requestDate; }
        public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

        public LocalDateTime getProcessedDate() { return processedDate; }
        public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }

        public String getAttachmentPath() { return attachmentPath; }
        public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    /**
     * 사용자 DTO 클래스
     */
    public static class UserDto {
        private Long id;
        private String name;
        private String email;

        // Constructors
        public UserDto() {}

        public UserDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 