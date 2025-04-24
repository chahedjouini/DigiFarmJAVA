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

import java.sql.SQLException;
import java.util.List;

public class AjouterEtude {

    @FXML private DatePicker dateField;
    @FXML private ComboBox<Culture> cultureCombo;
    @FXML private ComboBox<Expert> expertCombo;
    @FXML private ComboBox<Climat> climatCombo;
    @FXML private ComboBox<TypeSol> typeSolCombo;
    @FXML private CheckBox irrigationCheck;
    @FXML private CheckBox fertilisationCheck;
    @FXML private TextField prixField;
    @FXML private TextField rendementField;
    @FXML private TextField precipitationField;
    @FXML private TextField mainField;
    @FXML private Label messageLabel;

    private final EtudeService etudeService = new EtudeService();
    private final ExpertService expertService = new ExpertService();
    private final CultureService cultureService = new CultureService();

    @FXML
    public void initialize() {
        climatCombo.getItems().setAll(Climat.values());
        typeSolCombo.getItems().setAll(TypeSol.values());

        try {
            List<Expert> experts = expertService.select();
            List<Culture> cultures = cultureService.select();
            expertCombo.getItems().setAll(experts);
            cultureCombo.getItems().setAll(cultures);
        } catch (SQLException e) {
            showError("Erreur de chargement : " + e.getMessage());
        }
    }

    @FXML
    private void onAjout() {
        try {
            if (dateField.getValue() == null ||
                    cultureCombo.getValue() == null ||
                    expertCombo.getValue() == null ||
                    climatCombo.getValue() == null ||
                    typeSolCombo.getValue() == null ||
                    prixField.getText().isBlank() ||
                    rendementField.getText().isBlank() ||
                    precipitationField.getText().isBlank() ||
                    mainField.getText().isBlank()) {

                showError("Tous les champs sont obligatoires.");
                return;
            }

            float prix = Float.parseFloat(prixField.getText());
            float rendement = Float.parseFloat(rendementField.getText());
            float precipitations = Float.parseFloat(precipitationField.getText());
            float mainOeuvre = Float.parseFloat(mainField.getText());

            if (prix <= 0 || rendement <= 0 || precipitations <= 0 || mainOeuvre <= 0) {
                showError("Tous les champs numériques doivent être positifs.");
                return;
            }

            Etude etude = new Etude();
            etude.setDateR(dateField.getValue());
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

            etudeService.add(etude);
            showSuccess("Étude ajoutée !");
            closeWindow();

        } catch (NumberFormatException e) {
            showError("Veuillez saisir des nombres valides dans les champs numériques.");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    private void closeWindow() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
