package controllers;

import entities.Abonnement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AbonnementService;

public class AjouterAbonnement {

    @FXML private TextField idcField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField numeroField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField dureeField;
    @FXML private Label messageLabel;

    private final AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("bronze", "silver", "gold");
    }

    @FXML
    private void onAjouter() {
        // Vérification que tous les champs sont remplis
        if (idcField.getText().isEmpty() || nomField.getText().isEmpty() || prenomField.getText().isEmpty()
                || numeroField.getText().isEmpty() || typeCombo.getValue() == null || dureeField.getText().isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Vérification que le numéro est composé exactement de 8 chiffres
        String numeroText = numeroField.getText();
        if (!numeroText.matches("\\d{8}")) {
            messageLabel.setText("Le numéro de téléphone doit contenir exactement 8 chiffres.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            int idc = Integer.parseInt(idcField.getText());
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            int numero = Integer.parseInt(numeroText);
            String type = typeCombo.getValue();
            int duree = Integer.parseInt(dureeField.getText());

            Abonnement abonnement = new Abonnement();
            abonnement.setIdc(idc);
            abonnement.setNom(nom);
            abonnement.setPrenom(prenom);
            abonnement.setNumero(numero);
            abonnement.setTypeabb(type);
            abonnement.setDureeabb(duree);
            abonnement.calculerPrix();

            abonnementService.add(abonnement);
            messageLabel.setText("Abonnement ajouté avec succès !");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de l'ajout.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

}
