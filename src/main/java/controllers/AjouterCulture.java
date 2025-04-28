package controllers;

import entities.Culture;
import enums.BesoinsEngrais;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.CultureService;

import java.time.LocalDate;

public class AjouterCulture {

    @FXML private TextField nomField;
    @FXML private TextField surfaceField;
    @FXML private DatePicker datePlantationPicker;
    @FXML private DatePicker dateRecoltePicker;
    @FXML private TextField regionField;
    @FXML private TextField typeCultureField;
    @FXML private TextField densiteField;
    @FXML private TextField eauField;
    @FXML private ComboBox<BesoinsEngrais> engraisCombo;
    @FXML private TextField rendementField;
    @FXML private TextField coutField;
    @FXML private TextField userIdField;
    @FXML private Label messageLabel;

    private final CultureService cultureService = new CultureService();

    @FXML
    public void initialize() {
        engraisCombo.getItems().addAll(BesoinsEngrais.values());
    }

    @FXML
    private void onAjout() {
        try {
            Culture culture = new Culture(
                    nomField.getText().trim(),
                    Float.parseFloat(surfaceField.getText().trim()),
                    datePlantationPicker.getValue(),
                    dateRecoltePicker.getValue(),
                    regionField.getText().trim(),
                    typeCultureField.getText().trim(),
                    Float.parseFloat(densiteField.getText().trim()),
                    Float.parseFloat(eauField.getText().trim()),
                    engraisCombo.getValue(),
                    Float.parseFloat(rendementField.getText().trim()),
                    Float.parseFloat(coutField.getText().trim()),
                    Integer.parseInt(userIdField.getText().trim())
            );

            cultureService.add(culture);
            messageLabel.setText("Culture ajoutée avec succès !");
            messageLabel.setStyle("-fx-text-fill: green");
            clearFields();

        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red");
        }
    }

    private void clearFields() {
        nomField.clear();
        surfaceField.clear();
        datePlantationPicker.setValue(null);
        dateRecoltePicker.setValue(null);
        regionField.clear();
        typeCultureField.clear();
        densiteField.clear();
        eauField.clear();
        engraisCombo.setValue(null);
        rendementField.clear();
        coutField.clear();
        userIdField.clear();
    }
}
