package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.Map;

public class AnimalStatistiquesController {

    @FXML private Label totalAnimauxLabel;
    @FXML private Label moyenneAgeLabel;
    @FXML private PieChart animauxParTypePieChart;
    @FXML private PieChart raceDistributionPieChart;
    private final AnimalServiceImpl animalService = new AnimalServiceImpl();

    @FXML
    public void initialize() {
        // Nombre total d'animaux
        int totalAnimaux = animalService.getAllAnimals().size();
        totalAnimauxLabel.setText("Nombre total d'animaux : " + totalAnimaux);

        // Moyenne d'âge
        Double moyenneAge = animalService.getAverageAge();
        if (moyenneAge != null) {
            moyenneAgeLabel.setText(String.format("Moyenne d'âge : %.2f ans", moyenneAge));
        } else {
            moyenneAgeLabel.setText("Moyenne d'âge : N/A");
        }

        // Distribution des animaux par type
        Map<String, Long> animauxParTypeMap = animalService.groupAnimalsByType();
        ObservableList<PieChart.Data> typePieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : animauxParTypeMap.entrySet()) {
            typePieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        animauxParTypePieChart.setData(typePieChartData);

        // Distribution des animaux par race
        Map<String, Long> raceDistributionMap = animalService.getRaceDistribution();
        ObservableList<PieChart.Data> racePieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : raceDistributionMap.entrySet()) {
            racePieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        raceDistributionPieChart.setData(racePieChartData);
    }

}