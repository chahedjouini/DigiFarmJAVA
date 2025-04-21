package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class VeterinaireReadController {

    @FXML private TableView<Veterinaire> veterinaireTable;
    @FXML private TableColumn<Veterinaire, Number> idCol;
    @FXML private TableColumn<Veterinaire, String> nomCol;
    @FXML private TableColumn<Veterinaire, Number> numTelCol;
    @FXML private TableColumn<Veterinaire, String> emailCol;
    @FXML private TableColumn<Veterinaire, String> adresse_cabineCol;

    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        nomCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        numTelCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getnum_tel()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        adresse_cabineCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getadresse_cabine()));

        ObservableList<Veterinaire> list = FXCollections.observableArrayList(veterinaireService.getAllVeterinaires());
        veterinaireTable.setItems(list);
    }
}
