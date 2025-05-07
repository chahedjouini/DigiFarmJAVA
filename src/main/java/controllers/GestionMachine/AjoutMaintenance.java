package controllers.GestionMachine;

import services.MaintenancePredictionClient;
import entities.MaintenanceRecord;
import entities.GestionMachine.Maintenance;
import entities.GestionMachine.Technicien;
import entities.GestionMachine.Machine;
import services.GestionMachine.MaintenanceService;
import services.GestionMachine.TechnicienService;
import services.GestionMachine.MachineService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AjoutMaintenance {

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
    @FXML private Button predictButton;
    @FXML private Label resultLabel;
    @FXML private Label errorLabel;
    @FXML private TableView<MaintenanceRecord> historyTable;
    @FXML private TableColumn<MaintenanceRecord, String> dateColumn;
    @FXML private TableColumn<MaintenanceRecord, Double> coutColumn;
    @FXML private TableColumn<MaintenanceRecord, Integer> temperatureColumn;
    @FXML private TableColumn<MaintenanceRecord, Integer> humiditeColumn;
    @FXML private TableColumn<MaintenanceRecord, Double> consoCarburantColumn;
    @FXML private TableColumn<MaintenanceRecord, Double> consoEnergieColumn;
    @FXML private TableColumn<MaintenanceRecord, String> statusColumn;
    @FXML private TableColumn<MaintenanceRecord, Integer> idMachineColumn;

    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final TechnicienService technicienService = new TechnicienService();
    private final MachineService machineService = new MachineService();
    private final MaintenancePredictionClient predictionClient = new MaintenancePredictionClient();
    private Map<String, Integer> techNameToId = new HashMap<>();

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Planifiée", "En cours", "Terminée", "Annulée"
        ));
        statusComboBox.setValue("Planifiée");

        dateEntretienPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()) ||
                        date.isBefore(LocalDate.now().minusYears(10)));
            }
        });

        configureNumericField(coutField, true, MIN_COUT);
        configureNumericField(consoCarburantField, false, MIN_CONSO);
        configureNumericField(consoEnergieField, false, MIN_CONSO);
        configureIntegerField(temperatureField, false, MIN_TEMPERATURE, MAX_TEMPERATURE);
        configureIntegerField(humiditeField, false, MIN_HUMIDITE, MAX_HUMIDITE);
        configureEtatPredField();

        addRealTimeValidationFeedback(coutField);
        addRealTimeValidationFeedback(temperatureField);
        addRealTimeValidationFeedback(humiditeField);
        addRealTimeValidationFeedback(consoCarburantField);
        addRealTimeValidationFeedback(consoEnergieField);
        addRealTimeValidationFeedback(etatPredField);

        loadMachineIds();
        loadTechnicienIds();
        initializeHistoryTable();
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

    private void initializeHistoryTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEntretien"));
        coutColumn.setCellValueFactory(new PropertyValueFactory<>("cout"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        humiditeColumn.setCellValueFactory(new PropertyValueFactory<>("humidite"));
        consoCarburantColumn.setCellValueFactory(new PropertyValueFactory<>("consoCarburant"));
        consoEnergieColumn.setCellValueFactory(new PropertyValueFactory<>("consoEnergie"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        idMachineColumn.setCellValueFactory(new PropertyValueFactory<>("idMachine"));

        try {
            List<Maintenance> maintenances = maintenanceService.getAll();
            ObservableList<MaintenanceRecord> records = FXCollections.observableArrayList();
            for (Maintenance maintenance : maintenances) {
                MaintenanceRecord record = new MaintenanceRecord();
                record.setDateEntretien(maintenance.getDate_entretien().toString());
                record.setCout(maintenance.getCout());
                record.setTemperature(maintenance.getTemperature() != null ? maintenance.getTemperature() : 0);
                record.setHumidite(maintenance.getHumidite() != null ? maintenance.getHumidite() : 0);
                record.setConsoCarburant(maintenance.getConso_carburant() != null ? maintenance.getConso_carburant() : 0.0);
                record.setConsoEnergie(maintenance.getConso_energie() != null ? maintenance.getConso_energie() : 0.0);
                record.setStatus(maintenance.getStatus());
                record.setIdMachine(maintenance.getId_machine_id());
                records.add(record);
            }
            historyTable.setItems(records);
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement de l'historique de maintenance : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void predict() {
        try {
            if (coutField.getText().trim().isEmpty() ||
                    temperatureField.getText().trim().isEmpty() ||
                    humiditeField.getText().trim().isEmpty() ||
                    consoCarburantField.getText().trim().isEmpty() ||
                    consoEnergieField.getText().trim().isEmpty()) {
                errorLabel.setText("All prediction fields (Cost, Temperature, Humidity, Fuel, Energy) are required.");
                resultLabel.setText("");
                return;
            }

            double cost = Double.parseDouble(coutField.getText().trim());
            double temperature = Double.parseDouble(temperatureField.getText().trim());
            double humidity = Double.parseDouble(humiditeField.getText().trim());
            double fuelConsumption = Double.parseDouble(consoCarburantField.getText().trim());
            double energyConsumption = Double.parseDouble(consoEnergieField.getText().trim());

            if (cost < MIN_COUT) {
                errorLabel.setText("Cost must be positive or zero.");
                resultLabel.setText("");
                return;
            }
            if (temperature < MIN_TEMPERATURE || temperature > MAX_TEMPERATURE) {
                errorLabel.setText("Temperature must be between " + MIN_TEMPERATURE + " and " + MAX_TEMPERATURE + ".");
                resultLabel.setText("");
                return;
            }
            if (humidity < MIN_HUMIDITE || humidity > MAX_HUMIDITE) {
                errorLabel.setText("Humidity must be between " + MIN_HUMIDITE + " and " + MAX_HUMIDITE + ".");
                resultLabel.setText("");
                return;
            }
            if (fuelConsumption < MIN_CONSO) {
                errorLabel.setText("Fuel Consumption must be positive or zero.");
                resultLabel.setText("");
                return;
            }
            if (energyConsumption < MIN_CONSO) {
                errorLabel.setText("Energy Consumption must be positive or zero.");
                resultLabel.setText("");
                return;
            }

            String prediction = predictionClient.predictMaintenance(cost, temperature, humidity, fuelConsumption, energyConsumption);
            resultLabel.setText("Predicted Status: " + (prediction.equals("terminée") ? "Completed" : "EnAttente"));
            errorLabel.setText("");
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter valid numbers for all fields.");
            resultLabel.setText("");
        } catch (Exception e) {
            errorLabel.setText("Prediction Error: " + e.getMessage());
            resultLabel.setText("");
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

            maintenance.setTemperature(parseIntOrNull(temperatureField.getText().trim()));
            maintenance.setHumidite(parseIntOrNull(humiditeField.getText().trim()));
            maintenance.setConso_carburant(parseDoubleOrNull(consoCarburantField.getText().trim()));
            maintenance.setConso_energie(parseDoubleOrNull(consoEnergieField.getText().trim()));

            maintenanceService.add(maintenance);
            showAlert("Succès", "Maintenance ajoutée avec succès", Alert.AlertType.INFORMATION);
            clearFields();
            initializeHistoryTable();
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
        if (dateEntretienPicker.getValue() == null) {
            showAlert("Validation", "La date d'entretien est requise", Alert.AlertType.WARNING);
            dateEntretienPicker.requestFocus();
            return false;
        }

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

        if (statusComboBox.getValue() == null) {
            showAlert("Validation", "Le statut est requis", Alert.AlertType.WARNING);
            statusComboBox.requestFocus();
            return false;
        }

        if (machineIdComboBox.getValue() == null) {
            showAlert("Validation", "La machine est requise", Alert.AlertType.WARNING);
            machineIdComboBox.requestFocus();
            return false;
        }

        if (technicienIdComboBox.getValue() == null) {
            showAlert("Validation", "Le technicien est requis", Alert.AlertType.WARNING);
            technicienIdComboBox.requestFocus();
            return false;
        }

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
        resultLabel.setText("");
        errorLabel.setText("");
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