package controllers.GestionMachine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.GestionMachine.Technicien;
import services.GestionMachine.TechnicienService;
import javafx.util.converter.FloatStringConverter;

import java.io.IOException;
import java.util.regex.Pattern;

public class AjoutTechnicien {

    // Regex patterns and constants
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z]*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern SPECIALITE_LOCALISATION_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-]{0,100}$");
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_SPECIALITE_LENGTH = 100;
    private static final int MAX_LOCALISATION_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final float MIN_LATITUDE = -90.0f;
    private static final float MAX_LATITUDE = 90.0f;
    private static final float MIN_LONGITUDE = -180.0f;
    private static final float MAX_LONGITUDE = 180.0f;

    @FXML private TextField nameField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField localisationField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private Button annulerButton;
    @FXML private Button ajouterButton;

    private final TechnicienService technicienService = new TechnicienService();

    @FXML
    public void initialize() {
        // Configure text field validations
        configureTextField(nameField, MAX_NAME_LENGTH, NAME_PATTERN, true);
        configureTextField(prenomField, MAX_NAME_LENGTH, NAME_PATTERN, true);
        configureTextField(specialiteField, MAX_SPECIALITE_LENGTH, SPECIALITE_LOCALISATION_PATTERN, true);
        configureTextField(localisationField, MAX_LOCALISATION_LENGTH, SPECIALITE_LOCALISATION_PATTERN, false);
        configureEmailField(emailField);
        configurePhoneField(telephoneField);
        configureFloatField(latitudeField, MIN_LATITUDE, MAX_LATITUDE, false);
        configureFloatField(longitudeField, MIN_LONGITUDE, MAX_LONGITUDE, false);

        // Add focus listener for email validation
        emailField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused && emailField.getText() != null) {
                String email = emailField.getText().trim();
                boolean valid = email.isEmpty() || EMAIL_PATTERN.matcher(email).matches();
                emailField.setStyle(valid ? "-fx-border-color: none;" : "-fx-border-color: red;");
            } else {
                emailField.setStyle("-fx-border-color: none;");
            }
        });

        // Add focus listener for telephone validation
        telephoneField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused && telephoneField.getText() != null) {
                String phone = telephoneField.getText().trim();
                boolean valid = phone.isEmpty() || PHONE_PATTERN.matcher(phone).matches();
                telephoneField.setStyle(valid ? "-fx-border-color: none;" : "-fx-border-color: red;");
            } else {
                telephoneField.setStyle("-fx-border-color: none;");
            }
        });

        // Add real-time visual feedback for other fields
        addRealTimeValidationFeedback(nameField, NAME_PATTERN);
        addRealTimeValidationFeedback(prenomField, NAME_PATTERN);
        addRealTimeValidationFeedback(specialiteField, SPECIALITE_LOCALISATION_PATTERN);
        addRealTimeValidationFeedback(localisationField, SPECIALITE_LOCALISATION_PATTERN);
        // Skip real-time validation for emailField and telephoneField
        addRealTimeFloatValidationFeedback(latitudeField, MIN_LATITUDE, MAX_LATITUDE);
        addRealTimeFloatValidationFeedback(longitudeField, MIN_LONGITUDE, MAX_LONGITUDE);

        // Set prompt text for better UX
        emailField.setPromptText("exemple@domaine.com");
        telephoneField.setPromptText("12345678");
    }

    private void configureTextField(TextField textField, int maxLength, Pattern pattern, boolean required) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change == null || change.getControlNewText() == null) return null;
            String newText = change.getControlNewText().trim();
            if (newText.isEmpty() && !required) return change;
            if (newText.length() > maxLength) return null;
            if (!pattern.matcher(newText).matches() && !newText.isEmpty()) return null;
            // Auto-capitalize first letter for name and prenom
            if ((textField == nameField || textField == prenomField) && change.getCaretPosition() == 1 &&
                    !newText.isEmpty() && Character.isLowerCase(newText.charAt(0))) {
                change.setText(change.getText().toUpperCase());
            }
            return change;
        }));
    }

    private void configureEmailField(TextField emailField) {
        emailField.setTextFormatter(new TextFormatter<>(change -> {
            if (change == null || change.getControlNewText() == null) {
                System.out.println("Email TextFormatter: Null change detected");
                return null;
            }
            String newText = change.getControlNewText().trim();
            System.out.println("Email input: " + newText); // Debug
            if (newText.length() > MAX_EMAIL_LENGTH) {
                System.out.println("Email rejected: Too long");
                return null;
            }
            if (!newText.matches("^[A-Za-z0-9@._+-]*$") && !newText.isEmpty()) {
                System.out.println("Email rejected: Invalid characters");
                return null;
            }
            return change;
        }));
    }

    private void configurePhoneField(TextField telephoneField) {
        telephoneField.setTextFormatter(new TextFormatter<>(change -> {
            if (change == null || change.getControlNewText() == null) {
                System.out.println("Phone TextFormatter: Null change detected");
                return null;
            }
            String newText = change.getControlNewText().trim();
            System.out.println("Phone input: " + newText); // Debug
            if (newText.length() > 8) {
                System.out.println("Phone rejected: Too long");
                return null;
            }
            if (!newText.matches("^\\d*$") && !newText.isEmpty()) {
                System.out.println("Phone rejected: Non-digits");
                return null;
            }
            return change;
        }));
    }

    private void configureFloatField(TextField field, float minValue, float maxValue, boolean required) {
        field.setTextFormatter(new TextFormatter<>(new FloatStringConverter(), null, change -> {
            if (change == null || change.getControlNewText() == null) return null;
            String newText = change.getControlNewText().trim();
            if (newText.isEmpty() && !required) return change;
            try {
                float value = Float.parseFloat(newText);
                if (value < minValue || value > maxValue) return null;
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    private void addRealTimeValidationFeedback(TextField field, Pattern pattern) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            String trimmed = newValue.trim();
            boolean valid = trimmed.isEmpty() || pattern.matcher(trimmed).matches();
            field.setStyle(valid ? "-fx-border-color: none;" : "-fx-border-color: red;");
        });
    }

    private void addRealTimePhoneValidationFeedback(TextField field) {
        // No longer used
    }

    private void addRealTimeFloatValidationFeedback(TextField field, float minValue, float maxValue) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            String trimmed = newValue.trim();
            boolean valid;
            if (trimmed.isEmpty()) {
                valid = true;
            } else {
                try {
                    float value = Float.parseFloat(trimmed);
                    valid = value >= minValue && value <= maxValue;
                } catch (NumberFormatException e) {
                    valid = false;
                }
            }
            field.setStyle(valid ? "-fx-border-color: none;" : "-fx-border-color: red;");
        });
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        try {
            Technicien technicien = createTechnicienFromInput();
            technicienService.add(technicien);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Technicien ajouté avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        // Name validation
        if (!validateTextField(nameField, "Nom", MAX_NAME_LENGTH, NAME_PATTERN, true)) {
            return false;
        }

        // Prenom validation
        if (!validateTextField(prenomField, "Prénom", MAX_NAME_LENGTH, NAME_PATTERN, true)) {
            return false;
        }

        // Specialite validation
        if (!validateTextField(specialiteField, "Spécialité", MAX_SPECIALITE_LENGTH, SPECIALITE_LOCALISATION_PATTERN, true)) {
            return false;
        }

        // Email validation
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        if (email.isEmpty()) {
            showAlert("Erreur", "L'email est obligatoire !");
            emailField.requestFocus();
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert("Erreur", "L'email doit être au format valide (ex: exemple@domaine.com) !");
            emailField.requestFocus();
            return false;
        }
        if (email.length() > MAX_EMAIL_LENGTH) {
            showAlert("Erreur", "L'email ne doit pas dépasser " + MAX_EMAIL_LENGTH + " caractères !");
            emailField.requestFocus();
            return false;
        }

        // Telephone validation
        String telephone = telephoneField.getText() != null ? telephoneField.getText().trim() : "";
        if (telephone.isEmpty()) {
            showAlert("Erreur", "Le téléphone est obligatoire !");
            telephoneField.requestFocus();
            return false;
        }
        if (!PHONE_PATTERN.matcher(telephone).matches()) {
            showAlert("Erreur", "Le téléphone doit être un numéro à 8 chiffres !");
            telephoneField.requestFocus();
            return false;
        }

        // Localisation validation (optional)
        if (!localisationField.getText().trim().isEmpty() &&
                !validateTextField(localisationField, "Localisation", MAX_LOCALISATION_LENGTH, SPECIALITE_LOCALISATION_PATTERN, false)) {
            return false;
        }

        // Latitude validation (optional)
        if (!latitudeField.getText().trim().isEmpty()) {
            try {
                float latitude = Float.parseFloat(latitudeField.getText().trim());
                if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
                    showAlert("Erreur", "La latitude doit être entre " + MIN_LATITUDE + " et " + MAX_LATITUDE + " !");
                    latitudeField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La latitude doit être un nombre valide !");
                latitudeField.requestFocus();
                return false;
            }
        }

        // Longitude validation (optional)
        if (!longitudeField.getText().trim().isEmpty()) {
            try {
                float longitude = Float.parseFloat(longitudeField.getText().trim());
                if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
                    showAlert("Erreur", "La longitude doit être entre " + MIN_LONGITUDE + " et " + MAX_LONGITUDE + " !");
                    longitudeField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La longitude doit être un nombre valide !");
                longitudeField.requestFocus();
                return false;
            }
        }

        // Check for duplicate email (optional, implement if needed)
        // if (technicienService.emailExists(email)) {
        //     showAlert("Erreur", "Cet email est déjà utilisé !");
        //     emailField.requestFocus();
        //     return false;
        // }

        return true;
    }

    private boolean validateTextField(TextField field, String fieldName, int maxLength, Pattern pattern, boolean required) {
        String value = field.getText() != null ? field.getText().trim() : "";
        if (value.isEmpty()) {
            if (required) {
                showAlert("Erreur", fieldName + " est obligatoire !");
                field.requestFocus();
                return false;
            }
            return true;
        }
        if (!pattern.matcher(value).matches()) {
            showAlert("Erreur", fieldName + " doit contenir uniquement des lettres" +
                    (field == specialiteField || field == localisationField ? ", chiffres, espaces ou tirets" : "") + " !");
            field.requestFocus();
            return false;
        }
        if (value.length() > maxLength) {
            showAlert("Erreur", fieldName + " ne doit pas dépasser " + maxLength + " caractères !");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private Technicien createTechnicienFromInput() {
        Technicien technicien = new Technicien();
        technicien.setName(nameField.getText().trim());
        technicien.setPrenom(prenomField.getText().trim());
        technicien.setSpecialite(specialiteField.getText().trim());
        technicien.setEmail(emailField.getText().trim());
        technicien.setTelephone(Integer.parseInt(telephoneField.getText().trim()));
        String localisation = localisationField.getText().trim();
        if (!localisation.isEmpty()) {
            technicien.setLocalisation(localisation);
        }
        String latitude = latitudeField.getText().trim();
        if (!latitude.isEmpty()) {
            technicien.setLatitude(Float.parseFloat(latitude));
        }
        String longitude = longitudeField.getText().trim();
        if (!longitude.isEmpty()) {
            technicien.setLongitude(Float.parseFloat(longitude));
        }
        return technicien;
    }

    @FXML
    private void handleAnnulerButton(ActionEvent event) {
        try {
            // Load using the correct path (relative to resources folder)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/viewMachine.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) annulerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Échec du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void clearFields() {
        nameField.clear();
        prenomField.clear();
        specialiteField.clear();
        emailField.clear();
        telephoneField.clear();
        localisationField.clear();
        latitudeField.clear();
        longitudeField.clear();
        resetFieldStyles();
    }

    private void resetFieldStyles() {
        nameField.setStyle("-fx-border-color: none;");
        prenomField.setStyle("-fx-border-color: none;");
        specialiteField.setStyle("-fx-border-color: none;");
        emailField.setStyle("-fx-border-color: none;");
        telephoneField.setStyle("-fx-border-color: none;");
        localisationField.setStyle("-fx-border-color: none;");
        latitudeField.setStyle("-fx-border-color: none;");
        longitudeField.setStyle("-fx-border-color: none;");
    }
}