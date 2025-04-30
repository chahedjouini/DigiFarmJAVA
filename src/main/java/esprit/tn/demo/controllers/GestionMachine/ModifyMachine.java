package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Machine;
import esprit.tn.demo.services.GestionMachine.MachineService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

public class ModifyMachine {

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
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;

    private MachineService machineService = new MachineService();
    private Machine machineToModify;

    public void setMachineData(Machine machine) {
        this.machineToModify = machine;
        populateFields();
    }

    @FXML
    public void initialize() {
        etatComboBox.getItems().addAll("en_maintenance", "actif", "inactif");
        etatPredComboBox.getItems().addAll("en_maintenance", "actif", "inactif");
        configureTextField(nomField, MAX_NAME_LENGTH);
        configureTextField(typeField, MAX_TYPE_LENGTH);
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

    private void populateFields() {
        if (machineToModify != null) {
            nomField.setText(machineToModify.getNom());
            typeField.setText(machineToModify.getType());
            if (machineToModify.getDate_achat() != null) {
                // Convert java.sql.Date to LocalDate
                LocalDate localDate = ((java.sql.Date) machineToModify.getDate_achat()).toLocalDate();
                dateAchatPicker.setValue(localDate);
            }
            etatPredComboBox.setValue(machineToModify.getEtat_pred());
            etatComboBox.setValue(machineToModify.getEtat());
        }
    }

    @FXML
    private void modifierMachine(ActionEvent event) {
        if (!validateInputs()) return;
        try {
            updateMachineFromFields();
            machineService.update(machineToModify);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Machine mise à jour avec succès !");
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour : vérifiez la connexion à la base de données.");
            e.printStackTrace();
        }
    }

    @FXML
    private void annulerModification(ActionEvent event) {
        closeWindow();
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

    private void updateMachineFromFields() {
        machineToModify.setNom(nomField.getText().trim());
        machineToModify.setType(typeField.getText().trim());
        machineToModify.setDate_achat(Date.from(
                dateAchatPicker.getValue().atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        ));
        machineToModify.setEtat_pred(etatPredComboBox.getValue());
        machineToModify.setEtat(etatComboBox.getValue());
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

    private void clearErrorLabels() {
        nomErrorLabel.setText("");
        typeErrorLabel.setText("");
        dateErrorLabel.setText("");
        etatErrorLabel.setText("");
        etatPredErrorLabel.setText("");
    }

    private Label getErrorLabelForField(TextField field) {
        if (field == nomField) return nomErrorLabel;
        if (field == typeField) return typeErrorLabel;
        return new Label();
    }
}