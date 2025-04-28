package controllers;

import entities.User;
import enums.Role;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.UserService;
import utils.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Label errorLabel;

    private final UserService userService = UserService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser la ComboBox avec les valeurs de l'enum Role
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        // Sélectionner AGRICULTEUR par défaut
        roleComboBox.setValue(Role.AGRICULTEUR);
    }

    @FXML
    private void handleRegister() {
        // Récupération des valeurs saisies
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        Role selectedRole = roleComboBox.getValue();

        // 1. Validation des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole == null) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        // 2. Validation du format de l'email
        if (!PasswordUtils.isValidEmail(email)) {
            showError("Format d'adresse email invalide");
            return;
        }

        // 3. Validation du mot de passe (au moins 8 caractères et contenant au moins un chiffre)
        if (!PasswordUtils.isValidPassword(password)) {
            showError("Le mot de passe doit contenir au moins 8 caractères et au moins un chiffre");
            return;
        }

        // 4. Vérification de la correspondance des mots de passe
        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }

        // 5. Vérification de l'unicité de l'email
        if (!userService.isEmailUnique(email)) {
            showError("Cette adresse email est déjà utilisée");
            return;
        }

        // 6. Création de l'utilisateur avec le mot de passe non hashé (le service se chargera du hashage)
        User user = new User(nom, prenom, email, password, selectedRole);
        User createdUser = userService.createUser(user);

        if (createdUser != null) {
            try {
                // Redirection vers le dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent root = loader.load();

                // Passer l'utilisateur connecté au contrôleur du dashboard
                DashboardController dashboardController = loader.getController();
                dashboardController.setCurrentUser(createdUser);

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - Digifarm");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement de l'interface");
            }
        } else {
            showError("Erreur lors de la création du compte");
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Retour à l'écran de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Digifarm");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface de connexion");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}