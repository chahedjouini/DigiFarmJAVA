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
            String telText = telField.getText().trim();
            String email = emailField.getText().trim();
            String zone = zoneField.getText().trim();
            Dispo dispo = dispoCombo.getValue();

            // Vérification des champs vides
            if (nom.isEmpty() || prenom.isEmpty() || telText.isEmpty() || email.isEmpty() || zone.isEmpty() || dispo == null) {
                showMessage("Tous les champs doivent être remplis.", false);
                return;
            }

            // Vérification de la longueur des noms (max 255)
            if (nom.length() > 255 || prenom.length() > 255) {
                showMessage("Nom et prénom ne doivent pas dépasser 255 caractères.", false);
                return;
            }

            // Vérification de l'email
            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showMessage("Email invalide.", false);
                return;
            }

            // Vérification du numéro (8 chiffres)
            if (!telText.matches("\\d{8}")) {
                showMessage("Le numéro de téléphone doit contenir exactement 8 chiffres.", false);
                return;
            }

            int tel = Integer.parseInt(telText);

            // Création de l'objet Expert
            Expert expert = new Expert(nom, prenom, tel, email, zone, dispo);
            expertService.add(expert);

            showMessage("Expert ajouté avec succès !", true);
            clearFields();

        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), false);
        }
    }

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setTextFill(success ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
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
