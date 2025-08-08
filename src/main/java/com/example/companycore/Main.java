package com.example.companycore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/companycore/view/login/loginView.fxml"));

            Scene scene = new Scene(loader.load(), 1400, 800);

            primaryStage.setTitle("CompanyCore");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            
            // 전체화면 기능 추가
            setupFullscreenSupport(primaryStage, scene);
            
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("애플리케이션 시작 실패:");
            e.printStackTrace();
        }
    }
    
    private void setupFullscreenSupport(Stage stage, Scene scene) {
        // F11 키로 전체화면 토글
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F11:
                    toggleFullscreen(stage);
                    break;
                case ESCAPE:
                    if (stage.isFullScreen()) {
                        stage.setFullScreen(false);
                    }
                    break;
            }
        });
        
        // 창 크기 변경 시 리사이징 처리
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            handleWindowResize(stage, scene);
        });
        
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            handleWindowResize(stage, scene);
        });
    }
    
    private void toggleFullscreen(Stage stage) {
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);
        }
    }
    
    private void handleWindowResize(Stage stage, Scene scene) {
        // 창 크기에 맞춰서 컨텐츠 리사이징
        double width = stage.getWidth();
        double height = stage.getHeight();
        
        // 최소 크기 보장
        if (width < 800) width = 800;
        if (height < 600) height = 600;
        
        // 루트 컨테이너 크기 조정
        if (scene.getRoot() != null) {
            Parent root = scene.getRoot();
            if (root instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region region = (javafx.scene.layout.Region) root;
                region.setPrefSize(width, height);
                region.setMinSize(800, 600);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
