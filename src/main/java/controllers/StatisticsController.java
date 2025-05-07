package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import services.EtudeService;
import javafx.scene.chart.XYChart;
import entities.Etude;
import entities.Culture;

import java.sql.SQLException;
import java.util.List;

public class StatisticsController {

    @FXML private BarChart<String, Number> studyCostChart;
    private final EtudeService etudeService = new EtudeService();

    @FXML
    public void initialize() {
        loadStudyCostData();
    }

    private void loadStudyCostData() {
        try {
            List<Etude> etudes = etudeService.select();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Average Cost per Culture");

            etudes.stream()
                    .map(etude -> etude.getCulture())
                    .distinct()
                    .forEach(culture -> {
                        double totalCost = etudes.stream()
                                .filter(etude -> etude.getCulture().equals(culture))
                                .mapToDouble(Etude::getPrix)
                                .sum();
                        double averageCost = totalCost / etudes.stream()
                                .filter(etude -> etude.getCulture().equals(culture))
                                .count();

                        series.getData().add(new XYChart.Data<>(culture.getNom(), averageCost));
                    });

            studyCostChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
