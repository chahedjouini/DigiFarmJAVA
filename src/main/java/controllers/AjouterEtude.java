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
            experts.removeIf(expert -> expert.getDispo().toString().equals("non disponible"));
            expertCombo.getItems().setAll(experts);

            List<Culture> cultures = cultureService.select();
            cultureCombo.getItems().setAll(cultures);
        } catch (SQLException e) {
            messageLabel.setText("Erreur de chargement : " + e.getMessage());
        }
    }

    @FXML
    private void onAjout() {
        try {
            Etude etude = new Etude();
            etude.setDateR(dateField.getValue());
            etude.setCulture(cultureCombo.getValue());
            etude.setExpert(expertCombo.getValue());
            etude.setClimat(climatCombo.getValue());
            etude.setTypeSol(typeSolCombo.getValue());
            etude.setIrrigation(irrigationCheck.isSelected());
            etude.setFertilisation(fertilisationCheck.isSelected());
            etude.setPrix(Float.parseFloat(prixField.getText()));
            etude.setRendement(Float.parseFloat(rendementField.getText()));
            etude.setPrecipitations(Float.parseFloat(precipitationField.getText()));
            etude.setMainOeuvre(Float.parseFloat(mainField.getText()));


            etudeService.add(etude);
            expertService.markAsUnavailable(etude.getExpert().getId());

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Étude ajoutée !");
            closeWindow();
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
