package com.example.companycore.service;

import com.example.companycore.model.entity.Attendance;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 출근 관련 API 클라이언트
 * 출근/퇴근, 출근 기록 조회, 통계 등
 */
public class AttendanceApiClient extends BaseApiClient {
    private static AttendanceApiClient instance;

    private AttendanceApiClient() {
        super();
    }

    public static AttendanceApiClient getInstance() {
        if (instance == null) {
            synchronized (AttendanceApiClient.class) {
                if (instance == null) {
                    instance = new AttendanceApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 출근 체크인을 수행합니다.
     */
    public boolean checkIn(Long userId) {
        try {
            String endpoint = "/attendance/check-in?userId=" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "출근 체크인");

            if (response.statusCode() == 200) {
                System.out.println("출근 체크인 성공!");
                return true;
            } else {
                System.out.println("출근 체크인 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return false;
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "출근 체크인");
            return false;
        }
    }

    /**
     * 퇴근 체크아웃을 수행합니다.
     */
    public boolean checkOut(Long userId) {
        try {
            String endpoint = "/attendance/check-out?userId=" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "퇴근 체크아웃");

            if (response.statusCode() == 200) {
                System.out.println("퇴근 체크아웃 성공!");
                return true;
            } else {
                System.out.println("퇴근 체크아웃 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return false;
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "퇴근 체크아웃");
            return false;
        }
    }

    /**
     * 사용자의 출근 기록을 가져옵니다.
     */
    public List<Attendance> getUserAttendance(Long userId) {
        try {
            String endpoint = "/attendance/user/" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "사용자 출근 기록 요청");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Attendance> attendances = objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("사용자 출근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("사용자 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return new ArrayList<>();
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "사용자 출근 기록 요청");
            return new ArrayList<>();
        }
    }

    /**
     * 사용자의 특정 기간 출근 기록을 가져옵니다.
     */
    public List<Attendance> getUserAttendanceByDateRange(Long userId, String startDate, String endDate) {
        try {
            String endpoint = "/attendance/user/" + userId + "/range?startDate=" + startDate + "&endDate=" + endDate;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "기간별 출근 기록 요청");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Attendance> attendances = objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("기간별 출근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("기간별 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return new ArrayList<>();
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "기간별 출근 기록 요청");
            return new ArrayList<>();
        }
    }

    /**
     * 특정 날짜의 출근 기록을 가져옵니다.
     */
    public List<Attendance> getAttendanceByDate(String workDate) {
        try {
            String endpoint = "/attendance/date/" + workDate;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Attendance> attendances = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("날짜별 출근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("날짜별 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("날짜별 출근 기록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 오늘의 대시보드 정보를 가져옵니다.
     */
    public String getTodayDashboard() {
        try {
            String endpoint = "/attendance/dashboard/today";
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("오늘 대시보드 요청 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("오늘 대시보드 요청 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 사용자의 출근 통계를 가져옵니다.
     */
    public String getUserAttendanceStats(Long userId, String startDate, String endDate) {
        try {
            String endpoint = "/attendance/stats/user/" + userId + "?startDate=" + startDate + "&endDate=" + endDate;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("사용자 출근 통계 요청 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("사용자 출근 통계 요청 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 월별 출근 통계를 가져옵니다.
     */
    public String getMonthlyAttendanceStats(Long userId, int year, int month) {
        try {
            String endpoint = "/attendance/stats/monthly/" + userId + "?year=" + year + "&month=" + month;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("월별 출근 통계 요청 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("월별 출근 통계 요청 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 상태별 출근 기록을 가져옵니다.
     */
    public List<Attendance> getAttendanceByStatus(String status, String date, Long userId) {
        try {
            StringBuilder endpoint = new StringBuilder("/attendance/status/" + status);
            if (date != null) endpoint.append("?date=").append(date);
            if (userId != null) endpoint.append(endpoint.toString().contains("?") ? "&" : "?").append("userId=").append(userId);

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint.toString())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Attendance> attendances = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("상태별 출근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("상태별 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("상태별 출근 기록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 퇴근하지 않은 출근 기록을 가져옵니다.
     */
    public List<Attendance> getNotCheckedOutAttendance(Long userId) {
        try {
            String endpoint = "/attendance/not-checked-out/" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Attendance> attendances = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("미퇴근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("미퇴근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("미퇴근 기록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 새 출근 기록을 생성합니다.
     */
    public Attendance createAttendance(Attendance attendance) {
        try {
            String json = objectMapper.writeValueAsString(attendance);
            HttpRequest request = createAuthenticatedRequestBuilder("/attendance")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                try {
                    Attendance createdAttendance = objectMapper.readValue(response.body(), Attendance.class);
                    return createdAttendance;
                } catch (Exception e) {
                    System.out.println("생성된 출근 기록 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("출근 기록 생성 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("출근 기록 생성 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 출근 기록을 업데이트합니다.
     */
    public boolean updateAttendance(Long attendanceId, Attendance attendance) {
        try {
            String json = objectMapper.writeValueAsString(attendance);
            String endpoint = "/attendance/" + attendanceId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println("출근 기록 업데이트 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 출근 기록을 삭제합니다.
     */
    public boolean deleteAttendance(Long attendanceId) {
        try {
            String endpoint = "/attendance/" + attendanceId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            System.out.println("출근 기록 삭제 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 특정 출근 기록을 가져옵니다.
     */
    public Attendance getAttendanceById(Long attendanceId) {
        try {
            String endpoint = "/attendance/" + attendanceId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    Attendance attendance = objectMapper.readValue(response.body(), Attendance.class);
                    return attendance;
                } catch (Exception e) {
                    System.out.println("출근 기록 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("출근 기록 조회 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("출근 기록 조회 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 최근 출근 기록 5개를 가져옵니다.
     */
    public List<Attendance> getRecentAttendance(Long userId) {
        try {
            String endpoint = "/attendance/recent?userId=" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "최근 출근 기록 요청");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    // "data" 필드에서 출근 기록 리스트를 추출
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    JsonNode dataNode = rootNode.path("data");

                    List<Attendance> attendances = objectMapper.readValue(dataNode.toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class));
                    return attendances;
                } catch (Exception e) {
                    System.out.println("최근 출근 기록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("최근 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return new ArrayList<>();
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "최근 출근 기록 요청");
            return new ArrayList<>();
        }
    }
} 