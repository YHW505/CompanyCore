package com.example.companycore.service;

import com.example.companycore.model.dto.NoticeItem;
import com.example.companycore.model.dto.UserDto;
import com.example.companycore.model.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 관련 API 클라이언트
 * 사용자 조회, 생성, 수정, 공지사항 등
 */
public class UserApiClient extends BaseApiClient {
    private static UserApiClient instance;

    private UserApiClient() {
        super();
    }

    public static UserApiClient getInstance() {
        if (instance == null) {
            synchronized (UserApiClient.class) {
                if (instance == null) {
                    instance = new UserApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 공지사항 목록을 가져옵니다.
     */
    public List<NoticeItem> getNotices() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/notices")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "공지사항 요청");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    System.out.println("서버에서 빈 공지사항 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                try {
                    List<NoticeItem> notices = objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, NoticeItem.class));
                    System.out.println("공지사항 파싱 성공! 개수: " + notices.size());
                    return notices;
                } catch (Exception parseException) {
                    System.out.println("공지사항 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + responseBody);
                    return new ArrayList<>();
                }
            } else {
                System.out.println("공지사항 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + getSafeResponseBody(response));
                return new ArrayList<>();
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "공지사항 요청");
            return new ArrayList<>();
        }
    }

    /**
     * JSON 복구 메서드들
     */
    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String tryRepairJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "[]";
        }

        System.out.println("🔍 JSON 복구 시작...");
        String trimmedJson = json.trim();

        // 배열로 시작하지 않는 경우 처리
        if (!trimmedJson.startsWith("[")) {
            System.out.println("⚠️ JSON이 배열로 시작하지 않음");
            if (trimmedJson.startsWith("{")) {
                // 단일 객체인 경우 배열로 감싸기
                return "[" + trimmedJson + "]";
            } else {
                return "[]";
            }
        }

        // JSON이 잘린 경우 복구
        if (!trimmedJson.endsWith("]")) {
            System.out.println("🔧 잘린 JSON 감지, 복구 시도...");
            String truncatedJson = trimmedJson;
            
            // 마지막 완전한 객체까지 찾기
            int lastValidIndex = findLastValidObject(trimmedJson);
            if (lastValidIndex > 0) {
                truncatedJson = trimmedJson.substring(0, lastValidIndex + 1) + "]";
                System.out.println("🔧 잘린 JSON 복구 완료, 길이: " + truncatedJson.length());
                return truncatedJson;
            }
        }

        // 중괄호 불균형 확인 및 복구
        int openBraces = 0;
        int closeBraces = 0;
        for (char c : trimmedJson.toCharArray()) {
            if (c == '{') openBraces++;
            if (c == '}') closeBraces++;
        }

        if (openBraces != closeBraces) {
            System.out.println("⚠️ 중괄호 불균형 감지, 복구 중...");
            String result = trimmedJson;
            while (openBraces > closeBraces) {
                result += "}";
                closeBraces++;
            }
            while (closeBraces > openBraces) {
                result = "{" + result;
                openBraces++;
            }
            System.out.println("🔧 JSON 복구 완료, 길이: " + result.length());
            return result;
        }

