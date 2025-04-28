package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.stage.Stage;
import services.UserService;
import utils.ResetCodeManager;

import java.io.IOException;
import java.util.List;

public class ResetPasswordController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    public void handleResetPassword(ActionEvent event) {
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String userEmail = ResetCodeManager.getInstance().getUserEmail();

        // Vérification des champs vides
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // Vérification de la correspondance des mots de passe
        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        // Vérification de la validité du mot de passe
        if (!isValidPassword(newPassword)) {
            statusLabel.setText("Le mot de passe doit contenir au moins 8 caractères, incluant des lettres et des chiffres.");
            return;
        }

        // Réinitialisation du mot de passe dans la base de données
        boolean success = resetPasswordInDatabase(userEmail, newPassword);
        if (success) {
            // Effacer le code de réinitialisation après utilisation
            ResetCodeManager.getInstance().clearData();
            statusLabel.setText("Mot de passe réinitialisé avec succès.");

            // Ajouter un délai avant de rediriger
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::navigateToLoginPage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            statusLabel.setText("Erreur lors de la réinitialisation du mot de passe. Veuillez réessayer.");
        }
    }

    private boolean isValidPassword(String password) {
        // Au moins 8 caractères, incluant des lettres et des chiffres
        return password.length() >= 8 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*[0-9].*");
    }

    private boolean resetPasswordInDatabase(String email, String newPassword) {
        try {
            UserService userService = UserService.getInstance();

            // Récupérer l'utilisateur par email
            User user = null;
            List<User> allUsers = userService.getAllUsers();
            for (User u : allUsers) {
                if (u.getEmail().equals(email)) {
                    user = u;
                    break;
                }
            }

            if (user != null) {
                // Mettre à jour le mot de passe
                user.setPassword(newPassword);
                userService.updateUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void navigateToLoginPage() {
        try {
            // Charger la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et y charger la nouvelle scène
            Stage stage = (Stage) passwordField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion - DigiFarm");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page de connexion.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            // Charger la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et y charger la nouvelle scène
            Stage stage = (Stage) passwordField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion - DigiFarm");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page de connexion.");
        }
    }
}