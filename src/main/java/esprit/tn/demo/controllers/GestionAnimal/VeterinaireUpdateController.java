package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.services.GestionAnimal.VeterinaireServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class VeterinaireUpdateController {

    @FXML private TextField idField;
    @FXML private TextField nomField;
    @FXML private TextField numTelField;
    @FXML private TextField emailField;
    @FXML private TextField adresseCabineField;

    private final VeterinaireServiceImpl service = new VeterinaireServiceImpl();
    private Veterinaire veterinaire; // ADD THIS

    // Setter to pre-fill the form
    public void setVeterinaire(Veterinaire veterinaire) {
        this.veterinaire = veterinaire;
        if (veterinaire != null) {
            idField.setText(String.valueOf(veterinaire.getId()));
        }
    }
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

    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
