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
import javafx.util.StringConverter;

public class SuiviUpdateController {

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
    private Suivi suivi;

    public void setSuivi(Suivi suivi) {
        this.suivi = suivi;
        if (suivi != null) {
            populateFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No Suivi data provided for update.");
        }
    }

    @FXML
    public void initialize() {
        // Populate ComboBoxes
        animalComboBox.setItems(FXCollections.observableArrayList(animalService.getAllAnimals()));
        veterinaireComboBox.setItems(FXCollections.observableArrayList(veterinaireService.getAllVeterinaires()));

        // Configure ComboBox to display names for Animal
        animalComboBox.setConverter(new StringConverter<Animal>() {
            @Override
            public String toString(Animal animal) {
                return animal != null ? animal.getNom() : "";
            }

            @Override
            public Animal fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return animalComboBox.getItems().stream()
                        .filter(animal -> animal.getNom().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Configure ComboBox to display names for Veterinaire
        veterinaireComboBox.setConverter(new StringConverter<Veterinaire>() {
            @Override
            public String toString(Veterinaire veterinaire) {
                return veterinaire != null ? veterinaire.getNom() : "";
            }

            @Override
            public Veterinaire fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return veterinaireComboBox.getItems().stream()
                        .filter(veterinaire -> veterinaire.getNom().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void populateFields() {
        animalComboBox.setValue(suivi.getAnimal());
        temperatureField.setText(String.valueOf(suivi.getTemperature()));
        rythmeCardiaqueField.setText(String.valueOf(suivi.getRythmeCardiaque()));
        etatField.setText(suivi.getEtat());
        idClientField.setText(String.valueOf(suivi.getIdClient()));
        analysisField.setText(suivi.getAnalysis());
        veterinaireComboBox.setValue(suivi.getVeterinaire());
    }

    @FXML
    private void handleUpdateSuivi() {
        if (suivi == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Suivi data to update. Please ensure a Suivi is selected.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        suivi.setAnimal(animalComboBox.getValue());
        suivi.setTemperature(Float.parseFloat(temperatureField.getText()));
        suivi.setRythmeCardiaque(Float.parseFloat(rythmeCardiaqueField.getText()));
        suivi.setEtat(etatField.getText());
        suivi.setIdClient(Integer.parseInt(idClientField.getText()));
        suivi.setAnalysis(analysisField.getText());
        suivi.setVeterinaire(veterinaireComboBox.getValue());

        try {
            suiviService.updateSuivi(suivi);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Suivi updated successfully!");
            Stage stage = (Stage) animalComboBox.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Suivi: " + e.getMessage());
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
}