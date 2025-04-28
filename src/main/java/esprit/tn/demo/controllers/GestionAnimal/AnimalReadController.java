package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AnimalReadController {

    @FXML
    private TableView<Animal> animalTable;
    @FXML
    private TableColumn<Animal, Number> idCol;
    @FXML
    private TableColumn<Animal, String> nomCol;
    @FXML
    private TableColumn<Animal, String> typeCol;
    @FXML
    private TableColumn<Animal, String> raceCol;
    @FXML
    private TableColumn<Animal, Number> ageCol;
    @FXML
    private TableColumn<Animal, Number> poidsCol;
    @FXML
    private TableColumn<Animal, Void> actionsCol;
    @FXML
    private TextField searchNomField;
    @FXML
    private TextField searchTypeField;
    @FXML
    private TextField searchRaceField;

    private final AnimalServiceImpl animalService = new AnimalServiceImpl();
    private ObservableList<Animal> allAnimals;
    private FilteredList<Animal> filteredAnimals;

    @FXML
    public void initialize() {
        // Set up columns
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        nomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        raceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRace()));
        ageCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAge()));
        poidsCol.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getPoids()));

        // Define actions column with update and delete buttons
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                updateButton.setOnAction(event -> {
                    Animal animal = getTableView().getItems().get(getIndex());
                    if (animal != null) {
                        openUpdateAnimalForm(animal);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Animal selected for update.");
                    }
                });

                deleteButton.setOnAction(event -> {
                    Animal animal = getTableView().getItems().get(getIndex());
                    if (animal != null) {
                        openDeleteAnimalForm(animal);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Selection Error", "No Animal selected for deletion.");
                    }
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

        // Load animal data and set up filtering
        loadAnimalData();
        setupSearchListeners();
    }

    // Load the animal data into the table and initialize FilteredList
    private void loadAnimalData() {
        allAnimals = FXCollections.observableArrayList(animalService.getAllAnimals());
        filteredAnimals = new FilteredList<>(allAnimals, p -> true); // Initially show all
        animalTable.setItems(filteredAnimals);
    }

    // Set up listeners for the search fields
    private void setupSearchListeners() {
        searchNomField.textProperty().addListener((observable, oldValue, newValue) -> filterAnimals());
        searchTypeField.textProperty().addListener((observable, oldValue, newValue) -> filterAnimals());
        searchRaceField.textProperty().addListener((observable, oldValue, newValue) -> filterAnimals());
    }

    // Filter the animals based on the search terms
    private void filterAnimals() {
        if (filteredAnimals != null) {
            filteredAnimals.setPredicate(animal -> {
                String nomFiltre = searchNomField.getText().toLowerCase();
                String typeFiltre = searchTypeField.getText().toLowerCase();
                String raceFiltre = searchRaceField.getText().toLowerCase();

                // Check if the animal's properties contain the filter text
                boolean nomMatch = nomFiltre.isEmpty() || animal.getNom().toLowerCase().contains(nomFiltre);
                boolean typeMatch = typeFiltre.isEmpty() || animal.getType().toLowerCase().contains(typeFiltre);
                boolean raceMatch = raceFiltre.isEmpty() || animal.getRace().toLowerCase().contains(raceFiltre);

                // Combine the matches (you can modify this for OR logic if needed)
                return nomMatch && typeMatch && raceMatch;
            });
        }
    }


    // Handle adding a new animal
    @FXML
    private void handleAddAnimal() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/AnimalAddView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalAddView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Animal");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadAnimalData();  // Refresh the table after adding an animal
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Animal form: " + e.getMessage());
        }
    }

    // Open the form for updating an animal
    private void openUpdateAnimalForm(Animal animal) {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/AnimalUpdateView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalUpdateView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Update Animal");
            stage.initModality(Modality.APPLICATION_MODAL);
            AnimalUpdateController controller = loader.getController();
            controller.setAnimal(animal);
            stage.showAndWait();
            loadAnimalData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Update Animal form: " + e.getMessage());
        }
    }

    // Open the form for deleting an animal
    private void openDeleteAnimalForm(Animal animal) {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/AnimalDeleteView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalDeleteView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Delete Animal");
            stage.initModality(Modality.APPLICATION_MODAL);
            AnimalDeleteController controller = loader.getController();
            controller.setAnimal(animal);
            stage.showAndWait();
            loadAnimalData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Delete Animal form: " + e.getMessage());
        }
    }

    // Show an alert box with a given message
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
            URL location = getClass().getResource("/esprit/tn/demo/AnimalStatistiquesView.fxml");
            if (location == null) {
                throw new IOException("Cannot find AnimalStatistiquesView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Statistiques des Animaux");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Animal Statistics form: " + e.getMessage());
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
    private void handleOpenSuivi() {
        try {
            URL location = getClass().getResource("/esprit/tn/demo/SuiviReadView.fxml");
            if (location == null) {
                throw new IOException("Cannot find SuiviReadView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(location);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Suivi des Animaux");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Suivi form: " + e.getMessage());
        }
    }
}