package esprit.tn.demo.controllers.GestionAnimal;

import esprit.tn.demo.services.GestionAnimal.SuiviServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class SuiviDeleteController {

    @FXML
    private TextField idField;

    private final SuiviServiceImpl suiviService = new SuiviServiceImpl();

    @FXML
    private void handleDelete() {
        try {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un ID.");
                return;
            }

            int id = Integer.parseInt(idText);

            // Vérifier si le suivi existe
            if (suiviService.getSuiviById(id) != null) {
                suiviService.deleteSuivi(id);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le suivi a été supprimé avec succès.");
                idField.clear(); // Reset field after success
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun suivi trouvé avec l'ID " + id);
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être un nombre entier valide.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
