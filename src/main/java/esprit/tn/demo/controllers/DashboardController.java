package esprit.tn.demo.controllers;


import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private StackPane entityContentPane;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    // Sous-menus pour chaque section
    @FXML private VBox adminSubMenu;
    @FXML private VBox etudeSubMenu;
    @FXML private VBox animauxSubMenu;
    @FXML private VBox abonnementSubMenu;
    @FXML private VBox machineSubMenu;
    @FXML private VBox stockSubMenu;

    // Référence au bouton de menu actif et bouton d'entité actif
    private Button activeNavButton = null;
    private Button activeEntityButton = null;
    private VBox activeSubMenu = null;

//    private final UserService userService = UserService.getInstance();
//    private User currentUser;

    private final Map<String, String[]> gestionEntities = new HashMap<>() {{
        put("Gestion Admin", new String[]{"User"});
        put("Gestion Étude", new String[]{"Expert", "Culture", "Etude"});
        put("Gestion Animaux", new String[]{"Animal", "Vétérinaire", "Etat"});
        put("Gestion Abonnement", new String[]{"Facture", "Abonnement"});
        put("Gestion Machine", new String[]{"Machine", "Technicien", "Maintenance"});
        put("Gestion Stock", new String[]{"Produit", "Quantité"});
    }};

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        // S'assurer que tous les sous-menus sont cachés au démarrage
        hideAllSubMenus();

        // Préparer les sous-menus avec les entités
        initializeSubMenus();
    }

    // Initialiser tous les sous-menus avec leurs entités
    private void initializeSubMenus() {
        setupSubMenu(adminSubMenu, "Gestion Admin");
        setupSubMenu(etudeSubMenu, "Gestion Étude");
        setupSubMenu(animauxSubMenu, "Gestion Animaux");
        setupSubMenu(abonnementSubMenu, "Gestion Abonnement");
        setupSubMenu(machineSubMenu, "Gestion Machine");
        setupSubMenu(stockSubMenu, "Gestion Stock");
    }

    // Configurer un sous-menu spécifique avec ses entités
    private void setupSubMenu(VBox subMenu, String gestionName) {
        if (subMenu != null) {
            subMenu.getChildren().clear();

            for (String entityName : gestionEntities.getOrDefault(gestionName, new String[]{})) {
                Button btn = createEntityButton(entityName);
                subMenu.getChildren().add(btn);
            }
        }
    }




    @FXML
    private void showGestionEtude(ActionEvent event) {
        handleNavButtonClick("Gestion Étude", etudeSubMenu, event);
    }

    @FXML
    private void showGestionAnimaux(ActionEvent event) {
        handleNavButtonClick("Gestion Animaux", animauxSubMenu, event);
    }

    @FXML
    private void showGestionAbonnement(ActionEvent event) {
        handleNavButtonClick("Gestion Abonnement", abonnementSubMenu, event);
    }

    @FXML
    private void showGestionMachine(ActionEvent event) {
        handleNavButtonClick("Gestion Machine", machineSubMenu, event);
    }

    @FXML
    private void showGestionStock(ActionEvent event) {
        handleNavButtonClick("Gestion Stock", stockSubMenu, event);
    }

    // Gestion du clic sur les boutons de navigation
    private void handleNavButtonClick(String sectionTitle, VBox subMenu, ActionEvent event) {
        // Récupérer le bouton source
        Button clickedButton = (Button) event.getSource();

        // Mise à jour du style du bouton actif
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("active");
        }
        clickedButton.getStyleClass().add("active");
        activeNavButton = clickedButton;

        // Réinitialiser la sélection d'entité
        if (activeEntityButton != null) {
            activeEntityButton.getStyleClass().remove("active");
            activeEntityButton = null;
        }

        // Gérer l'affichage des sous-menus
        toggleSubMenu(subMenu);
    }

    // Basculer l'affichage d'un sous-menu
    private void toggleSubMenu(VBox subMenu) {
        // Si le sous-menu cliqué est déjà ouvert, on le ferme
        if (subMenu.isVisible()) {
            subMenu.setVisible(false);
            subMenu.setManaged(false);
            activeSubMenu = null;
            return;
        }

        // Cacher tous les sous-menus
        hideAllSubMenus();

        // Afficher le sous-menu cliqué
        subMenu.setVisible(true);
        subMenu.setManaged(true);
        activeSubMenu = subMenu;
    }

    // Cacher tous les sous-menus
    private void hideAllSubMenus() {
        if (adminSubMenu != null) {
            adminSubMenu.setVisible(false);
            adminSubMenu.setManaged(false);
        }
        if (etudeSubMenu != null) {
            etudeSubMenu.setVisible(false);
            etudeSubMenu.setManaged(false);
        }
        if (animauxSubMenu != null) {
            animauxSubMenu.setVisible(false);
            animauxSubMenu.setManaged(false);
        }
        if (abonnementSubMenu != null) {
            abonnementSubMenu.setVisible(false);
            abonnementSubMenu.setManaged(false);
        }
        if (machineSubMenu != null) {
            machineSubMenu.setVisible(false);
            machineSubMenu.setManaged(false);
        }
        if (stockSubMenu != null) {
            stockSubMenu.setVisible(false);
            stockSubMenu.setManaged(false);
        }
    }

    // Création d'un bouton d'entité
    private Button createEntityButton(String name) {
        Button button = new Button(name);
        button.getStyleClass().add("submenu-button");
        button.setMaxWidth(Double.MAX_VALUE);

        // Gestionnaire d'événement du clic
        button.setOnAction(event -> {
            if (activeEntityButton != null) {
                activeEntityButton.getStyleClass().remove("active");
            }
            button.getStyleClass().add("active");
            activeEntityButton = button;

            // Charger le contenu FXML pour cette entité
            String fxmlFile = "/Afficher" + name + ".fxml";
            System.out.println("FXML path = " + fxmlFile);
            System.out.println("Resolved URL = " + getClass().getResource(fxmlFile));
            loadAnimatedFXML(fxmlFile);
        });

        return button;
    }

    private void loadAnimatedFXML(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), entityContentPane);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            try {
                // DEBUG : vérifier le chemin
                URL resourceUrl = getClass().getResource(fxmlPath);
                if (resourceUrl == null) {
                    throw new IOException("FXML non trouvé à l'emplacement : " + fxmlPath);
                }

                Node newView = FXMLLoader.load(resourceUrl);
                entityContentPane.getChildren().setAll(newView);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), entityContentPane);
                fadeIn.setToValue(1.0);
                fadeIn.play();

            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement de " + fxmlPath + " : " + e.getMessage());
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

    @FXML
    private void showGestionCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/views/commande-view.fxml"));
            Parent root = loader.load();
            entityContentPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}