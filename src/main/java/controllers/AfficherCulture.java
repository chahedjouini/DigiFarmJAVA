package controllers;

import entities.Culture;
import enums.BesoinsEngrais;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import services.CultureService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherCulture {

    @FXML private TableView<Culture> cultureTable;
    @FXML private TableColumn<Culture, Integer> idCol;
    @FXML private TableColumn<Culture, String> nomCol;
    @FXML private TableColumn<Culture, Float> surfaceCol;
    @FXML private TableColumn<Culture, String> plantationCol;
    @FXML private TableColumn<Culture, String> recolteCol;
    @FXML private TableColumn<Culture, String> regionCol;
    @FXML private TableColumn<Culture, String> typeCol;
    @FXML private TableColumn<Culture, Float> densiteCol;
    @FXML private TableColumn<Culture, Float> eauCol;
    @FXML private TableColumn<Culture, BesoinsEngrais> engraisCol;
    @FXML private TableColumn<Culture, Float> rendementCol;
    @FXML private TableColumn<Culture, Float> coutCol;
    @FXML private TableColumn<Culture, Integer> userCol;

    @FXML private StackPane contentPane; // optionnel si défini dans le FXML
    private final CultureService cultureService = new CultureService();
    private ObservableList<Culture> cultureList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        surfaceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getSurface()).asObject());
        plantationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDatePlantation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        recolteCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateRecolte().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        regionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRegion()));
        typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTypeCulture()));
        densiteCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getDensitePlantation()).asObject());
        eauCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getBesoinsEau()).asObject());
        engraisCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getBesoinsEngrais()));
        rendementCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getRendementMoyen()).asObject());
        coutCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getCoutMoyen()).asObject());
        userCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdUser()).asObject());

        loadCultures();
    }

    private void loadCultures() {
        try {
            List<Culture> list = cultureService.select();
            cultureList = FXCollections.observableArrayList(list);
            cultureTable.setItems(cultureList);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCulture.fxml"));
            Node node = loader.load();
            setContent(node);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Culture selected = cultureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une culture.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCulture.fxml"));
            Node node = loader.load();
            ModifierCulture controller = loader.getController();
            controller.setCulture(selected);
            setContent(node);
        } catch (IOException e) {
            showAlert("Erreur", "Chargement de la modification impossible.");
        }
    }

    private void setContent(Node node) {
        StackPane parentPane = (StackPane) cultureTable.getScene().lookup("#entityContentPane");
        parentPane.getChildren().setAll(node);
    }

    @FXML
    private void onSupprimer() {
        Culture selected = cultureTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une culture.");
            return;
        }

        try {
            cultureService.delete(selected.getId());
            cultureList.remove(selected);
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
