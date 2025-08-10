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
import java.util.ArrayList;

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
     * 결재 요청 수정
     * @param approvalId 결재 ID
     * @param approvalDto 수정할 결재 데이터
     * @return 수정된 결재 정보
     */
    public ApprovalDto updateApproval(Long approvalId, ApprovalDto approvalDto) {
        try {
            String requestBody = objectMapper.writeValueAsString(approvalDto);
            System.out.println("🔍 결재 요청 수정 - 요청 본문: " + requestBody);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId);
            HttpRequest request = builder
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "결재 요청 수정");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                // System.out.println("✅ 결재 요청 수정 성공 - 응답 본문: " + responseBody);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, ApprovalDto.class);
                }
            } else {
                System.err.println("❌ 결재 요청 수정 실패 - 상태 코드: " + response.statusCode());
                // String responseBody = getSafeResponseBody(response);
                // System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 결재 요청 수정 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            handleChunkedTransferError(e, "결재 요청 수정");
        }
        return null;
    }

    /**
     * 결재 요청 생성
     * @param approvalDto 결재 요청 데이터
     * @return 생성된 결재 정보
     */
    public ApprovalDto createApproval(ApprovalDto approvalDto) {
        try {
            String requestBody = objectMapper.writeValueAsString(approvalDto);
            System.out.println("🔍 결재 요청 생성 - 요청 본문: " + requestBody);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/create");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "결재 요청 생성");

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                String responseBody = getSafeResponseBody(response);
                // System.out.println("✅ 결재 요청 생성 성공 - 응답 본문: " + responseBody);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode.has("data")) {
                        return objectMapper.treeToValue(jsonNode.get("data"), ApprovalDto.class);
                    }
                }
            } else {
                System.err.println("❌ 결재 요청 생성 실패 - 상태 코드: " + response.statusCode());
                // String responseBody = getSafeResponseBody(response);
                // System.err.println("❌ 오류 응답: " + responseBody);
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
            // 현재 로그인한 사용자 ID 가져오기
            Long approverId = getCurrentUserId();

            Map<String, Object> requestBody = Map.of("approverId", approverId);
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId + "/approve");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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
            Long approverId = getCurrentUserId();

            Map<String, Object> requestBody = Map.of(
                    "rejectionReason", rejectionReason,
                    "approverId", approverId
            );
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/" + approvalId + "/reject");
            HttpRequest request = builder
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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

    // 현재 로그인한 사용자 ID를 얻는 헬퍼 메서드
    private Long getCurrentUserId() {
        var currentUser = ApiClient.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUserId();
        } else {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }
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
            // logResponseInfo(response, "결재 상세 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                // Base64 인코딩된 첨부파일 내용은 로그에서 제외
                // String logResponseBody = responseBody;
                // if (responseBody != null && responseBody.contains("attachmentContent")) {
                //     try {
                //         JsonNode jsonNode = objectMapper.readTree(responseBody);
                //         if (jsonNode.has("attachmentContent") && jsonNode.get("attachmentContent").asText() != null) {
                //             String attachmentContent = jsonNode.get("attachmentContent").asText();
                //             if (!attachmentContent.isEmpty()) {
                //                 logResponseBody = responseBody.replace(attachmentContent, "[Base64 첨부파일 내용 생략]");
                //             }
                //         }
                //     } catch (Exception e) {
                //         // JSON 파싱 실패 시 원본 사용
                //         logResponseBody = responseBody;
                //     }
                // }
                // System.out.println("📋 상세 조회 응답 본문: " + logResponseBody);
                
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
                        // 직접 JSON 객체로 응답하는 경우 - 서버 응답 구조에 맞게 수동 파싱
                        result = new ApprovalDto();
                        result.setId(jsonNode.get("id").asLong());
                        result.setTitle(jsonNode.get("title").asText());
                        result.setContent(jsonNode.get("content").asText());
                        result.setStatus(jsonNode.get("status").asText());
                        
                        // 날짜 파싱
                        if (jsonNode.has("requestDate")) {
                            try {
                                result.setRequestDate(LocalDateTime.parse(jsonNode.get("requestDate").asText()));
                            } catch (Exception e) {
                                System.err.println("날짜 파싱 오류: " + e.getMessage());
                            }
                        }
                        
                        // 첨부파일 정보
                        if (jsonNode.has("attachmentFilename")) {
                            result.setAttachmentFilename(jsonNode.get("attachmentFilename").asText());
                        }
                        if (jsonNode.has("attachmentSize")) {
                            result.setAttachmentSize(jsonNode.get("attachmentSize").asLong());
                        }
                        if (jsonNode.has("attachmentContent")) {
                            result.setAttachmentContent(jsonNode.get("attachmentContent").asText());
                        }
                        
                        // 요청자 정보 파싱
                        if (jsonNode.has("requester") && !jsonNode.get("requester").isNull()) {
                            JsonNode requesterNode = jsonNode.get("requester");
                            UserDto requester = new UserDto();
                            requester.setUserId(requesterNode.get("userId").asLong());
                            requester.setUsername(requesterNode.get("username").asText());
                            // 서버 응답의 department 필드를 departmentName으로 매핑
                            if (requesterNode.has("department")) {
                                requester.setDepartmentName(requesterNode.get("department").asText());
                            }
                            result.setRequester(requester);
                        }
                        
                        // 승인자 정보 파싱
                        if (jsonNode.has("approver") && !jsonNode.get("approver").isNull()) {
                            JsonNode approverNode = jsonNode.get("approver");
                            UserDto approver = new UserDto();
                            approver.setUserId(approverNode.get("userId").asLong());
                            approver.setUsername(approverNode.get("username").asText());
                            // 서버 응답의 department 필드를 departmentName으로 매핑
                            if (approverNode.has("department")) {
                                approver.setDepartmentName(approverNode.get("department").asText());
                            }
                            result.setApprover(approver);
                        }
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
     * 내가 요청한 결재 목록 조회 (최적화된 버전)
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
            // logResponseInfo(response, "내가 요청한 결재 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // 직접 파싱하여 성능 최적화
                    List<ApprovalDto> results = objectMapper.readValue(responseBody, new TypeReference<List<ApprovalDto>>() {});
                    
                    // 성능 최적화: 불필요한 중첩 객체 참조 제거
                    for (ApprovalDto dto : results) {
                        // UserDto 참조를 최소화하여 메모리 사용량 감소
                        if (dto.getRequester() != null) {
                            // DepartmentDto 참조 제거
                            dto.getRequester().setDepartment(null);
                        }
                        if (dto.getApprover() != null) {
                            // DepartmentDto 참조 제거
                            dto.getApprover().setDepartment(null);
                        }
                    }
                    
                    return results;
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
            // logResponseInfo(response, "내가 요청한 결재 목록 조회 (페이지네이션)");

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
            // logResponseInfo(response, "내가 결재해야 할 목록 조회");

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
            // logResponseInfo(response, "내가 결재해야 할 목록 조회 (페이지네이션)");

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
            // logResponseInfo(response, "내가 결재해야 할 대기중인 목록 조회");

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
    public Map<String, Object> getMyPendingWithPagination(Integer departmentId, int page, int size, String sortBy, String sortDir) {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            String endpoint = String.format("/approvals/department/%d/pending?page=%d&size=%d&sortBy=%s&sortDir=desc",
                    departmentId, page, size, sortBy, sortDir);

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder(endpoint);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "내가 결재해야 할 대기중인 목록 조회 (페이지네이션)");

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
            // logResponseInfo(response, "모든 결재 목록 조회");

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
            System.out.println("🗑️ 결재 삭제 시도 - ID: " + approvalId);
            
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("❌ 현재 사용자 정보를 가져올 수 없습니다.");
                return false;
            }
            
            // API 문서에 따른 요청 본문 생성
            Map<String, Long> requestBody = Map.of("requesterId", currentUser.getUserId());
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            System.out.println("📝 삭제 요청 본문: " + jsonBody);
            
            // API 문서에 따른 올바른 엔드포인트 사용
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-request/" + approvalId);
            HttpRequest request = builder
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "결재 삭제");

            if (response.statusCode() == 200) {
                System.out.println("✅ 결재 삭제 성공 - ID: " + approvalId);
                return true;
            } else {
                System.err.println("❌ 결재 삭제 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ 결재 삭제 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
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
            // logResponseInfo(response, "결재 제목 검색");

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
     * 내가 요청한 결재 목록 조회 (간단한 버전 - 성능 최적화)
     * @return 내가 요청한 결재 목록
     */
    public List<ApprovalDto> getMyRequestsSimple() {
        try {
            // 현재 사용자 정보 가져오기
            var currentUser = ApiClient.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("현재 사용자 정보를 가져올 수 없습니다.");
                return null;
            }

            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/approvals/my-requests/" + currentUser.getUserId() + "/simple");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "내가 요청한 결재 목록 조회 (간단한 버전)");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // 간단한 Map 형태로 파싱
                    List<Map<String, Object>> simpleResponses = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
                    
                                         // Map을 ApprovalDto로 변환
                     List<ApprovalDto> results = new ArrayList<>();
                     for (Map<String, Object> simpleResponse : simpleResponses) {
                         ApprovalDto dto = new ApprovalDto();
                         
                         // 안전한 타입 변환
                         Object idObj = simpleResponse.get("id");
                         if (idObj != null) {
                             dto.setId(Long.valueOf(idObj.toString()));
                         }
                         
                         dto.setTitle((String) simpleResponse.get("title"));
                         dto.setContent((String) simpleResponse.get("content"));
                         dto.setStatus((String) simpleResponse.get("status"));
                        // 날짜 파싱 (서버에서 ISO 형식으로 반환됨)
                        String requestDateStr = (String) simpleResponse.get("requestDate");
                        if (requestDateStr != null) {
                            try {
                                dto.setRequestDate(LocalDateTime.parse(requestDateStr));
                            } catch (Exception e) {
                                System.err.println("날짜 파싱 오류: " + requestDateStr + " - " + e.getMessage());
                                // 기본값 설정
                                dto.setRequestDate(LocalDateTime.now());
                            }
                        }
                        
                                                 // 첨부파일 정보는 목록에서 제외 (상세보기에서만 확인)
                         // Object attachmentFilenameObj = simpleResponse.get("attachmentFilename");
                         // if (attachmentFilenameObj != null) {
                         //     dto.setAttachmentFilename(attachmentFilenameObj.toString());
                         // }
                         // 
                         // Object attachmentSizeObj = simpleResponse.get("attachmentSize");
                         // if (attachmentSizeObj != null) {
                         //     try {
                         //         dto.setAttachmentSize(Long.valueOf(attachmentSizeObj.toString()));
                         //     } catch (NumberFormatException e) {
                         //         System.err.println("첨부파일 크기 파싱 오류: " + attachmentSizeObj);
                         //     }
                         // }
                        
                                                 // 사용자 정보 (간단한 형태)
                         @SuppressWarnings("unchecked")
                         Map<String, Object> requesterInfo = (Map<String, Object>) simpleResponse.get("requester");
                         if (requesterInfo != null) {
                             UserDto requester = new UserDto();
                             String username = (String) requesterInfo.get("username");
                             String department = (String) requesterInfo.get("department");
                             
                             // 디버깅 정보 출력
                             System.out.println("🔍 클라이언트 부서 정보 확인:");
                             System.out.println("  - 사용자명: " + username);
                             System.out.println("  - 부서명: " + department);
                             
                             requester.setUsername(username);
                             requester.setDepartmentName(department);
                             dto.setRequester(requester);
                         }
                        
                        results.add(dto);
                    }
                    
                    return results;
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "내가 요청한 결재 목록 조회 (간단한 버전)");
        }
        return null;
    }

} 