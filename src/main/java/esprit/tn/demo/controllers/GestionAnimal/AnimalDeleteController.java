package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AnimalDeleteController {

    @FXML private TextField idField;
    private final AnimalServiceImpl service = new AnimalServiceImpl();
    private Animal animal; // ADD THIS

    // Setter to pre-fill the form
    public void setAnimal(Animal animal) {
        this.animal = animal;
        if (animal != null && idField != null) {
            idField.setText(String.valueOf(animal.getId()));
        }
    }

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

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
