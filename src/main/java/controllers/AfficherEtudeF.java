package controllers;

import entities.Culture;
import entities.Etude;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.EtudeService;
import utils.PDFGenerator;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import enums.Role;
import entities.User;

public class AfficherEtudeF implements FrontboardController.UserAwareController {

    @FXML private GridPane etudeGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    private final EtudeService etudesService = new EtudeService();
    private ObservableList<Etude> etudeList;
    private Etude selectedEtude;
    private User currentUser;

    @FXML
    public void initialize() {
        setupSortComboBox();
        setupSearch();
    }

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        etudesService.setUser(user); // important
        loadEtudes();
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll("Nom (A-Z)", "Nom (Z-A)", "Date (A-Z)", "Date (Z-A)");
        sortComboBox.setValue("Nom (A-Z)");
        sortComboBox.setOnAction(e -> sortEtudes());
    }

    private void sortEtudes() {
        if (etudeList == null) return;

        switch (sortComboBox.getValue()) {
            case "Nom (A-Z)" -> etudeList.sort((e1, e2) -> e1.getCulture().getNom().compareToIgnoreCase(e2.getCulture().getNom()));
            case "Nom (Z-A)" -> etudeList.sort((e1, e2) -> e2.getCulture().getNom().compareToIgnoreCase(e1.getCulture().getNom()));
            case "Date (A-Z)" -> etudeList.sort((e1, e2) -> e1.getDateR().compareTo(e2.getDateR()));
            case "Date (Z-A)" -> etudeList.sort((e1, e2) -> e2.getDateR().compareTo(e1.getDateR()));
        }
        filterEtudes(searchField.getText());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEtudes(newVal));
    }

    private void filterEtudes(String searchText) {
        etudeGrid.getChildren().clear();
        int row = 0, col = 0;

        for (Etude e : etudeList) {
            if (matchesSearch(e, searchText)) {
                VBox card = createEtudeCard(e);
                etudeGrid.add(card, col, row);
                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
                animateCard(card);
            }
        }
    }

    private boolean matchesSearch(Etude e, String text) {
        if (text == null || text.isEmpty()) return true;
        text = text.toLowerCase();
        return e.getCulture().getNom().toLowerCase().contains(text)
                || e.getExpert().getNom().toLowerCase().contains(text)
                || e.getClimat().toString().toLowerCase().contains(text)
                || e.getTypeSol().toString().toLowerCase().contains(text);
    }

    private VBox createEtudeCard(Etude etude) {
        VBox card = new VBox(10);
        card.getStyleClass().add("etude-card");

        Label title = new Label("Culture: " + etude.getCulture().getNom());
        title.getStyleClass().add("card-title");

        VBox content = new VBox(5);
        content.getStyleClass().add("card-content");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        addCardField(content, "Date", etude.getDateR().format(formatter));
        addCardField(content, "Expert", etude.getExpert().getNom());
        addCardField(content, "Climat", etude.getClimat().toString());
        addCardField(content, "Sol", etude.getTypeSol().toString());
        addCardField(content, "Irrigation", etude.isIrrigation() ? "Oui" : "Non");
        addCardField(content, "Fertilisation", etude.isFertilisation() ? "Oui" : "Non");
        addCardField(content, "Prix", etude.getPrix() + " DT");
        addCardField(content, "Rendement", etude.getRendement() + " T");
        addCardField(content, "Précipitations", etude.getPrecipitations() + " mm");
        addCardField(content, "Main-d'œuvre", etude.getMainOeuvre() + " H");

        Button pdfButton = new Button("Générer PDF");
        pdfButton.setOnAction(event -> onGeneratePDF(etude));

        card.getChildren().addAll(title, content, pdfButton);
        card.setOnMouseClicked(e -> {
            selectedEtude = etude;
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
        etudeGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-card"));
        selectedCard.getStyleClass().add("selected-card");
    }

    private void animateCard(VBox card) {
        TranslateTransition t = new TranslateTransition(Duration.millis(500), card);
        t.setFromX(100); t.setToX(0); t.play();
        FadeTransition f = new FadeTransition(Duration.millis(500), card);
        f.setFromValue(0); f.setToValue(1); f.play();
    }

    private void loadEtudes() {
        try {
            List<Etude> list = (currentUser != null && currentUser.getRole() != Role.ADMIN)
                    ? etudesService.getEtudesByUser()
                    : etudesService.select();

            etudeList = FXCollections.observableArrayList(list);
            filterEtudes("");
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEtude.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter Étude");
            stage.setScene(scene);
            stage.showAndWait();
            loadEtudes();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre d’ajout.");
        }
    }

    @FXML private void onModifier() {
        if (selectedEtude == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEtude.fxml"));
            Scene scene = new Scene(loader.load());
            ModifierEtude controller = loader.getController();
            controller.setEtude(selectedEtude);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier Étude");
            stage.showAndWait();
            loadEtudes();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d’ouvrir la fenêtre de modification.");
        }
    }

    @FXML private void onSupprimer() {
        if (selectedEtude == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }
        try {
            etudesService.delete(selectedEtude.getId());
            etudeList.remove(selectedEtude);
            filterEtudes(searchField.getText());
        } catch (SQLException e) {
            showAlert("Erreur", "Suppression échouée : " + e.getMessage());
        }
    }

    @FXML private void onGeneratePDF(Etude selectedEtude) {
        if (selectedEtude == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }
        try {
            String outputPath = "C:\\Users\\yassi\\OneDrive - ESPRIT\\Desktop\\" + "etude_" + selectedEtude.getId() + ".pdf";
            PDFGenerator.generateEtudePDF(selectedEtude, outputPath);
            showAlert("Succès", "PDF généré avec succès.");
            File pdfFile = new File(outputPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void onStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stat.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Statistiques des Etudes");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les statistiques.");
        }
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
