package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SuiviReadController {

    private static final Logger logger = LoggerFactory.getLogger(SuiviReadController.class);

    @FXML
    private TableView<Suivi> suiviTableView;
    @FXML
    private TableColumn<Suivi, Integer> idColumn;
    @FXML
    private TableColumn<Suivi, String> animalColumn;
    @FXML
    private TableColumn<Suivi, Float> temperatureColumn;
    @FXML
    private TableColumn<Suivi, Float> rythmeCardiaqueColumn;
    @FXML
    private TableColumn<Suivi, String> etatColumn;
    @FXML
    private TableColumn<Suivi, Integer> idClientColumn;
    @FXML
    private TableColumn<Suivi, String> analysisColumn;
    @FXML
    private TableColumn<Suivi, String> veterinaireNomColumn;
    @FXML
    private TableColumn<Suivi, Void> actionsColumn;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();
    private final AnimalServiceImpl animalService = new AnimalServiceImpl();
    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        animalColumn.setCellValueFactory(cellData -> {
            Suivi suivi = cellData.getValue();
            return new SimpleStringProperty(
                    suivi.getAnimal() != null ? suivi.getAnimal().getNom() : "N/A"
            );
        });
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        rythmeCardiaqueColumn.setCellValueFactory(new PropertyValueFactory<>("rythmeCardiaque"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        analysisColumn.setCellValueFactory(new PropertyValueFactory<>("analysis"));
        veterinaireNomColumn.setCellValueFactory(cellData -> {
            Suivi suivi = cellData.getValue();
            return new SimpleStringProperty(
                    suivi.getVeterinaire() != null ? suivi.getVeterinaire().getNom() : "N/A"
            );
        });

        analysisColumn.setVisible(true);

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                updateButton.setOnAction(event -> {
                    Suivi suivi = getTableView().getItems().get(getIndex());
                    if (suivi == null) {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Suivi selected for update.");
                        return;
                    }
                    System.out.println("Selected Suivi for update: " + suivi);
                    openUpdateSuiviForm(suivi);
                });
                deleteButton.setOnAction(event -> {
                    Suivi suivi = getTableView().getItems().get(getIndex());
                    if (suivi == null) {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Suivi selected for deletion.");
                        return;
                    }
                    System.out.println("Selected Suivi for deletion: " + suivi);
                    openDeleteSuviForm(suivi);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, updateButton, deleteButton));
                }
            }
        });

        suiviTableView.setOnMouseClicked(event -> {
            Suivi selectedSuivi = suiviTableView.getSelectionModel().getSelectedItem();
            if (selectedSuivi != null) {
                String animalName = selectedSuivi.getAnimal() != null ? selectedSuivi.getAnimal().getNom() : "N/A";
                String veterinaireName = selectedSuivi.getVeterinaire() != null ? selectedSuivi.getVeterinaire().getNom() : "N/A";
                showAlert(Alert.AlertType.INFORMATION, "Details",
                        "Animal Name: " + animalName + "\nVeterinaire Name: " + veterinaireName);
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection", "No Suivi selected.");
            }
        });

        // Add a new button for analysis
        TableColumn<Suivi, Void> analyzeColumn = new TableColumn<>("Analyze");
        analyzeColumn.setCellFactory(col -> new TableCell<>() {
            private final Button analyzeButton = new Button("Analyze");

            {
                analyzeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                analyzeButton.setOnAction(event -> {
                    Suivi suivi = getTableView().getItems().get(getIndex());
                    if (suivi == null) {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Suivi selected for analysis.");
                        return;
                    }
                    analyzeSuivi(suivi);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(analyzeButton);
                }
            }
        });

        suiviTableView.getColumns().add(analyzeColumn);

        loadSuiviData();
    }

    private void analyzeSuivi(Suivi suivi) {
        JSONObject data = new JSONObject();
        data.put("temperature", suivi.getTemperature());
        data.put("rythme_cardiaque", suivi.getRythmeCardiaque());
        String etat = suivi.getEtat();
        if ("Bon".equals(etat)) {
            etat = "Hel";
        }
        data.put("etat", etat);

        logger.info("Sending data to Open Source AI: {}", data.toString());

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api-inference.huggingface.co/models/gpt2"); // Replace with your desired model URL

        try {
            httpPost.setHeader("Authorization", "Bearer " + "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"); // Ensure environment variable is set
            httpPost.setHeader("Content-Type", "application/json");

            JSONObject payload = new JSONObject();
            payload.put("inputs", "Analyze the following medical data: " + data.toString());
            JSONObject parameters = new JSONObject();
            parameters.put("max_length", 100);
            parameters.put("temperature", 0.7);
            payload.put("parameters", parameters);
            JSONObject options = new JSONObject();
            options.put("wait_for_model", true);
            payload.put("options", options);

            StringEntity requestEntity = new StringEntity(payload.toString());
            httpPost.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);

            logger.info("Open Source AI API response: {}", responseString);

            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                try {
                    JSONObject responseJson = new JSONObject(responseString.substring(1, responseString.length() - 1)); // Remove array brackets
                    String generatedText = responseJson.getString("generated_text");
                    suivi.setAnalysis(generatedText);
                    suiviService.updateSuivi(suivi);
                    suiviTableView.refresh(); // Refresh the table to show the updated analysis
                    showAlert(Alert.AlertType.INFORMATION, "Analysis Result", "Analysis: " + generatedText);
                } catch (Exception e) {
                    logger.error("Error parsing API response: {}", e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Analysis Error", "Failed to process analysis result.");
                }
            } else {
                JSONObject errorJson = new JSONObject(responseString);
                String errorMessage = errorJson.has("error") ? errorJson.getString("error") : "Unknown error";
                logger.error("Hugging Face API Error: {}", errorMessage);
                showAlert(Alert.AlertType.ERROR, "API Error", "Hugging Face API Error: " + errorMessage);
            }

        } catch (IOException e) {
            logger.error("Error calling Open Source AI API: {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Analysis Error", "Failed to call the AI analysis service.");
        } finally {
            httpPost.releaseConnection();
        }
    }

    private void loadSuiviData() {
        List<Suivi> suivis = suiviService.getAllSuivis();
        // Load associated Animal and Veterinaire objects for display
        for (Suivi suivi : suivis) {
            if (suivi.getAnimal() != null) {
                Animal animal = animalService.getAnimalById(suivi.getAnimal().getId());
                suivi.setAnimal(animal);
            }
            if (suivi.getVeterinaire() != null) {
                Veterinaire veterinaire = veterinaireService.getVeterinaireById(suivi.getVeterinaire().getId());
                suivi.setVeterinaire(veterinaire);
            }
        }
        suiviTableView.setItems(FXCollections.observableArrayList(suivis));
    }

    @FXML
    private void handleAddSuivi() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviAddView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviAddView.fxml at esprit/tn/demo/SuiviAddView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Suivi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Suivi form: " + e.getMessage());
        }
    }

    private void openUpdateSuiviForm(Suivi suivi) {
        if (suivi == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Update form: Suivi is null.");
            return;
        }
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviUpdateView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviUpdateView.fxml at src/main/resources/esprit/tn/demo/SuiviUpdateView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Suivi");
            stage.initModality(Modality.APPLICATION_MODAL);
            SuiviUpdateController controller = loader.getController();
            controller.setSuivi(suivi);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Update Suivi form: " + e.getMessage());
        }
    }

    private void openDeleteSuviForm(Suivi suivi) {
        if (suivi == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Delete form: Suvi is null.");
            return;
        }
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviDeleteView.fxml");
            System.out.println("Attempting to load SuviDeleteView.fxml from: esprit/tn/demo/SuviDeleteView.fxml");
            System.out.println("Resource URL: " + location);
            if (location == null) {
                throw new IOException("Cannot find SuviDeleteView.fxml at esprit/tn/demo/SuviDeleteView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Delete Suvi");
            stage.initModality(Modality.APPLICATION_MODAL);
            SuiviDeleteController controller = loader.getController();
            controller.setSuivi(suivi);
            stage.showAndWait();
            loadSuiviData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Delete Suvi form: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenStatistiques() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/StatistiqueView.fxml");
            if (location == null) {
                throw new IOException("Impossible de trouver StatistiqueView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques des Suivis");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre des statistiques : " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenAnimaux() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/AnimalReadView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalReadView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Animaux");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Animaux form: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenVeterinaires() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/VeterinaireReadView.fxml");
            if (location == null) {
                throw new IOException("Cannot find VeterinaireReadView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Liste des Vétérinaires");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Veterinaires form: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Suivis");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Animal", "Température", "Rythme Cardiaque", "État", "ID Client", "Analyse", "Vétérinaire"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (Suivi suivi : suiviTableView.getItems()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(suivi.getId());
            row.createCell(1).setCellValue(suivi.getAnimal() != null ? suivi.getAnimal().getNom() : "N/A");

            Float temperature = suivi.getTemperature();
            if (temperature != null) {
                row.createCell(2).setCellValue(temperature.doubleValue());
            }

            Float rythmeCardiaque = suivi.getRythmeCardiaque();
            if (rythmeCardiaque != null) {
                row.createCell(3).setCellValue(rythmeCardiaque.doubleValue());
            }

            row.createCell(4).setCellValue(suivi.getEtat());

            Integer idClient = suivi.getIdClient();
            if (idClient != null) {
                row.createCell(5).setCellValue(idClient.intValue());
            }

            row.createCell(6).setCellValue(suivi.getAnalysis());
            row.createCell(7).setCellValue(suivi.getVeterinaire() != null ? suivi.getVeterinaire().getNom() : "N/A");
        }

        // Auto-resize columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the workbook to a file
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = "Suivis_" + dtf.format(now) + ".xlsx";

        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Data exported to " + fileName);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Error during Excel export: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

