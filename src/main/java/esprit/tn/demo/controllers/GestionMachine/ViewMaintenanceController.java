package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Maintenance;
import esprit.tn.demo.services.GestionMachine.MaintenanceService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class ViewMaintenanceController implements Initializable {

    @FXML private TableView<Maintenance> maintenanceTableView;
    @FXML private TableColumn<Maintenance, Integer> machineIdCol;
    @FXML private TableColumn<Maintenance, Integer> technicienIdCol;
    @FXML private TableColumn<Maintenance, String> dateEntretienCol;
    @FXML private TableColumn<Maintenance, Double> coutCol;
    @FXML private TableColumn<Maintenance, Integer> temperatureCol;
    @FXML private TableColumn<Maintenance, Integer> humiditeCol;
    @FXML private TableColumn<Maintenance, Double> consoCarburantCol;
    @FXML private TableColumn<Maintenance, Double> consoEnergieCol;
    @FXML private TableColumn<Maintenance, String> statusCol;
    @FXML private TableColumn<Maintenance, String> etatPredCol;
    @FXML private TableColumn<Maintenance, Void> actionsCol;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Label logoutLabel;

    private final MaintenanceService maintenanceService = new MaintenanceService();
    private final ObservableList<Maintenance> maintenanceList = FXCollections.observableArrayList();
    private FilteredList<Maintenance> filteredMaintenanceList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadMaintenances();
        setupSearchFilter();
        setupActionButtons();
        setupButtonActions();
    }

    private void setupTableColumns() {
        machineIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId_machine_id()).asObject());
        technicienIdCol.setCellValueFactory(cellData -> {
            Integer techId = cellData.getValue().getId_technicien_id();
            return new SimpleIntegerProperty(techId != null ? techId : 0).asObject();
        });

        dateEntretienCol.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(
                    cellData.getValue().getDate_entretien() != null ?
                            dateFormat.format(cellData.getValue().getDate_entretien()) : ""
            );
        });

        coutCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCout()).asObject());
        temperatureCol.setCellValueFactory(cellData -> {
            Integer temp = cellData.getValue().getTemperature();
            return new SimpleIntegerProperty(temp != null ? temp : 0).asObject();
        });
        humiditeCol.setCellValueFactory(cellData -> {
            Integer humidite = cellData.getValue().getHumidite();
            return new SimpleIntegerProperty(humidite != null ? humidite : 0).asObject();
        });
        consoCarburantCol.setCellValueFactory(cellData -> {
            Double conso = cellData.getValue().getConso_carburant();
            return new SimpleDoubleProperty(conso != null ? conso : 0.0).asObject();
        });
        consoEnergieCol.setCellValueFactory(cellData -> {
            Double conso = cellData.getValue().getConso_energie();
            return new SimpleDoubleProperty(conso != null ? conso : 0.0).asObject();
        });
        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));
        etatPredCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtat_pred() != null ?
                        cellData.getValue().getEtat_pred() : ""));
    }

    private void loadMaintenances() {
        maintenanceList.clear();
        maintenanceList.addAll(maintenanceService.getAll());
        filteredMaintenanceList = new FilteredList<>(maintenanceList, p -> true);
        maintenanceTableView.setItems(filteredMaintenanceList);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMaintenanceList.setPredicate(maintenance -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(maintenance.getId_machine_id()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getId_technicien_id() != null &&
                        String.valueOf(maintenance.getId_technicien_id()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getDate_entretien() != null &&
                        new SimpleDateFormat("dd/MM/yyyy").format(maintenance.getDate_entretien()).contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(maintenance.getCout()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getTemperature() != null &&
                        String.valueOf(maintenance.getTemperature()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getHumidite() != null &&
                        String.valueOf(maintenance.getHumidite()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getConso_carburant() != null &&
                        String.valueOf(maintenance.getConso_carburant()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getConso_energie() != null &&
                        String.valueOf(maintenance.getConso_energie()).contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getStatus() != null &&
                        maintenance.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (maintenance.getEtat_pred() != null &&
                        maintenance.getEtat_pred().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void setupActionButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("btn-primary");
                deleteButton.getStyleClass().add("btn-danger");

                editButton.setOnAction(event -> {
                    Maintenance maintenance = getTableView().getItems().get(getIndex());
                    editMaintenance(maintenance);
                });

                deleteButton.setOnAction(event -> {
                    Maintenance maintenance = getTableView().getItems().get(getIndex());
                    deleteMaintenance(maintenance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupButtonActions() {
        addButton.setOnAction(event -> handleAddMaintenance());
        refreshButton.setOnAction(event -> refreshMaintenanceList());
        logoutLabel.setOnMouseClicked(event -> handleLogout());
    }

    private void handleAddMaintenance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutMaintenanace.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editMaintenance(Maintenance maintenance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/maintenance-update.fxml"));
            Parent root = loader.load();
            ModifyMaintenanceController controller = loader.getController();
            controller.setMaintenanceData(maintenance);
            Stage stage = new Stage();
            stage.setTitle("Modifier la maintenance");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> refreshMaintenanceList());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteMaintenance(Maintenance maintenance) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer cette maintenance ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la maintenance pour la machine ID: " +
                maintenance.getId_machine_id() + " datée du " +
                (maintenance.getDate_entretien() != null ?
                        new SimpleDateFormat("dd/MM/yyyy").format(maintenance.getDate_entretien()) : "inconnue") + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                maintenanceService.delete(maintenance);
                refreshMaintenanceList();
                showAlert("Succès", "Maintenance supprimée avec succès");
            }
        });
    }

    private void refreshMaintenanceList() {
        loadMaintenances();
        searchField.clear();
    }

    private void handleLogout() {
        try {
            Stage stage = (Stage) logoutLabel.getScene().getWindow();
            stage.close();
            showAlert("Information", "Déconnexion réussie");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}