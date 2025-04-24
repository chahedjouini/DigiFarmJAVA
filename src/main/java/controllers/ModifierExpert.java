package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ExpertService;

public class ModifierExpert {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telField;
    @FXML private TextField emailField;
    @FXML private TextField zoneField;
    @FXML private ComboBox<Dispo> dispoCombo;
    @FXML private Label messageLabel;

    private Expert expert;
    private final ExpertService expertService = new ExpertService();

    public void setExpert(Expert expert) {
        this.expert = expert;

        nomField.setText(expert.getNom());
        prenomField.setText(expert.getPrenom());
        telField.setText(String.valueOf(expert.getTel()));
        emailField.setText(expert.getEmail());
        zoneField.setText(expert.getZone());
        dispoCombo.getItems().setAll(Dispo.values());
        dispoCombo.setValue(expert.getDispo());
    }

    @FXML
    private void onModifier() {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String telText = telField.getText().trim();
            String email = emailField.getText().trim();
            String zone = zoneField.getText().trim();
            Dispo dispo = dispoCombo.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || telText.isEmpty() || email.isEmpty() || zone.isEmpty() || dispo == null) {
                showMessage("Tous les champs doivent être remplis.", false);
                return;
            }

            if (nom.length() > 255 || prenom.length() > 255) {
                showMessage("Nom et prénom ne doivent pas dépasser 255 caractères.", false);
                return;
            }

            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showMessage("Email invalide.", false);
                return;
            }

            if (!telText.matches("\\d{8}")) {
                showMessage("Le numéro de téléphone doit contenir exactement 8 chiffres.", false);
                return;
            }

            int tel = Integer.parseInt(telText);

            // Mise à jour des champs
            expert.setNom(nom);
            expert.setPrenom(prenom);
            expert.setTel(tel);
            expert.setEmail(email);
            expert.setZone(zone);
            expert.setDispo(dispo);

            expertService.update(expert);
            showMessage("Modifié avec succès", true);
            closeWindow();

        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), false);
        }
    }

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setTextFill(success ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }

    private void closeWindow() {
        ((Stage) nomField.getScene().getWindow()).close();
    }


}
