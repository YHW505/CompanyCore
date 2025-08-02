package com.example.companycore.service;

import com.example.companycore.model.dto.LoginRequest;
import com.example.companycore.model.dto.LoginResponse;
import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.model.entity.User;
import com.example.companycore.model.entity.Task;
import com.example.companycore.model.entity.Attendance;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ApiClient {
    private static final String BASE_URL = "http://100.100.100.66:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    // 🔥 토큰 저장 필드 추가
    private String authToken;

    // 싱글톤 패턴 적용
    private static ApiClient instance;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // ObjectMapper 설정 개선
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        // 🔥 핵심 수정: 알 수 없는 속성 무시 설정
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 추가 권장 설정들
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        
        // Enum 처리 설정 추가
        this.objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
        this.objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {  // 스레드 안전성 개선
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }
    
    // 🔥 토큰 관리 메서드들 추가
    public String getAuthToken() {
        return authToken;
    }
    
    public boolean hasValidToken() {
        return authToken != null && !authToken.trim().isEmpty();
    }
    
    public void clearToken() {
        this.authToken = null;
        System.out.println("인증 토큰이 삭제되었습니다.");
    }
    
    // 🔥 JWT 토큰 분석 메서드
    private void analyzeToken(String token) {
        try {
            if (token != null && token.contains(".")) {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    // Base64 디코딩 (URL 안전한 Base64)
                    String payload = parts[1];
                    // URL 안전한 Base64 디코딩
                    String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(payload));
                    System.out.println("🔍 JWT 토큰 분석:");
                    System.out.println("   - 헤더: " + parts[0]);
                    System.out.println("   - 페이로드: " + decodedPayload);
                    System.out.println("   - 서명: " + parts[2].substring(0, Math.min(20, parts[2].length())) + "...");
                    
                    // 만료 시간 확인
                    if (decodedPayload.contains("exp")) {
                        System.out.println("   - 만료 시간 정보 포함됨");
                        // 현재 시간과 비교
                        long currentTime = System.currentTimeMillis() / 1000; // Unix timestamp
                        System.out.println("   - 현재 시간 (Unix): " + currentTime);
                        
                        // exp 값 추출 (더 안전한 방법)
                        if (decodedPayload.contains("\"exp\":")) {
                            try {
                                String expPart = decodedPayload.split("\"exp\":")[1];
                                // 쉼표나 중괄호로 끝나는 부분까지 추출
                                if (expPart.contains(",")) {
                                    expPart = expPart.split(",")[0];
                                } else if (expPart.contains("}")) {
                                    expPart = expPart.split("}")[0];
                                }
                                long expTime = Long.parseLong(expPart.trim());
                                System.out.println("   - 만료 시간 (Unix): " + expTime);
                                System.out.println("   - 남은 시간: " + (expTime - currentTime) + "초");
                                
                                if (expTime < currentTime) {
                                    System.out.println("   ⚠️ 토큰이 만료되었습니다!");
                                } else {
                                    System.out.println("   ✅ 토큰이 유효합니다.");
                                }
                            } catch (Exception e) {
                                System.out.println("   ⚠️ 만료 시간 파싱 오류: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("토큰 분석 중 오류: " + e.getMessage());
            // 간단한 토큰 정보만 출력
            System.out.println("🔍 토큰 길이: " + token.length());
            System.out.println("🔍 토큰 시작: " + token.substring(0, Math.min(50, token.length())) + "...");
        }
    }
    
    // 🔥 인증 헤더를 추가하는 헬퍼 메서드
    private HttpRequest.Builder createAuthenticatedRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");
        
        // 토큰이 있으면 Authorization 헤더 추가
        if (authToken != null && !authToken.trim().isEmpty()) {
            // 방법 1: Bearer 토큰 방식 (현재 사용 중)
            String authHeader = "Bearer " + authToken;
            builder.header("Authorization", authHeader);
            System.out.println("🔐 인증 헤더 추가 (Bearer): " + authHeader);
            System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
            
            // 방법 2: X-Auth-Token 헤더 사용 (서버가 다른 형식을 기대할 수 있음)
            // builder.header("X-Auth-Token", authToken);
            // System.out.println("🔐 인증 헤더 추가 (X-Auth-Token): " + authToken);
            
            // 방법 3: 토큰을 쿼리 파라미터로 전송 (일부 서버에서 사용)
            // String urlWithToken = BASE_URL + endpoint + "?token=" + authToken;
            // builder.uri(URI.create(urlWithToken));
            
            // 방법 4: 토큰을 쿠키로 전송 (일부 서버에서 사용)
            // builder.header("Cookie", "auth-token=" + authToken);
        } else {
            System.out.println("⚠️ 경고: 인증 토큰이 없습니다! (" + endpoint + ")");
        }
        
        return builder;
    }

    // 🔥 X-Auth-Token 헤더를 사용하는 대체 인증 메서드
    private HttpRequest.Builder createAuthenticatedRequestBuilderWithXAuthToken(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");
        
        // 토큰이 있으면 X-Auth-Token 헤더 추가
        if (authToken != null && !authToken.trim().isEmpty()) {
            builder.header("X-Auth-Token", authToken);
            System.out.println("🔐 인증 헤더 추가 (X-Auth-Token): " + authToken);
            System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
        } else {
            System.out.println("⚠️ 경고: 인증 토큰이 없습니다! (" + endpoint + ")");
        }
        
        return builder;
    }
    
    // 🔥 토큰을 쿼리 파라미터로 전송하는 대체 인증 메서드
    private HttpRequest.Builder createAuthenticatedRequestBuilderWithQueryToken(String endpoint) {
        String urlWithToken = BASE_URL + endpoint + "?token=" + authToken;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(urlWithToken))
                .header("Content-Type", "application/json");
        
        System.out.println("🔐 토큰을 쿼리 파라미터로 전송: " + urlWithToken);
        System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
        
        return builder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            String json = objectMapper.writeValueAsString(loginRequest);
            System.out.println("요청 JSON: " + json);
            System.out.println("요청 URL: " + BASE_URL + "/auth/login");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("응답 상태 코드: " + response.statusCode());
            System.out.println("응답 헤더: " + response.headers().map());
            System.out.println("응답 본문: '" + response.body() + "'");
            System.out.println("응답 본문 길이: " + response.body().length());

            if (response.statusCode() == 200) {
                // 응답이 비어있는지 확인
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 응답을 받았습니다!");
                    return null;
                }

                try {
                    // 🔥 JSON 파싱 시 더 자세한 로그 추가
                    System.out.println("JSON 파싱 시도 중...");
                    LoginResponse loginResponse = objectMapper.readValue(response.body(), LoginResponse.class);
                    System.out.println("JSON 파싱 성공!");
                    return loginResponse;

                } catch (Exception parseException) {
                    System.out.println("JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return null;
                }

            } else {
                System.out.println("로그인 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("로그인 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public boolean authenticate(String employeeCode, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(employeeCode, password);
            LoginResponse response = login(loginRequest);

            // 🚨 문제: token이 null이면 무조건 false 반환
            if (response != null && response.getToken() != null && !response.getToken().trim().isEmpty()) {
                this.authToken = response.getToken(); // 토큰 저장
                System.out.println("인증 성공! 토큰: " + this.authToken);
                analyzeToken(this.authToken); // 토큰 분석
                return true;
            } else {
                System.out.println("인증 실패 - 토큰이 없습니다.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 공지사항 목록을 서버에서 가져옴
     * 
     * @return 공지사항 목록
     */
    public List<NoticeItem> getNotices() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices").GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("공지사항 요청 상태 코드: " + response.statusCode());
            System.out.println("공지사항 응답: " + response.body());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 공지사항 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<NoticeItem>으로 파싱
                    List<NoticeItem> notices = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class)
                    );
                    System.out.println("공지사항 파싱 성공! 개수: " + notices.size());
                    return notices;

                } catch (Exception parseException) {
                    System.out.println("공지사항 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("공지사항 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("공지사항 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 사용자 목록을 서버에서 가져옴
     * 
     * @return 사용자 목록
     */
    public List<User> getUsers() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/users").GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("사용자 목록 요청 상태 코드: " + response.statusCode());
            System.out.println("사용자 목록 응답: " + response.body());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 사용자 목록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<User>로 파싱
                    List<User> users = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, User.class)
                    );
                    System.out.println("사용자 목록 파싱 성공! 개수: " + users.size());
                    return users;

                } catch (Exception parseException) {
                    System.out.println("사용자 목록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("사용자 목록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("사용자 목록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 현재 로그인된 사용자 정보를 가져옴
     * 
     * @return 현재 사용자 정보
     */
    public User getCurrentUser() {
        try {
            // 🔥 실제 API 호출 (서버 인증 문제 해결됨)
            HttpRequest request = createAuthenticatedRequestBuilder("/user/info").GET().build();

            System.out.println("🔍 현재 사용자 정보 요청 전송 중...");
            System.out.println("📋 요청 헤더: " + request.headers().map());
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("현재 사용자 정보 요청 상태 코드: " + response.statusCode());
            System.out.println("📋 응답 헤더: " + response.headers().map());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 사용자 정보 응답을 받았습니다!");
                    return null;
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data")) {
                        User user = objectMapper.treeToValue(jsonNode.get("data"), User.class);
                        System.out.println("현재 사용자 정보 파싱 성공!");
                        return user;
                    } else {
                        // data 필드가 없는 경우 직접 User로 파싱
                        User user = objectMapper.readValue(response.body(), User.class);
                        System.out.println("현재 사용자 정보 파싱 성공!");
                        return user;
                    }

                } catch (Exception parseException) {
                    System.out.println("현재 사용자 정보 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return null;
                }

            } else {
                System.out.println("현재 사용자 정보 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("현재 사용자 정보 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 사용자 정보 업데이트
     * 
     * @param user 업데이트할 사용자 정보
     * @return 성공 여부
     */
    public boolean updateUser(User user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/user/update"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("사용자 정보 업데이트 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("사용자 정보 업데이트 성공!");
                return true;
            } else {
                System.out.println("사용자 정보 업데이트 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("사용자 정보 업데이트 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 비밀번호 변경
     * 
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("currentPassword", currentPassword);
            requestBody.put("newPassword", newPassword);
            
            String json = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/user/change-password"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("비밀번호 변경 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("비밀번호 변경 성공!");
                return true;
            } else {
                System.out.println("비밀번호 변경 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("비밀번호 변경 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 사용자 생성
     * 
     * @param user 생성할 사용자 정보
     * @return 생성된 사용자 정보
     */
    public User createUser(User user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("사용자 생성 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                try {
                    User createdUser = objectMapper.readValue(response.body(), User.class);
                    System.out.println("사용자 생성 성공!");
                    return createdUser;
                } catch (Exception parseException) {
                    System.out.println("사용자 생성 응답 파싱 실패: " + parseException.getMessage());
                    return null;
                }
            } else {
                System.out.println("사용자 생성 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("사용자 생성 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== TASK API METHODS ====================
    
    /**
     * 모든 작업 목록을 서버에서 가져옴
     * 
     * @return 작업 목록
     */
    public List<Task> getTasks() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tasks"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("작업 목록 요청 상태 코드: " + response.statusCode());
            System.out.println("작업 목록 응답: " + response.body());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 작업 목록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Task>로 파싱
                    List<Task> tasks = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                    );
                    System.out.println("작업 목록 파싱 성공! 개수: " + tasks.size());
                    return tasks;

                } catch (Exception parseException) {
                    System.out.println("작업 목록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("작업 목록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 특정 사용자에게 할당된 작업 목록을 가져옴
     * 
     * @param userId 사용자 ID
     * @return 할당된 작업 목록
     */
    public List<Task> getTasksAssignedToUser(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tasks/assigned-to/" + userId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("할당된 작업 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 할당된 작업 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("할당된 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else {
                        // data 필드가 없는 경우 직접 List<Task>로 파싱
                        List<Task> tasks = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("할당된 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    }

                } catch (Exception parseException) {
                    System.out.println("할당된 작업 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("할당된 작업 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("할당된 작업 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 상태별 작업 목록을 가져옴
     * 
     * @param status 작업 상태
     * @return 상태별 작업 목록
     */
    public List<Task> getTasksByStatus(String status) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tasks/status/" + status))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("상태별 작업 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 상태별 작업 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("상태별 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else {
                        // data 필드가 없는 경우 직접 List<Task>로 파싱
                        List<Task> tasks = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("상태별 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    }

                } catch (Exception parseException) {
                    System.out.println("상태별 작업 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("상태별 작업 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("상태별 작업 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 작업 타입별 작업 목록을 가져옴
     * 
     * @param taskType 작업 타입
     * @return 타입별 작업 목록
     */
    public List<Task> getTasksByType(String taskType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tasks/type/" + taskType))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("타입별 작업 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 타입별 작업 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("타입별 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else {
                        // data 필드가 없는 경우 직접 List<Task>로 파싱
                        List<Task> tasks = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("타입별 작업 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    }

                } catch (Exception parseException) {
                    System.out.println("타입별 작업 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("타입별 작업 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("타입별 작업 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 키워드로 작업을 검색
     * 
     * @param keyword 검색 키워드
     * @param searchIn 검색 범위 (TITLE, DESCRIPTION, BOTH)
     * @return 검색된 작업 목록
     */
    public List<Task> searchTasks(String keyword, String searchIn) {
        try {
            String url = BASE_URL + "/tasks/search?keyword=" + keyword;
            if (searchIn != null && !searchIn.isEmpty()) {
                url += "&searchIn=" + searchIn;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("작업 검색 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 작업 검색 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("작업 검색 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else {
                        // data 필드가 없는 경우 직접 List<Task>로 파싱
                        List<Task> tasks = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("작업 검색 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    }

                } catch (Exception parseException) {
                    System.out.println("작업 검색 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("작업 검색 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("작업 검색 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 복합 조건으로 작업을 필터링
     * 
     * @param assignedTo 할당받은 사용자 ID
     * @param assignedBy 할당한 사용자 ID
     * @param status 작업 상태
     * @param taskType 작업 타입
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sortBy 정렬 기준
     * @param sortDir 정렬 방향
     * @return 필터링된 작업 목록
     */
    public List<Task> filterTasks(Long assignedTo, Long assignedBy, String status, String taskType, 
                                 String startDate, String endDate, Integer page, Integer size, 
                                 String sortBy, String sortDir) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/tasks/filter?");
            
            if (assignedTo != null) urlBuilder.append("assignedTo=").append(assignedTo).append("&");
            if (assignedBy != null) urlBuilder.append("assignedBy=").append(assignedBy).append("&");
            if (status != null) urlBuilder.append("status=").append(status).append("&");
            if (taskType != null) urlBuilder.append("taskType=").append(taskType).append("&");
            if (startDate != null) urlBuilder.append("startDate=").append(startDate).append("&");
            if (endDate != null) urlBuilder.append("endDate=").append(endDate).append("&");
            if (page != null) urlBuilder.append("page=").append(page).append("&");
            if (size != null) urlBuilder.append("size=").append(size).append("&");
            if (sortBy != null) urlBuilder.append("sortBy=").append(sortBy).append("&");
            if (sortDir != null) urlBuilder.append("sortDir=").append(sortDir).append("&");
            
            // 마지막 & 제거
            String url = urlBuilder.toString();
            if (url.endsWith("&")) {
                url = url.substring(0, url.length() - 1);
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("작업 필터링 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 작업 필터링 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // API 응답 구조에 맞춰 파싱
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    if (jsonNode.has("data") && jsonNode.get("data").has("content")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").get("content").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("작업 필터링 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else if (jsonNode.has("data")) {
                        List<Task> tasks = objectMapper.readValue(
                            jsonNode.get("data").toString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("작업 필터링 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    } else {
                        // data 필드가 없는 경우 직접 List<Task>로 파싱
                        List<Task> tasks = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class)
                        );
                        System.out.println("작업 필터링 파싱 성공! 개수: " + tasks.size());
                        return tasks;
                    }

                } catch (Exception parseException) {
                    System.out.println("작업 필터링 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("작업 필터링 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("작업 필터링 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ==================== ATTENDANCE API METHODS ====================
    
    /**
     * 출근 처리
     * 
     * @param userId 사용자 ID
     * @return 성공 여부
     */
    public boolean checkIn(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/check-in?userId=" + userId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("출근 처리 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("출근 처리 성공!");
                return true;
            } else {
                System.out.println("출근 처리 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("출근 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 퇴근 처리
     * 
     * @param userId 사용자 ID
     * @return 성공 여부
     */
    public boolean checkOut(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/check-out?userId=" + userId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("퇴근 처리 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("퇴근 처리 성공!");
                return true;
            } else {
                System.out.println("퇴근 처리 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("퇴근 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 사용자별 출근 기록 조회
     * 
     * @param userId 사용자 ID
     * @return 출근 기록 목록
     */
    public List<Attendance> getUserAttendance(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/user/" + userId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("사용자 출근 기록 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 출근 기록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Attendance>로 파싱
                    List<Attendance> attendances = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class)
                    );
                    System.out.println("사용자 출근 기록 파싱 성공! 개수: " + attendances.size());
                    return attendances;

                } catch (Exception parseException) {
                    System.out.println("사용자 출근 기록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("사용자 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("사용자 출근 기록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 날짜 범위별 출근 기록 조회
     * 
     * @param userId 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 출근 기록 목록
     */
    public List<Attendance> getUserAttendanceByDateRange(Long userId, String startDate, String endDate) {
        try {
            String url = BASE_URL + "/attendance/user/" + userId + "/date-range?startDate=" + startDate + "&endDate=" + endDate;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("날짜 범위 출근 기록 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 날짜 범위 출근 기록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Attendance>로 파싱
                    List<Attendance> attendances = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class)
                    );
                    System.out.println("날짜 범위 출근 기록 파싱 성공! 개수: " + attendances.size());
                    return attendances;

                } catch (Exception parseException) {
                    System.out.println("날짜 범위 출근 기록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("날짜 범위 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("날짜 범위 출근 기록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 특정 날짜의 모든 출근 기록 조회
     * 
     * @param workDate 작업 날짜 (YYYY-MM-DD)
     * @return 출근 기록 목록
     */
    public List<Attendance> getAttendanceByDate(String workDate) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/date/" + workDate))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("날짜별 출근 기록 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 날짜별 출근 기록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Attendance>로 파싱
                    List<Attendance> attendances = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class)
                    );
                    System.out.println("날짜별 출근 기록 파싱 성공! 개수: " + attendances.size());
                    return attendances;

                } catch (Exception parseException) {
                    System.out.println("날짜별 출근 기록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("날짜별 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("날짜별 출근 기록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 오늘의 출근 현황 대시보드 조회
     * 
     * @return 대시보드 데이터 (JSON 형태로 반환)
     */
    public String getTodayDashboard() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/today/dashboard"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("오늘 대시보드 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("오늘 대시보드 조회 성공!");
                return response.body();
            } else {
                System.out.println("오늘 대시보드 조회 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("오늘 대시보드 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 특정 사용자의 출근 통계 조회
     * 
     * @param userId 사용자 ID
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @return 통계 데이터 (JSON 형태로 반환)
     */
    public String getUserAttendanceStats(Long userId, String startDate, String endDate) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/attendance/user/" + userId + "/stats");
            if (startDate != null && endDate != null) {
                urlBuilder.append("?startDate=").append(startDate).append("&endDate=").append(endDate);
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("사용자 출근 통계 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("사용자 출근 통계 조회 성공!");
                return response.body();
            } else {
                System.out.println("사용자 출근 통계 조회 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("사용자 출근 통계 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 월별 출근 통계 조회
     * 
     * @param userId 사용자 ID
     * @param year 년도
     * @param month 월
     * @return 통계 데이터 (JSON 형태로 반환)
     */
    public String getMonthlyAttendanceStats(Long userId, int year, int month) {
        try {
            String url = BASE_URL + "/attendance/monthly-stats?userId=" + userId + "&year=" + year + "&month=" + month;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("월별 출근 통계 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("월별 출근 통계 조회 성공!");
                return response.body();
            } else {
                System.out.println("월별 출근 통계 조회 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("월별 출근 통계 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 상태별 출근 기록 조회
     * 
     * @param status 상태 (PRESENT/LATE)
     * @param date 날짜 (선택)
     * @param userId 사용자 ID (선택)
     * @return 출근 기록 목록
     */
    public List<Attendance> getAttendanceByStatus(String status, String date, Long userId) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/attendance/status/" + status);
            if (date != null || userId != null) {
                urlBuilder.append("?");
                if (date != null) urlBuilder.append("date=").append(date);
                if (userId != null) {
                    if (date != null) urlBuilder.append("&");
                    urlBuilder.append("userId=").append(userId);
                }
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("상태별 출근 기록 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 상태별 출근 기록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Attendance>로 파싱
                    List<Attendance> attendances = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class)
                    );
                    System.out.println("상태별 출근 기록 파싱 성공! 개수: " + attendances.size());
                    return attendances;

                } catch (Exception parseException) {
                    System.out.println("상태별 출근 기록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("상태별 출근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("상태별 출근 기록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 미퇴근 기록 조회
     * 
     * @param userId 사용자 ID (선택)
     * @return 미퇴근 기록 목록
     */
    public List<Attendance> getNotCheckedOutAttendance(Long userId) {
        try {
            String url = BASE_URL + "/attendance/not-checked-out";
            if (userId != null) {
                url += "?userId=" + userId;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("미퇴근 기록 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 미퇴근 기록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    // JSON 배열을 List<Attendance>로 파싱
                    List<Attendance> attendances = objectMapper.readValue(
                        response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Attendance.class)
                    );
                    System.out.println("미퇴근 기록 파싱 성공! 개수: " + attendances.size());
                    return attendances;

                } catch (Exception parseException) {
                    System.out.println("미퇴근 기록 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return new ArrayList<>();
                }

            } else {
                System.out.println("미퇴근 기록 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("미퇴근 기록 요청 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 출근 기록 생성 (관리자용)
     * 
     * @param attendance 출근 기록 객체
     * @return 생성된 출근 기록
     */
    public Attendance createAttendance(Attendance attendance) {
        try {
            String json = objectMapper.writeValueAsString(attendance);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("출근 기록 생성 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                try {
                    Attendance createdAttendance = objectMapper.readValue(response.body(), Attendance.class);
                    System.out.println("출근 기록 생성 성공!");
                    return createdAttendance;
                } catch (Exception parseException) {
                    System.out.println("출근 기록 생성 응답 파싱 실패: " + parseException.getMessage());
                    return null;
                }
            } else {
                System.out.println("출근 기록 생성 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("출근 기록 생성 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 출근 기록 수정 (관리자용)
     * 
     * @param attendanceId 출근 기록 ID
     * @param attendance 수정할 출근 기록 객체
     * @return 성공 여부
     */
    public boolean updateAttendance(Long attendanceId, Attendance attendance) {
        try {
            String json = objectMapper.writeValueAsString(attendance);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/" + attendanceId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("출근 기록 수정 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("출근 기록 수정 성공!");
                return true;
            } else {
                System.out.println("출근 기록 수정 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("출근 기록 수정 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 출근 기록 삭제 (관리자용)
     * 
     * @param attendanceId 출근 기록 ID
     * @return 성공 여부
     */
    public boolean deleteAttendance(Long attendanceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/" + attendanceId))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("출근 기록 삭제 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("출근 기록 삭제 성공!");
                return true;
            } else {
                System.out.println("출근 기록 삭제 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return false;
            }

        } catch (Exception e) {
            System.out.println("출근 기록 삭제 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * ID로 출근 기록 조회
     * 
     * @param attendanceId 출근 기록 ID
     * @return 출근 기록
     */
    public Attendance getAttendanceById(Long attendanceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/attendance/" + attendanceId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("출근 기록 조회 요청 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 출근 기록 응답을 받았습니다!");
                    return null;
                }

                try {
                    Attendance attendance = objectMapper.readValue(response.body(), Attendance.class);
                    System.out.println("출근 기록 조회 성공!");
                    return attendance;

                } catch (Exception parseException) {
                    System.out.println("출근 기록 조회 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    parseException.printStackTrace();
                    return null;
                }

            } else {
                System.out.println("출근 기록 조회 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }

        } catch (Exception e) {
            System.out.println("출근 기록 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
