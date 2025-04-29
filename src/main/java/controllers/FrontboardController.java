package controllers;

import entities.User;
import enums.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton; // Ajout pour le MenuButton
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.UserService;
import utils.RememberMeStore;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType; // Pour la boîte de dialogue de confirmation

public class FrontboardController implements Initializable {
    // ========== FXML Components ==========
    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label userEmailLabel;
    @FXML private StackPane contentPane;
    @FXML private Button logoutButton;

    // Changer de Button à MenuButton
    @FXML private MenuButton profileBtn;

    // Menus spécifiques aux rôles
    @FXML private Button etudeMesDemandesBtn;
    @FXML private Button etudeConsulterBtn;
    @FXML private Button animauxMesAnimauxBtn;
    @FXML private Button animauxConsulterVetoBtn;
    @FXML private Button abonnementMonAbonnementBtn;
    @FXML private Button abonnementFacturesBtn;
    @FXML private Button machineReserverBtn;
    @FXML private Button machineConsulterBtn;
    @FXML private Button stockAcheterBtn;
    @FXML private Button stockCommandesBtn;

    // ========== Service & Data ==========
    private final UserService userService = UserService.getInstance();
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation par défaut (sera mise à jour par setCurrentUser)
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue");
        }
    }

    /**
     * Définit l'utilisateur actuel et configure l'interface en fonction de son rôle
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user != null) {
            // Mettre à jour les labels d'information utilisateur
            if (welcomeLabel != null) {
                welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
            }

            if (userRoleLabel != null) {
                userRoleLabel.setText("Rôle: " + user.getRole().toString());
            }

            if (userEmailLabel != null) {
                userEmailLabel.setText("Email: " + user.getEmail());
            }

            // Configurer les menus spécifiques au rôle
            configureMenusByRole(user.getRole());
        }
    }

    /**
     * Configure l'affichage des menus en fonction du rôle de l'utilisateur
     */
    private void configureMenusByRole(Role role) {
        boolean isClient = (role == Role.CLIENT);
        boolean isAgriculteur = (role == Role.AGRICULTEUR);

        // Exemple: certains boutons peuvent être visibles uniquement pour les agriculteurs
        if (etudeMesDemandesBtn != null) etudeMesDemandesBtn.setVisible(isAgriculteur);
        if (etudeMesDemandesBtn != null) etudeMesDemandesBtn.setManaged(isAgriculteur);

        if (animauxMesAnimauxBtn != null) animauxMesAnimauxBtn.setVisible(isAgriculteur);
        if (animauxMesAnimauxBtn != null) animauxMesAnimauxBtn.setManaged(isAgriculteur);

        // Les boutons de consultation peuvent être visibles pour tous
        if (etudeConsulterBtn != null) etudeConsulterBtn.setVisible(true);
        if (animauxConsulterVetoBtn != null) animauxConsulterVetoBtn.setVisible(true);

        // Les fonctionnalités d'abonnement sont pour tous
        if (abonnementMonAbonnementBtn != null) abonnementMonAbonnementBtn.setVisible(true);
        if (abonnementFacturesBtn != null) abonnementFacturesBtn.setVisible(true);

        // Les machines peuvent être réservées par les agriculteurs
        if (machineReserverBtn != null) machineReserverBtn.setVisible(isAgriculteur);
        if (machineReserverBtn != null) machineReserverBtn.setManaged(isAgriculteur);

        // Mais consultables par tous
        if (machineConsulterBtn != null) machineConsulterBtn.setVisible(true);

        // Les produits peuvent être achetés par tous
        if (stockAcheterBtn != null) stockAcheterBtn.setVisible(true);
        if (stockCommandesBtn != null) stockCommandesBtn.setVisible(true);
    }

    // ========== Navigation & Actions ==========

    @FXML
    private void handleLogout() {
        try {
            // Détruire la session utilisateur
            userService.logout();
            currentUser = null;

            // Effacer les données "Se souvenir de moi"
            RememberMeStore.clear();

            // Obtenir la fenêtre actuelle
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();

            // Charger l'interface de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Remplacer le contenu de la fenêtre par l'interface de login
            Scene loginScene = new Scene(root);
            currentStage.setScene(loginScene);
            currentStage.setTitle("Connexion - DigiFarm");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    /**
     * Charge et affiche un contenu FXML dans le panneau principal
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Si le FXML a un contrôleur qui nécessite l'utilisateur actuel
            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).setUser(currentUser);
            }

            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de " + fxmlPath + ": " + e.getMessage());
        }
    }

    // Interface pour les contrôleurs qui ont besoin de connaître l'utilisateur actuel
    public interface UserAwareController {
        void setUser(User user);
    }

    // ========== Nouvelle méthodes pour le menu profil ==========

    @FXML
    private void handleShowProfile() {
        // Charger le profil de l'utilisateur
        loadContent("/UserProfile.fxml");
    }

    @FXML
    private void handleEditProfile() {
        // Ouvrir le formulaire d'édition du profil utilisateur
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur actuel au contrôleur du formulaire
            UserFormController controller = loader.getController();
            controller.setUser(currentUser);

            // Créer une nouvelle fenêtre pour l'édition
            Stage stage = new Stage();
            stage.setTitle("Modifier mon compte");
            stage.setScene(new Scene(root));

            // Afficher la fenêtre modale
            stage.showAndWait();

            // Mettre à jour les données utilisateur après modification
            if (currentUser != null) {
                // Rafraîchir les informations de l'utilisateur depuis la base de données
                User refreshedUser = userService.getUserById(currentUser.getId());
                if (refreshedUser != null) {
                    setCurrentUser(refreshedUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProfile() {
        // Demander confirmation avant suppression
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer votre compte");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer définitivement votre compte? Cette action est irréversible.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer le compte
                userService.deleteUser(currentUser.getId());

                // Déconnecter l'utilisateur et rediriger vers la page de connexion
                handleLogout();

                // Afficher une confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Compte supprimé");
                alert.setHeaderText(null);
                alert.setContentText("Votre compte a été supprimé avec succès.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression du compte: " + e.getMessage());
            }
        }
    }

    // ========== Handlers des boutons de menu ==========

    // Note: handleProfile est supprimé car remplacé par les méthodes ci-dessus

    @FXML
    private void handleEtudeMesDemandes() {
        loadContent("/EtudeMesDemandes.fxml");
    }

    @FXML
    private void handleEtudeConsulter() {
        loadContent("/EtudeConsulter.fxml");
    }

    @FXML
    private void handleAnimauxMesAnimaux() {
        loadContent("/AnimauxMesAnimaux.fxml");
    }

    @FXML
    private void handleAnimauxConsulterVeto() {
        loadContent("/AnimauxConsulterVeto.fxml");
    }

    @FXML
    private void handleAbonnementMon() {
        loadContent("/AbonnementMon.fxml");
    }

    @FXML
    private void handleAbonnementFactures() {
        loadContent("/AbonnementFactures.fxml");
    }

    @FXML
    private void handleMachineReserver() {
        loadContent("/MachineReserver.fxml");
    }

    @FXML
    private void handleMachineConsulter() {
        loadContent("/MachineConsulter.fxml");
    }

    @FXML
    private void handleStockAcheter() {
        loadContent("/StockAcheter.fxml");
    }

    @FXML
    private void handleStockCommandes() {
        loadContent("/StockCommandes.fxml");
    }

    // ========== Utilitaires ==========

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}