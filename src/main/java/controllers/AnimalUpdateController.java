package controllers;


import entities.Animal;
import services.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AnimalUpdateController {

    @FXML private TextField idField, nomField, typeField, raceField, ageField, poidsField;

    private final AnimalServiceImpl service = new AnimalServiceImpl();
    private Animal animal;

    public void setAnimal(Animal animal) {
        this.animal = animal;
        if (animal != null) {
            populateFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No Animal data provided for update.");
        }
    }

    @FXML
    public void initialize() {
        // Initialize any necessary components here
    }

    private void populateFields() {
        idField.setText(String.valueOf(animal.getId()));
        nomField.setText(animal.getNom());
        typeField.setText(animal.getType());
        raceField.setText(animal.getRace());
        ageField.setText(String.valueOf(animal.getAge()));
        poidsField.setText(String.valueOf(animal.getPoids()));
    }

    @FXML
    private void handleUpdateAnimal() {
        if (animal == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Animal data to update. Please ensure an Animal is selected.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        animal.setNom(nomField.getText());
        animal.setType(typeField.getText());
        animal.setAge(Integer.parseInt(ageField.getText()));
        animal.setPoids(Float.parseFloat(poidsField.getText()));
        animal.setRace(raceField.getText());

        try {
            service.updateAnimal(animal);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Animal updated successfully!");
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Animal: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) idField.getScene().getWindow();
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
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the animal's race.");
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
