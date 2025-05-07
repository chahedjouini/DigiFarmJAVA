package utils;

import java.time.LocalDate;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;

public class ValidationUtils {
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s-]{2,50}$");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d*\\.?\\d+$");

    // Expert validation
    public static String validateExpertForm(String nom, String prenom, String tel, String email, String zone, String specialite) {
        if (nom.isEmpty() || prenom.isEmpty() || tel.isEmpty() || email.isEmpty() || zone.isEmpty() || specialite.isEmpty()) {
            return "Tous les champs sont obligatoires";
        }

        if (!isValidName(nom)) {
            return "Format du nom invalide";
        }

        if (!isValidName(prenom)) {
            return "Format du prénom invalide";
        }

        if (!tel.matches("\\d{8}")) {
            return "Le numéro de téléphone doit contenir exactement 8 chiffres";
        }

        if (!isValidEmail(email)) {
            return "Format d'email invalide";
        }

        if (zone.length() > 255) {
            return "La zone ne doit pas dépasser 255 caractères";
        }

        if (specialite.length() > 255) {
            return "La spécialité ne doit pas dépasser 255 caractères";
        }

        return null; // No validation errors
    }

    // Culture validation
    public static String validateCultureForm(String nom, String typeCulture, String region,
                                           Double surface, Double densitePlantation,
                                           Double besoinsEau, String besoinsEngrais,
                                           Double rendementMoyen, Double coutMoyen,
                                           LocalDate datePlantation, LocalDate dateRecolte) {
        StringBuilder errors = new StringBuilder();

        if (!NAME_PATTERN.matcher(nom).matches()) {
            errors.append("Le nom doit contenir entre 2 et 50 caractères alphabétiques\n");
        }
        if (typeCulture == null || typeCulture.trim().isEmpty()) {
            errors.append("Le type de culture est obligatoire\n");
        }
        if (region == null || region.trim().isEmpty()) {
            errors.append("La région est obligatoire\n");
        }
        if (surface == null || surface <= 0) {
            errors.append("La surface doit être supérieure à 0\n");
        }
        if (densitePlantation == null || densitePlantation <= 0) {
            errors.append("La densité de plantation doit être supérieure à 0\n");
        }
        if (besoinsEau == null || besoinsEau < 0) {
            errors.append("Les besoins en eau doivent être positifs ou nuls\n");
        }
        if (besoinsEngrais == null || besoinsEngrais.trim().isEmpty()) {
            errors.append("Les besoins en engrais sont obligatoires\n");
        }
        if (rendementMoyen == null || rendementMoyen < 0) {
            errors.append("Le rendement moyen doit être positif ou nul\n");
        }
        if (coutMoyen == null || coutMoyen < 0) {
            errors.append("Le coût moyen doit être positif ou nul\n");
        }
        if (datePlantation == null) {
            errors.append("La date de plantation est obligatoire\n");
        }
        if (dateRecolte == null) {
            errors.append("La date de récolte est obligatoire\n");
        }
        if (datePlantation != null && dateRecolte != null && !dateRecolte.isAfter(datePlantation)) {
            errors.append("La date de récolte doit être postérieure à la date de plantation\n");
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

    // Etude validation
    public static String validateEtudeForm(LocalDate dateR, Double prix, Double rendement,
                                         Double mainOeuvre, Double precipitations,
                                         String climat, String typeSol) {
        StringBuilder errors = new StringBuilder();

        if (dateR == null) {
            errors.append("La date est obligatoire\n");
        }
        if (dateR != null && dateR.isAfter(LocalDate.now())) {
            errors.append("La date ne peut pas être dans le futur\n");
        }
        if (prix == null || prix < 0) {
            errors.append("Le prix doit être positif ou nul\n");
        }
        if (rendement == null || rendement < 0) {
            errors.append("Le rendement doit être positif ou nul\n");
        }
        if (mainOeuvre == null || mainOeuvre < 0) {
            errors.append("Le coût de main d'œuvre doit être positif ou nul\n");
        }
        if (precipitations == null || precipitations < 0) {
            errors.append("Les précipitations doivent être positives ou nulles\n");
        }
        if (climat == null || climat.trim().isEmpty()) {
            errors.append("Le climat est obligatoire\n");
        }
        if (typeSol == null || typeSol.trim().isEmpty()) {
            errors.append("Le type de sol est obligatoire\n");
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

    // Utility methods
    public static boolean isValidDouble(String value) {
        return value != null && NUMERIC_PATTERN.matcher(value).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.matches("^[\\p{L}\\s'-]+$") && name.length() <= 255;
    }

    public static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 