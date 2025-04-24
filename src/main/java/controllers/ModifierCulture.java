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
            String nom = nomField.getText().trim();
            String surfaceText = surfaceField.getText().trim();
            String densiteText = densiteField.getText().trim();
            String eauText = eauField.getText().trim();
            String rendementText = rendementField.getText().trim();
            String coutText = coutField.getText().trim();
            String userIdText = userIdField.getText().trim();

            if (nom.isEmpty() || nom.length() > 255
                    || regionField.getText().trim().isEmpty()
                    || typeCultureField.getText().trim().isEmpty()
                    || surfaceText.isEmpty() || densiteText.isEmpty() || eauText.isEmpty()
                    || rendementText.isEmpty() || coutText.isEmpty()
                    || datePlantationPicker.getValue() == null
                    || dateRecoltePicker.getValue() == null
                    || engraisCombo.getValue() == null
                    || userIdText.isEmpty()) {
                showError("Tous les champs sont obligatoires et doivent être valides.");
                return;
            }

            float surface = Float.parseFloat(surfaceText);
            float densite = Float.parseFloat(densiteText);
            float eau = Float.parseFloat(eauText);
            float rendement = Float.parseFloat(rendementText);
            float cout = Float.parseFloat(coutText);
            int userId = Integer.parseInt(userIdText);

            if (surface <= 0 || densite <= 0 || eau <= 0 || rendement <= 0 || cout <= 0) {
                showError("Les valeurs numériques doivent être positives.");
                return;
            }

            // Mise à jour de la culture
            culture.setNom(nom);
            culture.setSurface(surface);
            culture.setDatePlantation(datePlantationPicker.getValue());
            culture.setDateRecolte(dateRecoltePicker.getValue());
            culture.setRegion(regionField.getText().trim());
            culture.setTypeCulture(typeCultureField.getText().trim());
            culture.setDensitePlantation(densite);
            culture.setBesoinsEau(eau);
            culture.setBesoinsEngrais(engraisCombo.getValue());
            culture.setRendementMoyen(rendement);
            culture.setCoutMoyen(cout);
            culture.setIdUser(userId);

            cultureService.update(culture);
            showSuccess("Culture modifiée avec succès !");
            closeWindow();

        } catch (NumberFormatException e) {
            showError("Format numérique invalide. Vérifiez les champs numériques.");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
