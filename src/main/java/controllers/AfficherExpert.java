package controllers;

import entities.Expert;
import enums.Dispo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ExpertService;
import utils.ValidationUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AfficherExpert {

    @FXML private GridPane expertGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    private final ExpertService service = new ExpertService();
    private ObservableList<Expert> expertList;
    private Expert selectedExpert;

    @FXML
    public void initialize() {
        setupSortComboBox();
        loadExperts();
        setupSearch();
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll(
                "Nom (A-Z)",
                "Nom (Z-A)",
                "Prénom (A-Z)",
                "Prénom (Z-A)",
                "Zone géographique (A-Z)",
                "Zone géographique (Z-A)",
                "Disponibilité"
        );
        sortComboBox.setValue("Nom (A-Z)");
        sortComboBox.setOnAction(e -> sortExperts());
    }

    private void sortExperts() {
        if (expertList == null) return;

        switch (sortComboBox.getValue()) {
            case "Nom (A-Z)":
                expertList.sort(Comparator.comparing(Expert::getNom, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                expertList.sort((e1, e2) -> String.CASE_INSENSITIVE_ORDER.compare(e2.getNom(), e1.getNom()));
                break;
            case "Prénom (A-Z)":
                expertList.sort(Comparator.comparing(Expert::getPrenom, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Prénom (Z-A)":
                expertList.sort((e1, e2) -> String.CASE_INSENSITIVE_ORDER.compare(e2.getPrenom(), e1.getPrenom()));
                break;
            case "Zone géographique (A-Z)":
                expertList.sort(Comparator.comparing(Expert::getZone, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Zone géographique (Z-A)":
                expertList.sort((e1, e2) -> String.CASE_INSENSITIVE_ORDER.compare(e2.getZone(), e1.getZone()));
                break;
            case "Disponibilité":
                expertList.sort(Comparator.comparing(Expert::getDispo));
                break;
        }
        filterExperts(searchField.getText());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterExperts(newValue);
        });
    }

    private void filterExperts(String searchText) {
        expertGrid.getChildren().clear();
        int row = 0;
        int col = 0;

        for (Expert expert : expertList) {
            if (matchesSearch(expert, searchText)) {
                VBox card = createExpertCard(expert);
                expertGrid.add(card, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    private boolean matchesSearch(Expert expert, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }
        searchText = searchText.toLowerCase();
        return expert.getNom().toLowerCase().contains(searchText) ||
                expert.getPrenom().toLowerCase().contains(searchText) ||
                expert.getZone().toLowerCase().contains(searchText);
    }

    private VBox createExpertCard(Expert expert) {
        VBox card = new VBox(10);
        card.getStyleClass().add("expert-card");

        Label title = new Label(expert.getNom() + " " + expert.getPrenom());
        title.getStyleClass().add("card-title");

        VBox content = new VBox(5);
        content.getStyleClass().add("card-content");

        addCardField(content, "Email", expert.getEmail());
        addCardField(content, "Zone", expert.getZone());

        Label status = new Label(expert.getDispo() == Dispo.DISPONIBLE ? "Disponible" : "Non disponible");
        status.getStyleClass().add(expert.getDispo() == Dispo.DISPONIBLE ? "status-available" : "status-unavailable");

        card.getChildren().addAll(title, content, status);

        card.setOnMouseClicked(e -> {
            selectedExpert = expert;
            updateCardSelection(card);
        });

        return card;
    }

    private void addCardField(VBox container, String label, String value) {
        HBox field = new HBox(5);
        Label labelNode = new Label(label + ":");
        labelNode.getStyleClass().add("field-label");
        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("field-value");
        field.getChildren().addAll(labelNode, valueNode);
        container.getChildren().add(field);
    }

    private void updateCardSelection(VBox selectedCard) {
        expertGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-card"));
        selectedCard.getStyleClass().add("selected-card");
    }

    private void loadExperts() {
        try {
            List<Expert> experts = service.select();
            expertList = FXCollections.observableArrayList(experts);
            filterExperts("");
        } catch (SQLException e) {
            showError("Erreur lors du chargement des experts: " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterExpert.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un expert");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            loadExperts();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue d'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        if (selectedExpert == null) {
            ValidationUtils.showAlert("Erreur", "Veuillez sélectionner un expert à modifier", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierExpert.fxml"));
            Parent root = loader.load();

            ModifierExpert controller = loader.getController();
            controller.setExpert(selectedExpert);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Expert");
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> refreshTable());

            stage.showAndWait();
        } catch (IOException e) {
            ValidationUtils.showAlert("Erreur", "Erreur lors du chargement de la fenêtre de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshTable() {
        try {
            ObservableList<Expert> experts = FXCollections.observableArrayList(service.select());
            expertList = experts;
            filterExperts("");
        } catch (SQLException e) {
            ValidationUtils.showAlert("Erreur", "Erreur lors du rafraîchissement de la table: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onSupprimer() {
        if (selectedExpert == null) {
            showAlert("Information", "Veuillez sélectionner un expert à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'expert");
        alert.setContentText("Voulez-vous vraiment supprimer cet expert ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                service.delete(selectedExpert.getId());
                loadExperts();
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer l'expert: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
