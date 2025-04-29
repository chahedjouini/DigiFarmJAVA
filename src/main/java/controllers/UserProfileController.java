package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import services.UserService;
import utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class UserProfileController implements FrontboardController.UserAwareController {
    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label dateMemberLabel;
    @FXML private Button changePasswordBtn;
    @FXML private Label messageLabel;

    private final UserService userService = UserService.getInstance();
    private User currentUser;

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            nomLabel.setText(currentUser.getNom());
            prenomLabel.setText(currentUser.getPrenom());
            emailLabel.setText(currentUser.getEmail());
            roleLabel.setText(currentUser.getRole().toString());

            // Comme nous n'avons pas la date d'inscription, utilisons la date actuelle
            // Vous pourriez ajouter un champ dateCreation à votre entité User si nécessaire
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateMemberLabel.setText(dateFormat.format(new Date()));
        }
    }

    @FXML
    private void handleChangePassword() {
        // Création d'une boîte de dialogue pour l'ancien mot de passe
        TextInputDialog oldPasswordDialog = new TextInputDialog();
        oldPasswordDialog.setTitle("Changer de mot de passe");
        oldPasswordDialog.setHeaderText("Veuillez entrer votre mot de passe actuel");
        oldPasswordDialog.setContentText("Mot de passe actuel:");

        // Récupérer l'ancien mot de passe
        Optional<String> oldPasswordResult = oldPasswordDialog.showAndWait();
        if (oldPasswordResult.isPresent()) {
            String oldPassword = oldPasswordResult.get();

            // Vérifier que l'ancien mot de passe est correct
            if (!PasswordUtils.verifyPassword(oldPassword, currentUser.getPassword())) {
                showError("Mot de passe incorrect.");
                return;
            }

            // Création d'une boîte de dialogue pour le nouveau mot de passe
            TextInputDialog newPasswordDialog = new TextInputDialog();
            newPasswordDialog.setTitle("Changer de mot de passe");
            newPasswordDialog.setHeaderText("Entrez votre nouveau mot de passe");
            newPasswordDialog.setContentText("Nouveau mot de passe:");

            // Récupérer le nouveau mot de passe
            Optional<String> newPasswordResult = newPasswordDialog.showAndWait();
            if (newPasswordResult.isPresent()) {
                String newPassword = newPasswordResult.get();

                // Vérifier que le nouveau mot de passe est valide
                if (!PasswordUtils.isValidPassword(newPassword)) {
                    showError("Le mot de passe doit contenir au moins 8 caractères et un chiffre.");
                    return;
                }

                // Confirmation du nouveau mot de passe
                TextInputDialog confirmDialog = new TextInputDialog();
                confirmDialog.setTitle("Changer de mot de passe");
                confirmDialog.setHeaderText("Confirmez votre nouveau mot de passe");
                confirmDialog.setContentText("Confirmation:");

                Optional<String> confirmResult = confirmDialog.showAndWait();
                if (confirmResult.isPresent()) {
                    String confirmPassword = confirmResult.get();

                    // Vérifier que les mots de passe correspondent
                    if (!newPassword.equals(confirmPassword)) {
                        showError("Les mots de passe ne correspondent pas.");
                        return;
                    }

                    // Mettre à jour le mot de passe
                    currentUser.setPassword(newPassword);
                    try {
                        userService.updateUser(currentUser);
                        showSuccess("Mot de passe modifié avec succès!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Erreur lors de la modification du mot de passe: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}