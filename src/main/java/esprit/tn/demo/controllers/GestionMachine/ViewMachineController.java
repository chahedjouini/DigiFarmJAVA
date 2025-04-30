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
import javafx.stage.Modality;

public class ViewMachineController implements Initializable {

    @FXML private TableView<Machine> machineTableView;
    @FXML private TableColumn<Machine, String> nomCol;
    @FXML private TableColumn<Machine, String> typeCol;
    @FXML private TableColumn<Machine, String> dateAchatCol;
    @FXML private TableColumn<Machine, String> etatCol;
    @FXML private TableColumn<Machine, String> etatPredCol;
    @FXML private TableColumn<Machine, Integer> ownerCol;
    @FXML private TableColumn<Machine, Void> actionsCol;

    @FXML private ComboBox<String> searchCriteriaComboBox;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Button logout;

    private final MachineService machineService = new MachineService();
    private final ObservableList<Machine> machineList = FXCollections.observableArrayList();
    private FilteredList<Machine> filteredMachineList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadMachines();
        setupSearchCriteria();
        setupSearchFilter();
        setupActionButtons();
        setupButtonActions();
    }

    private void setupTableColumns() {
        nomCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom()));
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));
        dateAchatCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(dateFormat.format(cellData.getValue().getDate_achat())));
        etatCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtat()));
        etatPredCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtat_pred()));
        ownerCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getOwner_id()).asObject());
    }

    private void setupSearchCriteria() {
        searchCriteriaComboBox.getItems().addAll("Nom", "Date Achat", "État");
        searchCriteriaComboBox.setValue("Nom"); // Default selection
    }

    private void setupSearchFilter() {
        filteredMachineList = new FilteredList<>(machineList, p -> true);
        machineTableView.setItems(filteredMachineList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMachineList.setPredicate(machine -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();
                String criterion = searchCriteriaComboBox.getValue();

                if (criterion == null) {
                    return true; // No criterion selected, show all
                }

                switch (criterion) {
                    case "Nom":
                        return machine.getNom() != null &&
                                machine.getNom().toLowerCase().contains(lowerCaseFilter);
                    case "Date Achat":
                        String formattedDate = dateFormat.format(machine.getDate_achat());
                        return formattedDate.contains(lowerCaseFilter);
                    case "État":
                        return machine.getEtat() != null &&
                                machine.getEtat().toLowerCase().contains(lowerCaseFilter);
                    default:
                        return true;
                }
            });
        });

        searchCriteriaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Trigger filter update when criterion changes
            filteredMachineList.setPredicate(machine -> {
                String searchText = searchField.getText();
                if (searchText == null || searchText.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = searchText.toLowerCase().trim();

                if (newValue == null) {
                    return true;
                }

                switch (newValue) {
                    case "Nom":
                        return machine.getNom() != null &&
                                machine.getNom().toLowerCase().contains(lowerCaseFilter);
                    case "Date Achat":
                        String formattedDate = dateFormat.format(machine.getDate_achat());
                        return formattedDate.contains(lowerCaseFilter);
                    case "État":
                        return machine.getEtat() != null &&
                                machine.getEtat().toLowerCase().contains(lowerCaseFilter);
                    default:
                        return true;
                }
            });
        });
    }

    private void loadMachines() {
        try {
            machineList.clear();
            machineList.addAll(machineService.getAll());
            filteredMachineList = new FilteredList<>(machineList, p -> true);
            machineTableView.setItems(filteredMachineList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les machines : " + e.getMessage());
        }
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
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void setupButtonActions() {
        addButton.setOnAction(event -> handleAddMachine());
        refreshButton.setOnAction(event -> refreshMachineList());
        if (logout != null) {
            logout.setOnAction(event -> handleLogout());
        } else {
            System.err.println("Warning: logout button is null. Check viewMachine.fxml for fx:id='logout'.");
        }
    }

    private void handleAddMachine() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutMachine.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Machine");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnHidden(e -> refreshMachineList());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue d'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editMachine(Machine machine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/modify-machine.fxml"));
            Parent root = loader.load();
            ModifyMachine controller = loader.getController();
            controller.setMachineData(machine);
            Stage stage = new Stage();
            stage.setTitle("Modifier la machine");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnHidden(e -> refreshMachineList());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la vue de modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteMachine(Machine machine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer cette machine ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la machine : " + machine.getNom() + "?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    machineService.delete(machine);
                    refreshMachineList();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Machine supprimée avec succès");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la machine : " + e.getMessage());
                }
            }
        });
    }

    private void refreshMachineList() {
        loadMachines();
        searchField.clear();
        searchCriteriaComboBox.setValue("Nom");
    }

    @FXML
    public void handleLogout() {
        try {
            Stage stage = (Stage) machineTableView.getScene().getWindow();
            stage.close();
            showAlert(Alert.AlertType.INFORMATION, "Information", "Déconnexion réussie");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}