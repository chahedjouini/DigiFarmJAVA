package controllers;

import entities.Abonnement;
import entities.Facture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AbonnementService;
import services.FactureService;

import java.sql.SQLException;
import java.util.List;

public class AjouterFacture {

    @FXML private ComboBox<Abonnement> abonnementCombo;
    @FXML private TextField cinField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    private final FactureService factureService = new FactureService();
    private final AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        try {
            List<Abonnement> abonnements = abonnementService.select();
            abonnementCombo.getItems().addAll(abonnements);
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des abonnements.");
        }
    }

    @FXML
    private void onAjouter() {
        try {
            Abonnement selected = abonnementCombo.getValue();
            int cin = Integer.parseInt(cinField.getText().trim());
            String email = emailField.getText().trim();

            if (selected == null || email.isEmpty()) {
                messageLabel.setText("Veuillez remplir tous les champs.");
                return;
            }

            Facture facture = new Facture();
            facture.setAbonnement(selected);
            facture.setCin(cin);
            facture.setEmail(email);

            factureService.add(facture);
            messageLabel.setText("Facture ajoutée avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }
}
