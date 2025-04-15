package controllers;

import entities.Abonnement;
import entities.Facture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AbonnementService;
import services.FactureService;

import java.sql.SQLException;
import java.util.List;

public class ModifierFacture {

    @FXML private TextField cinField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Abonnement> abonnementCombo;
    @FXML private Label messageLabel;

    private final FactureService factureService = new FactureService();
    private final AbonnementService abonnementService = new AbonnementService();
    private Facture facture;

    public void setFacture(Facture facture) {
        this.facture = facture;

        cinField.setText(String.valueOf(facture.getCin()));
        emailField.setText(facture.getEmail());

        try {
            List<Abonnement> abonnements = abonnementService.select();
            abonnementCombo.getItems().addAll(abonnements);
            abonnementCombo.setValue(facture.getAbonnement());
        } catch (SQLException e) {
            messageLabel.setText("Erreur chargement abonnements");
        }
    }

    @FXML
    private void onModifier() {
        try {
            facture.setCin(Integer.parseInt(cinField.getText()));
            facture.setEmail(emailField.getText());
            facture.setAbonnement(abonnementCombo.getValue());

            factureService.update(facture);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Modifié avec succès !");
            closeWindow();
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void closeWindow() {
        ((Stage) cinField.getScene().getWindow()).close();
    }
}
