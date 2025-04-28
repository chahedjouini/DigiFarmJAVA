package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.ExpertService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class AjouterExpert {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telField;
    @FXML private TextField emailField;
    @FXML private TextField zoneField;
    @FXML private ComboBox<Dispo> dispoCombo;
    @FXML private Label messageLabel;
    @FXML private Label nomErrorLabel;
    @FXML private Label prenomErrorLabel;
    @FXML private Label telErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label zoneErrorLabel;
    @FXML private Label dispoErrorLabel;

    private final ExpertService expertService = new ExpertService();

    @FXML
    public void initialize() {
        dispoCombo.getItems().addAll(Dispo.values());
    }

    @FXML
    private void onAjout() {
        // Clear previous error messages
        clearErrorMessages();

        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String tel = telField.getText().trim();
            String email = emailField.getText().trim();
            String zone = zoneField.getText().trim();
            Dispo dispo = dispoCombo.getValue();

            boolean isValid = true;

            // Validate fields
            if (nom.isEmpty()) {
                nomErrorLabel.setText("Nom est requis.");
                isValid = false;
            }

            if (prenom.isEmpty()) {
                prenomErrorLabel.setText("Prénom est requis.");
                isValid = false;
            }

            if (tel.isEmpty() || !tel.matches("\\d{8}")) {
                telErrorLabel.setText("Téléphone doit être composé de 8 chiffres.");
                isValid = false;
            }

            if (email.isEmpty() || !email.matches("[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}")) {
                emailErrorLabel.setText("Email invalide.");
                isValid = false;
            }

            if (zone.isEmpty()) {
                zoneErrorLabel.setText("Zone géographique est requise.");
                isValid = false;
            }

            if (dispo == null) {
                dispoErrorLabel.setText("Disponibilité est requise.");
                isValid = false;
            }

            if (!isValid) {
                return;  // Stop the process if validation fails
            }

            Expert expert = new Expert(nom, prenom, Integer.parseInt(tel), email, zone, dispo);
            expertService.add(expert);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Expert ajouté avec succès !");
            clearFields();

            // After adding the expert, navigate back to the expert list inside the dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherExpert.fxml"));
            Parent root = loader.load();
            StackPane dashboardContent = (StackPane) nomField.getScene().getRoot().lookup("#entityContentPane");
            dashboardContent.getChildren().setAll(root);

        } catch (NumberFormatException e) {
            messageLabel.setText("Erreur : Téléphone invalide.");
            messageLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearErrorMessages() {
        nomErrorLabel.setText("");
        prenomErrorLabel.setText("");
        telErrorLabel.setText("");
        emailErrorLabel.setText("");
        zoneErrorLabel.setText("");
        dispoErrorLabel.setText("");
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        telField.clear();
        emailField.clear();
        zoneField.clear();
        dispoCombo.getSelectionModel().clearSelection();
    }

    @FXML
    private void onRetour() {
        // Navigate back to the previous page (AfficherExpert)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherExpert.fxml"));
            Parent root = loader.load();
            // Assuming you are replacing content in a StackPane
            StackPane dashboardContent = (StackPane) nomField.getScene().getRoot().lookup("#entityContentPane");

            // Load the form inside the StackPane
            dashboardContent.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue d'ajout.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
