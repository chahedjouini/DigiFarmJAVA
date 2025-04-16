package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Technicien;
import esprit.tn.demo.services.GestionMachine.TechnicienService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifyTechnicien {

    @FXML private TextField nameField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField localisationField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private TechnicienService technicienService = new TechnicienService();
    private Technicien technicien;

    public void setTechnicienData(Technicien technicien) {
        this.technicien = technicien;
        populateFields();
    }

    private void populateFields() {
        if (technicien != null) {
            nameField.setText(technicien.getName());
            prenomField.setText(technicien.getPrenom());
            specialiteField.setText(technicien.getSpecialite());
            emailField.setText(technicien.getEmail());
            telephoneField.setText(String.valueOf(technicien.getTelephone()));
            localisationField.setText(technicien.getLocalisation());
            latitudeField.setText(String.valueOf(technicien.getLatitude()));
            longitudeField.setText(String.valueOf(technicien.getLongitude()));
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            updateTechnicienFromFields();
            technicienService.update(technicien);
            showAlert("Succès", "Technicien mis à jour avec succès !");
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private boolean validateInputs() {
        String nameText = nameField.getText().trim();
        if (nameText.isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire !");
            nameField.requestFocus();
            return false;
        }

        String prenomText = prenomField.getText().trim();
        if (prenomText.isEmpty()) {
            showAlert("Erreur", "Le prénom est obligatoire !");
            prenomField.requestFocus();
            return false;
        }

        String specialiteText = specialiteField.getText().trim();
        if (specialiteText.isEmpty()) {
            showAlert("Erreur", "La spécialité est obligatoire !");
            specialiteField.requestFocus();
            return false;
        }

        String emailText = emailField.getText().trim();
        if (emailText.isEmpty()) {
            showAlert("Erreur", "L'email est obligatoire !");
            emailField.requestFocus();
            return false;
        }
        if (!emailText.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Erreur", "L'email doit être valide !");
            emailField.requestFocus();
            return false;
        }

        String telephoneText = telephoneField.getText().trim();
        if (telephoneText.isEmpty()) {
            showAlert("Erreur", "Le téléphone est obligatoire !");
            telephoneField.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(telephoneText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le téléphone doit être un nombre entier !");
            telephoneField.requestFocus();
            return false;
        }

        String localisationText = localisationField.getText().trim();
        if (localisationText.isEmpty()) {
            showAlert("Erreur", "La localisation est obligatoire !");
            localisationField.requestFocus();
            return false;
        }

        String latitudeText = latitudeField.getText().trim();
        if (latitudeText.isEmpty()) {
            showAlert("Erreur", "La latitude est obligatoire !");
            latitudeField.requestFocus();
            return false;
        }
        try {
            Float.parseFloat(latitudeText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La latitude doit être un nombre valide !");
            latitudeField.requestFocus();
            return false;
        }

        String longitudeText = longitudeField.getText().trim();
        if (longitudeText.isEmpty()) {
            showAlert("Erreur", "La longitude est obligatoire !");
            longitudeField.requestFocus();
            return false;
        }
        try {
            Float.parseFloat(longitudeText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La longitude doit être un nombre valide !");
            longitudeField.requestFocus();
            return false;
        }

        return true;
    }

    private void updateTechnicienFromFields() {
        technicien.setName(nameField.getText().trim());
        technicien.setPrenom(prenomField.getText().trim());
        technicien.setSpecialite(specialiteField.getText().trim());
        technicien.setEmail(emailField.getText().trim());
        technicien.setTelephone(Integer.parseInt(telephoneField.getText().trim()));
        technicien.setLocalisation(localisationField.getText().trim());
        technicien.setLatitude(Float.parseFloat(latitudeField.getText().trim()));
        technicien.setLongitude(Float.parseFloat(longitudeField.getText().trim()));
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}