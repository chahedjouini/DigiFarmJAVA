package controllers;

import entities.Etude;  // Ensure this import is correct
import enums.Climat;
import enums.TypeSol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.EtudeService;
import utils.PDFGenerator;  // Ensure this import is correct
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.List;
import java.util.Map;

import javafx.scene.chart.PieChart;

public class AfficherEtude {

    @FXML private GridPane etudeGrid;
    @FXML private TextField searchField;

    private final EtudeService service = new EtudeService();
    private ObservableList<Etude> etudeList;
    private Etude selectedEtude;

    @FXML
    public void initialize() {
        loadEtudes();
        setupSearch();
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

        // Add the PDF button here
        Button pdfButton = new Button("Générer PDF");
        pdfButton.setOnAction(event -> onGeneratePDF(etude));  // Pass the Etude to the onGeneratePDF method

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

    private void loadEtudes() {
        try {
            List<Etude> list = service.select();
            etudeList = FXCollections.observableArrayList(list);
            filterEtudes("");
        } catch (SQLException e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
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

    @FXML
    private void onModifier() {
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

    @FXML
    private void onSupprimer() {
        if (selectedEtude == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }

        try {
            service.delete(selectedEtude.getId());
            etudeList.remove(selectedEtude);
            filterEtudes(searchField.getText());
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


    @FXML
    private void onGeneratePDF(Etude selectedEtude) {
        if (selectedEtude == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une étude.");
            return;
        }

        try {
            // Define the output path where you want to save the PDF
            String outputPath = "C:\\Users\\yassi\\OneDrive - ESPRIT\\Desktop\\" + "etude_" + selectedEtude.getId() + ".pdf";

            // Generate the PDF
            PDFGenerator.generateEtudePDF(selectedEtude, outputPath);

            // Show success alert
            showAlert("Succès", "PDF généré avec succès.");

            // Attempt to open the PDF if generated successfully
            File pdfFile = new File(outputPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(pdfFile); // Open the file with the default application
            } else {
                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF.");
            }

        } catch (Exception e) {
            // Show error message if an exception occurs
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }
    @FXML
    private void onStatistics() {
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


}
