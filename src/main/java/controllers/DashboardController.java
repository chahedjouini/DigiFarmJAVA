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
import services.IUserService;
import services.impl.UserService;

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

    private final IUserService userService = new UserService();
    private User currentUser;

    private final Map<String, String[]> gestionEntities = new HashMap<>() {{
        put("Gestion Admin", new String[]{"User"});
        put("Gestion Étude", new String[]{"Expert", "Culture", "Etude"});
        put("Gestion Animaux", new String[]{"Animal", "Vétérinaire", "Etat"});
        put("Gestion Abonnement", new String[]{"Facture", "Abonnement"});
        put("Gestion Machine", new String[]{"Machine", "Technicien", "Maintenance"});
        put("Gestion Stock", new String[]{"Produit", "Quantité"});
    }};

    @FXML
    public void initialize() {
        // Ne pas tenter de rediriger avant que la scène soit configurée
        if (currentUser == null) {
            // On ne fait rien ici, car le setCurrentUser sera appelé après l'initialisation du contrôleur
        } else {
            welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + " " + currentUser.getPrenom());
            updateEntityList("Gestion Admin");
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
            // Une fois l'utilisateur défini, on peut mettre à jour la liste des entités
            updateEntityList("Gestion Admin");
        }
    }

    private void redirectToLogin() {
        try {
            // Vérifier que la scène est disponible avant de rediriger
            if (entityList.getScene() != null && entityList.getScene().getWindow() != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) entityList.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        userService.logout();
        redirectToLogin();
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
}
