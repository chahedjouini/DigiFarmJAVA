package controllers;

import entities.Suivi;
import services.SuiviServiceImpl;

import entities.Animal;

import entities.Veterinaire;
import services.AnimalServiceImpl;
import services.VeterinaireServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class SuiviAddController {

    @FXML private ComboBox<Animal> animalComboBox;
    @FXML private TextField temperatureField;
    @FXML private TextField rythmeCardiaqueField;
    @FXML private TextField etatField;
    @FXML private TextField idClientField;
    @FXML private TextField analysisField;
    @FXML private ComboBox<Veterinaire> veterinaireComboBox;

    @FXML private Label animalErrorLabel;
    @FXML private Label temperatureErrorLabel;
    @FXML private Label rythmeCardiaqueErrorLabel;
    @FXML private Label etatErrorLabel;
    @FXML private Label idClientErrorLabel;
    @FXML private Label analysisErrorLabel;
    @FXML private Label veterinaireErrorLabel;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();
    private final AnimalServiceImpl animalService = new AnimalServiceImpl();
    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();

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

    @FXML
    private void handleAddSuivi() {
        // Réinitialiser les messages d'erreur
        animalErrorLabel.setText("");
        temperatureErrorLabel.setText("");
        rythmeCardiaqueErrorLabel.setText("");
        etatErrorLabel.setText("");
        idClientErrorLabel.setText("");
        analysisErrorLabel.setText("");
        veterinaireErrorLabel.setText("");

        boolean isValid = true;

        if (animalComboBox.getValue() == null) {
            animalErrorLabel.setText("L'animal est obligatoire.");
            isValid = false;
        }

        if (temperatureField.getText().isEmpty()) {
            temperatureErrorLabel.setText("La température est obligatoire.");
            isValid = false;
        } else if (!isValidFloat(temperatureField.getText())) {
            temperatureErrorLabel.setText("La température doit être un nombre valide.");
            isValid = false;
        }

        if (rythmeCardiaqueField.getText().isEmpty()) {
            rythmeCardiaqueErrorLabel.setText("Le rythme cardiaque est obligatoire.");
            isValid = false;
        } else if (!isValidFloat(rythmeCardiaqueField.getText())) {
            rythmeCardiaqueErrorLabel.setText("Le rythme cardiaque doit être un nombre valide.");
            isValid = false;
        }

        if (etatField.getText().isEmpty()) {
            etatErrorLabel.setText("L'état est obligatoire.");
            isValid = false;
        }

        if (idClientField.getText().isEmpty()) {
            idClientErrorLabel.setText("L'ID Client est obligatoire.");
            isValid = false;
        } else if (!isValidInteger(idClientField.getText())) {
            idClientErrorLabel.setText("L'ID Client doit être un nombre entier valide.");
            isValid = false;
        } else if (Integer.parseInt(idClientField.getText()) <= 0) {
            idClientErrorLabel.setText("L'ID Client doit être un nombre positif.");
            isValid = false;
        }

        if (analysisField.getText().isEmpty()) {
            analysisErrorLabel.setText("L'analyse est obligatoire.");
            isValid = false;
        }

        if (veterinaireComboBox.getValue() == null) {
            veterinaireErrorLabel.setText("Le vétérinaire est obligatoire.");
            isValid = false;
        }

        if (isValid) {
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
                showAlert(Alert.AlertType.INFORMATION, "Success", "Suivi ajouté avec succès !");
                Stage stage = (Stage) animalComboBox.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le suivi : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) animalComboBox.getScene().getWindow();
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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}