package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ExpertService;

import java.sql.SQLException;
import java.util.List;

public class AfficherExpert {

    @FXML private TableView<Expert> expertTable;
    @FXML private TableColumn<Expert, Integer> idCol;
    @FXML private TableColumn<Expert, String> nomCol;
    @FXML private TableColumn<Expert, String> prenomCol;
    @FXML private TableColumn<Expert, Integer> telCol;
    @FXML private TableColumn<Expert, String> emailCol;
    @FXML private TableColumn<Expert, String> zoneCol;
    @FXML private TableColumn<Expert, Dispo> dispoCol;

    private final ExpertService expertService = new ExpertService();
    private ObservableList<Expert> expertList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        prenomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        telCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTel()).asObject());
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        zoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getZone()));
        dispoCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDispo()));

        loadExperts();
    }

    private void loadExperts() {
        try {
            List<Expert> list = expertService.select();
            expertList = FXCollections.observableArrayList(list);
            expertTable.setItems(expertList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les experts : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterExpert.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Ajouter Expert");
            stage.setScene(scene);
            stage.showAndWait();

            loadExperts(); // Refresh après ajout

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Expert selected = expertTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un expert.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierExpert.fxml"));
            Scene scene = new Scene(loader.load());

            ModifierExpert controller = loader.getController();
            controller.setExpert(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Expert");
            stage.setScene(scene);
            stage.showAndWait();

            loadExperts(); // Refresh après modification

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    private void onSupprimer() {
        Expert selected = expertTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un expert.");
            return;
        }

        try {
            expertService.delete(selected.getId());
            expertList.remove(selected);
        } catch (SQLException e) {
            showAlert("Erreur", "Suppression échouée : " + e.getMessage());
        }
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
