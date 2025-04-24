package controllers;

import entities.Culture;
import entities.Etude;
import entities.Expert;
import enums.Climat;
import enums.TypeSol;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CultureService;
import services.EtudeService;
import services.ExpertService;

import java.util.List;

public class ModifierEtude {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Culture> cultureCombo;
    @FXML private ComboBox<Expert> expertCombo;
    @FXML private ComboBox<Climat> climatCombo;
    @FXML private ComboBox<TypeSol> typeSolCombo;
    @FXML private CheckBox irrigationCheck;
    @FXML private CheckBox fertilisationCheck;
    @FXML private TextField prixField;
    @FXML private TextField rendementField;
    @FXML private TextField precipitationsField;
    @FXML private TextField mainOeuvreField;
    @FXML private Label messageLabel;

    private final EtudeService etudeService = new EtudeService();
    private final CultureService cultureService = new CultureService();
    private final ExpertService expertService = new ExpertService();

    private Etude etude;

    public void setEtude(Etude etude) {
        this.etude = etude;
        fillFields();
    }

    private void fillFields() {
        try {
            cultureCombo.getItems().setAll(cultureService.select());
            expertCombo.getItems().setAll(expertService.select());
            climatCombo.getItems().setAll(Climat.values());
            typeSolCombo.getItems().setAll(TypeSol.values());

            datePicker.setValue(etude.getDateR());
            cultureCombo.setValue(etude.getCulture());
            expertCombo.setValue(etude.getExpert());
            climatCombo.setValue(etude.getClimat());
            typeSolCombo.setValue(etude.getTypeSol());
            irrigationCheck.setSelected(etude.isIrrigation());
            fertilisationCheck.setSelected(etude.isFertilisation());
            prixField.setText(String.valueOf(etude.getPrix()));
            rendementField.setText(String.valueOf(etude.getRendement()));
            precipitationsField.setText(String.valueOf(etude.getPrecipitations()));
            mainOeuvreField.setText(String.valueOf(etude.getMainOeuvre()));
        } catch (Exception e) {
            showError("Erreur de chargement : " + e.getMessage());
        }
    }

    @FXML
    private void onModifier() {
        try {
            if (datePicker.getValue() == null ||
                    cultureCombo.getValue() == null ||
                    expertCombo.getValue() == null ||
                    climatCombo.getValue() == null ||
                    typeSolCombo.getValue() == null ||
                    prixField.getText().isBlank() ||
                    rendementField.getText().isBlank() ||
                    precipitationsField.getText().isBlank() ||
                    mainOeuvreField.getText().isBlank()) {
                showError("Tous les champs doivent être remplis.");
                return;
            }

            float prix = Float.parseFloat(prixField.getText());
            float rendement = Float.parseFloat(rendementField.getText());
            float precipitations = Float.parseFloat(precipitationsField.getText());
            float mainOeuvre = Float.parseFloat(mainOeuvreField.getText());

            if (prix <= 0 || rendement <= 0 || precipitations <= 0 || mainOeuvre <= 0) {
                showError("Tous les champs numériques doivent être des valeurs positives.");
                return;
            }

            // Mise à jour de l’étude
            etude.setDateR(datePicker.getValue());
            etude.setCulture(cultureCombo.getValue());
            etude.setExpert(expertCombo.getValue());
            etude.setClimat(climatCombo.getValue());
            etude.setTypeSol(typeSolCombo.getValue());
            etude.setIrrigation(irrigationCheck.isSelected());
            etude.setFertilisation(fertilisationCheck.isSelected());
            etude.setPrix(prix);
            etude.setRendement(rendement);
            etude.setPrecipitations(precipitations);
            etude.setMainOeuvre(mainOeuvre);

            etudeService.update(etude);
            showSuccess("Étude modifiée avec succès !");
            closeWindow();

        } catch (NumberFormatException e) {
            showError("Veuillez saisir des nombres valides dans les champs numériques.");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    private void closeWindow() {
        ((Stage) datePicker.getScene().getWindow()).close();
    }
}
