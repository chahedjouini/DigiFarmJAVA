package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Maintenance;
import esprit.tn.demo.services.GestionMachine.MaintenanceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Date;

public class ModifyMaintenanceController {

    @FXML private TextField machineIdField;
    @FXML private TextField technicienIdField;
    @FXML private DatePicker dateEntretienPicker;
    @FXML private TextField coutField;
    @FXML private TextField temperatureField;
    @FXML private TextField humiditeField;
    @FXML private TextField consoCarburantField;
    @FXML private TextField consoEnergieField;
    @FXML private TextField statusField;
    @FXML private TextField etatPredField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private MaintenanceService maintenanceService = new MaintenanceService();
    private Maintenance maintenance;

    public void setMaintenanceData(Maintenance maintenance) {
        this.maintenance = maintenance;
        populateFields();
    }

    private void populateFields() {
        if (maintenance != null) {
            machineIdField.setText(String.valueOf(maintenance.getId_machine_id()));
            technicienIdField.setText(maintenance.getId_technicien_id() != null ?
                    String.valueOf(maintenance.getId_technicien_id()) : "");
            if (maintenance.getDate_entretien() != null) {
                dateEntretienPicker.setValue(new java.sql.Date(maintenance.getDate_entretien().getTime()).toLocalDate());
            }
            coutField.setText(String.valueOf(maintenance.getCout()));
            temperatureField.setText(maintenance.getTemperature() != null ?
                    String.valueOf(maintenance.getTemperature()) : "");
            humiditeField.setText(maintenance.getHumidite() != null ?
                    String.valueOf(maintenance.getHumidite()) : "");
            consoCarburantField.setText(maintenance.getConso_carburant() != null ?
                    String.valueOf(maintenance.getConso_carburant()) : "");
            consoEnergieField.setText(maintenance.getConso_energie() != null ?
                    String.valueOf(maintenance.getConso_energie()) : "");
            statusField.setText(maintenance.getStatus() != null ? maintenance.getStatus() : "");
            etatPredField.setText(maintenance.getEtat_pred() != null ? maintenance.getEtat_pred() : "");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            updateMaintenanceFromFields();
            maintenanceService.update(maintenance);
            showAlert("Succès", "Maintenance mise à jour avec succès !");
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInputs() {
        // Machine ID (required, integer)
        String machineIdText = machineIdField.getText().trim();
        if (machineIdText.isEmpty()) {
            showAlert("Erreur", "L'ID de la machine est obligatoire !");
            machineIdField.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(machineIdText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de la machine doit être un nombre entier !");
            machineIdField.requestFocus();
            return false;
        }

        // Technicien ID (optional, integer)
        String technicienIdText = technicienIdField.getText().trim();
        if (!technicienIdText.isEmpty()) {
            try {
                Integer.parseInt(technicienIdText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID du technicien doit être un nombre entier !");
                technicienIdField.requestFocus();
                return false;
            }
        }

        // Date Entretien (required)
        if (dateEntretienPicker.getValue() == null) {
            showAlert("Erreur", "La date d'entretien est obligatoire !");
            dateEntretienPicker.requestFocus();
            return false;
        }

        // Cout (required, double)
        String coutText = coutField.getText().trim();
        if (coutText.isEmpty()) {
            showAlert("Erreur", "Le coût est obligatoire !");
            coutField.requestFocus();
            return false;
        }
        try {
            Double.parseDouble(coutText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le coût doit être un nombre valide !");
            coutField.requestFocus();
            return false;
        }

        // Temperature (optional, integer)
        String tempText = temperatureField.getText().trim();
        if (!tempText.isEmpty()) {
            try {
                Integer.parseInt(tempText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La température doit être un nombre entier !");
                temperatureField.requestFocus();
                return false;
            }
        }

        // Humidite (optional, integer)
        String humiditeText = humiditeField.getText().trim();
        if (!humiditeText.isEmpty()) {
            try {
                Integer.parseInt(humiditeText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'humidité doit être un nombre entier !");
                humiditeField.requestFocus();
                return false;
            }
        }

        // Conso Carburant (optional, double)
        String consoCarburantText = consoCarburantField.getText().trim();
        if (!consoCarburantText.isEmpty()) {
            try {
                Double.parseDouble(consoCarburantText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La consommation de carburant doit être un nombre valide !");
                consoCarburantField.requestFocus();
                return false;
            }
        }

        // Conso Energie (optional, double)
        String consoEnergieText = consoEnergieField.getText().trim();
        if (!consoEnergieText.isEmpty()) {
            try {
                Double.parseDouble(consoEnergieText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La consommation d'énergie doit être un nombre valide !");
                consoEnergieField.requestFocus();
                return false;
            }
        }

        // Status (required)
        if (statusField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le statut est obligatoire !");
            statusField.requestFocus();
            return false;
        }

        // Etat Pred (optional, no specific validation)
        return true;
    }

    private void updateMaintenanceFromFields() {
        maintenance.setId_machine_id(Integer.parseInt(machineIdField.getText().trim()));
        String technicienIdText = technicienIdField.getText().trim();
        maintenance.setId_technicien_id(technicienIdText.isEmpty() ? null : Integer.parseInt(technicienIdText));
        maintenance.setDate_entretien(java.sql.Date.valueOf(dateEntretienPicker.getValue()));
        maintenance.setCout(Double.parseDouble(coutField.getText().trim()));
        String tempText = temperatureField.getText().trim();
        maintenance.setTemperature(tempText.isEmpty() ? null : Integer.parseInt(tempText));
        String humiditeText = humiditeField.getText().trim();
        maintenance.setHumidite(humiditeText.isEmpty() ? null : Integer.parseInt(humiditeText));
        String consoCarburantText = consoCarburantField.getText().trim();
        maintenance.setConso_carburant(consoCarburantText.isEmpty() ? null : Double.parseDouble(consoCarburantText));
        String consoEnergieText = consoEnergieField.getText().trim();
        maintenance.setConso_energie(consoEnergieText.isEmpty() ? null : Double.parseDouble(consoEnergieText));
        maintenance.setStatus(statusField.getText().trim());
        String etatPredText = etatPredField.getText().trim();
        maintenance.setEtat_pred(etatPredText.isEmpty() ? null : etatPredText);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}