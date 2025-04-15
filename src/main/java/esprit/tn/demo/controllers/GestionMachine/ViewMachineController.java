package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Machine;
import esprit.tn.demo.services.GestionMachine.MachineService;
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

public class ViewMachineController implements Initializable {

    @FXML private TableView<Machine> machineTableView;
    @FXML private TableColumn<Machine, Integer> idCol;
    @FXML private TableColumn<Machine, String> nomCol;
    @FXML private TableColumn<Machine, String> typeCol;
    @FXML private TableColumn<Machine, String> dateAchatCol;
    @FXML private TableColumn<Machine, String> etatCol;
    @FXML private TableColumn<Machine, String> etatPredCol;
    @FXML private TableColumn<Machine, Integer> ownerCol;
    @FXML private TableColumn<Machine, Void> actionsCol;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Button logout;

    private final MachineService machineService = new MachineService();
    private final ObservableList<Machine> machineList = FXCollections.observableArrayList();
    private FilteredList<Machine> filteredMachineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadMachines();
        setupSearchFilter();
        setupActionButtons();
        setupButtonActions();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId_machine()).asObject());
        nomCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom()));
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        // Format date column
        dateAchatCol.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(
                    dateFormat.format(cellData.getValue().getDate_achat())
            );
        });

        etatCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtat()));
        etatPredCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtat_pred()));
        ownerCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getOwner_id()).asObject());

        setupActionButtons();
    }

    private void loadMachines() {
        machineList.clear();
        machineList.addAll(machineService.getAll());
        filteredMachineList = new FilteredList<>(machineList, p -> true);
        machineTableView.setItems(filteredMachineList);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMachineList.setPredicate(machine -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (machine.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (machine.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (machine.getEtat().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (machine.getEtat_pred() != null &&
                        machine.getEtat_pred().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(machine.getOwner_id()).contains(lowerCaseFilter)) {
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
                    Machine machine = getTableView().getItems().get(getIndex());
                    editMachine(machine);
                });

                deleteButton.setOnAction(event -> {
                    Machine machine = getTableView().getItems().get(getIndex());
                    deleteMachine(machine);
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
        addButton.setOnAction(event -> handleAddMachine());
        refreshButton.setOnAction(event -> refreshMachineList());
        logout.setOnAction(event -> handleLogout());
    }

    private void handleAddMachine() {
        try {
            // Load the AjoutMachine.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutMachine.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) addButton.getScene().getWindow();

            // Create new scene and set it on the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editMachine(Machine machine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/machine-update.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the machine data
            ModifyMachine controller = loader.getController();
            controller.setMachineData(machine);

            Stage stage = new Stage();
            stage.setTitle("Modifier la machine");
            stage.setScene(new Scene(root));
            stage.show();

            // Refresh the table after modification (optional)
            stage.setOnHidden(e -> refreshMachineList());

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteMachine(Machine machine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer cette machine ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la machine: " + machine.getNom() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                machineService.delete(machine);
                refreshMachineList();
                showAlert("Succès", "Machine supprimée avec succès");
            }
        });
    }

    private void refreshMachineList() {
        loadMachines();
        searchField.clear();
    }

    private void handleLogout() {
        try {
            // Implementation for logout
            Stage stage = (Stage) logout.getScene().getWindow();
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