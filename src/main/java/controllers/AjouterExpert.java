package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ExpertService;

public class AjouterExpert {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telField;
    @FXML private TextField emailField;
    @FXML private TextField zoneField;
    @FXML private ComboBox<Dispo> dispoCombo;
    @FXML private Label messageLabel;

    private final ExpertService expertService = new ExpertService();

    @FXML
    public void initialize() {
        dispoCombo.getItems().addAll(Dispo.values());
    }

    @FXML
    private void onAjout() {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            int tel = Integer.parseInt(telField.getText().trim());
            String email = emailField.getText().trim();
            String zone = zoneField.getText().trim();
            Dispo dispo = dispoCombo.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || zone.isEmpty() || dispo == null) {
                messageLabel.setText("Tous les champs doivent être remplis.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            Expert expert = new Expert(nom, prenom, tel, email, zone, dispo);
            expertService.add(expert);
            messageLabel.setText("Expert ajouté avec succès !");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            clearFields();

        } catch (NumberFormatException e) {
            messageLabel.setText("Téléphone invalide.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        telField.clear();
        emailField.clear();
        zoneField.clear();
        dispoCombo.getSelectionModel().clearSelection();
    }
}
