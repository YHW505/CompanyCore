package com.example.companycore.service;

import com.example.companycore.model.dto.LoginRequest;
import com.example.companycore.model.dto.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * 모든 API 클라이언트의 기본 클래스
 * 공통 기능: 인증, HTTP 클라이언트, ObjectMapper 설정
 */
public abstract class BaseApiClient {
    protected static final String BASE_URL = "http://100.100.100.66:8083/api";
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected String authToken;

    protected BaseApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)  // HTTP 버전 명시
                .build();

        // ObjectMapper 설정
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
        this.objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
    }

    // 토큰 관리 메서드들
    public String getAuthToken() {
        return authToken;
    }

    public boolean hasValidToken() {
        return authToken != null && !authToken.trim().isEmpty();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearToken() {
        this.authToken = null;
        System.out.println("인증 토큰이 삭제되었습니다.");
    }

    // JWT 토큰 분석 메서드
    protected void analyzeToken(String token) {
        try {
            if (token != null && token.contains(".")) {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = parts[1];
                    // Base64 디코딩 (패딩 추가)
                    while (payload.length() % 4 != 0) {
                        payload += "=";
                    }
                    String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
                    
                    System.out.println("🔍 JWT 토큰 분석:");
                    System.out.println("   - 헤더: " + parts[0]);
                    System.out.println("   - 페이로드: " + decodedPayload);
                    System.out.println("   - 서명: " + parts[2].substring(0, Math.min(20, parts[2].length())) + "...");

                    // 만료 시간 확인
                    if (decodedPayload.contains("\"exp\":")) {
                        System.out.println("   - 만료 시간 정보 포함됨");
                        
                        // JSON 파싱하여 만료 시간 추출
                        Map<String, Object> payloadMap = objectMapper.readValue(decodedPayload, Map.class);
                        Long expTime = (Long) payloadMap.get("exp");
                        Long currentTime = System.currentTimeMillis() / 1000;
                        
                        System.out.println("   - 현재 시간 (Unix): " + currentTime);
                        if (expTime != null) {
                            System.out.println("   - 만료 시간 (Unix): " + expTime);
                            System.out.println("   - 남은 시간: " + (expTime - currentTime) + "초");
                            
                            if (currentTime >= expTime) {
                                System.out.println("   ⚠️ 토큰이 만료되었습니다!");
                            } else {
                                System.out.println("   ✅ 토큰이 유효합니다.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("   ⚠️ 만료 시간 파싱 오류: " + e.getMessage());
        }
    }

    // 안전한 HTTP 응답 처리 유틸리티 메서드
    protected String getSafeResponseBody(HttpResponse<String> response) {
        String responseBody = response.body();
        if (responseBody == null) {
            System.out.println("응답 본문이 null입니다.");
            return "";
        }
        return responseBody;
    }

    // HTTP 응답 상태 확인 및 로깅
    protected void logResponseInfo(HttpResponse<String> response, String operation) {
        System.out.println(operation + " 상태 코드: " + response.statusCode());
        System.out.println(operation + " 응답 헤더: " + response.headers().map());
        
        String responseBody = getSafeResponseBody(response);
        if (!responseBody.isEmpty()) {
            System.out.println(operation + " 응답 본문 길이: " + responseBody.length());
            // 응답 본문을 출력하는걸 없애서 pdf의 인코딩 문자가 로그에 안뜨게 함.
//            System.out.println(operation + " 응답 본문: '" + responseBody + "'");
        } else {
            System.out.println(operation + " 응답 본문이 비어있습니다.");
        }
    }

    // Chunked transfer encoding 오류 처리
    protected void handleChunkedTransferError(Exception e, String operation) {
        System.out.println(operation + " 중 예외 발생: " + e.getMessage());
        System.out.println("예외 타입: " + e.getClass().getSimpleName());
        if (e.getMessage() != null && e.getMessage().contains("chunked")) {
            System.out.println("⚠️ Chunked transfer encoding 오류가 발생했습니다.");
            System.out.println("이는 서버 응답 처리 중 발생하는 문제일 수 있습니다.");
            System.out.println("서버 연결 상태를 확인해주세요.");
        }
        e.printStackTrace();
    }

    // 인증된 요청 빌더 생성
    protected HttpRequest.Builder createAuthenticatedRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");  // Accept 헤더 추가

        if (hasValidToken()) {
            String authHeader = "Bearer " + authToken;
            builder.header("Authorization", authHeader);
            System.out.println("🔐 인증 헤더 추가 (Bearer): " + authHeader);
            System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
        } else {
            System.out.println("⚠️ 경고: 인증 토큰이 없습니다! (" + endpoint + ")");
        }

        return builder;
    }

    protected HttpRequest.Builder createAuthenticatedRequestBuilderWithXAuthToken(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");  // Accept 헤더 추가

        if (hasValidToken()) {
            builder.header("X-Auth-Token", authToken);
            System.out.println("🔐 인증 헤더 추가 (X-Auth-Token): " + authToken);
            System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
        } else {
            System.out.println("⚠️ 경고: 인증 토큰이 없습니다! (" + endpoint + ")");
        }

        return builder;
    }

    protected HttpRequest.Builder createAuthenticatedRequestBuilderWithQueryToken(String endpoint) {
        String urlWithToken = BASE_URL + endpoint + "?token=" + authToken;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(urlWithToken))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");  // Accept 헤더 추가

        if (hasValidToken()) {
            System.out.println("🔐 토큰을 쿼리 파라미터로 전송: " + urlWithToken);
            System.out.println("📡 API 요청: " + endpoint + " (토큰 길이: " + authToken.length() + ")");
        } else {
            System.out.println("⚠️ 경고: 인증 토큰이 없습니다! (" + endpoint + ")");
        }

        return builder;
    }

    // 로그인 메서드 (공통)
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            String json = objectMapper.writeValueAsString(loginRequest);
            System.out.println("요청 JSON: " + json);
            System.out.println("요청 URL: " + BASE_URL + "/auth/login");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")  // Accept 헤더 추가
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "로그인");
            
            // 응답 본문 안전하게 읽기
            String responseBody = getSafeResponseBody(response);

            if (response.statusCode() == 200) {
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    System.out.println("서버에서 빈 응답을 받았습니다!");
                    return null;
                }

                try {
                    System.out.println("JSON 파싱 시도 중...");
                    System.out.println("파싱할 JSON: " + responseBody);
                    
                    // 먼저 JsonNode로 파싱해서 구조 확인
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    System.out.println("JSON 구조: " + jsonNode.toString());
                    
                    LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
                    System.out.println("JSON 파싱 성공!");
                    System.out.println("파싱된 응답: " + loginResponse);
                    return loginResponse;
                } catch (Exception parseException) {
                    System.out.println("JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + responseBody);
                    parseException.printStackTrace();
                    return null;
                }
            } else {
                System.out.println("로그인 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + responseBody);
                return null;
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "로그인");
            return null;
        }
    }

    // 인증 메서드 (공통)
    public boolean authenticate(String employeeCode, String password) {
        System.out.println("=== 인증 시작 ===");
        System.out.println("직원코드: " + employeeCode);
        System.out.println("패스워드: [" + password + "]");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmployeeCode(employeeCode);
        loginRequest.setPassword(password);

        LoginResponse response = login(loginRequest);

        if (response == null) {
            System.out.println("❌ 로그인 응답이 null입니다.");
            return false;
        }

        System.out.println("📋 로그인 응답 분석:");
        System.out.println("- 토큰: " + (response.getToken() != null ? "있음 (" + response.getToken().length() + "자)" : "없음"));
        System.out.println("- 사용자명: " + response.getUsername());
        System.out.println("- 직원코드: " + response.getEmployeeCode());
        System.out.println("- 역할: " + response.getRole());
        System.out.println("- 첫 로그인: " + response.getIsFirstLogin());

        // 🚨 문제: token이 null이면 무조건 false 반환
        if (response.getToken() != null && !response.getToken().trim().isEmpty()) {
            this.authToken = response.getToken(); // 토큰 저장
            System.out.println("✅ 인증 성공! 토큰: " + this.authToken);
            System.out.println("사용자 정보: " + response.getUsername() + " (" + response.getEmployeeCode() + ")");
            System.out.println("역할: " + response.getRole());
            System.out.println("첫 로그인: " + response.getIsFirstLogin());
            analyzeToken(this.authToken); // 토큰 분석
            return true;
        } else {
            System.out.println("❌ 인증 실패 - 토큰이 없습니다.");
            System.out.println("응답 전체: " + response);
            return false;
        }
    }
} 