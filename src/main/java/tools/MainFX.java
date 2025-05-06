package tools;

import controllers.DashboardController;
import entities.User;
import enums.Role;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.UserService;
import utils.RememberMeStore;

import java.util.Optional;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Vérifier si un utilisateur est mémorisé
        Optional<RememberMeStore.RememberedUser> rememberedUser = RememberMeStore.load();

        if (rememberedUser.isPresent()) {
            // Vérifier que l'utilisateur existe toujours en base
            UserService userService = UserService.getInstance();
            User user = userService.getUserById(rememberedUser.get().getId());

            if (user != null) {
                // L'utilisateur existe, on le connecte automatiquement
                showDashboard(primaryStage, user);
                return; // On saute l'écran de login
            } else {
                // L'utilisateur n'existe plus, on supprime les données mémorisées
                RememberMeStore.clear();
            }
        }

        // Pas d'utilisateur mémorisé ou utilisateur invalide, on affiche l'écran de login
        showLogin(primaryStage);
    }

    private void showLogin(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Connexion - Digifarm");
        stage.setScene(scene);
        stage.show();
    }

    private void showDashboard(Stage stage, User user) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
        Parent root = loader.load();

        // Passer l'utilisateur connecté au contrôleur du dashboard
        DashboardController dashboardController = loader.getController();
        dashboardController.setCurrentUser(user);

        Scene scene = new Scene(root);
        stage.setTitle("Dashboard - Digifarm");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}