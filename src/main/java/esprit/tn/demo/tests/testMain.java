package esprit.tn.demo.tests;

import esprit.tn.demo.services.GestionVente.StripeCallbackServer;
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
            StripeCallbackServer.start();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/Dashboard.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace(); // ⬅️ Montre ici ce qu'il imprime dans la console
        }
    }
}