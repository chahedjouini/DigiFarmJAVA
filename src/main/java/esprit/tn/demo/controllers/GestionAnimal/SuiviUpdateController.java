package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.GestionAnimal.AnimalServiceImpl;
import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SuiviUpdateController {

    @FXML private TextField idField;
    @FXML private ComboBox<Animal> animalComboBox;
    @FXML private TextField temperatureField;
    @FXML private TextField rythmeCardiaqueField;
    @FXML private TextField etatField;
    @FXML private TextField idClientField;
    @FXML private TextField analysisField;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();
    private final AnimalServiceImpl animalService = new AnimalServiceImpl();

    @FXML
    public void initialize() {
        List<Animal> animals = animalService.getAllAnimals();
        animalComboBox.getItems().addAll(animals);
    }

    @FXML
    public void handleUpdate() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Animal selectedAnimal = animalComboBox.getValue();
            float temp = Float.parseFloat(temperatureField.getText().trim());
            float rythme = Float.parseFloat(rythmeCardiaqueField.getText().trim());
            String etat = etatField.getText().trim();
            int idClient = Integer.parseInt(idClientField.getText().trim());
            String analysis = analysisField.getText().trim();

            if (selectedAnimal == null || etat.isEmpty() || analysis.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            Suivi s = new Suivi(id, selectedAnimal, temp, rythme, etat, idClient, analysis);
            suiviService.updateSuivi(s);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Suivi mis à jour avec succès.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Certains champs doivent contenir des nombres valides.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Optional method to populate the form with an existing Suivi (e.g. from a TableView selection)
    public void setSuiviData(Suivi s) {
        idField.setText(String.valueOf(s.getId()));
        animalComboBox.setValue(s.getAnimal());
        temperatureField.setText(String.valueOf(s.getTemperature()));
        rythmeCardiaqueField.setText(String.valueOf(s.getRythmeCardiaque()));
        etatField.setText(s.getEtat());
        idClientField.setText(String.valueOf(s.getIdClient()));
        analysisField.setText(s.getAnalysis());
    }
}
