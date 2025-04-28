package utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import services.EtudeService;

import java.util.Map;

public class PieChartExample extends Application {

    private EtudeService etudeService = new EtudeService();

    @Override
    public void start(Stage primaryStage) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Etudes Statistics by Climat");

        // Fetching statistics for climat
        try {
            Map<String, Integer> climatStats = etudeService.getStatisticsByClimat();
            for (Map.Entry<String, Integer> entry : climatStats.entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(pieChart, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pie Chart Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
