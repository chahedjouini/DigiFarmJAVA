package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class AnimalUpdateController {

    @FXML private TextField idField, nomField, typeField, raceField, ageField, poidsField;

    private final AnimalServiceImpl service = new AnimalServiceImpl();

    @FXML
    public void handleUpdateAnimal() {
        try {
            Animal animal = new Animal(
                    Integer.parseInt(idField.getText()),
                    nomField.getText(),
                    typeField.getText(),
                    Integer.parseInt(ageField.getText()),
                    Float.parseFloat(poidsField.getText()),
                    raceField.getText());

            service.updateAnimal(animal);
            new Alert(Alert.AlertType.INFORMATION, "Animal mis à jour !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
