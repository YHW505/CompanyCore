package com.example.companycore.service;

import com.example.companycore.model.dto.TaskDto;
import com.example.companycore.model.entity.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 작업 관련 API 클라이언트
 * 작업 조회, 생성, 수정, 삭제 등
 */
public class TaskApiClient extends BaseApiClient {
    private static TaskApiClient instance;

    private TaskApiClient() {
        super();
    }

    public static TaskApiClient getInstance() {
        if (instance == null) {
            synchronized (TaskApiClient.class) {
                if (instance == null) {
                    instance = new TaskApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 모든 작업 목록을 가져옵니다.
     */
    public List<Task> getTasks() {
        try {
            HttpRequest request = createAuthenticatedRequestBuilder("/tasks")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Task> tasks = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                    return tasks;
                } catch (Exception e) {
                    System.out.println("작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 특정 사용자에게 할당된 작업 목록을 가져옵니다.
     */
    public List<Task> getTasksAssignedToUser(Long userId) {
        return getTasksAssignedToUser(userId, null);
    }

    /**
     * 특정 사용자에게 할당된 작업 목록을 가져옵니다. (상태별 필터링 포함)
     */
//    public List<Task> getTasksAssignedToUser(Long userId, String status) {
//        try {
//            StringBuilder endpointBuilder = new StringBuilder("/tasks/user/").append(userId);
//            if (status != null && !status.trim().isEmpty()) {
//                endpointBuilder.append("?status=").append(status);
//            }
//            String endpoint = endpointBuilder.toString();
//
//            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                if (response.body() == null || response.body().trim().isEmpty()) {
//                    return new ArrayList<>();
//                }
//
//                try {
//                    JsonNode rootNode = objectMapper.readTree(response.body());
//                    JsonNode dataNode = rootNode.get("data");
//                    if (dataNode != null && dataNode.isArray()) {
//                        List<Task> tasks = objectMapper.readValue(dataNode.toString(),
//                                objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
//                        return tasks;
//                    } else {
//                        System.out.println("응답에 'data' 필드가 없거나 배열이 아닙니다.");
//                        return new ArrayList<>();
//                    }
//                } catch (Exception e) {
//                    System.out.println("사용자 작업 목록 파싱 실패: " + e.getMessage());
//                    return new ArrayList<>();
//                }
//            } else {
//                System.out.println("사용자 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
//                return new ArrayList<>();
//            }
//        } catch (Exception e) {
//            System.out.println("사용자 작업 목록 요청 중 예외 발생: " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }
    /**
     * 특정 사용자에게 할당된 작업 목록을 가져옵니다. (상태별 필터링 포함)
     */
    public List<Task> getTasksAssignedToUser(Long userId, String status) {
        try {
            StringBuilder endpointBuilder = new StringBuilder("/tasks/user/").append(userId);
            if (status != null && !status.trim().isEmpty()) {
                endpointBuilder.append("?status=").append(status);
            }
            String endpoint = endpointBuilder.toString();

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    // JSON 응답을 JsonNode로 파싱하고 "data" 부분만 추출
                    JsonNode dataNode = objectMapper.readTree(response.body()).get("data");

                    if (dataNode != null && dataNode.isArray()) {
                        // TypeReference를 사용한 더 간결한 변환
                        return objectMapper.convertValue(dataNode, new TypeReference<List<Task>>() {});
                    } else {
                        System.out.println("응답에 'data' 배열이 없습니다.");
                        return new ArrayList<>();
                    }

                } catch (Exception e) {
                    System.out.println("사용자 작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("사용자 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("사용자 작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 특정 사용자가 담당자로 할당받은 작업 목록을 가져옵니다.
     */
    public List<Task> getMyAssignedTasks(Long userId) {
        try {
            String endpoint = "/tasks/user/" + userId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Task> tasks = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                    return tasks;
                } catch (Exception e) {
                    System.out.println("사용자에게 할당된 작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("사용자에게 할당된 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("사용자에게 할당된 작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 상태별 작업 목록을 가져옵니다.
     */
    public List<Task> getTasksByStatus(String status) {
        try {
            String endpoint = "/tasks/status/" + status;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Task> tasks = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                    return tasks;
                } catch (Exception e) {
                    System.out.println("상태별 작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("상태별 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("상태별 작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 작업 유형별 목록을 가져옵니다.
     */
    public List<Task> getTasksByType(String taskType) {
        try {
            String endpoint = "/tasks/type/" + taskType;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Task> tasks = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                    return tasks;
                } catch (Exception e) {
                    System.out.println("유형별 작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("유형별 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("유형별 작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 작업을 검색합니다.
     */
    public List<Task> searchTasks(String keyword, String searchIn) {
        try {
            String endpoint = "/tasks/search?keyword=" + keyword + "&searchIn=" + searchIn;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new ArrayList<>();
                }

                try {
                    List<Task> tasks = objectMapper.readValue(response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                    return tasks;
                } catch (Exception e) {
                    System.out.println("작업 검색 결과 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("작업 검색 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("작업 검색 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 여러 조건으로 작업을 필터링합니다.
     */
    public List<Task> filterTasks(Long assignedTo, Long assignedBy, String status, String taskType,
                                 String startDate, String endDate, Integer page, Integer size,
                                 String sortBy, String sortDir) {
        try {
            StringBuilder endpoint = new StringBuilder("/tasks/filter?");
            
            if (assignedTo != null) endpoint.append("assignedTo=").append(assignedTo).append("&");
            if (assignedBy != null) endpoint.append("assignedBy=").append(assignedBy).append("&");
            if (status != null) endpoint.append("status=").append(status).append("&");
            if (taskType != null) endpoint.append("taskType=").append(taskType).append("&");
            if (startDate != null) endpoint.append("startDate=").append(startDate).append("&");
            if (endDate != null) endpoint.append("endDate=").append(endDate).append("&");
            if (page != null) endpoint.append("page=").append(page).append("&");
            if (size != null) endpoint.append("size=").append(size).append("&");
            if (sortBy != null) endpoint.append("sortBy=").append(sortBy).append("&");
            if (sortDir != null) endpoint.append("sortDir=").append(sortDir).append("&");

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
                    // 응답이 페이지네이션 구조일 수 있으므로 처리
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    
                    if (rootNode.has("content")) {
                        // 페이지네이션 응답
                        JsonNode contentNode = rootNode.get("content");
                        List<Task> tasks = objectMapper.readValue(contentNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                        return tasks;
                    } else {
                        // 일반 배열 응답
                        List<Task> tasks = objectMapper.readValue(response.body(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Task.class));
                        return tasks;
                    }
                } catch (Exception e) {
                    System.out.println("필터링된 작업 목록 파싱 실패: " + e.getMessage());
                    return new ArrayList<>();
                }
            } else {
                System.out.println("작업 필터링 요청 실패 - 상태 코드: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("작업 필터링 요청 중 예외 발생: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 새 작업을 생성합니다.
     */
    public TaskDto createTask(TaskDto taskDto) {
        try {
            String json = objectMapper.writeValueAsString(taskDto);
            HttpRequest request = createAuthenticatedRequestBuilder("/tasks")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                try {
                    TaskDto createdTask = objectMapper.readValue(response.body(), TaskDto.class);
                    return createdTask;
                } catch (Exception e) {
                    System.out.println("생성된 작업 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("작업 생성 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("작업 생성 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 작업을 업데이트합니다.
     */
    public boolean updateTask(Long taskId, Task task) {
        try {
            String json = objectMapper.writeValueAsString(task);
            String endpoint = "/tasks/" + taskId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            System.out.println("작업 업데이트 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 작업을 삭제합니다.
     */
    public boolean deleteTask(Long taskId) {
        try {
            String endpoint = "/tasks/" + taskId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            System.out.println("작업 삭제 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 특정 작업을 가져옵니다.
     */
    public Task getTaskById(Long taskId) {
        try {
            String endpoint = "/tasks/" + taskId;
            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                try {
                    Task task = objectMapper.readValue(response.body(), Task.class);
                    return task;
                } catch (Exception e) {
                    System.out.println("작업 파싱 실패: " + e.getMessage());
                    return null;
                }
            } else {
                System.out.println("작업 조회 실패 - 상태 코드: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("작업 조회 중 예외 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 특정 사용자의 특정 타입 작업 조회 (페이지네이션 포함)
     * @param userId 사용자 ID
     * @param taskType 작업 타입
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 필드
     * @param sortDir 정렬 방향 (asc/desc)
     * @return 페이지네이션된 작업 목록
     */
    public Map<String, Object> getTasksByAssignedToAndTypeWithPagination(Long userId, String taskType, int page, int size, String sortBy, String sortDir) {
        try {
            String endpoint = String.format("/tasks/assigned-to/%d/type/%s/page?page=%d&size=%d&sortBy=%s&sortDir=%s",
                    userId, taskType, page, size, sortBy, sortDir);

            HttpRequest request = createAuthenticatedRequestBuilder(endpoint)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                if (response.body() == null || response.body().trim().isEmpty()) {
                    return new HashMap<>();
                }

                try {
                    return objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                } catch (Exception e) {
                    System.out.println("페이지네이션된 작업 목록 파싱 실패: " + e.getMessage());
                    return new HashMap<>();
                }
            } else {
                System.out.println("페이지네이션된 작업 목록 요청 실패 - 상태 코드: " + response.statusCode());
                return new HashMap<>();
            }
        } catch (Exception e) {
            System.out.println("페이지네이션된 작업 목록 요청 중 예외 발생: " + e.getMessage());
            return new HashMap<>();
        }
    }
} 