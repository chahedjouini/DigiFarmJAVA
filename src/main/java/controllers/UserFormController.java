package controllers;

import entities.User;
import enums.Role;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import utils.PasswordUtils;

public class UserFormController {
    @FXML private Label titleLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final UserService userService = UserService.getInstance();
    private User user;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Initialiser la ComboBox avec tous les rôles disponibles
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        roleComboBox.setValue(Role.CLIENT); // Valeur par défaut
    }

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            // Mode édition
            isEditMode = true;
            titleLabel.setText("Modifier l'utilisateur");

            // Remplir les champs avec les données de l'utilisateur
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            passwordField.setText(""); // Ne pas afficher le mot de passe pour des raisons de sécurité
            roleComboBox.setValue(user.getRole());
        } else {
            // Mode ajout
            isEditMode = false;
            titleLabel.setText("Ajouter un utilisateur");
            clearFields();
        }
    }

    @FXML
    private void handleSave() {
        // Récupérer les valeurs des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        Role role = roleComboBox.getValue();

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || role == null) {
            showError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        // Validation de l'email
        if (!PasswordUtils.isValidEmail(email)) {
            showError("Format d'email invalide");
            return;
        }

        try {
            if (!isEditMode) {
                // Mode ajout
                if (password.isEmpty()) {
                    showError("Le mot de passe est obligatoire pour un nouvel utilisateur");
                    return;
                }

                // Validation du mot de passe
                if (!PasswordUtils.isValidPassword(password)) {
                    showError("Le mot de passe doit contenir au moins 8 caractères et un chiffre");
                    return;
                }

                // Vérifier l'unicité de l'email
                if (!userService.isEmailUnique(email)) {
                    showError("Cette adresse email est déjà utilisée");
                    return;
                }

                // Créer un nouvel utilisateur
                User newUser = new User();
                newUser.setNom(nom);
                newUser.setPrenom(prenom);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setRole(role);
                newUser.setResetToken(null);

                userService.createUser(newUser);
            } else {
                // Mode édition
                user.setNom(nom);
                user.setPrenom(prenom);

                // Vérifier si l'email a changé et s'il est unique
                if (!user.getEmail().equals(email)) {
                    if (!userService.isEmailUnique(email)) {
                        showError("Cette adresse email est déjà utilisée");
                        return;
                    }
                    user.setEmail(email);
                }

                // Modification du mot de passe si nécessaire
                if (!password.isEmpty()) {
                    if (!PasswordUtils.isValidPassword(password)) {
                        showError("Le mot de passe doit contenir au moins 8 caractères et un chiffre");
                        return;
                    }
                    user.setPassword(password);
                }

                user.setRole(role);

                userService.updateUser(user);
            }

            // Fermer la fenêtre
            closeWindow();
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(Role.CLIENT);
        errorLabel.setText("");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}