package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class SuiviAddController {

    @FXML private ComboBox<Animal> animalComboBox;
    @FXML private TextField temperatureField;
    @FXML private TextField rythmeCardiaqueField;
    @FXML private TextField etatField;
    @FXML private TextField idClientField;
    @FXML private TextField analysisField;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();

    @FXML
    public void handleAdd() {
        try {
            // Get input values
            float temp = Float.parseFloat(temperatureField.getText());
            float rythme = Float.parseFloat(rythmeCardiaqueField.getText());
            String etat = etatField.getText();
            int idClient = Integer.parseInt(idClientField.getText());
            String analysis = analysisField.getText();

            // Validation
         //   if (selectedAnimal == null || etat.isEmpty() || analysis.isEmpty()) {
              //  showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
               // return;
          //  }

            // Create and add the Suivi
            Suivi s = new Suivi(0, selectedAnimal, temp, rythme, etat, idClient, analysis);
            suiviService.addSuivi(s);

            // Show success message
            showAlert(AlertType.INFORMATION, "Succès", "Suivi ajouté avec succès.");

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "Température, rythme cardiaque et ID client doivent être des nombres valides.");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
