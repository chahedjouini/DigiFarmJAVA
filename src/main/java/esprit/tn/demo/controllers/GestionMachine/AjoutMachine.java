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
    private static final int MAX_ETAT_PRED_LENGTH = 100;

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private DatePicker dateAchatPicker;
    @FXML private TextField etatPredField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private Button annulerButton;
    @FXML private Button btnAjout;

    private final MachineService machineService = new MachineService();

    @FXML
    public void initialize() {
        // Initialize the state ComboBox
        etatComboBox.getItems().addAll(
                "en_maintenance",
                "actif",
                "inactif"

        );
        etatComboBox.setValue("en_maintenance");

        // Configure text field validations
        configureTextField(nomField, MAX_NAME_LENGTH);
        configureTextField(typeField, MAX_TYPE_LENGTH);
        configureTextField(etatPredField, MAX_ETAT_PRED_LENGTH);
    }

    private void configureTextField(TextField textField, int maxLength) {
        // Text formatter for real-time validation
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Skip validation if empty
            if (newText.isEmpty()) return change;

            // Auto-capitalize first letter
            if (change.getCaretPosition() == 1 && Character.isLowerCase(newText.charAt(0))) {
                change.setText(change.getText().toUpperCase());
                newText = change.getControlNewText(); // Update newText
            }

            // Validate length
            if (newText.length() > maxLength) {
                return null;
            }

            // Validate alphabetic pattern
            return ALPHA_PATTERN.matcher(newText).matches() ? change : null;
        }));
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            Machine newMachine = createMachineFromInput();
            machineService.add(newMachine);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Machine ajoutée avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        // Name validation
        if (!validateAlphaField(nomField, "Nom", MAX_NAME_LENGTH, true)) {
            return false;
        }

        // Type validation
        if (!validateAlphaField(typeField, "Type", MAX_TYPE_LENGTH, true)) {
            return false;
        }

        // Date validation
        if (dateAchatPicker.getValue() == null) {
            showAlert("Erreur", "La date d'achat est obligatoire !");
            dateAchatPicker.requestFocus();
            return false;
        }
        if (dateAchatPicker.getValue().isAfter(LocalDate.now())) {
            showAlert("Erreur", "La date d'achat ne peut pas être dans le futur !");
            dateAchatPicker.requestFocus();
            return false;
        }

        // State validation
        if (etatComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner un état !");
            etatComboBox.requestFocus();
            return false;
        }

        // Optional previous state validation
        if (!etatPredField.getText().isEmpty() &&
                !validateAlphaField(etatPredField, "État précédent", MAX_ETAT_PRED_LENGTH, false)) {
            return false;
        }

        return true;
    }

    private boolean validateAlphaField(TextField field, String fieldName, int maxLength, boolean required) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            if (required) {
                showAlert("Erreur", fieldName + " est obligatoire !");
                field.requestFocus();
                return false;
            }
            return true;
        }

        if (!ALPHA_PATTERN.matcher(value).matches()) {
            showAlert("Erreur", fieldName + " doit:\n- Commencer par une majuscule\n- Contenir uniquement des lettres");
            field.requestFocus();
            return false;
        }

        if (value.length() > maxLength) {
            showAlert("Erreur", fieldName + " ne doit pas dépasser " + maxLength + " caractères");
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
                dateAchatPicker.getValue().atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        ));

        if (!etatPredField.getText().trim().isEmpty()) {
            machine.setEtat_pred(etatPredField.getText().trim());
        }

        machine.setEtat(etatComboBox.getValue());
        machine.setOwner_id(1); // Default or get from session

        return machine;
    }


    @FXML
    private void handleAnnulerButton(ActionEvent event) {
        try {
            // Load using the correct path (relative to resources folder)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/machine-update.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) annulerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Échec du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void clearFields() {
        nomField.clear();
        typeField.clear();
        dateAchatPicker.setValue(null);
        etatPredField.clear();
        etatComboBox.setValue("en_maintenance");
    }
}