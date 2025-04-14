package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AnimalController {
    @FXML private TextField nomField, typeField, raceField, ageField, poidsField;
    @FXML private Button addButton, updateButton, deleteButton;
    @FXML private TableView<Animal> animalTable;
    @FXML private TableColumn<Animal, Integer> idCol, ageCol;
    @FXML private TableColumn<Animal, String> nomCol, typeCol, raceCol;
    @FXML private TableColumn<Animal, Float> poidsCol;

    private final AnimalService animalService = new AnimalService();
    private final ObservableList<Animal> animalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAnimals();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        raceCol.setCellValueFactory(new PropertyValueFactory<>("race"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        poidsCol.setCellValueFactory(new PropertyValueFactory<>("poids"));
    }

    @FXML
    private void addAnimal() {
        try {
            Animal animal = new Animal(
                    0, // ID will be auto-generated
                    nomField.getText(),
                    typeField.getText(),
                    Integer.parseInt(ageField.getText()),
                    Float.parseFloat(poidsField.getText()),
                    raceField.getText()
            );
            animalService.add(animal);
            loadAnimals();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for age and weight", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateAnimal() {
        Animal selectedAnimal = animalTable.getSelectionModel().getSelectedItem();
        if (selectedAnimal != null) {
            try {
                selectedAnimal.setNom(nomField.getText());
                selectedAnimal.setType(typeField.getText());
                selectedAnimal.setRace(raceField.getText());
                selectedAnimal.setAge(Integer.parseInt(ageField.getText()));
                selectedAnimal.setPoids(Float.parseFloat(poidsField.getText()));
                animalService.update(selectedAnimal);
                loadAnimals();
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter valid numbers for age and weight", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void deleteAnimal() {
        Animal selectedAnimal = animalTable.getSelectionModel().getSelectedItem();
        if (selectedAnimal != null) {
            animalService.delete(selectedAnimal); // Fixed this line
            loadAnimals();
            clearFields();
        }
    }

    private void loadAnimals() {
        animalList.clear();
        animalList.addAll(animalService.getAll());
        animalTable.setItems(animalList);
    }

    private void clearFields() {
        nomField.clear();
        typeField.clear();
        raceField.clear();
        ageField.clear();
        poidsField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}