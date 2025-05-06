package controllers;

import entities.Culture;
import enums.BesoinsEngrais;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.CultureService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class AfficherCulture {

    @FXML private GridPane cultureGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML
    private Label outputLabel;
    private final CultureService cultureService = new CultureService();
    private ObservableList<Culture> cultureList;
    private Culture selectedCulture;

    @FXML
    public void initialize() {
        setupSortComboBox();
        loadCultures();
        setupSearch();
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll(
                "Nom (A-Z)",
                "Nom (Z-A)",
                "Rendement croissant",
                "Rendement décroissant",
                "Région (A-Z)"
        );
        sortComboBox.setValue("Nom (A-Z)");
        sortComboBox.setOnAction(e -> sortCultures());
    }

    private void sortCultures() {
        if (cultureList == null) return;

        switch (sortComboBox.getValue()) {
            case "Nom (A-Z)":
                cultureList.sort(Comparator.comparing(Culture::getNom, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                cultureList.sort((c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(c2.getNom(), c1.getNom()));
                break;
            case "Rendement croissant":
                cultureList.sort(Comparator.comparing(Culture::getRendementMoyen));
                break;
            case "Rendement décroissant":
                cultureList.sort((c1, c2) -> Float.compare(c2.getRendementMoyen(), c1.getRendementMoyen()));
                break;
            case "Région (A-Z)":
                cultureList.sort(Comparator.comparing(Culture::getRegion, String.CASE_INSENSITIVE_ORDER));
                break;
        }

        filterCultures(searchField.getText());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCultures(newValue);
        });
    }

    private void filterCultures(String searchText) {
        cultureGrid.getChildren().clear();
        int row = 0;
        int col = 0;

        for (Culture culture : cultureList) {
            if (matchesSearch(culture, searchText)) {
                VBox card = createCultureCard(culture);
                cultureGrid.add(card, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    private boolean matchesSearch(Culture culture, String searchText) {
        if (searchText == null || searchText.isEmpty()) return true;

        searchText = searchText.toLowerCase();
        return culture.getNom().toLowerCase().contains(searchText) ||
                culture.getRegion().toLowerCase().contains(searchText) ||
                culture.getTypeCulture().toLowerCase().contains(searchText);
    }

    private VBox createCultureCard(Culture culture) {
        VBox card = new VBox(10);
        card.getStyleClass().add("culture-card");

        Label title = new Label(culture.getNom());
        title.getStyleClass().add("card-title");

        VBox content = new VBox(5);
        content.getStyleClass().add("card-content");

        addCardField(content, "Type", culture.getTypeCulture());
        addCardField(content, "Région", culture.getRegion());
        addCardField(content, "Rendement", culture.getRendementMoyen() + " t/ha");
        addCardField(content, "Engrais", culture.getBesoinsEngrais().name());

        Label date = new Label("Plantation: " +
                culture.getDatePlantation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        content.getChildren().add(date);

        card.getChildren().addAll(title, content);

        card.setOnMouseClicked(e -> {
            selectedCulture = culture;
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
        cultureGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-card"));
        selectedCard.getStyleClass().add("selected-card");
    }

    private void loadCultures() {
        try {
            List<Culture> list = cultureService.select();
            cultureList = FXCollections.observableArrayList(list);
            filterCultures("");
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCulture.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter Culture");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadCultures();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'ajout.");
        }
    }

    @FXML
    private void onModifier() {
        if (selectedCulture == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une culture.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCulture.fxml"));
            Parent root = loader.load();

            ModifierCulture controller = loader.getController();
            controller.setCulture(selectedCulture);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Culture");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadCultures());

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Chargement de la modification impossible.");
        }
    }

    @FXML
    private void onSupprimer() {
        if (selectedCulture == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une culture.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la culture");
        alert.setContentText("Voulez-vous vraiment supprimer cette culture ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                cultureService.delete(selectedCulture.getId());
                loadCultures();
            } catch (SQLException e) {
                showAlert("Erreur", "Suppression échouée : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onOpenCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendar.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Calendrier des Cultures");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le calendrier.");
        }
    }
    @FXML
    private void onPredire() {
        if (selectedCulture == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une culture.");
            return;
        }

        try {
            String command = "python src/main/resources/script/analyse_rendement.py --densite "
                    + selectedCulture.getDensitePlantation() +
                    " --eau " + selectedCulture.getBesoinsEau() +
                    " --cout " + selectedCulture.getCoutMoyen();
            Process process = new ProcessBuilder(command.split(" ")).start();


            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();


            showPredictionAlert(output.toString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showPredictionAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prediction Result");
        alert.setHeaderText("Prediction Completed");
        alert.setContentText("The predicted result is: \n" + message);

        alert.showAndWait();
    }



    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}