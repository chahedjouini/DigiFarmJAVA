package controllers;

import entities.Etude;
import enums.Climat;
import enums.TypeSol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import services.EtudeService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherEtude {

    @FXML private TableView<Etude> etudeTable;
    @FXML private TableColumn<Etude, Integer> idCol;
    @FXML private TableColumn<Etude, String> dateCol;
    @FXML private TableColumn<Etude, String> cultureCol;
    @FXML private TableColumn<Etude, String> expertCol;
    @FXML private TableColumn<Etude, Climat> climatCol;
    @FXML private TableColumn<Etude, TypeSol> solCol;
    @FXML private TableColumn<Etude, Boolean> irrigationCol;
    @FXML private TableColumn<Etude, Boolean> fertilisationCol;
    @FXML private TableColumn<Etude, Float> prixCol;
    @FXML private TableColumn<Etude, Float> rendementCol;
    @FXML private TableColumn<Etude, Float> precipitationCol;
    @FXML private TableColumn<Etude, Float> mainOeuvreCol;

    @FXML private StackPane contentPane;

    private final EtudeService service = new EtudeService();
    private ObservableList<Etude> etudeList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateR().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        cultureCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCulture().getNom()));
        expertCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getExpert().getNom()));
        climatCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getClimat()));
        solCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTypeSol()));
        irrigationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isIrrigation()).asObject());
        fertilisationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isFertilisation()).asObject());
        prixCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getPrix()).asObject());
        rendementCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getRendement()).asObject());
        precipitationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getPrecipitations()).asObject());
        mainOeuvreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleFloatProperty(data.getValue().getMainOeuvre()).asObject());

        loadEtudes();
    }

    private void loadEtudes() {
        try {
            List<Etude> list = service.select();
            etudeList = FXCollections.observableArrayList(list);
            etudeTable.setItems(etudeList);
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEtude.fxml"));
            Node node = loader.load();
            setContent(node);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l’ajout.");
        }
    }

    @FXML
    private void onModifier() {
        Etude selected = etudeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEtude.fxml"));
            Node node = loader.load();
            ModifierEtude controller = loader.getController();
            controller.setEtude(selected);
            setContent(node);
        } catch (IOException e) {
            showAlert("Erreur", "Chargement de la modification impossible.");
        }
    }

    private void setContent(Node node) {
        StackPane pane = (StackPane) etudeTable.getScene().lookup("#entityContentPane");
        pane.getChildren().setAll(node);
    }

    @FXML
    private void onSupprimer() {
        Etude selected = etudeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }

        try {
            service.delete(selected.getId());
            etudeList.remove(selected);
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
