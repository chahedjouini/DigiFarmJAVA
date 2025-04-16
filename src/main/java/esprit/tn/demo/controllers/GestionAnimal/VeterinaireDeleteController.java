package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VeterinaireDeleteController {

    @FXML private TextField idField;
    private final VeterinaireServiceImpl service = new VeterinaireServiceImpl();

    @FXML
    public void handleDeleteVeterinaire() {
        try {
            int id = Integer.parseInt(idField.getText());
            service.deleteVeterinaire(id);
            new Alert(Alert.AlertType.INFORMATION, "Vétérinaire supprimé !").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage()).show();
        }
    }
}
