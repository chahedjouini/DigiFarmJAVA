package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.stage.Stage;
import utils.EmailSender;
import utils.ResetCodeManager;
import services.UserService;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;

    private static final Random RANDOM = new Random();

    public void handleResetRequest(ActionEvent event) {
        String email = emailField.getText();
        if (isEmailExist(email)) {
            String resetCode = generateRandomCode();
            // Stocker le code et l'email dans le gestionnaire
            ResetCodeManager.getInstance().setResetCode(resetCode);
            ResetCodeManager.getInstance().setUserEmail(email);

            boolean sent = EmailSender.sendPasswordResetEmail(email, resetCode);
            if (sent) {
                statusLabel.setText("Un code de réinitialisation a été envoyé à votre email.");
                navigateToEnterCodePage();
            } else {
                statusLabel.setText("Erreur lors de l'envoi de l'email. Essayez à nouveau.");
            }
        } else {
            statusLabel.setText("Cette adresse email n'est pas enregistrée.");
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Connexion - DigiFarm");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page de connexion.");
        }
    }

    private boolean isEmailExist(String email) {
        // Utiliser la méthode existante dans UserService pour vérifier si l'email existe
        return UserService.getInstance().emailExists(email);
    }

    private String generateRandomCode() {
        return String.format("%04d", RANDOM.nextInt(10000));
    }

    private void navigateToEnterCodePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EnterCodePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur de navigation vers la page de saisie du code.");
        }
    }
}