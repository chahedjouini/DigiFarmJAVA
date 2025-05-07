package controllers;

import entities.Suivi;
import services.SuiviServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;

public class StatistiqueController {

    @FXML
    private Label totalSuivisLabel;

    @FXML
    private PieChart etatPieChart;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();

    @FXML
    public void initialize() {
        loadStatistiques();
    }

    private void loadStatistiques() {
        java.util.List<Suivi> suivis = suiviService.getAllSuivis();
        int totalSuivis = suivis.size();
        totalSuivisLabel.setText("Nombre total de suivis : " + totalSuivis);

        // Calculer la répartition par état
        Map<String, Integer> etatCounts = new HashMap<>();
        for (Suivi suivi : suivis) {
            etatCounts.put(suivi.getEtat(), etatCounts.getOrDefault(suivi.getEtat(), 0) + 1);
        }

        // Créer les données pour le PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : etatCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        etatPieChart.setData(pieChartData);
    }
}