package controllers;

import entities.Veterinaire;
import services.VeterinaireServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VeterinaireAddController {

    @FXML private TextField nomField;
    @FXML private TextField numTelField;
    @FXML private TextField emailField;
    @FXML private TextField adresse_cabineField;

    private final VeterinaireServiceImpl service = new VeterinaireServiceImpl();

    @FXML
    public void handleAddVeterinaire() {
        try {
            String nom = nomField.getText();
            int numTel = Integer.parseInt(numTelField.getText());
            String email = emailField.getText();
            String adresse = adresse_cabineField.getText();

            Veterinaire v = new Veterinaire(0, nom, numTel, email, adresse);
            service.addVeterinaire(v);
            new Alert(Alert.AlertType.INFORMATION, "Vétérinaire ajouté !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
