package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Maintenance;
import esprit.tn.demo.entities.GestionMachine.Technicien;
import esprit.tn.demo.entities.GestionMachine.Machine;
import esprit.tn.demo.services.GestionMachine.MaintenanceService;
import esprit.tn.demo.services.GestionMachine.TechnicienService;
import esprit.tn.demo.services.GestionMachine.MachineService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AjoutMaintenance {

    // Regex patterns and constants
    private static final Pattern ETAT_PRED_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-]{0,100}$");
    private static final double MIN_COUT = 0.0;
    private static final int MIN_TEMPERATURE = -50;
    private static final int MAX_TEMPERATURE = 100;
    private static final int MIN_HUMIDITE = 0;
    private static final int MAX_HUMIDITE = 100;
    private static final double MIN_CONSO = 0.0;

    @FXML private TextField coutField;
    @FXML private TextField temperatureField;
    @FXML private TextField humiditeField;
    @FXML private TextField consoCarburantField;
    @FXML private TextField consoEnergieField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField etatPredField;
    @FXML private DatePicker dateEntretienPicker;
    @FXML private ComboBox<Integer> machineIdComboBox;
    @FXML private ComboBox<String> technicienIdComboBox;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final TechnicienService technicienService = new TechnicienService();
    private final MachineService machineService = new MachineService();
    private Map<String, Integer> techNameToId = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Planifiée", "En cours", "Terminée", "Annulée"
        ));
        statusComboBox.setValue("Planifiée");

        // Configure DatePicker to restrict future dates and limit range
        dateEntretienPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()) ||
                        date.isBefore(LocalDate.now().minusYears(10)));
            }
        });

        // Configure text field validations
        configureNumericField(coutField, true, MIN_COUT);
        configureNumericField(consoCarburantField, false, MIN_CONSO);
        configureNumericField(consoEnergieField, false, MIN_CONSO);
        configureIntegerField(temperatureField, false, MIN_TEMPERATURE, MAX_TEMPERATURE);
        configureIntegerField(humiditeField, false, MIN_HUMIDITE, MAX_HUMIDITE);
        configureEtatPredField();

        // Add real-time visual feedback
        addRealTimeValidationFeedback(coutField);
        addRealTimeValidationFeedback(temperatureField);
        addRealTimeValidationFeedback(humiditeField);
        addRealTimeValidationFeedback(consoCarburantField);
        addRealTimeValidationFeedback(consoEnergieField);
        addRealTimeValidationFeedback(etatPredField);

        loadMachineIds();
        loadTechnicienIds();
    }

    private void configureNumericField(TextField field, boolean required, double minValue) {
        field.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() && !required) {
                return change;
            }
            try {
                double value = Double.parseDouble(newText);
                if (value < minValue) {
                    return null;
                }
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    private void configureIntegerField(TextField field, boolean required, int minValue, int maxValue) {
        field.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() && !required) {
                return change;
            }
            try {
                int value = Integer.parseInt(newText);
                if (value < minValue || value > maxValue) {
                    return null;
                }
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    private void configureEtatPredField() {
        etatPredField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText().trim();
            if (newText.length() > 100 || !ETAT_PRED_PATTERN.matcher(newText).matches()) {
                return null;
            }
            return change;
        }));
    }

    private void addRealTimeValidationFeedback(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            String trimmed = newValue.trim();
            boolean valid;
            if (field == etatPredField) {
                valid = trimmed.isEmpty() || ETAT_PRED_PATTERN.matcher(trimmed).matches();
            } else {
                try {
                    if (field == temperatureField || field == humiditeField) {
                        if (trimmed.isEmpty()) {
                            valid = true;
                        } else {
                            int value = Integer.parseInt(trimmed);
                            valid = (field == temperatureField) ?
                                    value >= MIN_TEMPERATURE && value <= MAX_TEMPERATURE :
                                    value >= MIN_HUMIDITE && value <= MAX_HUMIDITE;
                        }
                    } else {
                        if (trimmed.isEmpty() && (field != coutField)) {
                            valid = true;
                        } else {
                            double value = Double.parseDouble(trimmed);
                            valid = value >= (field == coutField ? MIN_COUT : MIN_CONSO);
                        }
                    }
                } catch (NumberFormatException e) {
                    valid = false;
                }
            }
            field.setStyle(valid ? "-fx-border-color: none;" : "-fx-border-color: red;");
        });
    }

    private void loadMachineIds() {
        ObservableList<Integer> machineIds = FXCollections.observableArrayList();
        try {
            List<Machine> machines = machineService.getAll();
            for (Machine machine : machines) {
                machineIds.add(machine.getId_machine());
            }
            machineIdComboBox.setItems(machineIds);
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement des IDs de machines : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadTechnicienIds() {
        ObservableList<String> technicienNames = FXCollections.observableArrayList();
        try {
            List<Technicien> techniciens = technicienService.getAll();
            techNameToId.clear();
            for (Technicien tech : techniciens) {
                String displayName = tech.getId() + " - " + tech.getName() + " " + tech.getPrenom();
                technicienNames.add(displayName);
                techNameToId.put(displayName, tech.getId());
            }
            technicienIdComboBox.setItems(technicienNames);
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement des IDs de techniciens : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateInputs()) {
            return;
        }

        try {
            Maintenance maintenance = new Maintenance();
            maintenance.setId_machine_id(machineIdComboBox.getValue());
            maintenance.setId_technicien_id(techNameToId.get(technicienIdComboBox.getValue()));
            maintenance.setDate_entretien(java.sql.Date.valueOf(dateEntretienPicker.getValue()));
            maintenance.setCout(Double.parseDouble(coutField.getText().trim()));
            maintenance.setStatus(statusComboBox.getValue());
            String etatPred = etatPredField.getText().trim();
            maintenance.setEtat_pred(etatPred.isEmpty() ? null : etatPred);

            // Optional fields
            maintenance.setTemperature(parseIntOrNull(temperatureField.getText().trim()));
            maintenance.setHumidite(parseIntOrNull(humiditeField.getText().trim()));
            maintenance.setConso_carburant(parseDoubleOrNull(consoCarburantField.getText().trim()));
            maintenance.setConso_energie(parseDoubleOrNull(consoEnergieField.getText().trim()));

            maintenanceService.add(maintenance);
            showAlert("Succès", "Maintenance ajoutée avec succès", Alert.AlertType.INFORMATION);
            clearFields();
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout de la maintenance : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Integer parseIntOrNull(String value) {
        try {
            return value.trim().isEmpty() ? null : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String value) {
        try {
            return value.trim().isEmpty() ? null : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInputs() {
        // Date validation
        if (dateEntretienPicker.getValue() == null) {
            showAlert("Validation", "La date d'entretien est requise", Alert.AlertType.WARNING);
            dateEntretienPicker.requestFocus();
            return false;
        }

        // Cost validation
        String coutText = coutField.getText().trim();
        try {
            double cout = Double.parseDouble(coutText);
            if (cout < MIN_COUT) {
                showAlert("Validation", "Le coût doit être positif ou zéro", Alert.AlertType.WARNING);
                coutField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation", "Le coût doit être un nombre valide", Alert.AlertType.WARNING);
            coutField.requestFocus();
            return false;
        }

        // Status validation
        if (statusComboBox.getValue() == null) {
            showAlert("Validation", "Le statut est requis", Alert.AlertType.WARNING);
            statusComboBox.requestFocus();
            return false;
        }

        // Machine ID validation
        if (machineIdComboBox.getValue() == null) {
            showAlert("Validation", "La machine est requise", Alert.AlertType.WARNING);
            machineIdComboBox.requestFocus();
            return false;
        }

        // Technician ID validation
        if (technicienIdComboBox.getValue() == null) {
            showAlert("Validation", "Le technicien est requis", Alert.AlertType.WARNING);
            technicienIdComboBox.requestFocus();
            return false;
        }

        // Optional fields validation
        if (!temperatureField.getText().trim().isEmpty()) {
            try {
                int temp = Integer.parseInt(temperatureField.getText().trim());
                if (temp < MIN_TEMPERATURE || temp > MAX_TEMPERATURE) {
                    showAlert("Validation", "La température doit être entre " + MIN_TEMPERATURE + " et " + MAX_TEMPERATURE, Alert.AlertType.WARNING);
                    temperatureField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "La température doit être un nombre entier valide", Alert.AlertType.WARNING);
                temperatureField.requestFocus();
                return false;
            }
        }

        if (!humiditeField.getText().trim().isEmpty()) {
            try {
                int humidite = Integer.parseInt(humiditeField.getText().trim());
                if (humidite < MIN_HUMIDITE || humidite > MAX_HUMIDITE) {
                    showAlert("Validation", "L'humidité doit être entre " + MIN_HUMIDITE + " et " + MAX_HUMIDITE, Alert.AlertType.WARNING);
                    humiditeField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "L'humidité doit être un nombre entier valide", Alert.AlertType.WARNING);
                humiditeField.requestFocus();
                return false;
            }
        }

        if (!consoCarburantField.getText().trim().isEmpty()) {
            try {
                double conso = Double.parseDouble(consoCarburantField.getText().trim());
                if (conso < MIN_CONSO) {
                    showAlert("Validation", "La consommation de carburant doit être positive ou zéro", Alert.AlertType.WARNING);
                    consoCarburantField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "La consommation de carburant doit être un nombre valide", Alert.AlertType.WARNING);
                consoCarburantField.requestFocus();
                return false;
            }
        }

        if (!consoEnergieField.getText().trim().isEmpty()) {
            try {
                double conso = Double.parseDouble(consoEnergieField.getText().trim());
                if (conso < MIN_CONSO) {
                    showAlert("Validation", "La consommation d'énergie doit être positive ou zéro", Alert.AlertType.WARNING);
                    consoEnergieField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation", "La consommation d'énergie doit être un nombre valide", Alert.AlertType.WARNING);
                consoEnergieField.requestFocus();
                return false;
            }
        }

        // EtatPred validation
        String etatPred = etatPredField.getText().trim();
        if (!etatPred.isEmpty() && !ETAT_PRED_PATTERN.matcher(etatPred).matches()) {
            showAlert("Validation", "L'état précédent doit contenir uniquement des lettres, chiffres, espaces ou tirets", Alert.AlertType.WARNING);
            etatPredField.requestFocus();
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        coutField.clear();
        temperatureField.clear();
        humiditeField.clear();
        consoCarburantField.clear();
        consoEnergieField.clear();
        etatPredField.clear();
        dateEntretienPicker.setValue(null);
        statusComboBox.setValue("Planifiée");
        machineIdComboBox.setValue(null);
        technicienIdComboBox.setValue(null);
        resetFieldStyles();
    }

    private void resetFieldStyles() {
        coutField.setStyle("-fx-border-color: none;");
        temperatureField.setStyle("-fx-border-color: none;");
        humiditeField.setStyle("-fx-border-color: none;");
        consoCarburantField.setStyle("-fx-border-color: none;");
        consoEnergieField.setStyle("-fx-border-color: none;");
        etatPredField.setStyle("-fx-border-color: none;");
    }
}