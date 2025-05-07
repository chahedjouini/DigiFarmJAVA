package controllers;

import entities.Abonnement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AbonnementService;

public class ModifierAbonnement {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField numeroField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField dureeField;
    @FXML private Label messageLabel;

    private final AbonnementService service = new AbonnementService();
    private Abonnement abonnement;

    public void setAbonnement(Abonnement ab) {
        this.abonnement = ab;
        nomField.setText(ab.getNom());
        prenomField.setText(ab.getPrenom());
        numeroField.setText(String.valueOf(ab.getNumero()));
        typeCombo.getItems().addAll("bronze", "silver", "gold");
        typeCombo.setValue(ab.getTypeabb());
        dureeField.setText(String.valueOf(ab.getDureeabb()));
    }

    @FXML
    private void onModifier() {
        try {
            abonnement.setNom(nomField.getText());
            abonnement.setPrenom(prenomField.getText());
            abonnement.setNumero(Integer.parseInt(numeroField.getText()));
            abonnement.setTypeabb(typeCombo.getValue());
            abonnement.setDureeabb(Integer.parseInt(dureeField.getText()));
            abonnement.calculerPrix();
            service.update(abonnement);
            ((Stage) nomField.getScene().getWindow()).close();
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }
}
