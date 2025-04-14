package esprit.tn.demo.tests;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private final TableView<Animal> table = new TableView<>();
    private final AnimalService animalService = new AnimalService();

    // Form fields
    private final TextField nomField = new TextField();
    private final TextField typeField = new TextField();
    private final TextField raceField = new TextField();
    private final TextField ageField = new TextField();
    private final TextField poidsField = new TextField();

    @Override
    public void start(Stage stage) {
        setupTableColumns();
        setupFormFields();
        setupButtons();
        refreshTable();

        // Main layout
        VBox root = new VBox(10,
                table,
                createInputBox(),
                createButtonBox()
        );
        root.setPadding(new Insets(10));

        // Scene setup
        Scene scene = new Scene(root, 900, 500);
        stage.setScene(scene);
        stage.setTitle("Animal Management System");
        stage.show();
    }

    private void setupTableColumns() {
        TableColumn<Animal, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cell -> cell.getValue().nomProperty());

        TableColumn<Animal, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());

        TableColumn<Animal, String> raceCol = new TableColumn<>("Race");
        raceCol.setCellValueFactory(cell -> cell.getValue().raceProperty());

        TableColumn<Animal, Number> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(cell -> cell.getValue().ageProperty());

        TableColumn<Animal, Number> poidsCol = new TableColumn<>("Poids (kg)");
        poidsCol.setCellValueFactory(cell -> cell.getValue().poidsProperty());

        table.getColumns().addAll(nomCol, typeCol, raceCol, ageCol, poidsCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupFormFields() {
        nomField.setPromptText("Nom");
        typeField.setPromptText("Type");
        raceField.setPromptText("Race");
        ageField.setPromptText("Age");
        poidsField.setPromptText("Poids (kg)");

        // Numeric validation
        ageField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                ageField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        poidsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                poidsField.setText(oldVal);
            }
        });
    }

    private HBox createInputBox() {
        return new HBox(10, nomField, typeField, raceField, ageField, poidsField);
    }

    private HBox createButtonBox() {
        Button addBtn = new Button("Ajouter");
        Button updateBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");
        Button clearBtn = new Button("Effacer");

        addBtn.setOnAction(e -> handleAdd());
        updateBtn.setOnAction(e -> handleUpdate());
        deleteBtn.setOnAction(e -> handleDelete());
        clearBtn.setOnAction(e -> clearFields());

        return new HBox(10, addBtn, updateBtn, deleteBtn, clearBtn);
    }

    private void setupButtons() {
        table.setOnMouseClicked(e -> {
            Animal selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                populateForm(selected);
            }
        });
    }

    private void handleAdd() {
        try {
            Animal newAnimal = createAnimalFromForm();
            animalService.add(newAnimal);
            refreshTable();
            clearFields();
            showAlert("Succès", "Animal ajouté avec succès", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs valides pour l'âge et le poids", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleUpdate() {
        Animal selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Avertissement", "Veuillez sélectionner un animal à modifier", Alert.AlertType.WARNING);
            return;
        }

        try {
            updateAnimalFromForm(selected);
            animalService.update(selected);
            refreshTable();
            showAlert("Succès", "Animal modifié avec succès", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs valides pour l'âge et le poids", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDelete() {
        Animal selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Avertissement", "Veuillez sélectionner un animal à supprimer", Alert.AlertType.WARNING);
            return;
        }

        try {
            animalService.delete(selected);
            refreshTable();
            clearFields();
            showAlert("Succès", "Animal supprimé avec succès", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Animal createAnimalFromForm() throws NumberFormatException {
        return new Animal(
                0, // ID will be generated by database
                nomField.getText(),
                typeField.getText(),
                Integer.parseInt(ageField.getText()),
                Float.parseFloat(poidsField.getText()),
                raceField.getText()
        );
    }

    private void updateAnimalFromForm(Animal animal) throws NumberFormatException {
        animal.setNom(nomField.getText());
        animal.setType(typeField.getText());
        animal.setRace(raceField.getText());
        animal.setAge(Integer.parseInt(ageField.getText()));
        animal.setPoids(Float.parseFloat(poidsField.getText()));
    }

    private void populateForm(Animal animal) {
        nomField.setText(animal.getNom());
        typeField.setText(animal.getType());
        raceField.setText(animal.getRace());
        ageField.setText(String.valueOf(animal.getAge()));
        poidsField.setText(String.valueOf(animal.getPoids()));
    }

    private void refreshTable() {
        ObservableList<Animal> data = FXCollections.observableArrayList(animalService.getAll());
        table.setItems(data);
    }

    private void clearFields() {
        nomField.clear();
        typeField.clear();
        raceField.clear();
        ageField.clear();
        poidsField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}