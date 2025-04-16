package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AnimalDeleteController {

    @FXML private TextField idField;
    private final AnimalServiceImpl service = new AnimalServiceImpl();

    @FXML
    public void handleDeleteAnimal() {
        try {
            int id = Integer.parseInt(idField.getText());
            service.deleteAnimal(id);
            new Alert(Alert.AlertType.INFORMATION, "Animal supprimé !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
