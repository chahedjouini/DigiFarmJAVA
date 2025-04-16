package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AnimalReadController {

    @FXML private TableView<Animal> tableView;
    @FXML private TableColumn<Animal, Integer> idCol;
    @FXML private TableColumn<Animal, String> nomCol;
    @FXML private TableColumn<Animal, String> typeCol;
    @FXML private TableColumn<Animal, String> raceCol;
    @FXML private TableColumn<Animal, Integer> ageCol;
    @FXML private TableColumn<Animal, Float> poidsCol;

    private final AnimalServiceImpl service = new AnimalServiceImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        nomCol.setCellValueFactory(cell -> cell.getValue().nomProperty());
        typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());
        raceCol.setCellValueFactory(cell -> cell.getValue().raceProperty());
        ageCol.setCellValueFactory(cell -> cell.getValue().ageProperty().asObject());
        poidsCol.setCellValueFactory(cell -> cell.getValue().poidsProperty().asObject());
    }

    @FXML
    public void loadAnimals() {
        tableView.setItems(FXCollections.observableArrayList(service.getAllAnimals()));
    }
}
