package com.example.companycore.service;

import com.example.companycore.model.dto.LoginRequest;
import com.example.companycore.model.dto.LoginResponse;
import com.example.companycore.model.dto.NoticeItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

public class ApiClient {
    private static final String BASE_URL = "http://100.100.100.66:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

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
            return response != null && response.getToken() != null && !response.getToken().trim().isEmpty();

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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/notices"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

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
}
