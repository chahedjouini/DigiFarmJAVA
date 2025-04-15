package esprit.tn.demo.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class testMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/esprit/tn/demo/ViewMachine.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Machine Management");
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML:");
            e.printStackTrace();
            // More specific error handling
            if (e.toString().contains("IllegalAccessException")) {
                System.err.println("\nFIX REQUIRED: Add this to module-info.java:");
                System.err.println("exports esprit.tn.demo.controllers.GestionMachine to javafx.fxml;");
            }
        }
    }
}