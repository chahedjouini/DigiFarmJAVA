package controllers;

import entities.Culture;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import services.CultureService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CalendarController {

    private static final CultureService cultureService = new CultureService();

    @FXML
    private GridPane calendarGrid;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    public void initialize() {
        setupComboBoxes();
        try {
            loadCalendar(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupComboBoxes() {
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
        monthComboBox.setValue("January");

        for (int i = 2022; i <= 2030; i++) {
            yearComboBox.getItems().add(i);
        }
        yearComboBox.setValue(LocalDate.now().getYear());
    }

    @FXML
    private void changeMonth() {
        try {
            loadCalendar(yearComboBox.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeYear() {
        try {
            loadCalendar(yearComboBox.getValue(), monthComboBox.getSelectionModel().getSelectedIndex() + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCalendar(int year, int month) throws SQLException {
        calendarGrid.getChildren().clear();
        List<Culture> cultures = cultureService.select();
        LocalDate today = LocalDate.of(year, month, 1);
        int daysInMonth = today.lengthOfMonth();

        for (int i = 0; i < 7; i++) {
            calendarGrid.add(new Label(LocalDate.of(year, month, 1).plusDays(i).getDayOfWeek().toString()), i, 0);
        }

        int row = 1, col = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            DatePicker datePicker = new DatePicker();
            LocalDate date = LocalDate.of(year, month, day);
            datePicker.setValue(date);

            VBox dayBox = new VBox();
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-weight: bold;");
            dayBox.getChildren().add(dayLabel);
            dayBox.getChildren().add(datePicker);

            for (Culture culture : cultures) {
                if (culture.getDatePlantation() != null && culture.getDatePlantation().equals(date)) {
                    datePicker.setStyle("-fx-background-color: #ADD8E6;");
                    Label cultureNameLabel = new Label(culture.getNom());
                    cultureNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E90FF;");
                    dayBox.getChildren().add(cultureNameLabel);
                }
                if (culture.getDateRecolte() != null && culture.getDateRecolte().equals(date)) {
                    datePicker.setStyle("-fx-background-color: #90EE90;");
                    Label cultureNameLabel = new Label(culture.getNom());
                    cultureNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #32CD32;");
                    dayBox.getChildren().add(cultureNameLabel);
                }
            }

            calendarGrid.add(dayBox, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void onCloseCalendar() {
        calendarGrid.getScene().getWindow().hide();
    }
}
