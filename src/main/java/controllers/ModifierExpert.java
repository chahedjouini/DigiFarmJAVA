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
            expert.setNom(nomField.getText());
            expert.setPrenom(prenomField.getText());
            expert.setTel(Integer.parseInt(telField.getText()));
            expert.setEmail(emailField.getText());
            expert.setZone(zoneField.getText());
            expert.setDispo(dispoCombo.getValue());

            expertService.update(expert);
            messageLabel.setText("Modifié avec succès");
            closeWindow();

        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void closeWindow() {
        ((Stage) nomField.getScene().getWindow()).close();
    }
}
