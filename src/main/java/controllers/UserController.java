package controllers;

import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML private Label welcomeLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Label messageLabel;

    private final UserService userService = UserService.getInstance();
    private User currentUser;
    private ObservableList<User> userList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (userTable != null) {
            setupTableColumns();
        }
        loadUserData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
        }
    }

    private void setupTableColumns() {
        if (idColumn != null) idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nomColumn != null) nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        if (prenomColumn != null) prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        if (emailColumn != null) emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (roleColumn != null) roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        if (actionsColumn != null) {
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");
                private final HBox pane = new HBox(5, editButton, deleteButton);

                {
                    // Style des boutons
                    editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-size: 11px;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 11px;");

                    pane.setPadding(new Insets(5));

                    // Action du bouton Modifier
                    editButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        handleEditUser(user);
                    });

                    // Action du bouton Supprimer
                    deleteButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        handleDeleteUser(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            });
        }
    }

    private void loadUserData() {
        try {
            // Récupérer tous les utilisateurs
            List<User> users = userService.getAllUsers();
            userList = FXCollections.observableArrayList(users);

            // Vérifier si userTable existe avant de l'utiliser
            if (userTable != null) {
                userTable.setItems(userList);
                userTable.refresh();
            }

            if (messageLabel != null) {
                messageLabel.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadUserData();
    }

    @FXML
    private void handleAdd() {
        // Méthode ajoutée pour compatibilité avec AfficherUser.fxml
        handleAddUser();
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    private void handleEditUser(User user) {
        showUserDialog(user);
    }

    private void handleDeleteUser(User user) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'utilisateur");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.deleteUser(user.getId());
                showSuccess("Utilisateur supprimé avec succès");
                loadUserData();
            } catch (Exception e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogout() {
        userService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Trouver une fenêtre valide à utiliser
            Stage stage = null;
            if (welcomeLabel != null && welcomeLabel.getScene() != null) {
                stage = (Stage) welcomeLabel.getScene().getWindow();
            } else if (userTable != null && userTable.getScene() != null) {
                stage = (Stage) userTable.getScene().getWindow();
            } else if (messageLabel != null && messageLabel.getScene() != null) {
                stage = (Stage) messageLabel.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } else {
                System.err.println("Erreur: Impossible de trouver une fenêtre valide pour la redirection");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la redirection: " + e.getMessage());
        }
    }

    private void showUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(user == null ? "Ajouter un utilisateur" : "Modifier l'utilisateur");
            stage.setScene(new Scene(root));

            // Gérer la fermeture de la fenêtre pour mettre à jour le tableau
            stage.setOnHidden(e -> loadUserData());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    private void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}