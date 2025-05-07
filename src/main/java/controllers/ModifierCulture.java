package controllers;

import entities.Culture;
import enums.BesoinsEngrais;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CultureService;

public class ModifierCulture {

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

    private Culture culture;
    private final CultureService cultureService = new CultureService();

    public void setCulture(Culture culture) {
        this.culture = culture;

        nomField.setText(culture.getNom());
        surfaceField.setText(String.valueOf(culture.getSurface()));
        datePlantationPicker.setValue(culture.getDatePlantation());
        dateRecoltePicker.setValue(culture.getDateRecolte());
        regionField.setText(culture.getRegion());
        typeCultureField.setText(culture.getTypeCulture());
        densiteField.setText(String.valueOf(culture.getDensitePlantation()));
        eauField.setText(String.valueOf(culture.getBesoinsEau()));
        engraisCombo.getItems().setAll(BesoinsEngrais.values());
        engraisCombo.setValue(culture.getBesoinsEngrais());
        rendementField.setText(String.valueOf(culture.getRendementMoyen()));
        coutField.setText(String.valueOf(culture.getCoutMoyen()));
        userIdField.setText(String.valueOf(culture.getIdUser()));
    }

    @FXML
    private void onModifier() {
        try {
            culture.setNom(nomField.getText());
            culture.setSurface(Float.parseFloat(surfaceField.getText()));
            culture.setDatePlantation(datePlantationPicker.getValue());
            culture.setDateRecolte(dateRecoltePicker.getValue());
            culture.setRegion(regionField.getText());
            culture.setTypeCulture(typeCultureField.getText());
            culture.setDensitePlantation(Float.parseFloat(densiteField.getText()));
            culture.setBesoinsEau(Float.parseFloat(eauField.getText()));
            culture.setBesoinsEngrais(engraisCombo.getValue());
            culture.setRendementMoyen(Float.parseFloat(rendementField.getText()));
            culture.setCoutMoyen(Float.parseFloat(coutField.getText()));
            culture.setIdUser(Integer.parseInt(userIdField.getText()));
            cultureService.update(culture);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Culture modifiée avec succès !");
            closeWindow();

        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
