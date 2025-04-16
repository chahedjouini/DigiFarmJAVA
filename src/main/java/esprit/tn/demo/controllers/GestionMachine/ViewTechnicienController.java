package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Technicien;
import esprit.tn.demo.services.GestionMachine.TechnicienService;
import javafx.beans.property.SimpleFloatProperty;
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
import java.util.ResourceBundle;

public class ViewTechnicienController implements Initializable {

    @FXML private TableView<Technicien> technicienTableView;
    @FXML private TableColumn<Technicien, String> nameCol;
    @FXML private TableColumn<Technicien, String> prenomCol;
    @FXML private TableColumn<Technicien, String> specialiteCol;
    @FXML private TableColumn<Technicien, String> emailCol;
    @FXML private TableColumn<Technicien, Integer> telephoneCol;
    @FXML private TableColumn<Technicien, String> localisationCol;
    @FXML private TableColumn<Technicien, Float> latitudeCol;
    @FXML private TableColumn<Technicien, Float> longitudeCol;
    @FXML private TableColumn<Technicien, Void> actionsCol;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private Label logoutLabel;

    private final TechnicienService technicienService = new TechnicienService();
    private final ObservableList<Technicien> technicienList = FXCollections.observableArrayList();
    private FilteredList<Technicien> filteredTechnicienList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadTechniciens();
        setupSearchFilter();
        setupActionButtons();
        setupButtonActions();
    }

    private void setupTableColumns() {
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        prenomCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrenom()));
        specialiteCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpecialite()));
        emailCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));
        telephoneCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTelephone()).asObject());
        localisationCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLocalisation()));
        latitudeCol.setCellValueFactory(cellData ->
                new SimpleFloatProperty(cellData.getValue().getLatitude()).asObject());
        longitudeCol.setCellValueFactory(cellData ->
                new SimpleFloatProperty(cellData.getValue().getLongitude()).asObject());
    }

    private void loadTechniciens() {
        technicienList.clear();
        technicienList.addAll(technicienService.getAll());
        filteredTechnicienList = new FilteredList<>(technicienList, p -> true);
        technicienTableView.setItems(filteredTechnicienList);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTechnicienList.setPredicate(technicien -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (technicien.getName() != null && technicien.getName().toLowerCase().contains(lowerCaseFilter)) ||
                        (technicien.getPrenom() != null && technicien.getPrenom().toLowerCase().contains(lowerCaseFilter)) ||
                        (technicien.getSpecialite() != null && technicien.getSpecialite().toLowerCase().contains(lowerCaseFilter)) ||
                        (technicien.getEmail() != null && technicien.getEmail().toLowerCase().contains(lowerCaseFilter)) ||
                        String.valueOf(technicien.getTelephone()).contains(lowerCaseFilter) ||
                        (technicien.getLocalisation() != null && technicien.getLocalisation().toLowerCase().contains(lowerCaseFilter)) ||
                        String.valueOf(technicien.getLatitude()).contains(lowerCaseFilter) ||
                        String.valueOf(technicien.getLongitude()).contains(lowerCaseFilter);
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
                    Technicien technicien = getTableView().getItems().get(getIndex());
                    editTechnicien(technicien);
                });

                deleteButton.setOnAction(event -> {
                    Technicien technicien = getTableView().getItems().get(getIndex());
                    deleteTechnicien(technicien);
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
        addButton.setOnAction(event -> handleAddTechnicien());
        refreshButton.setOnAction(event -> refreshTechnicienList());
        logoutLabel.setOnMouseClicked(event -> handleLogout());
    }

    private void handleAddTechnicien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutTechnicien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un technicien");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> refreshTechnicienList());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue d'ajout: " + e.getMessage());
        }
    }

    private void editTechnicien(Technicien technicien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/technicien-update.fxml"));
            Parent root = loader.load();
            ModifyTechnicien controller = loader.getController();
            controller.setTechnicienData(technicien);
            Stage stage = new Stage();
            stage.setTitle("Modifier le technicien");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(e -> refreshTechnicienList());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la vue de modification: " + e.getMessage());
        }
    }

    private void deleteTechnicien(Technicien technicien) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer ce technicien ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le technicien: " +
                technicien.getPrenom() + " " + technicien.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                technicienService.delete(technicien);
                refreshTechnicienList();
                showAlert("Succès", "Technicien supprimé avec succès");
            }
        });
    }

    private void refreshTechnicienList() {
        loadTechniciens();
        searchField.clear();
    }

    private void handleLogout() {
        Stage stage = (Stage) logoutLabel.getScene().getWindow();
        stage.close();
        showAlert("Information", "Déconnexion réussie");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}