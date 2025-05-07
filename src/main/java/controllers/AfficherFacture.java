package controllers;

import entities.Facture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import services.FactureService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherFacture {

    @FXML private TableView<Facture> factureTable;
    @FXML private TableColumn<Facture, Integer> idCol;
    @FXML private TableColumn<Facture, String> dateCol;
    @FXML private TableColumn<Facture, Float> prixCol;
    @FXML private TableColumn<Facture, Integer> cinCol;
    @FXML private TableColumn<Facture, String> emailCol;
    @FXML private TableColumn<Facture, String> abonnementCol;

    private final FactureService service = new FactureService();
    private ObservableList<Facture> list;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDatef().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        prixCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getPrixt()).asObject());
        cinCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCin()).asObject());
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        abonnementCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getAbonnement().getId())));

        loadFactures();
    }

    private void loadFactures() {
        try {
            List<Facture> factures = service.select();
            list = FXCollections.observableArrayList(factures);
            factureTable.setItems(list);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFacture.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ajouter Facture");
            stage.showAndWait();
            loadFactures();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre d’ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Facture selected = factureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une facture.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFacture.fxml"));
            Scene scene = new Scene(loader.load());
            ModifierFacture controller = loader.getController();
            controller.setFacture(selected);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier Facture");
            stage.showAndWait();

            loadFactures();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    private void onSupprimer() {
        Facture selected = factureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une facture.");
            return;
        }

        try {
            service.delete(selected.getId());
            list.remove(selected);
        } catch (SQLException e) {
            showAlert("Erreur", "Suppression échouée : " + e.getMessage());
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
