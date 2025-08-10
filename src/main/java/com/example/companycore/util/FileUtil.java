package com.example.companycore.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * 파일 처리 유틸리티 클래스
 * Base64 인코딩/디코딩 및 파일 관련 기능을 제공
 */
public class FileUtil {

    /**
     * 파일을 Base64 문자열로 인코딩
     * @param file 인코딩할 파일
     * @return Base64 인코딩된 문자열
     * @throws IOException 파일 읽기 오류 시
     */
    public static String encodeFileToBase64(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }
        
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * 파일 경로를 Base64 문자열로 인코딩
     * @param filePath 인코딩할 파일 경로
     * @return Base64 인코딩된 문자열
     * @throws IOException 파일 읽기 오류 시
     */
    public static String encodeFileToBase64(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }
        
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return null;
        }
        
        byte[] fileContent = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * 바이트 배열을 Base64 문자열로 인코딩
     * @param bytes 인코딩할 바이트 배열
     * @return Base64 인코딩된 문자열
     */
    public static String encodeToBase64(byte[] bytes) {
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64 문자열을 바이트 배열로 디코딩
     * @param base64String 디코딩할 Base64 문자열
     * @return 디코딩된 바이트 배열
     */
    public static byte[] decodeFromBase64(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) return null;
        return Base64.getDecoder().decode(base64String);
    }

    /**
     * Base64 문자열을 파일로 저장
     * @param base64String Base64 문자열
     * @param outputPath 저장할 파일 경로
     * @throws IOException 파일 쓰기 오류 시
     */
    public static void saveBase64ToFile(String base64String, String outputPath) throws IOException {
        if (base64String == null || base64String.trim().isEmpty()) {
            throw new IllegalArgumentException("Base64 문자열이 비어있습니다.");
        }
        
        byte[] fileContent = decodeFromBase64(base64String);
        if (fileContent != null) {
            Path path = Path.of(outputPath);
            Files.write(path, fileContent);
        }
    }

    /**
     * 바이트 배열을 파일로 저장
     * @param bytes 저장할 바이트 배열
     * @param outputPath 저장할 파일 경로
     * @throws IOException 파일 쓰기 오류 시
     */
    public static void saveBytesToFile(byte[] bytes, String outputPath) throws IOException {
        if (bytes == null) {
            throw new IllegalArgumentException("저장할 바이트 배열이 null입니다.");
        }
        Path path = Path.of(outputPath);
        Files.write(path, bytes);
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     * @param bytes 바이트 수
     * @return 변환된 문자열 (예: "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 파일 확장자로부터 MIME 타입 추정
     * @param filename 파일명
     * @return MIME 타입
     */
    public static String getMimeType(String filename) {
        if (filename == null) return "application/octet-stream";
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * MIME 타입으로부터 파일 확장자 추정
     * @param contentType MIME 타입
     * @return 파일 확장자 (예: ".pdf", ".jpg"), 알 수 없으면 null
     */
    public static String getExtensionFromContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return null;
        }
        String lowerContentType = contentType.toLowerCase();
        if (lowerContentType.contains("pdf")) return ".pdf";
        if (lowerContentType.contains("word") || lowerContentType.contains("document")) return ".docx";
        if (lowerContentType.contains("excel") || lowerContentType.contains("spreadsheet")) return ".xlsx";
        if (lowerContentType.contains("powerpoint") || lowerContentType.contains("presentation")) return ".pptx";
        if (lowerContentType.contains("text/plain")) return ".txt";
        if (lowerContentType.contains("image/jpeg")) return ".jpg";
        if (lowerContentType.contains("image/png")) return ".png";
        if (lowerContentType.contains("image/gif")) return ".gif";
        if (lowerContentType.contains("application/zip")) return ".zip";
        if (lowerContentType.contains("application/x-rar-compressed")) return ".rar";
        // 기타 일반적인 타입
        if (lowerContentType.contains("image/")) return ".jpg"; // 기본 이미지 확장자
        if (lowerContentType.contains("text/")) return ".txt"; // 기본 텍스트 확장자
        if (lowerContentType.contains("application/octet-stream")) return ".bin"; // 이진 파일
        
        return null; // 알 수 없는 타입
    }

    /**
     * 파일명이 유효한지 검사
     * @param filename 검사할 파일명
     * @return 유효 여부
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // 위험한 문자들 제외
        String dangerousChars = "<>:\"/\\|?*";
        for (char c : dangerousChars.toCharArray()) {
            if (filename.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        // 파일명 길이 제한
        return filename.length() <= 255;
    }

    /**
     * 파일 크기 제한 검사
     * @param file 검사할 파일
     * @param maxSizeInMB 최대 크기 (MB)
     * @return 제한 내 여부
     */
    public static boolean isFileSizeValid(File file, int maxSizeInMB) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        long fileSizeInBytes = file.length();
        long maxSizeInBytes = maxSizeInMB * 1024L * 1024L;
        
        return fileSizeInBytes <= maxSizeInBytes;
    }
} 