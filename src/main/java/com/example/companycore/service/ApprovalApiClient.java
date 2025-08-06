package com.example.companycore.service;

import com.example.companycore.model.dto.ApprovalDto;
import com.example.companycore.model.dto.UserDto;
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
            // Base64 인코딩된 첨부파일 내용은 로그에서 제외
            String logRequestBody = requestBody;
            if (approvalDto.getAttachmentContent() != null && !approvalDto.getAttachmentContent().isEmpty()) {
                logRequestBody = requestBody.replace(approvalDto.getAttachmentContent(), "[Base64 첨부파일 내용 생략]");
            }
            System.out.println("🔍 결재 요청 생성 - 요청 본문: " + logRequestBody);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/create");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 요청 생성");

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                String responseBody = getSafeResponseBody(response);
                System.out.println("✅ 결재 요청 생성 성공 - 응답 본문: " + responseBody);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            } else {
                System.err.println("❌ 결재 요청 생성 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 결재 요청 생성 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("🔍 결재 상세 조회 시도 - ID: " + approvalId);
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "결재 상세 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                // Base64 인코딩된 첨부파일 내용은 로그에서 제외
                String logResponseBody = responseBody;
                if (responseBody != null && responseBody.contains("attachmentContent")) {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        if (jsonNode.has("attachmentContent") && jsonNode.get("attachmentContent").asText() != null) {
                            String attachmentContent = jsonNode.get("attachmentContent").asText();
                            if (!attachmentContent.isEmpty()) {
                                logResponseBody = responseBody.replace(attachmentContent, "[Base64 첨부파일 내용 생략]");
                            }
                        }
                    } catch (Exception e) {
                        // JSON 파싱 실패 시 원본 사용
                        logResponseBody = responseBody;
                    }
                }
                System.out.println("📋 상세 조회 응답 본문: " + logResponseBody);
                
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    
                    // 서버 응답 구조 확인
                    System.out.println("📋 응답 구조 분석:");
                    System.out.println("  - data 필드 존재: " + jsonNode.has("data"));
                    System.out.println("  - 직접 객체 응답: " + (jsonNode.has("id") && jsonNode.has("title")));
                    
                    ApprovalDto result;
                    if (jsonNode.has("data")) {
                        // data 필드가 있는 경우
                        result = objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    } else {
                        // 직접 JSON 객체로 응답하는 경우
                        result = objectMapper.treeToValue(jsonNode, ApprovalDto.class);
                    }
                    
                    System.out.println("✅ 상세 조회 파싱 성공");
                    System.out.println("📋 파싱된 데이터:");
                    System.out.println("  - ID: " + result.getId());
                    System.out.println("  - 제목: " + result.getTitle());
                    System.out.println("  - 첨부파일명: " + result.getAttachmentFilename());
                    System.out.println("  - 첨부파일 크기: " + result.getAttachmentSize());
                    System.out.println("  - 첨부파일 내용 존재: " + (result.getAttachmentContent() != null));
                    
                    if (result.getAttachmentContent() == null) {
                        System.err.println("⚠️ 서버 응답에 attachmentContent 필드가 없습니다");
                        System.err.println("📋 서버 응답 필드들:");
                        jsonNode.fieldNames().forEachRemaining(field -> {
                            System.err.println("    - " + field + ": " + jsonNode.get(field));
                        });
                    }
                    
                    return result;
                } else {
                    System.err.println("❌ 응답 본문이 비어있습니다");
                }
            } else {
                System.err.println("❌ 상세 조회 실패 - 상태 코드: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ 결재 상세 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
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
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-requests/" + currentUser.getUserId());
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 요청한 결재 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // 서버에서 직접 배열로 응답하므로 data 필드 없이 직접 파싱
                    return objectMapper.convertValue(objectMapper.readTree(responseBody), new TypeReference<List<ApprovalDto>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 요청한 결재 목록 조회");
        }
        return null;
    }

    /**
     * 내가 요청한 결재 목록 조회 (페이지네이션 포함)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 필드
     * @param sortDir 정렬 방향 (asc/desc)
     * @return 페이지네이션된 결재 목록
     */
    public Map<String, Object> getMyRequestsWithPagination(int page, int size, String sortBy, String sortDir) {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            String endpoint = String.format("/approvals/my-requests/%d/page?page=%d&size=%d&sortBy=%s&sortDir=%s",
                    currentUser.getUserId(), page, size, sortBy, sortDir);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder(endpoint);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 요청한 결재 목록 조회 (페이지네이션)");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 요청한 결재 목록 조회 (페이지네이션)");
        }
        return null;
    }

    /**
     * 내가 결재해야 할 목록 조회
     * @return 내가 결재해야 할 목록
     */
    public List<ApprovalDto> getMyApprovals() {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-approvals/" + currentUser.getUserId());
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 결재해야 할 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // 서버에서 직접 배열로 응답하므로 data 필드 없이 직접 파싱
                    return objectMapper.convertValue(objectMapper.readTree(responseBody), new TypeReference<List<ApprovalDto>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 결재해야 할 목록 조회");
        }
        return null;
    }

    /**
     * 내가 결재해야 할 목록 조회 (페이지네이션 포함)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 필드
     * @param sortDir 정렬 방향 (asc/desc)
     * @return 페이지네이션된 결재 목록
     */
    public Map<String, Object> getMyApprovalsWithPagination(int page, int size, String sortBy, String sortDir) {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            String endpoint = String.format("/approvals/my-approvals/%d/page?page=%d&size=%d&sortBy=%s&sortDir=%s",
                    currentUser.getUserId(), page, size, sortBy, sortDir);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder(endpoint);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 결재해야 할 목록 조회 (페이지네이션)");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 결재해야 할 목록 조회 (페이지네이션)");
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
     * 내가 결재해야 할 대기중인 목록 조회 (페이지네이션 포함)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 필드
     * @param sortDir 정렬 방향 (asc/desc)
     * @return 페이지네이션된 대기중인 결재 목록
     */
    public Map<String, Object> getMyPendingWithPagination(int page, int size, String sortBy, String sortDir) {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            String endpoint = String.format("/approvals/pending/%d/page?page=%d&size=%d&sortBy=%s&sortDir=%s",
                    currentUser.getUserId(), page, size, sortBy, sortDir);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder(endpoint);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "내가 결재해야 할 대기중인 목록 조회 (페이지네이션)");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 결재해야 할 대기중인 목록 조회 (페이지네이션)");
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

} 