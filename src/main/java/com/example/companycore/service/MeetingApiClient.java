package com.example.companycore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 회의 관련 API 클라이언트
 * 회의 생성, 조회, 수정, 삭제 등의 기능을 제공
 */
public class MeetingApiClient extends BaseApiClient {
    private static MeetingApiClient instance;

    private MeetingApiClient() {
        super();
    }

    public static MeetingApiClient getInstance() {
        if (instance == null) {
            synchronized (MeetingApiClient.class) {
                if (instance == null) {
                    instance = new MeetingApiClient();
                }
            }
        }
        return instance;
    }

    /**
     * 모든 회의 조회 (간단한 버전 - 첨부파일 제외)
     * @return 회의 목록
     */
    public List<MeetingDto> getAllMeetingsSimple() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/simple");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "모든 회의 조회 (간단한 버전)");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    // 간단한 Map 형태로 파싱
                    List<Map<String, Object>> simpleResponses = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
                    
                    // Map을 MeetingDto로 변환
                    List<MeetingDto> results = new ArrayList<>();
                    for (Map<String, Object> simpleResponse : simpleResponses) {
                        MeetingDto dto = new MeetingDto();
                        
                        // 안전한 타입 변환
                        Object idObj = simpleResponse.get("meetingId");
                        if (idObj != null) {
                            dto.setMeetingId(Long.valueOf(idObj.toString()));
                        }
                        
                        dto.setTitle((String) simpleResponse.get("title"));
                        dto.setDescription((String) simpleResponse.get("description"));
                        dto.setLocation((String) simpleResponse.get("location"));
                        dto.setDepartment((String) simpleResponse.get("department"));
                        dto.setAuthor((String) simpleResponse.get("author"));
                        
                        // 날짜 파싱
                        String startTimeStr = (String) simpleResponse.get("startTime");
                        if (startTimeStr != null) {
                            try {
                                dto.setStartTime(LocalDateTime.parse(startTimeStr));
                            } catch (Exception e) {
                                System.err.println("시작 시간 파싱 오류: " + startTimeStr + " - " + e.getMessage());
                            }
                        }
                        
                        String endTimeStr = (String) simpleResponse.get("endTime");
                        if (endTimeStr != null) {
                            try {
                                dto.setEndTime(LocalDateTime.parse(endTimeStr));
                            } catch (Exception e) {
                                System.err.println("종료 시간 파싱 오류: " + endTimeStr + " - " + e.getMessage());
                            }
                        }
                        
                        // 첨부파일 정보는 제외 (상세보기에서만 확인)
                        
                        results.add(dto);
                    }
                    
