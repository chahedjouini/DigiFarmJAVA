package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VeterinaireUpdateController {

    @FXML private TextField idField;
    @FXML private TextField nomField;
    @FXML private TextField numTelField;
    @FXML private TextField emailField;
    @FXML private TextField adresseCabineField;

    private final VeterinaireServiceImpl service = new VeterinaireServiceImpl();

    @FXML
    public void handleUpdateVeterinaire() {
        try {
            int id = Integer.parseInt(idField.getText());
            Veterinaire v = new Veterinaire(id, nomField.getText(),
                    Integer.parseInt(numTelField.getText()),
                    emailField.getText(),
                    adresseCabineField.getText());
            service.updateVeterinaire(v);
            new Alert(Alert.AlertType.INFORMATION, "Mise à jour effectuée !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
