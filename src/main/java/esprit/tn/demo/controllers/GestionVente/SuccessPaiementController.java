package esprit.tn.demo.controllers.GestionVente;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SuccessPaiementController {

    @FXML
    private Button closeButton;

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }



}
