package com.example.companycore.service;

import com.example.companycore.model.dto.LeaveRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 휴가 관련 API 클라이언트
 * 휴가 신청, 조회, 승인/반려, 수정/삭제 등
 */
public class LeaveApiClient extends BaseApiClient {
    private static LeaveApiClient instance;

    private LeaveApiClient() {
        super();
    }

    public static LeaveApiClient getInstance() {
        if (instance == null) {
            synchronized (LeaveApiClient.class) {
                if (instance == null) {
                    instance = new LeaveApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 새로운 휴가 신청을 생성합니다.
     */
    public LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequest) {
        try {
            // 날짜를 문자열로 변환하여 전송
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("userId", leaveRequest.getUserId());
            requestBody.put("leaveType", leaveRequest.getLeaveType());
            requestBody.put("startDate", leaveRequest.getStartDate() != null ? leaveRequest.getStartDate().toString() : null);
            requestBody.put("endDate", leaveRequest.getEndDate() != null ? leaveRequest.getEndDate().toString() : null);
            requestBody.put("reason", leaveRequest.getReason());
            requestBody.put("status", leaveRequest.getStatus());
            
            String json = objectMapper.writeValueAsString(requestBody);
            System.out.println("전송할 JSON: " + json);
            
            HttpRequest request = createAuthenticatedRequestBuilder("/leave-requests")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                try {
                    LeaveRequestDto createdLeaveRequest = objectMapper.readValue(response.body(), LeaveRequestDto.class);
                    System.out.println("휴가 신청 생성 성공!");
                    return createdLeaveRequest;
                } catch (Exception e) {
                    System.out.println("생성된 휴가 신청 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("휴가 신청 생성 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 생성 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 모든 휴가 신청 목록을 가져옵니다.
     */
    public List<LeaveRequestDto> getAllLeaveRequests() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/leave-requests")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<LeaveRequestDto> leaveRequests = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, LeaveRequestDto.class));
                    return leaveRequests;
                } catch (Exception e) {
                    System.out.println("휴가 신청 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("휴가 신청 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 특정 ID의 휴가 신청을 가져옵니다.
     */
    public LeaveRequestDto getLeaveRequestById(Long leaveId) {
        try {
            String endpoint = "/leave-requests/" + leaveId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    LeaveRequestDto leaveRequest = objectMapper.readValue(response.body(), LeaveRequestDto.class);
                    return leaveRequest;
                } catch (Exception e) {
                    System.out.println("휴가 신청 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("휴가 신청 조회 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 조회 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 특정 사용자의 휴가 신청 목록을 가져옵니다.
     */
    public List<LeaveRequestDto> getLeaveRequestsByUserId(Long userId) {
        try {
            String endpoint = "/leave-requests?userId=" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<LeaveRequestDto> leaveRequests = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, LeaveRequestDto.class));
                    return leaveRequests;
                } catch (Exception e) {
                    System.out.println("사용자 휴가 신청 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("사용자 휴가 신청 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("사용자 휴가 신청 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 휴가 신청을 승인합니다.
     */
    public boolean approveLeaveRequest(Long leaveId, Long approverId) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("approverId", approverId);  // 서버가 기대하는 파라미터명으로 변경

            String json = objectMapper.writeValueAsString(requestBody);
            String endpoint = "/leave-requests/" + leaveId + "/approve";
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("휴가 신청 승인 성공!");
                return true;
            } else {
                System.out.println("휴가 신청 승인 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 승인 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 휴가 신청을 반려합니다.
     */
    public boolean rejectLeaveRequest(Long leaveId, Long rejectedBy, String rejectionReason) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("rejectedBy", rejectedBy);  // 서버와 일치
            requestBody.put("rejectionReason", rejectionReason);  // 서버와 일치

            String json = objectMapper.writeValueAsString(requestBody);
            String endpoint = "/leave-requests/" + leaveId + "/reject";
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("휴가 신청 반려 성공!");
                return true;
            } else {
                System.out.println("휴가 신청 반려 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 반려 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 휴가 신청을 취소합니다.
     */
    public boolean cancelLeaveRequest(Long leaveId, Long userId) {
        try {
            String endpoint = "/leave-requests/" + leaveId + "?userId=" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("휴가 신청 취소 성공!");
                return true;
            } else {
                System.out.println("휴가 신청 취소 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 취소 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 휴가 신청을 수정합니다.
     */
    public boolean updateLeaveRequest(Long leaveId, LeaveRequestDto leaveRequest) {
        try {
            String json = objectMapper.writeValueAsString(leaveRequest);
            String endpoint = "/leave-requests/" + leaveId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("휴가 신청 수정 성공!");
                return true;
            } else {
                System.out.println("휴가 신청 수정 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 수정 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 다양한 조건으로 휴가 신청을 검색합니다.
     */
    public List<LeaveRequestDto> searchLeaveRequests(String status, String type, Long userId, 
                                                   String startDate, String endDate) {
        try {
            StringBuilder endpoint = new StringBuilder("/leave-requests/search?");
            
            if (status != null) endpoint.append("status=").append(status).append("&");
            if (type != null) endpoint.append("type=").append(type).append("&");
            if (userId != null) endpoint.append("userId=").append(userId).append("&");
            if (startDate != null) endpoint.append("startDate=").append(startDate).append("&");
            if (endDate != null) endpoint.append("endDate=").append(endDate).append("&");

            // 마지막 & 제거
            if (endpoint.charAt(endpoint.length() - 1) == '&') {
                endpoint.setLength(endpoint.length() - 1);
            }

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<LeaveRequestDto> leaveRequests = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, LeaveRequestDto.class));
                    return leaveRequests;
                } catch (Exception e) {
                    System.out.println("휴가 신청 검색 결과 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("휴가 신청 검색 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("휴가 신청 검색 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 휴가 신청을 삭제합니다.
     */
    public boolean deleteLeaveRequest(Long leaveId) {
        try {
            String endpoint = "/leave-requests/" + leaveId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            System.out.println("휴가 신청 삭제 중 예외 발생: " + e.getMessage());
            return false;
        }
    }
} 