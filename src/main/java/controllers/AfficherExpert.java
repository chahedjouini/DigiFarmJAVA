package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import services.ExpertService;

import java.io.IOException;
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

    @FXML private StackPane contentPane; // optionnel si pas défini dans FXML mais accessible via lookup

    private final ExpertService expertService = new ExpertService();
    private ObservableList<Expert> expertList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        prenomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        telCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTel()).asObject());
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        zoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getZone()));
        dispoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDispo()));

        loadExperts();
    }

    private void loadExperts() {
        try {
            List<Expert> list = expertService.select();
            expertList = FXCollections.observableArrayList(list);
            expertTable.setItems(expertList);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterExpert.fxml"));
            Node node = loader.load();
            setContent(node);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Expert selected = expertTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un expert à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierExpert.fxml"));
            Node node = loader.load();

            ModifierExpert controller = loader.getController();
            controller.setExpert(selected);

            setContent(node);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la modification.");
        }
    }

    @FXML
    private void onSupprimer() {
        Expert selected = expertTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un expert à supprimer.");
            return;
        }

        try {
            expertService.delete(selected.getId());
            expertList.remove(selected);
        } catch (SQLException e) {
            showAlert("Erreur", "Suppression échouée : " + e.getMessage());
        }
    }

    private void setContent(Node node) {
        StackPane parentPane = (StackPane) expertTable.getScene().lookup("#entityContentPane");
        parentPane.getChildren().setAll(node);
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
