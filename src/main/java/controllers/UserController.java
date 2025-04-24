package controllers;

import entities.User;
import enums.Role;
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
    
    @FXML
    private TableView<User> userTableView;
    
    @FXML
    private TableColumn<User, Integer> idColumn;
    
    @FXML
    private TableColumn<User, String> nomColumn;
    
    @FXML
    private TableColumn<User, String> prenomColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, Role> roleColumn;
    
    @FXML
    private TableColumn<User, Void> actionsColumn;
    
    @FXML
    private Label messageLabel;
    
    private final UserService userService = UserService.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Configuration de la colonne des actions
        setupActionsColumn();
        
        // Charger les données initiales
        loadUserData();
    }
    
    private void setupActionsColumn() {
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
                    openUserForm(user);
                });
                
                // Action du bouton Supprimer
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    
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
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
    
    private void loadUserData() {
        try {
            List<User> users = userService.getAllUsers();
            ObservableList<User> userList = FXCollections.observableArrayList(users);
            userTableView.setItems(userList);
            clearMessage();
        } catch (Exception e) {
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadUserData();
    }
    
    @FXML
    private void handleAdd() {
        openUserForm(null); // null signifie qu'on veut ajouter un nouvel utilisateur
    }
    
    private void openUserForm(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            UserFormController controller = loader.getController();
            controller.setUser(user);
            
            // Créer la fenêtre
            Stage stage = new Stage();
            stage.setTitle(user == null ? "Ajouter un utilisateur" : "Modifier l'utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre parent
            
            // Gérer la fermeture de la fenêtre pour mettre à jour le tableau
            stage.setOnHidden(e -> loadUserData());
            
            stage.show();
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }
    
    private void clearMessage() {
        messageLabel.setText("");
    }
}