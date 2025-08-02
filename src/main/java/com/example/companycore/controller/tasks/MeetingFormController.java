package com.example.companycore.controller.tasks;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class MeetingFormController {

    @FXML private TextField titleField;
    @FXML private GridPane calendarGrid;
    @FXML private Button addFileBtn;
    @FXML private DatePicker datePicker;
    @FXML private ListView<String> attachmentList;

    private YearMonth currentMonth = YearMonth.now();

    @FXML
    public void initialize() {
        drawCalendar(currentMonth);
        attachmentList.setVisible(false);
        attachmentList.setManaged(false);
    }

    private void drawCalendar(YearMonth month) {
        calendarGrid.getChildren().clear();

        LocalDate firstDay = month.atDay(1);
        int daysInMonth = month.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 일요일 = 0

        int row = 1;
        int col = startDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-padding: 8; -fx-border-color: #ccc; -fx-alignment: center;");
            dayLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            calendarGrid.add(dayLabel, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void onSubmit() {
        LocalDate date = datePicker.getValue();

        if (date == null) {
            showAlert("날짜를 선택하세요.");
            return;
        }

        System.out.println("제목: " + titleField.getText());
        System.out.println("선택된 날짜: " + date);
        System.out.println("첨부파일: " + attachmentList.getItems());

        closeWindow();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("입력 오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        if (stage != null) stage.close();
    }

    @FXML
    public void onAddFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("첨부파일 선택");
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(
                addFileBtn.getScene().getWindow()
        );

        if (selectedFiles != null) {
            ObservableList<String> items = attachmentList.getItems();
            for (File file : selectedFiles) {
                items.add(file.getName());
            }
            attachmentList.setVisible(true);
            attachmentList.setManaged(true);
        }
    }

    @FXML
    public void onRemoveFile(ActionEvent actionEvent) {
        // ListView에서 선택된 항목 가져오기
        String selectedFile = attachmentList.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            // 선택된 항목 없으면 경고창 띄우기
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("삭제 오류");
            alert.setHeaderText(null);
            alert.setContentText("삭제할 파일을 선택하세요.");
            alert.showAndWait();
            return;
        }

        // 선택된 항목 삭제
        attachmentList.getItems().remove(selectedFile);

        // 삭제 후 리스트가 비면 숨기기 (선택 사항)
        if (attachmentList.getItems().isEmpty()) {
            attachmentList.setVisible(false);
            attachmentList.setManaged(false);
        }
    }
}