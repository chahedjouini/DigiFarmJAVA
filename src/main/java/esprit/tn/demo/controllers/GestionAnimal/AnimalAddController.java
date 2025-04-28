package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AnimalAddController {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;
    @FXML private TextField poidsField;

    @FXML private Label nomErrorLabel;
    @FXML private Label typeErrorLabel;
    @FXML private Label raceErrorLabel;
    @FXML private Label ageErrorLabel;
    @FXML private Label poidsErrorLabel;

    private final AnimalServiceImpl animalService = new AnimalServiceImpl();

    @FXML
    public void initialize() {
        // Initialization logic if needed (e.g., populate fields or pre-validate)
    }

    @FXML
    private void handleAddAnimal() {
        // Réinitialiser les messages d'erreur
        nomErrorLabel.setText("");
        typeErrorLabel.setText("");
        raceErrorLabel.setText("");
        ageErrorLabel.setText("");
        poidsErrorLabel.setText("");

        String nom = nomField.getText();
        String type = typeField.getText();
        String race = raceField.getText();
        String ageText = ageField.getText();
        String poidsText = poidsField.getText();

        boolean isValid = true;

        if (nom.isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire.");
            isValid = false;
        }

        if (type.isEmpty()) {
            typeErrorLabel.setText("Le type est obligatoire.");
            isValid = false;
        }

        if (race.isEmpty()) {
            raceErrorLabel.setText("La race est obligatoire.");
            isValid = false;
        }

        if (ageText.isEmpty()) {
            ageErrorLabel.setText("L'âge est obligatoire.");
            isValid = false;
        } else {
            if (!isValidInteger(ageText)) {
                ageErrorLabel.setText("L'âge doit être un nombre entier valide.");
                isValid = false;
            } else if (Integer.parseInt(ageText) <= 0) {
                ageErrorLabel.setText("L'âge doit être un nombre positif.");
                isValid = false;
            }
        }

        if (poidsText.isEmpty()) {
            poidsErrorLabel.setText("Le poids est obligatoire.");
            isValid = false;
        } else {
            if (!isValidFloat(poidsText)) {
                poidsErrorLabel.setText("Le poids doit être un nombre valide.");
                isValid = false;
            } else if (Float.parseFloat(poidsText) <= 0) {
                poidsErrorLabel.setText("Le poids doit être un nombre positif.");
                isValid = false;
            }
        }

        if (isValid) {
            Animal animal = new Animal(0, // L'ID sera généré par la base de données
                    nom,
                    type,
                    Integer.parseInt(ageText),
                    Float.parseFloat(poidsText),
                    race);

            try {
                animalService.addAnimal(animal);
                showAlert("Success", "Animal added successfully!");
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                showAlert("Error", "Failed to add Animal: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private boolean isValidFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}