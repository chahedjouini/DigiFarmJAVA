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

    public class ModifyMachine {

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
        @FXML private Button btnAnnuler;
        @FXML private Button btnModifier;

        private Machine machineToModify;
        private final MachineService machineService = new MachineService();

        public void setMachineData(Machine machine) {
            this.machineToModify = machine;
            populateFields();
        }

        @FXML
        public void initialize() {
            // Initialize the state ComboBox
            etatComboBox.getItems().addAll(
                    "en_maintenance",
                    "actif",
                    "inactif"

            );

            // Configure text field validations
            configureTextField(nomField, MAX_NAME_LENGTH);
            configureTextField(typeField, MAX_TYPE_LENGTH);
            configureTextField(etatPredField, MAX_ETAT_PRED_LENGTH);
        }

        private void populateFields() {
            if (machineToModify != null) {
                nomField.setText(machineToModify.getNom());
                typeField.setText(machineToModify.getType());
                dateAchatPicker.setValue(machineToModify.getDate_achat().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
                etatPredField.setText(machineToModify.getEtat_pred());
                etatComboBox.setValue(machineToModify.getEtat());
            }
        }

        private void configureTextField(TextField textField, int maxLength) {
            textField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();

                if (newText.isEmpty()) return change;

                // Auto-capitalize first letter
                if (change.getCaretPosition() == 1 && Character.isLowerCase(newText.charAt(0))) {
                    change.setText(change.getText().toUpperCase());
                    newText = change.getControlNewText();
                }

                if (newText.length() > maxLength) return null;

                return ALPHA_PATTERN.matcher(newText).matches() ? change : null;
            }));
        }

        @FXML
        void modifierMachine(ActionEvent event) {
            if (!validateInputs()) {
                return;
            }

            try {
                updateMachineFromFields();
                machineService.update(machineToModify);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Machine modifiée avec succès !");
                closeWindow();
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

        private void updateMachineFromFields() {
            machineToModify.setNom(nomField.getText().trim());
            machineToModify.setType(typeField.getText().trim());
            machineToModify.setDate_achat(Date.from(
                    dateAchatPicker.getValue().atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            ));
            machineToModify.setEtat_pred(etatPredField.getText().trim());
            machineToModify.setEtat(etatComboBox.getValue());
        }

        @FXML
        void annulerModification(ActionEvent event) {
            closeWindow();
        }

        private void closeWindow() {
            Stage stage = (Stage) btnAnnuler.getScene().getWindow();
            stage.close();
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
    }