                    return results;
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "모든 회의 조회 (간단한 버전)");
        }
        return null;
    }

    /**
     * 모든 회의 조회 (기존 버전 - 첨부파일 포함)
     * @return 회의 목록
     */
    public List<MeetingDto> getAllMeetings() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "모든 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "모든 회의 조회");
        }
        return null;
    }

    /**
     * 특정 회의 조회
     * @param meetingId 회의 ID
     * @return 회의 정보
     */
    public MeetingDto getMeetingById(Long meetingId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "특정 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, MeetingDto.class);
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "특정 회의 조회");
        }
        return null;
    }

    /**
     * 회의 생성
     * @param meetingDto 회의 정보
     * @return 생성된 회의 정보
     */
    public MeetingDto createMeeting(MeetingDto meetingDto) {
        try {
            String json = objectMapper.writeValueAsString(meetingDto);
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings");
            HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "회의 생성");

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, MeetingDto.class);
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의 생성");
        }
        return null;
    }

    /**
     * 첨부파일이 포함된 회의 생성
     * @param title 회의 제목
     * @param description 회의 설명
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @param location 회의실
     * @param department 부서
     * @param author 작성자
     * @param attachmentFilename 첨부파일명
     * @param attachmentContentType 첨부파일 타입
     * @param attachmentSize 첨부파일 크기
     * @param attachmentContent Base64 인코딩된 첨부파일 내용
     * @return 생성된 회의 정보
     */
    public MeetingDto createMeetingWithAttachment(String title, String description, LocalDateTime startTime, 
                                                LocalDateTime endTime, String location, String department, 
                                                String author, String attachmentFilename, String attachmentContentType, 
                                                Long attachmentSize, String attachmentContent) {
        try {
            MeetingDto meetingDto = new MeetingDto();
            meetingDto.setTitle(title);
            meetingDto.setDescription(description);
            meetingDto.setStartTime(startTime);
            meetingDto.setEndTime(endTime);
            meetingDto.setLocation(location);
            meetingDto.setDepartment(department);
            meetingDto.setAuthor(author);
            meetingDto.setAttachmentFilename(attachmentFilename);
            meetingDto.setAttachmentContentType(attachmentContentType);
            meetingDto.setAttachmentSize(attachmentSize);
            meetingDto.setAttachmentContent(attachmentContent);

            String json = objectMapper.writeValueAsString(meetingDto);
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings");
            HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "첨부파일이 포함된 회의 생성");

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, MeetingDto.class);
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "첨부파일이 포함된 회의 생성");
        }
        return null;
    }

    /**
     * 회의 수정
     * @param meetingId 회의 ID
     * @param meetingDto 수정할 회의 정보
     * @return 수정된 회의 정보
     */
    public MeetingDto updateMeeting(Long meetingId, MeetingDto meetingDto) {
        try {
            String json = objectMapper.writeValueAsString(meetingDto);
            System.out.println("📡 회의 수정 요청 - ID: " + meetingId);
            System.out.println("📄 첨부파일 정보: " + (meetingDto.getAttachmentFilename() != null ? meetingDto.getAttachmentFilename() : "null"));
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId);
            HttpRequest request = builder.PUT(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "회의 수정");

            System.out.println("📡 회의 수정 응답 - 상태 코드: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    MeetingDto result = objectMapper.readValue(responseBody, MeetingDto.class);
                    System.out.println("✅ 회의 수정 성공");
                    return result;
                }
            } else {
                System.err.println("❌ 회의 수정 실패 - 상태 코드: " + response.statusCode());
                String errorBody = getSafeResponseBody(response);
                if (errorBody != null) {
                    System.err.println("❌ 오류 응답: " + errorBody);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 회의 수정 중 예외 발생: " + e.getMessage());
            handleChunkedTransferError(e, "회의 수정");
        }
        return null;
    }

    /**
     * 회의 삭제
     * @param meetingId 회의 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteMeeting(Long meetingId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId);
            HttpRequest request = builder.DELETE().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "회의 삭제");

            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의 삭제");
        }
        return false;
    }

    /**
     * 날짜별 회의 조회
     * @param date 날짜 (YYYY-MM-DD 형식)
     * @return 해당 날짜의 회의 목록
     */
    public List<MeetingDto> getMeetingsByDate(String date) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/date?date=" + date);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "날짜별 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "날짜별 회의 조회");
        }
        return null;
    }

    /**
     * 회의실별 회의 조회
     * @param location 회의실명 (부분 일치 검색)
     * @return 해당 회의실의 회의 목록
     */
    public List<MeetingDto> getMeetingsByLocation(String location) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/location/" + location);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "회의실별 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의실별 회의 조회");
        }
        return null;
    }

    /**
     * 회의록 첨부파일 다운로드
     * @param meetingId 회의 ID
     * @return 첨부파일 내용 (바이트 배열)
     */
    public byte[] downloadMeetingAttachment(Long meetingId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId + "/attachment");
            HttpRequest request = builder.GET().build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            // logResponseInfo(response, "회의록 첨부파일 다운로드");

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("❌ 첨부파일 다운로드 실패 - 상태 코드: " + response.statusCode());
                // 오류 응답 본문이 텍스트일 경우를 대비하여 로깅
                try {
                    String errorBody = new String(response.body(), java.nio.charset.StandardCharsets.UTF_8);
                    System.err.println("❌ 오류 응답: " + errorBody);
                } catch (Exception e2) {
                    System.err.println("❌ 오류 응답 본문 파싱 실패: " + e2.getMessage());
                }
            }
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의록 첨부파일 다운로드");
        }
        return null;
    }

    /**
     * 회의록 첨부파일 업로드
     * @param meetingId 회의 ID
     * @param attachmentFilename 첨부파일명
     * @param attachmentContentType 첨부파일 타입
     * @param attachmentSize 첨부파일 크기
     * @param attachmentContent Base64 인코딩된 첨부파일 내용
     * @return 업로드 성공 여부
     */
    public boolean uploadMeetingAttachment(Long meetingId, String attachmentFilename, 
                                        String attachmentContentType, Long attachmentSize, 
                                        String attachmentContent) {
        try {
            MeetingDto meetingDto = new MeetingDto();
            meetingDto.setAttachmentFilename(attachmentFilename);
            meetingDto.setAttachmentContentType(attachmentContentType);
            meetingDto.setAttachmentSize(attachmentSize);
            meetingDto.setAttachmentContent(attachmentContent);

            String json = objectMapper.writeValueAsString(meetingDto);
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId + "/attachment");
            HttpRequest request = builder.PUT(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "회의록 첨부파일 업로드");

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의록 첨부파일 업로드");
        }
        return false;
    }

    /**
     * 부서별 회의 목록 조회
     * @param department 부서명
     * @return 부서별 회의 목록
     */
    public List<MeetingDto> getMeetingsByDepartment(String department) {
        try {
            String encodedDepartment = java.net.URLEncoder.encode(department, "UTF-8");
            System.out.println("🔍 부서별 회의 목록 조회 - 부서: " + department);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/department/" + encodedDepartment);
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "부서별 회의 목록 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            } else {
                System.err.println("❌ 부서별 회의 목록 조회 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 부서별 회의 목록 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            handleChunkedTransferError(e, "부서별 회의 목록 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 부서별 현재 진행중인 회의 조회
     * @param department 부서명
     * @return 부서별 현재 진행중인 회의 목록
     */
    public List<MeetingDto> getCurrentMeetingsByDepartment(String department) {
        try {
            String encodedDepartment = java.net.URLEncoder.encode(department, "UTF-8");
            System.out.println("🔍 부서별 현재 진행중인 회의 조회 - 부서: " + department);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/department/" + encodedDepartment + "/current");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "부서별 현재 진행중인 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            } else {
                System.err.println("❌ 부서별 현재 진행중인 회의 조회 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 부서별 현재 진행중인 회의 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            handleChunkedTransferError(e, "부서별 현재 진행중인 회의 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 부서별 예정된 회의 조회
     * @param department 부서명
     * @return 부서별 예정된 회의 목록
     */
    public List<MeetingDto> getUpcomingMeetingsByDepartment(String department) {
        try {
            String encodedDepartment = java.net.URLEncoder.encode(department, "UTF-8");
            System.out.println("🔍 부서별 예정된 회의 조회 - 부서: " + department);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/department/" + encodedDepartment + "/upcoming");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "부서별 예정된 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            } else {
                System.err.println("❌ 부서별 예정된 회의 조회 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 부서별 예정된 회의 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            handleChunkedTransferError(e, "부서별 예정된 회의 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 부서별 완료된 회의 조회
     * @param department 부서명
     * @return 부서별 완료된 회의 목록
     */
    public List<MeetingDto> getPastMeetingsByDepartment(String department) {
        try {
            String encodedDepartment = java.net.URLEncoder.encode(department, "UTF-8");
            System.out.println("🔍 부서별 완료된 회의 조회 - 부서: " + department);
            
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/department/" + encodedDepartment + "/past");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logResponseInfo(response, "부서별 완료된 회의 조회");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, new TypeReference<List<MeetingDto>>() {});
                }
            } else {
                System.err.println("❌ 부서별 완료된 회의 조회 실패 - 상태 코드: " + response.statusCode());
                String responseBody = getSafeResponseBody(response);
                System.err.println("❌ 오류 응답: " + responseBody);
            }
        } catch (Exception e) {
            System.err.println("❌ 부서별 완료된 회의 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            handleChunkedTransferError(e, "부서별 완료된 회의 조회");
        }
        return new ArrayList<>();
    }

    /**
     * 회의 DTO 클래스
     */
    public static class MeetingDto {
        private Long meetingId;
        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private String department;
        private String author;
        private String attachmentPath;
        // 첨부파일 필드를 서버와 일치시킴
        private String attachmentFilename;
        private String attachmentContentType;
        private Long attachmentSize;
        private String attachmentContent; // Base64 인코딩된 첨부파일 내용
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 기본 생성자
        public MeetingDto() {}

        // 생성자
        public MeetingDto(Long meetingId, String title, String description, LocalDateTime startTime, 
                         LocalDateTime endTime, String location, String department, String author, String attachmentPath, String attachmentContent) {
            this.meetingId = meetingId;
            this.title = title;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
            this.department = department;
            this.author = author;
            this.attachmentPath = attachmentPath;
            this.attachmentContent = attachmentContent;
        }

        // Getter와 Setter 메서드들
        public Long getMeetingId() { return meetingId; }
        public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getAttachmentPath() { return attachmentPath; }
        public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

        // 첨부파일 관련 getter/setter 수정
        public String getAttachmentFilename() { return attachmentFilename; }
        public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

        public String getAttachmentContentType() { return attachmentContentType; }
        public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

        public Long getAttachmentSize() { return attachmentSize; }
        public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }

        public String getAttachmentContent() { return attachmentContent; }
        public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        @Override
        public String toString() {
            return "MeetingDto{" +
                    "meetingId=" + meetingId +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", location='" + location + '\'' +
                    ", department='" + department + '\'' +
                    ", author='" + author + '\'' +
                    ", attachmentPath='" + attachmentPath + '\'' +
                    ", attachmentFilename='" + attachmentFilename + '\'' +
                    ", attachmentContentType='" + attachmentContentType + '\'' +
                    ", attachmentSize=" + attachmentSize +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }
} 