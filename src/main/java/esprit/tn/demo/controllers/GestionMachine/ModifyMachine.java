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

public class ModifyMachine {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private DatePicker dateAchatPicker;
    @FXML private TextField etatPredField;
    @FXML private ComboBox<String> etatComboBox;
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
    }

    private void populateFields() {
        if (machineToModify != null) {
            nomField.setText(machineToModify.getNom());
            typeField.setText(machineToModify.getType());
            if (machineToModify.getDate_achat() != null) {
                dateAchatPicker.setValue(machineToModify.getDate_achat().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }
            etatPredField.setText(machineToModify.getEtat_pred() != null ? machineToModify.getEtat_pred() : "");
            etatComboBox.setValue(machineToModify.getEtat());
        }
    }

    @FXML
    private void modifierMachine(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }
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
        String nomText = nomField.getText().trim();
        if (nomText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom est obligatoire !");
            nomField.requestFocus();
            return false;
        }
        String typeText = typeField.getText().trim();
        if (typeText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le type est obligatoire !");
            typeField.requestFocus();
            return false;
        }
        if (dateAchatPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date d'achat est obligatoire !");
            dateAchatPicker.requestFocus();
            return false;
        }
        if (dateAchatPicker.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date d'achat ne peut pas être dans le futur !");
            dateAchatPicker.requestFocus();
            return false;
        }
        if (etatComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'état est obligatoire !");
            etatComboBox.requestFocus();
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
        String etatPredText = etatPredField.getText().trim();
        machineToModify.setEtat_pred(etatPredText.isEmpty() ? null : etatPredText);
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
}