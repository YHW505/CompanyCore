package com.example.companycore.controller.profile;

import com.example.companycore.model.entity.User;
import com.example.companycore.model.entity.Attendance;
import com.example.companycore.service.ApiClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    
    // 비밀번호 변경 관련 컨트롤
    @FXML
    private PasswordField currentPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button changePasswordButton;
    
    @FXML
    private Button saveButton;
    
    // 프로필 정보 표시용 컨트롤
    @FXML
    private Label profileNameLabel;  // 상단 프로필 섹션의 이름
    
    @FXML
    private Label nameLabel;  // 하단 개인정보 섹션의 이름
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Label employeeCodeLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Label phoneLabel;
    
    @FXML
    private Label addressLabel;
    
    @FXML
    private Label joinDateLabel;
    
    // 근무 통계 관련 컨트롤
    @FXML
    private Label totalWorkDaysLabel;
    
    @FXML
    private Label avgWorkHoursLabel;
    
    @FXML
    private Label attendanceRateLabel;
    
    private User currentUser;
    private ApiClient apiClient;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        apiClient = ApiClient.getInstance();
        
        // 이벤트 핸들러 설정
        saveButton.setOnAction(event -> handleSave());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        
        // 사용자 정보 로드
        loadUserProfile();
    }
    
    /**
     * 사용자 프로필 정보를 로드합니다.
     */
    private void loadUserProfile() {
        try {
            System.out.println("프로필 정보 로드 시작...");
            
            // 🔥 로딩 상태 표시
            showLoadingState();
            
            // 백그라운드에서 사용자 정보 로드
            javafx.concurrent.Task<User> loadUserTask = new javafx.concurrent.Task<User>() {
                @Override
                protected User call() throws Exception {
                    return getCurrentUser();
                }
            };
            
            loadUserTask.setOnSucceeded(e -> {
                currentUser = loadUserTask.getValue();
                if (currentUser != null) {
                    updateProfileDisplay();
                    loadWorkStatistics(); // 🔥 근무 통계도 함께 로드
                    System.out.println("프로필 정보 로드 완료: " + currentUser.getUsername());
                } else {
                    showAlert("오류", "사용자 정보를 불러올 수 없습니다.", Alert.AlertType.ERROR);
                }
                hideLoadingState();
            });
            
            loadUserTask.setOnFailed(e -> {
                showAlert("오류", "프로필 정보 로드 중 오류가 발생했습니다.", Alert.AlertType.ERROR);
                hideLoadingState();
            });
            
            new Thread(loadUserTask).start();
            
        } catch (Exception e) {
            showAlert("오류", "프로필 정보 로드 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
            hideLoadingState();
        }
    }
    
    /**
     * 현재 로그인된 사용자 정보를 가져옵니다.
     * TODO: 실제 로그인 세션에서 사용자 정보를 가져오는 로직 구현
     */
    private User getCurrentUser() {
        try {
            System.out.println("🔍 getCurrentUser() 호출됨");
            
            // ApiClient를 통해 현재 사용자 정보를 가져옴
            User user = apiClient.getCurrentUser();
            
            if (user != null) {
                System.out.println("✅ 사용자 정보 성공적으로 가져옴:");
                System.out.println("  - 이름: " + user.getUsername());
                System.out.println("  - 사원번호: " + user.getEmployeeCode());
                System.out.println("  - 이메일: " + user.getEmail());
            } else {
                System.out.println("❌ 사용자 정보가 null입니다!");
            }
            
            return user;
        } catch (Exception e) {
            System.out.println("❌ 현재 사용자 정보를 가져오는 중 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 프로필 화면을 업데이트합니다.
     */
    private void updateProfileDisplay() {
        if (currentUser == null) {
            System.out.println("❌ currentUser가 null입니다!");
            return;
        }
        
        System.out.println("🔍 프로필 정보 업데이트 시작...");
        System.out.println("📋 받은 사용자 정보:");
        System.out.println("  - 이름: " + currentUser.getUsername());
        System.out.println("  - 사원번호: " + currentUser.getEmployeeCode());
        System.out.println("  - 이메일: " + currentUser.getEmail());
        System.out.println("  - 전화번호: " + currentUser.getPhone());
        System.out.println("  - 직급: " + (currentUser.getRole() != null ? currentUser.getRole().toString() : "null"));
        System.out.println("  - 입사일: " + currentUser.getJoinDate());
        
        // 기본 정보 업데이트 (상단과 하단 모두 업데이트)
        String userName = currentUser.getUsername();
        profileNameLabel.setText(userName);  // 상단 프로필 섹션
        nameLabel.setText(userName);        // 하단 개인정보 섹션
        roleLabel.setText(currentUser.getPositionName() != null ? currentUser.getPositionName() : "사원");
        employeeCodeLabel.setText(currentUser.getEmployeeCode());
        emailLabel.setText(currentUser.getEmail());
        phoneLabel.setText(currentUser.getPhone());
        
        // 주소 필드 설정
        addressLabel.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "정보 없음");
        
        // 입사일 포맷팅
        if (currentUser.getJoinDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
            joinDateLabel.setText(currentUser.getJoinDate().format(formatter));
        } else {
            joinDateLabel.setText("정보 없음");
        }
        
        System.out.println("✅ UI 업데이트 완료");
        
        // 근무 통계 정보 업데이트 (실제로는 데이터베이스에서 계산된 값 사용)
        updateWorkStatistics();
    }
    
    /**
     * 근무 통계 정보를 업데이트합니다.
     */
    private void updateWorkStatistics() {
        try {
            if (currentUser == null) {
                System.out.println("사용자 정보가 없어 근무 통계를 계산할 수 없습니다.");
                return;
            }
            
            // 🔥 실제 출근 데이터에서 통계 계산
            calculateWorkStatistics();
            
        } catch (Exception e) {
            showAlert("오류", "근무 통계 정보를 불러올 수 없습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * 실제 출근 데이터에서 근무 통계를 계산합니다.
     */
    private void calculateWorkStatistics() {
        try {
            // 🔥 백그라운드에서 출근 데이터 조회 및 통계 계산
            javafx.concurrent.Task<Void> calculateTask = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // 1. 사용자의 출근 기록 조회
                    List<Attendance> attendances = apiClient.getUserAttendance(currentUser.getUserId());
                    
                    // 2. 통계 계산
                    int totalWorkDays = attendances.size();
                    double totalWorkHours = 0;
                    int presentDays = 0;
                    
                    for (Attendance attendance : attendances) {
                        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                            // 근무 시간 계산 (시간 단위)
                            long workMinutes = java.time.Duration.between(
                                attendance.getCheckIn(), 
                                attendance.getCheckOut()
                            ).toMinutes();
                            
                            totalWorkHours += workMinutes / 60.0;
                            presentDays++;
                        }
                    }
                    
                    // 3. 평균 근무시간 계산
                    double avgWorkHours = presentDays > 0 ? totalWorkHours / presentDays : 0;
                    
                    // 4. 출근률 계산 (이번 달 기준)
                    int currentMonthDays = java.time.LocalDate.now().lengthOfMonth();
                    double attendanceRate = (double) presentDays / currentMonthDays * 100;
                    
                    // 5. UI 업데이트 (JavaFX Application Thread에서 실행)
                    javafx.application.Platform.runLater(() -> {
                        totalWorkDaysLabel.setText(totalWorkDays + "일");
                        avgWorkHoursLabel.setText(String.format("%.1f시간", avgWorkHours));
                        attendanceRateLabel.setText(String.format("%.1f%%", attendanceRate));
                        
                        System.out.println("근무 통계 업데이트 완료:");
                        System.out.println("  - 총 근무일수: " + totalWorkDays + "일");
                        System.out.println("  - 평균 근무시간: " + String.format("%.1f시간", avgWorkHours));
                        System.out.println("  - 출근률: " + String.format("%.1f%%", attendanceRate));
                    });
                    
                    return null;
                }
            };
            
            calculateTask.setOnFailed(e -> {
                System.out.println("근무 통계 계산 실패: " + calculateTask.getException().getMessage());
                // 실패 시 기본값 표시
                javafx.application.Platform.runLater(() -> {
                    totalWorkDaysLabel.setText("0일");
                    avgWorkHoursLabel.setText("0시간");
                    attendanceRateLabel.setText("0%");
                });
            });
            
            new Thread(calculateTask).start();
            
        } catch (Exception e) {
            System.out.println("근무 통계 계산 중 오류: " + e.getMessage());
            // 오류 시 기본값 표시
            totalWorkDaysLabel.setText("0일");
            avgWorkHoursLabel.setText("0시간");
            attendanceRateLabel.setText("0%");
        }
    }
    
    /**
     * 근무 통계 정보를 로드합니다.
     */
    private void loadWorkStatistics() {
        try {
            if (currentUser == null) {
                System.out.println("사용자 정보가 없어 근무 통계를 로드할 수 없습니다.");
                return;
            }
            
            System.out.println("근무 통계 로드 시작...");
            
            // 🔥 백그라운드에서 근무 통계 로드
            javafx.concurrent.Task<Void> loadStatsTask = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // 실제 출근 데이터 조회 및 통계 계산
                    updateWorkStatistics();
                    return null;
                }
            };
            
            loadStatsTask.setOnSucceeded(e -> {
                System.out.println("근무 통계 로드 완료");
            });
            
            loadStatsTask.setOnFailed(e -> {
                System.out.println("근무 통계 로드 실패: " + loadStatsTask.getException().getMessage());
                // 실패 시 기본값 표시
                javafx.application.Platform.runLater(() -> {
                    totalWorkDaysLabel.setText("0일");
                    avgWorkHoursLabel.setText("0시간");
                    attendanceRateLabel.setText("0%");
                });
            });
            
            new Thread(loadStatsTask).start();
            
        } catch (Exception e) {
            System.out.println("근무 통계 로드 중 오류: " + e.getMessage());
            // 오류 시 기본값 표시
            totalWorkDaysLabel.setText("0일");
            avgWorkHoursLabel.setText("0시간");
            attendanceRateLabel.setText("0%");
        }
    }
    
    @FXML
    public void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // 입력 검증
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("오류", "모든 필드를 입력해주세요.", Alert.AlertType.ERROR);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showAlert("오류", "새 비밀번호가 일치하지 않습니다.", Alert.AlertType.ERROR);
            return;
        }
        
        if (newPassword.length() < 6) {
            showAlert("오류", "새 비밀번호는 최소 6자 이상이어야 합니다.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // 실제 비밀번호 변경 API 호출
            boolean success = apiClient.changePassword(currentPassword, newPassword, currentUser.getUserId());
            
            if (success) {
                // 비밀번호 변경 성공
                showAlert("성공", "비밀번호가 성공적으로 변경되었습니다.", Alert.AlertType.INFORMATION);
                
                // 입력 필드 초기화
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                showAlert("오류", "비밀번호 변경에 실패했습니다.", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            showAlert("오류", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public void handleSave() {
        try {
            if (currentUser != null) {
                // 실제 프로필 정보 저장 API 호출
                boolean success = apiClient.updateUser(currentUser);
                
                if (success) {
                    showAlert("성공", "프로필 정보가 성공적으로 저장되었습니다.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("오류", "프로필 정보 저장에 실패했습니다.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("오류", "사용자 정보가 없습니다.", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            showAlert("오류", "프로필 정보 저장 중 오류가 발생했습니다: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * 로딩 상태를 표시합니다.
     */
    private void showLoadingState() {
        // 🔥 로딩 상태 표시 (향후 ProgressIndicator 추가 가능)
        System.out.println("로딩 중...");
        // saveButton.setDisable(true);
        // changePasswordButton.setDisable(true);
    }
    
    /**
     * 로딩 상태를 숨깁니다.
     */
    private void hideLoadingState() {
        // 🔥 로딩 상태 숨김
        System.out.println("로딩 완료");
        // saveButton.setDisable(false);
        // changePasswordButton.setDisable(false);
    }
} 