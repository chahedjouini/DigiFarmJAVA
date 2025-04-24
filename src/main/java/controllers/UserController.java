package controllers;

import entities.User;
import enums.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.UserService;
import utils.PasswordUtils;

import java.net.URL;
import java.sql.SQLException;
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
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private ComboBox<Role> roleComboBox;
    
    @FXML
    private Label messageLabel;
    
    private final UserService userService = UserService.getInstance();
    private User selectedUser;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Initialiser la ComboBox des rôles avec tous les rôles disponibles
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(Role.CLIENT); // Valeur par défaut
        
        // Gérer la sélection d'un utilisateur dans la table
        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
                populateFields(selectedUser);
            }
        });
        
        // Charger les données initiales
        loadUserData();
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
        clearFields();
        selectedUser = null;
    }
    
    @FXML
    private void handleEdit() {
        if (selectedUser == null) {
            showError("Veuillez sélectionner un utilisateur à modifier");
            return;
        }
        
        populateFields(selectedUser);
    }
    
    @FXML
    private void handleDelete() {
        if (selectedUser == null) {
            showError("Veuillez sélectionner un utilisateur à supprimer");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'utilisateur");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userService.deleteUser(selectedUser.getId());
                showSuccess("Utilisateur supprimé avec succès");
                loadUserData();
                clearFields();
                selectedUser = null;
            } catch (Exception e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleSave() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        Role role = roleComboBox.getValue();
        
        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || role == null) {
            showError("Veuillez remplir tous les champs obligatoires");
            return;
        }
        
        // Validation de l'email
        if (!PasswordUtils.isValidEmail(email)) {
            showError("Format d'email invalide");
            return;
        }
        
        try {
            if (selectedUser == null) {
                // Création d'un nouvel utilisateur
                if (password.isEmpty()) {
                    showError("Le mot de passe est obligatoire pour un nouvel utilisateur");
                    return;
                }
                
                // Validation du mot de passe
                if (!PasswordUtils.isValidPassword(password)) {
                    showError("Le mot de passe doit contenir au moins 8 caractères et un chiffre");
                    return;
                }
                
                // Vérifier l'unicité de l'email
                if (!userService.isEmailUnique(email)) {
                    showError("Cette adresse email est déjà utilisée");
                    return;
                }
                
                // Création d'un nouvel utilisateur (le service se chargera du hashage)
                User newUser = new User();
                newUser.setNom(nom);
                newUser.setPrenom(prenom);
                newUser.setEmail(email);
                newUser.setPassword(password); // Le service se chargera du hashage
                newUser.setRole(role);
                newUser.setResetToken(null); // reset_token sera NULL
                
                userService.createUser(newUser);
                showSuccess("Utilisateur créé avec succès");
            } else {
                // Mise à jour d'un utilisateur existant
                selectedUser.setNom(nom);
                selectedUser.setPrenom(prenom);
                
                // Vérifier si l'email a changé et s'il est unique
                if (!selectedUser.getEmail().equals(email) && !userService.isEmailUnique(email)) {
                    showError("Cette adresse email est déjà utilisée");
                    return;
                }
                
                selectedUser.setEmail(email);
                
                // Modification du mot de passe si nécessaire
                if (!password.isEmpty()) {
                    if (!PasswordUtils.isValidPassword(password)) {
                        showError("Le mot de passe doit contenir au moins 8 caractères et un chiffre");
                        return;
                    }
                    selectedUser.setPassword(password); // Le service se chargera du hashage
                }
                
                selectedUser.setRole(role);
                
                userService.updateUser(selectedUser);
                showSuccess("Utilisateur mis à jour avec succès");
            }
            
            loadUserData();
            clearFields();
            selectedUser = null;
        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
        selectedUser = null;
        clearMessage();
    }
    
    private void populateFields(User user) {
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(""); // Ne pas afficher le mot de passe pour des raisons de sécurité
        roleComboBox.setValue(user.getRole());
    }
    
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        // S'assurer que la ComboBox a toujours une valeur par défaut
        if (roleComboBox.getItems().isEmpty()) {
            roleComboBox.getItems().addAll(Role.values());
        }
        roleComboBox.setValue(Role.CLIENT);
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