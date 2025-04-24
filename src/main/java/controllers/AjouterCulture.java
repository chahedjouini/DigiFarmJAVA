package controllers;

import entities.Culture;
import enums.BesoinsEngrais;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.CultureService;

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

            Culture culture = new Culture(
                    nom,
                    surface,
                    datePlantationPicker.getValue(),
                    dateRecoltePicker.getValue(),
                    regionField.getText().trim(),
                    typeCultureField.getText().trim(),
                    densite,
                    eau,
                    engraisCombo.getValue(),
                    rendement,
                    cout,
                    userId
            );

            cultureService.add(culture);
            showSuccess("Culture ajoutée avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showError("Format numérique invalide. Vérifiez les champs numériques.");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
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

    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: red");
    }

    private void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: green");
    }
}
