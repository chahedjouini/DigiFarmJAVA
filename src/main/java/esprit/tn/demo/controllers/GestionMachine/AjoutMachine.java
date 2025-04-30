package esprit.tn.demo.controllers.GestionMachine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import esprit.tn.demo.entities.GestionMachine.Machine;
import esprit.tn.demo.services.GestionMachine.MachineService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

public class AjoutMachine {

    // Regex patterns and constants
    private static final Pattern ALPHA_PATTERN = Pattern.compile("^[A-Z][a-zA-Z]*(\\s[A-Z][a-zA-Z]*)*$");
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_TYPE_LENGTH = 30;

    @FXML private TextField nomField;
    @FXML private Label nomErrorLabel;
    @FXML private TextField typeField;
    @FXML private Label typeErrorLabel;
    @FXML private DatePicker dateAchatPicker;
    @FXML private Label dateErrorLabel;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private Label etatErrorLabel;
    @FXML private ComboBox<String> etatPredComboBox;
    @FXML private Label etatPredErrorLabel;
    @FXML private Button annulerButton;
    @FXML private Button btnAjout;
    @FXML private Button logout;

    private final MachineService machineService = new MachineService();

    @FXML
    public void initialize() {
        // Initialize the state ComboBox
        etatComboBox.getItems().addAll("en_maintenance", "actif", "inactif");
        etatComboBox.setValue("en_maintenance");

        // Initialize the previous state ComboBox
        etatPredComboBox.getItems().addAll("en_maintenance", "actif", "inactif");

        // Configure text field validations
        configureTextField(nomField, MAX_NAME_LENGTH);
        configureTextField(typeField, MAX_TYPE_LENGTH);

        // Clear all error labels initially
        clearErrorLabels();
    }

    private void configureTextField(TextField textField, int maxLength) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;
            if (change.getCaretPosition() == 1 && Character.isLowerCase(newText.charAt(0))) {
                change.setText(change.getText().toUpperCase());
                newText = change.getControlNewText();
            }
            if (newText.length() > maxLength) return null;
            return ALPHA_PATTERN.matcher(newText).matches() ? change : null;
        }));

        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            getErrorLabelForField(textField).setText("");
        });
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (!validateInputs()) return;
        try {
            Machine newMachine = createMachineFromInput();
            machineService.add(newMachine);
            showSuccessMessage("Machine ajoutée avec succès !");
            clearFields();
        } catch (Exception e) {
            showErrorMessage("Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        clearErrorLabels();
        boolean isValid = true;

        if (!validateAlphaField(nomField, "Nom", MAX_NAME_LENGTH, true, nomErrorLabel)) isValid = false;
        if (!validateAlphaField(typeField, "Type", MAX_TYPE_LENGTH, true, typeErrorLabel)) isValid = false;

        if (dateAchatPicker.getValue() == null) {
            dateErrorLabel.setText("La date d'achat est obligatoire !");
            dateAchatPicker.requestFocus();
            isValid = false;
        } else if (dateAchatPicker.getValue().isAfter(LocalDate.now())) {
            dateErrorLabel.setText("La date d'achat ne peut pas être dans le futur !");
            dateAchatPicker.requestFocus();
            isValid = false;
        }

        if (etatComboBox.getValue() == null) {
            etatErrorLabel.setText("Veuillez sélectionner un état !");
            etatComboBox.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateAlphaField(TextField field, String fieldName, int maxLength, boolean required, Label errorLabel) {
        String value = field.getText().trim();
        if (value.isEmpty() && required) {
            errorLabel.setText(fieldName + " est obligatoire !");
            field.requestFocus();
            return false;
        }
        if (!value.isEmpty() && !ALPHA_PATTERN.matcher(value).matches()) {
            errorLabel.setText(fieldName + " doit commencer par une majuscule et contenir uniquement des lettres");
            field.requestFocus();
            return false;
        }
        if (value.length() > maxLength) {
            errorLabel.setText(fieldName + " ne doit pas dépasser " + maxLength + " caractères");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private Machine createMachineFromInput() {
        Machine machine = new Machine();
        machine.setNom(nomField.getText().trim());
        machine.setType(typeField.getText().trim());
        machine.setDate_achat(Date.from(
                dateAchatPicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        ));
        if (etatPredComboBox.getValue() != null) {
            machine.setEtat_pred(etatPredComboBox.getValue());
        }
        machine.setEtat(etatComboBox.getValue());
        machine.setOwner_id(1); // Default or get from session
        return machine;
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorMessage("Échec du chargement de la page de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnulerButton(ActionEvent event) {
        // Close the current window
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearErrorLabels() {
        nomErrorLabel.setText("");
        typeErrorLabel.setText("");
        dateErrorLabel.setText("");
        etatPredErrorLabel.setText("");
        etatErrorLabel.setText("");
    }

    private void clearFields() {
        nomField.clear();
        typeField.clear();
        dateAchatPicker.setValue(null);
        etatPredComboBox.getSelectionModel().clearSelection();
        etatComboBox.setValue("en_maintenance");
        clearErrorLabels();
    }

    private Label getErrorLabelForField(TextField field) {
        if (field == nomField) return nomErrorLabel;
        if (field == typeField) return typeErrorLabel;
        return new Label();
    }
}