package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class VeterinaireDeleteController {

    @FXML private TextField idField;
    private final VeterinaireServiceImpl service = new VeterinaireServiceImpl();
    private Veterinaire veterinaire; // ADD THIS

    // Setter to pre-fill the form
    public void setVeterinaire(Veterinaire veterinaire) {
        this.veterinaire = veterinaire;
        if (veterinaire != null && idField != null) {
            idField.setText(String.valueOf(veterinaire.getId()));
        }
    }

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

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
