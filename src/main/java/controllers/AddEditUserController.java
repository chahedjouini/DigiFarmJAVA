package controllers;

import entities.User;
import enums.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;

import java.util.regex.Pattern;

public class AddEditUserController {
    @FXML
    private Label titleLabel;
    
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
    private Label errorLabel;
    
    private final UserService userService = UserService.getInstance();
    private User user;
    
    @FXML
    public void initialize() {
        ObservableList<Role> roles = FXCollections.observableArrayList(Role.values());
        roleComboBox.setItems(roles);
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            titleLabel.setText("Modifier un utilisateur");
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            roleComboBox.setValue(user.getRole());
        } else {
            titleLabel.setText("Ajouter un utilisateur");
        }
    }
    
    @FXML
    private void handleSave() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        Role role = roleComboBox.getValue();
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Format d'email invalide");
            return;
        }
        
        if (user == null && !userService.isEmailUnique(email)) {
            showError("Cet email est déjà utilisé");
            return;
        }
        
        if (user == null) {
            user = new User(nom, prenom, email, password, role);
            userService.createUser(user);
        } else {
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            userService.updateUser(user);
        }
        
        closeWindow();
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
} 