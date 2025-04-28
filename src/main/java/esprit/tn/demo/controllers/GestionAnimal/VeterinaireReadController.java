package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class VeterinaireReadController {

    @FXML private TableView<Veterinaire> veterinaireTable;
    @FXML private TableColumn<Veterinaire, Number> idCol;
    @FXML private TableColumn<Veterinaire, String> nomCol;
    @FXML private TableColumn<Veterinaire, Number> numTelCol;
    @FXML private TableColumn<Veterinaire, String> emailCol;
    @FXML private TableColumn<Veterinaire, String> adresse_cabineCol;
    @FXML private TableColumn<Veterinaire, Void> actionsCol;
    @FXML private TextField searchNomField;

    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();
    private ObservableList<Veterinaire> allVeterinaires;
    private FilteredList<Veterinaire> filteredVeterinaires;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        nomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        numTelCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getnum_tel()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        adresse_cabineCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getadresse_cabine()));

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                updateButton.setOnAction(event -> {
                    Veterinaire veterinaire = getTableView().getItems().get(getIndex());
                    if (veterinaire != null) {
                        openUpdateVeterinaireForm(veterinaire);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Veterinaire selected for update.");
                    }
                });

                deleteButton.setOnAction(event -> {
                    Veterinaire veterinaire = getTableView().getItems().get(getIndex());
                    if (veterinaire != null) {
                        openDeleteVeterinaireForm(veterinaire);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Veterinaire selected for deletion.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, updateButton, deleteButton));
                }
            }
        });

        veterinaireTable.setOnMouseClicked(event -> {
            Veterinaire selectedVeterinaire = veterinaireTable.getSelectionModel().getSelectedItem();
            if (selectedVeterinaire != null) {
                showAlert(Alert.AlertType.INFORMATION, "Veterinaire Details",
                        "Name: " + selectedVeterinaire.getNom() + "\n" +
                                "Phone: " + selectedVeterinaire.getnum_tel() + "\n" +
                                "Email: " + selectedVeterinaire.getEmail() + "\n" +
                                "Address: " + selectedVeterinaire.getadresse_cabine());
            }
        });

        loadVeterinaireData();
        setupSearchListeners(); // Initialize search listeners
    }

    private void loadVeterinaireData() {
        allVeterinaires = FXCollections.observableArrayList(veterinaireService.getAllVeterinaires());
        filteredVeterinaires = new FilteredList<>(allVeterinaires, p -> true); // Initially show all
        veterinaireTable.setItems(filteredVeterinaires);
    }

    private void setupSearchListeners() {
        searchNomField.textProperty().addListener((observable, oldValue, newValue) -> filterVeterinaires(newValue));
    }

    private void filterVeterinaires(String searchTerm) {
        if (filteredVeterinaires != null && searchTerm != null) {
            String lowerCaseSearchTerm = searchTerm.toLowerCase();
            filteredVeterinaires.setPredicate(veterinaire ->
                    veterinaire.getNom().toLowerCase().contains(lowerCaseSearchTerm));
        } else if (filteredVeterinaires != null) {
            filteredVeterinaires.setPredicate(p -> true); // Show all if search term is empty
        }
    }

    @FXML
    private void handleAddVeterinaire() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/VeterinaireAddView.fxml");
            if (location == null) {
                throw new IOException("Cannot find VeterinaireAddView.fxml at esprit/tn/demo/VeterinaireAddView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Veterinaire");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadVeterinaireData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Veterinaire form: " + e.getMessage());
        }
    }

    private void openUpdateVeterinaireForm(Veterinaire veterinaire) {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/VeterinaireUpdateView.fxml");
            if (location == null) {
                throw new IOException("Cannot find VeterinaireUpdateView.fxml at esprit/tn/demo/VeterinaireUpdateView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Veterinaire");
            stage.initModality(Modality.APPLICATION_MODAL);
            VeterinaireUpdateController controller = loader.getController();
            controller.setVeterinaire(veterinaire);
            stage.showAndWait();
            loadVeterinaireData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Update Veterinaire form: " + e.getMessage());
        }
    }

    private void openDeleteVeterinaireForm(Veterinaire veterinaire) {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/VeterinaireDeleteView.fxml");
            if (location == null) {
                throw new IOException("Cannot find VeterinaireDeleteView.fxml at esprit/tn/demo/VeterinaireDeleteView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Delete Veterinaire");
            stage.initModality(Modality.APPLICATION_MODAL);
            VeterinaireDeleteController controller = loader.getController();
            controller.setVeterinaire(veterinaire);
            stage.showAndWait();
            loadVeterinaireData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Delete Veterinaire form: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleOpenAnimaux() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/AnimalReadView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalReadView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Animaux");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Animaux form: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenSuivi() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviReadView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviReadView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Suivi des Animaux");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Suivi form: " + e.getMessage());
        }
    }
}