package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SuiviAddController {

    @FXML private ComboBox<Animal> animalComboBox;
    @FXML private TextField temperatureField;
    @FXML private TextField rythmeCardiaqueField;
    @FXML private TextField etatField;
    @FXML private TextField idClientField;
    @FXML private TextField analysisField;
    @FXML private ComboBox<Veterinaire> veterinaireComboBox;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();
    private final AnimalServiceImpl animalService = new AnimalServiceImpl();
    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();

    @FXML
    public void initialize() {
        animalComboBox.setItems(FXCollections.observableArrayList(animalService.getAllAnimals()));
        veterinaireComboBox.setItems(FXCollections.observableArrayList(veterinaireService.getAllVeterinaires()));
    }

    @FXML
    private void handleAddSuivi() {
        if (!validateInputs()) {
            return;
        }

        Suivi suivi = new Suivi();
        suivi.setAnimal(animalComboBox.getValue());
        suivi.setTemperature(Float.parseFloat(temperatureField.getText()));
        suivi.setRythmeCardiaque(Float.parseFloat(rythmeCardiaqueField.getText()));
        suivi.setEtat(etatField.getText());
        suivi.setIdClient(Integer.parseInt(idClientField.getText()));
        suivi.setAnalysis(analysisField.getText());
        suivi.setVeterinaire(veterinaireComboBox.getValue());

        try {
            suiviService.addSuivi(suivi);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Suivi added successfully!");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Suivi: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) animalComboBox.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        if (animalComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an animal.");
            return false;
        }
        if (temperatureField.getText().isEmpty() || !isValidFloat(temperatureField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid temperature.");
            return false;
        }
        if (rythmeCardiaqueField.getText().isEmpty() || !isValidFloat(rythmeCardiaqueField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid heart rate.");
            return false;
        }
        if (etatField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the state.");
            return false;
        }
        if (idClientField.getText().isEmpty() || !isValidInteger(idClientField.getText())) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid client ID.");
            return false;
        }
        if (analysisField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the analysis.");
            return false;
        }
        if (veterinaireComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a veterinaire.");
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

    private void clearFields() {
        animalComboBox.setValue(null);
        temperatureField.clear();
        rythmeCardiaqueField.clear();
        etatField.clear();
        idClientField.clear();
        analysisField.clear();
        veterinaireComboBox.setValue(null);
    }
}