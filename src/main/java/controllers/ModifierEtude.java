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

import java.time.LocalDate;
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
            List<Culture> cultures = cultureService.select();
            List<Expert> experts = expertService.select();

            cultureCombo.getItems().addAll(cultures);
            expertCombo.getItems().addAll(experts);
            climatCombo.getItems().addAll(Climat.values());
            typeSolCombo.getItems().addAll(TypeSol.values());

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
            messageLabel.setText("Erreur de chargement : " + e.getMessage());
        }
    }
    @FXML
    private void onModifier() {
        try {
            // Validate fields before proceeding
            if (datePicker.getValue() == null) {
                messageLabel.setText("La date de réalisation est obligatoire.");
                return;
            }

            // Validate numeric fields and convert
            try {
                etude.setPrix(Float.parseFloat(prixField.getText()));
                etude.setRendement(Float.parseFloat(rendementField.getText()));
                etude.setPrecipitations(Float.parseFloat(precipitationsField.getText()));
                etude.setMainOeuvre(Float.parseFloat(mainOeuvreField.getText()));
            } catch (NumberFormatException e) {
                messageLabel.setText("Veuillez entrer des valeurs numériques valides.");
                return;
            }

            // Set values from ComboBoxes and CheckBoxes
            etude.setDateR(datePicker.getValue());
            etude.setClimat(climatCombo.getValue());
            etude.setTypeSol(typeSolCombo.getValue());
            etude.setIrrigation(irrigationCheck.isSelected());
            etude.setFertilisation(fertilisationCheck.isSelected());

            // Update the Etude in the database
            etudeService.update(etude);


        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

}
