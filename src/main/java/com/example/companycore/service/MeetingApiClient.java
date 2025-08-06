package com.example.companycore.service;

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
     * 모든 회의 조회
     * @return 회의 목록
     */
    public List<MeetingDto> getAllMeetings() {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "모든 회의 조회");

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
            logResponseInfo(response, "특정 회의 조회");

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
            logResponseInfo(response, "회의 생성");

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
            logResponseInfo(response, "첨부파일이 포함된 회의 생성");

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
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId);
            HttpRequest request = builder.PUT(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "회의 수정");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, MeetingDto.class);
                }
            }
        } catch (Exception e) {
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
            logResponseInfo(response, "회의 삭제");

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
            logResponseInfo(response, "날짜별 회의 조회");

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
            logResponseInfo(response, "회의실별 회의 조회");

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
     * @return 첨부파일 정보 (Base64 인코딩된 내용 포함)
     */
    public MeetingDto downloadMeetingAttachment(Long meetingId) {
        try {
            HttpRequest.Builder builder = createAuthenticatedRequestBuilder("/meetings/" + meetingId + "/attachment");
            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logResponseInfo(response, "회의록 첨부파일 다운로드");

            if (response.statusCode() == 200) {
                String responseBody = getSafeResponseBody(response);
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    return objectMapper.readValue(responseBody, MeetingDto.class);
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
            logResponseInfo(response, "회의록 첨부파일 업로드");

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            handleChunkedTransferError(e, "회의록 첨부파일 업로드");
        }
        return false;
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
                         LocalDateTime endTime, String location, String department, String author, String attachmentPath) {
            this.meetingId = meetingId;
            this.title = title;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
            this.department = department;
            this.author = author;
            this.attachmentPath = attachmentPath;
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