package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.stage.Stage;
import utils.ResetCodeManager;

import java.io.IOException;

public class EnterCodeController {

    @FXML
    private TextField resetCodeField;
    @FXML
    private Label statusLabel;

    @FXML
    private void handleValidateCodeButton() {
        String enteredCode = resetCodeField.getText();
        // Vérifier si le code est valide
        if (isValidCode(enteredCode)) {
            // Rediriger l'utilisateur vers la page de modification du mot de passe
            navigateToResetPasswordPage();
        } else {
            statusLabel.setText("Code incorrect ou expiré. Essayez à nouveau.");
        }
    }

    private boolean isValidCode(String code) {
        // Récupérer le code stocké dans le gestionnaire et vérifier sa validité
        String storedCode = ResetCodeManager.getInstance().getResetCode();
        return code.equals(storedCode) && ResetCodeManager.getInstance().isCodeValid();
    }

    private void navigateToResetPasswordPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resetCodeField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page de réinitialisation du mot de passe.");
        }
    }

    @FXML
    private void handleBackToForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resetCodeField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page précédente.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resetCodeField.getScene().getWindow();
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