        return trimmedJson;
    }

    private int findLastValidObject(String json) {
        try {
            int lastBrace = json.lastIndexOf("}");
            if (lastBrace == -1) return -1;

            // 마지막 객체의 시작 위치 찾기
            int braceCount = 0;
            for (int i = lastBrace; i >= 0; i--) {
                char c = json.charAt(i);
                if (c == '}') braceCount++;
                if (c == '{') braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ 마지막 객체 찾기 실패: " + e.getMessage());
        }
        return -1;
    }

    /**
     * 사용자 목록을 가져옵니다.
     */
    public List<User> getUsers() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/users")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "사용자 목록 요청");

            // 응답 본문 안전하게 읽기
            String responseBody = getSafeResponseBody(response);

            if (response.statusCode() == 200) {
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    System.out.println("서버에서 빈 사용자 목록 응답을 받았습니다!");
                    return new ArrayList<>();
                }

                String jsonResponse = responseBody;
                System.out.println("📏 JSON 응답 길이: " + jsonResponse.length());

                // JSON 복구 시도
                if (!isValidJson(jsonResponse)) {
                    System.out.println("⚠️ 잘못된 JSON 형식: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())));
                    jsonResponse = tryRepairJson(jsonResponse);
                }

                // 큰 응답 처리
                if (jsonResponse.length() > 10000) {
                    System.out.println("⚠️ 큰 JSON 응답 감지, 완성도 검증 중...");
                    if (!isValidJson(jsonResponse)) {
                        System.out.println("⚠️ 잘못된 JSON 감지, 복구 시도...");
                        jsonResponse = tryRepairJson(jsonResponse);
                    }
                }

                List<User> userList = new ArrayList<>();

                try {
                    // UserDto로 먼저 파싱 시도
                    List<UserDto> userDtoList = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

                    for (UserDto dto : userDtoList) {
                        User user = new User();
                        user.setUserId(dto.getUserId());
                        user.setEmployeeCode(dto.getEmployeeCode());
                        user.setUsername(dto.getUsername());
                        user.setJoinDate(dto.getJoinDate());
                        user.setEmail(dto.getEmail());
                        user.setPhone(dto.getPhone());
                        user.setBirthDate(dto.getBirthDate());
                        user.setPositionId(dto.getPositionId());
                        user.setDepartmentId(dto.getDepartmentId());
                        user.setRole(dto.getRole());
                        // null 값 처리
                        user.setIsFirstLogin(dto.getIsFirstLogin() != null ? dto.getIsFirstLogin() : false);
                        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
                        user.setCreatedAt(dto.getCreatedAt());
                        user.setPositionName(dto.getPositionName());
                        // user.setDepartmentName(dto.getDepartmentName()); // User 엔티티에 departmentName 필드가 없음
                        userList.add(user);
                    }

                    System.out.println("✅ UserDto 파싱 성공! 개수: " + userList.size());
                    return userList;

                } catch (Exception dtoParseException) {
                    System.out.println("⚠️ UserDto 파싱 실패, User 엔티티로 직접 파싱 시도: " + dtoParseException.getMessage());

                    try {
                        // User 엔티티로 직접 파싱
                        userList = objectMapper.readValue(jsonResponse,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
                        
                        // null 값 처리
                        for (User user : userList) {
                            if (user.getIsFirstLogin() == null) {
                                user.setIsFirstLogin(false);
                            }
                            if (user.getIsActive() == null) {
                                user.setIsActive(true);
                            }
                        }
                        
                        System.out.println("✅ User 엔티티 직접 파싱 성공! 개수: " + userList.size());
                        return userList;

                    } catch (Exception userParseException) {
                        System.out.println("❌ User 엔티티 파싱도 실패: " + userParseException.getMessage());
                        return new ArrayList<>();
                    }
                                 }

             } else {
                 System.out.println("❌ 사용자 목록 요청 실패 - 상태 코드: " + response.statusCode());
                 System.out.println("🔍 오류 응답: " + responseBody);
                 return new ArrayList<>();
             }
         } catch (Exception e) {
             handleChunkedTransferError(e, "사용자 목록 요청");
             return new ArrayList<>();
         }
    }

    /**
     * 현재 로그인된 사용자 정보를 가져옵니다.
     */
    public User getCurrentUser() {
        try {
            System.out.println("🔍 현재 사용자 정보 요청 전송 중...");
            HttpRequest request = createAuthenticatedRequestBuilder("/user/info")
                    .GET()
                    .build();

            System.out.println("📋 요청 헤더: " + request.headers().map());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("현재 사용자 정보 요청 상태 코드: " + response.statusCode());
            System.out.println("📋 응답 헤더: " + response.headers().map());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    System.out.println("서버에서 빈 사용자 정보 응답을 받았습니다!");
                    return null;
                }

                System.out.println("🔍 받은 JSON 응답: " + response.body());

                try {
                    // UserUpdateResponse 구조로 파싱 시도
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    
                    // userInfo 필드 확인
                    if (rootNode.has("userInfo")) {
                        System.out.println("✅ UserUpdateResponse에서 userInfo 필드 파싱 성공!");
                        JsonNode userInfoNode = rootNode.get("userInfo");
                        User user = objectMapper.treeToValue(userInfoNode, User.class);
                        return user;
                    } else if (rootNode.has("data")) {
                        System.out.println("✅ UserUpdateResponse에서 data 필드 파싱 성공!");
                        JsonNode dataNode = rootNode.get("data");
                        if (dataNode.has("userInfo")) {
                            System.out.println("✅ data 필드에서 파싱 성공!");
                            JsonNode userInfoNode = dataNode.get("userInfo");
                            User user = objectMapper.treeToValue(userInfoNode, User.class);
                            return user;
                        } else {
                            System.out.println("❌ data 필드가 null입니다!");
                            return null;
                        }
                    } else {
                        // 직접 User로 파싱 시도
                        System.out.println("✅ 직접 User 파싱 성공!");
                        User user = objectMapper.readValue(response.body(), User.class);
                        return user;
                    }

                } catch (Exception parseException) {
                    System.out.println("현재 사용자 정보 JSON 파싱 실패: " + parseException.getMessage());
                    System.out.println("파싱하려던 JSON: " + response.body());
                    return null;
                }
            } else {
                System.out.println("현재 사용자 정보 요청 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.out.println("현재 사용자 정보 요청 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 사용자 정보를 업데이트합니다.
     */
    public boolean updateUser(User user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            HttpRequest request = createAuthenticatedRequestBuilder("/user/update")
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
            return false;
        }
    }

    /**
     * 비밀번호를 변경합니다.
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("currentPassword", currentPassword);
            requestBody.put("newPassword", newPassword);

            String json = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = createAuthenticatedRequestBuilder("/user/change-password")
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
            return false;
        }
    }

    /**
     * 새 사용자를 생성합니다.
     */
    public User createUser(User user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            HttpRequest request = createAuthenticatedRequestBuilder("/users")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                try {
                    User createdUser = objectMapper.readValue(response.body(), User.class);
                    System.out.println("사용자 생성 성공!");
                    return createdUser;
                } catch (Exception e) {
                    System.out.println("생성된 사용자 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("사용자 생성 실패 - 상태 코드: " + response.statusCode());
                System.out.println("오류 응답: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.out.println("사용자 생성 중 예외 발생: " + e.getMessage());
            return null;
        }
    }
} 