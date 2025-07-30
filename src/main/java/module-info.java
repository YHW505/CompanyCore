module com.example.companycore {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;
    requires transitive javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    // Jackson 관련 모듈 추가
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    opens com.example.companycore to javafx.fxml;
    exports com.example.companycore;
    exports com.example.companycore.controller;
    opens com.example.companycore.controller to javafx.fxml;

    // DTO 패키지 설정 추가 (이 부분이 누락되어 있었습니다!)
    exports com.example.companycore.model.dto;
    opens com.example.companycore.model.dto to com.fasterxml.jackson.databind;
}
