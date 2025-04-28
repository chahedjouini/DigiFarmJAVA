package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;




public class SuiviReadController {

    @FXML
    private TableView<Suivi> suiviTableView;
    @FXML
    private TableColumn<Suivi, Integer> idColumn;
    @FXML
    private TableColumn<Suivi, String> animalColumn;
    @FXML
    private TableColumn<Suivi, Float> temperatureColumn;
    @FXML
    private TableColumn<Suivi, Float> rythmeCardiaqueColumn;
    @FXML
    private TableColumn<Suivi, String> etatColumn;
    @FXML
    private TableColumn<Suivi, Integer> idClientColumn;
    @FXML
    private TableColumn<Suivi, String> analysisColumn;
    @FXML
    private TableColumn<Suivi, String> veterinaireNomColumn;
    @FXML
    private TableColumn<Suivi, Void> actionsColumn;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        animalColumn.setCellValueFactory(cellData -> {
            Suivi suivi = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    suivi.getAnimal() != null ? suivi.getAnimal().getNom() : "N/A"
            );
        });
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        rythmeCardiaqueColumn.setCellValueFactory(new PropertyValueFactory<>("rythmeCardiaque"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        analysisColumn.setCellValueFactory(new PropertyValueFactory<>("analysis"));
        veterinaireNomColumn.setCellValueFactory(cellData -> {
            Suivi suivi = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    suivi.getVeterinaire() != null ? suivi.getVeterinaire().getNom() : "N/A"
            );
        });

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                updateButton.setOnAction(event -> {
                    Suivi suivi = getTableView().getItems().get(getIndex());
                    if (suivi == null) {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Suivi selected for update.");
                        return;
                    }
                    System.out.println("Selected Suivi for update: " + suivi);
                    openUpdateSuiviForm(suivi);
                });
                deleteButton.setOnAction(event -> {
                    Suivi suivi = getTableView().getItems().get(getIndex());
                    if (suivi == null) {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Suivi selected for deletion.");
                        return;
                    }
                    System.out.println("Selected Suivi for deletion: " + suivi);
                    openDeleteSuiviForm(suivi);
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

        suiviTableView.setOnMouseClicked(event -> {
            Suivi selectedSuivi = suiviTableView.getSelectionModel().getSelectedItem();
            if (selectedSuivi != null) {
                String animalName = selectedSuivi.getAnimal() != null ? selectedSuivi.getAnimal().getNom() : "N/A";
                String veterinaireName = selectedSuivi.getVeterinaire() != null ? selectedSuivi.getVeterinaire().getNom() : "N/A";
                showAlert(Alert.AlertType.INFORMATION, "Details",
                        "Animal Name: " + animalName + "\nVeterinaire Name: " + veterinaireName);
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection", "No Suivi selected.");
            }
        });

        loadSuiviData();
    }

    private void loadSuiviData() {
        suiviTableView.setItems(FXCollections.observableArrayList(suiviService.getAllSuivis()));
    }

    @FXML
    private void handleAddSuivi() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviAddView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviAddView.fxml at esprit/tn/demo/SuiviAddView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Suivi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Suivi form: " + e.getMessage());
        }
    }

    private void openUpdateSuiviForm(Suivi suivi) {
        if (suivi == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Update form: Suivi is null.");
            return;
        }
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviUpdateView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviUpdateView.fxml at src/main/resources/esprit/tn/demo/SuiviUpdateView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Suivi");
            stage.initModality(Modality.APPLICATION_MODAL);
            SuiviUpdateController controller = loader.getController();
            controller.setSuivi(suivi);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Update Suivi form: " + e.getMessage());
        }
    }

    private void openDeleteSuiviForm(Suivi suivi) {
        if (suivi == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Delete form: Suivi is null.");
            return;
        }
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviDeleteView.fxml");
            System.out.println("Attempting to load SuiviDeleteView.fxml from: esprit/tn/demo/SuiviDeleteView.fxml");
            System.out.println("Resource URL: " + location);
            if (location == null) {
                throw new IOException("Cannot find SuiviDeleteView.fxml at esprit/tn/demo/SuiviDeleteView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Delete Suivi");
            stage.initModality(Modality.APPLICATION_MODAL);
            SuiviDeleteController controller = loader.getController();
            controller.setSuivi(suivi);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Delete Suivi form: " + e.getMessage());
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
    private void handleOpenStatistiques() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/StatistiqueView.fxml");
            if (location == null) {
                throw new IOException("Impossible de trouver StatistiqueView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques des Suivis");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre des statistiques : " + e.getMessage());
        }
    }
}