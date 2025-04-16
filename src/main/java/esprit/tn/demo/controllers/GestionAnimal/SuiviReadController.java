package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SuiviReadController {

    @FXML private TableView<Suivi> suiviTable;
    @FXML private TableColumn<Suivi, Number> idCol;
    @FXML private TableColumn<Suivi, String> animalCol;
    @FXML private TableColumn<Suivi, String> tempCol; // Changed to String for float
    @FXML private TableColumn<Suivi, String> rythmeCol; // Changed to String for float
    @FXML private TableColumn<Suivi, String> etatCol;
    @FXML private TableColumn<Suivi, Number> idClientCol;
    @FXML private TableColumn<Suivi, String> analysisCol;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        animalCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnimal().toString()));
        tempCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTemperature())));
        rythmeCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRythmeCardiaque())));
        etatCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtat()));
        idClientCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdClient()));
        analysisCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnalysis()));

        ObservableList<Suivi> list = FXCollections.observableArrayList(suiviService.getAllSuivis());
        suiviTable.setItems(list);
    }
}
