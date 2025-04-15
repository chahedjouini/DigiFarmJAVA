package controllers;

import entities.Abonnement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AbonnementService;

import java.sql.SQLException;
import java.util.List;

public class AfficherAbonnement {

    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, Integer> idCol;
    @FXML private TableColumn<Abonnement, Integer> idcCol;
    @FXML private TableColumn<Abonnement, String> nomCol;
    @FXML private TableColumn<Abonnement, String> prenomCol;
    @FXML private TableColumn<Abonnement, Integer> numeroCol;
    @FXML private TableColumn<Abonnement, String> typeCol;
    @FXML private TableColumn<Abonnement, Integer> dureeCol;
    @FXML private TableColumn<Abonnement, Float> prixCol;

    private final AbonnementService service = new AbonnementService();
    private ObservableList<Abonnement> list;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        idcCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdc()).asObject());
        nomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        prenomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        numeroCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getNumero()).asObject());
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTypeabb()));
        dureeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDureeabb()).asObject());
        prixCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getPrix()).asObject());

        loadAbonnements();
    }

    private void loadAbonnements() {
        try {
            List<Abonnement> abonnements = service.select();
            list = FXCollections.observableArrayList(abonnements);
            abonnementTable.setItems(list);
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du chargement : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAbonnement.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Abonnement");
            stage.setScene(scene);
            stage.showAndWait();
            loadAbonnements();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre d’ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Abonnement selected = abonnementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un abonnement.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierAbonnement.fxml"));
            Scene scene = new Scene(loader.load());

            ModifierAbonnement controller = loader.getController();
            controller.setAbonnement(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Abonnement");
            stage.setScene(scene);
            stage.showAndWait();

            loadAbonnements();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    private void onSupprimer() {
        Abonnement selected = abonnementTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un abonnement.");
            return;
        }

        try {
            service.delete(selected.getId());
            list.remove(selected);
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la suppression.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
