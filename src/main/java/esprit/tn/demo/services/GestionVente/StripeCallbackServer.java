package esprit.tn.demo.services.GestionVente;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.port;

public class StripeCallbackServer {

    public static void start() {
        port(9090);

        get("/success", (req, res) -> {
            System.out.println("✅ Paiement réussi reçu via Stripe !");
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(StripeCallbackServer.class.getResource("/esprit/tn/demo/SuccessPaiement.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Paiement réussi");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return "OK";
        });

        get("/cancel", (req, res) -> {
            System.out.println("❌ Paiement annulé reçu via Stripe !");
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(StripeCallbackServer.class.getResource("/esprit/tn/demo/CancelPaiement.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Paiement annulé");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return "Annulé";
        });
    }
}
