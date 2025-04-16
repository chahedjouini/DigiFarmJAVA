package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class AnimalAddController {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField raceField;
    @FXML private TextField ageField;
    @FXML private TextField poidsField;

    private final AnimalServiceImpl service = new AnimalServiceImpl();

    @FXML
    public void handleAddAnimal() {
        try {
            Animal animal = new Animal(0,
                    nomField.getText(),
                    typeField.getText(),
                    Integer.parseInt(ageField.getText()),
                    Float.parseFloat(poidsField.getText()),
                    raceField.getText());

            service.addAnimal(animal);
            new Alert(Alert.AlertType.INFORMATION, "Animal ajouté !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
