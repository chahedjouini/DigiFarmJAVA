package controllers;

import entities.User;
import enums.Role;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.UserService;
import utils.RememberMeStore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DashboardController {
    @FXML
    private VBox entityList;
    @FXML
    private StackPane entityContentPane;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutButton;
    @FXML private VBox etudeSubMenu;
    @FXML private VBox animauxSubMenu;
    @FXML private VBox abonnementSubMenu;
    @FXML private VBox machineSubMenu;
    @FXML private VBox stockSubMenu;

    private final UserService userService = UserService.getInstance();
    private User currentUser;

    private final Map<String, String[]> gestionEntities = new HashMap<>() {{
        put("Gestion Admin", new String[]{"User"});
        put("Gestion Étude", new String[]{"Expert", "Culture", "Etude"});
        put("Gestion Animaux", new String[]{"Animal", "Vétérinaire", "Etat"});
        put("Gestion Abonnement", new String[]{"Facture", "Abonnement"});
        put("Gestion Machine", new String[]{"Machine", "Technicien", "Maintenance"});
        put("Gestion Stock", new String[]{"Produit", "Quantité"});
    }};

    // Utilisation de initialize uniquement pour le cas où l'utilisateur est déjà configuré
    @FXML
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + " " + currentUser.getPrenom());
            updateEntityList("Gestion Admin");
        }
    }

    // Appelée par le contrôleur parent
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
            updateEntityList("Gestion Admin");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // 1. Détruire la session utilisateur
            userService.logout();
            currentUser = null;

            // 2. Effacer les données "Se souvenir de moi"
            RememberMeStore.clear();

            // 3. Obtenir la fenêtre actuelle
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();

            // 4. Charger l'interface de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // 5. Remplacer le contenu de la fenêtre par l'interface de login
            Scene loginScene = new Scene(root);
            currentStage.setScene(loginScene);
            currentStage.setTitle("Connexion");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    @FXML
    private void showGestionAdmin() {
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            updateEntityList("Gestion Admin");
        } else {
            showError("Accès non autorisé");
        }
    }

    @FXML
    private void showGestionEtude() {
        updateEntityList("Gestion Étude");
    }

    @FXML
    private void showGestionAnimaux() {
        updateEntityList("Gestion Animaux");
    }

    @FXML
    private void showGestionAbonnement() {
        updateEntityList("Gestion Abonnement");
    }

    @FXML
    private void showGestionMachine() {
        updateEntityList("Gestion Machine");
    }

    @FXML
    private void showGestionStock() {
        updateEntityList("Gestion Stock");
    }

    private void updateEntityList(String gestionName) {
        entityList.getChildren().clear();

        Label title = new Label("Entités de " + gestionName);
        title.getStyleClass().add("header-label");
        entityList.getChildren().add(title);

        for (String entityName : gestionEntities.getOrDefault(gestionName, new String[]{})) {
            Button btn = new Button(entityName);
            btn.getStyleClass().add("entity-button");
            btn.setPrefWidth(180);

            btn.setOnAction(e -> {
                String fxmlFile = "/Afficher" + entityName + ".fxml";
                loadAnimatedFXML(fxmlFile);
            });
            entityList.getChildren().add(btn);
        }
    }

    private void loadAnimatedFXML(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), entityContentPane);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            try {
                Node newView = FXMLLoader.load(getClass().getResource(fxmlPath));
                entityContentPane.getChildren().setAll(newView);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), entityContentPane);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fadeOut.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Méthode pour basculer l'affichage du sous-menu Étude
     */
    @FXML
    private void toggleEtudeSubMenu() {
        etudeSubMenu.setVisible(!etudeSubMenu.isVisible());
        etudeSubMenu.setManaged(!etudeSubMenu.isManaged());

        // Si vous voulez que seul un menu soit ouvert à la fois, ajoutez cette ligne
        // et répétez-la dans toutes les méthodes toggle pour fermer les autres menus
        if (etudeSubMenu.isVisible()) {
            animauxSubMenu.setVisible(false);
            animauxSubMenu.setManaged(false);
            abonnementSubMenu.setVisible(false);
            abonnementSubMenu.setManaged(false);
            machineSubMenu.setVisible(false);
            machineSubMenu.setManaged(false);
            stockSubMenu.setVisible(false);
            stockSubMenu.setManaged(false);
        }
    }

    /**
     * Méthode pour basculer l'affichage du sous-menu Animaux
     */
    @FXML
    private void toggleAnimauxSubMenu() {
        animauxSubMenu.setVisible(!animauxSubMenu.isVisible());
        animauxSubMenu.setManaged(!animauxSubMenu.isManaged());

        if (animauxSubMenu.isVisible()) {
            etudeSubMenu.setVisible(false);
            etudeSubMenu.setManaged(false);
            abonnementSubMenu.setVisible(false);
            abonnementSubMenu.setManaged(false);
            machineSubMenu.setVisible(false);
            machineSubMenu.setManaged(false);
            stockSubMenu.setVisible(false);
            stockSubMenu.setManaged(false);
        }
    }

    /**
     * Méthode pour basculer l'affichage du sous-menu Abonnement
     */
    @FXML
    private void toggleAbonnementSubMenu() {
        abonnementSubMenu.setVisible(!abonnementSubMenu.isVisible());
        abonnementSubMenu.setManaged(!abonnementSubMenu.isManaged());

        if (abonnementSubMenu.isVisible()) {
            etudeSubMenu.setVisible(false);
            etudeSubMenu.setManaged(false);
            animauxSubMenu.setVisible(false);
            animauxSubMenu.setManaged(false);
            machineSubMenu.setVisible(false);
            machineSubMenu.setManaged(false);
            stockSubMenu.setVisible(false);
            stockSubMenu.setManaged(false);
        }
    }

    /**
     * Méthode pour basculer l'affichage du sous-menu Machine
     */
    @FXML
    private void toggleMachineSubMenu() {
        machineSubMenu.setVisible(!machineSubMenu.isVisible());
        machineSubMenu.setManaged(!machineSubMenu.isManaged());

        if (machineSubMenu.isVisible()) {
            etudeSubMenu.setVisible(false);
            etudeSubMenu.setManaged(false);
            animauxSubMenu.setVisible(false);
            animauxSubMenu.setManaged(false);
            abonnementSubMenu.setVisible(false);
            abonnementSubMenu.setManaged(false);
            stockSubMenu.setVisible(false);
            stockSubMenu.setManaged(false);
        }
    }

    /**
     * Méthode pour basculer l'affichage du sous-menu Stock
     */
    @FXML
    private void toggleStockSubMenu() {
        stockSubMenu.setVisible(!stockSubMenu.isVisible());
        stockSubMenu.setManaged(!stockSubMenu.isManaged());

        if (stockSubMenu.isVisible()) {
            etudeSubMenu.setVisible(false);
            etudeSubMenu.setManaged(false);
            animauxSubMenu.setVisible(false);
            animauxSubMenu.setManaged(false);
            abonnementSubMenu.setVisible(false);
            abonnementSubMenu.setManaged(false);
            machineSubMenu.setVisible(false);
            machineSubMenu.setManaged(false);
        }
    }

}