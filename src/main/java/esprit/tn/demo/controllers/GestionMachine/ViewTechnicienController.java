package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Technicien;
import esprit.tn.demo.services.GestionMachine.TechnicienService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewTechnicienController {

    @FXML
    private TableView<Technicien> technicienTableView;
    @FXML
    private TableColumn<Technicien, String> nameCol, prenomCol, specialiteCol, emailCol, localisationCol;
    @FXML
    private TableColumn<Technicien, Integer> telephoneCol;
    @FXML
    private TableColumn<Technicien, Float> latitudeCol, longitudeCol;
    @FXML
    private TableColumn<Technicien, Void> actionsCol;
    @FXML
    private TextField searchField;
    @FXML
    private Label logoutLabel;

    private TechnicienService technicienService = new TechnicienService();
    private ObservableList<Technicien> technicienList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        specialiteCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        localisationCol.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        latitudeCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.setOnAction(event -> {
                    Technicien technicien = getTableView().getItems().get(getIndex());
                    showEditDialog(technicien);
                });
                deleteButton.setOnAction(event -> {
                    Technicien technicien = getTableView().getItems().get(getIndex());
                    technicienService.delete(technicien);
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton));
                }
            }
        });

        refreshTable();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchTechnicien(newValue));
    }

    @FXML
    private void addButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutTechnicien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Technicien");
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
@FXML
    private void showEditDialog(Technicien technicien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/technicien-update.fxml"));
            Parent root = loader.load();
            ModifyTechnicien controller = loader.getController();
            controller.setTechnicien(technicien);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Technicien");
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshButton() {
        refreshTable();
    }

    private void refreshTable() {
        technicienList.clear();
        technicienList.addAll(technicienService.getAll());
        technicienTableView.setItems(technicienList);
    }

    private void searchTechnicien(String query) {
        if (query.isEmpty()) {
            technicienTableView.setItems(technicienList);
        } else {
            ObservableList<Technicien> filteredList = FXCollections.observableArrayList();
            for (Technicien technicien : technicienList) {
                if (technicien.getName().toLowerCase().contains(query.toLowerCase()) ||
                        technicien.getPrenom().toLowerCase().contains(query.toLowerCase()) ||
                        technicien.getSpecialite().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(technicien);
                }
            }
            technicienTableView.setItems(filteredList);
        }
    }

    @FXML
    private void showTechnicianMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/technicianMap.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Technician Map");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/reports.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Maintenance Reports");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}