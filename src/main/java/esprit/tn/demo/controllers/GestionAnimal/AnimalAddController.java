package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AnimalAddController {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;
    @FXML private TextField poidsField;

    private final AnimalServiceImpl animalService = new AnimalServiceImpl();

    @FXML
    public void initialize() {
        // Initialization logic if needed (e.g., populate fields or pre-validate)
    }

    @FXML
    private void handleAddAnimal() {
        if (!validateInputs()) {
            return;
        }

        Animal animal = new Animal(0,
                nomField.getText(),
                typeField.getText(),
                Integer.parseInt(ageField.getText()),
                Float.parseFloat(poidsField.getText()),
                raceField.getText());

        try {
            animalService.addAnimal(animal);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Animal added successfully!");
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Animal: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        if (nomField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the animal's name.");
            return false;
        }
        if (typeField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the animal's type.");
            return false;
        }
        if (raceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the animal's breed.");
            return false;
        }
        if (ageField.getText().isEmpty() || !isValidInteger(ageField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid age.");
            return false;
        }
        if (poidsField.getText().isEmpty() || !isValidFloat(poidsField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid weight.");
            return false;
        }
        return true;
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
