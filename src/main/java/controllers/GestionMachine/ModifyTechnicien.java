package controllers.GestionMachine;

import entities.GestionMachine.Technicien;
import services.GestionMachine.TechnicienService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyTechnicien {

    @FXML
    private TextField nameField, prenomField, specialiteField, emailField, telephoneField, localisationField, latitudeField, longitudeField;

    private TechnicienService technicienService = new TechnicienService();
    private Technicien technicien;

    // Method to set the Technicien object and populate form fields
    public void setTechnicien(Technicien technicien) {
        this.technicien = technicien;
        nameField.setText(technicien.getName());
        prenomField.setText(technicien.getPrenom());
        specialiteField.setText(technicien.getSpecialite());
        emailField.setText(technicien.getEmail());
        telephoneField.setText(String.valueOf(technicien.getTelephone()));
        localisationField.setText(technicien.getLocalisation());
        latitudeField.setText(String.valueOf(technicien.getLatitude()));
        longitudeField.setText(String.valueOf(technicien.getLongitude()));
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            technicien.setName(nameField.getText());
            technicien.setPrenom(prenomField.getText());
            technicien.setSpecialite(specialiteField.getText());
            technicien.setEmail(emailField.getText());
            technicien.setTelephone(Integer.parseInt(telephoneField.getText()));
            technicien.setLocalisation(localisationField.getText());
            technicien.setLatitude(Float.parseFloat(latitudeField.getText()));
            technicien.setLongitude(Float.parseFloat(longitudeField.getText()));

            technicienService.update(technicien);
            closeWindow();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}