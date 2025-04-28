package controllers;

import entities.User;
import enums.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import services.IUserService;
import services.impl.UserService;
import utils.PasswordUtils;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button signUpButton;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private VBox loginForm;
    
    @FXML
    private VBox registerForm;
    
    @FXML
    private TextField registerNom;
    
    @FXML
    private TextField registerPrenom;
    
    @FXML
    private TextField registerEmail;
    
    @FXML
    private PasswordField registerPassword;
    
    @FXML
    private PasswordField confirmPassword;
    
    @FXML
    private ComboBox<Role> roleComboBox;
    
    @FXML
    private Label registerErrorLabel;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button backToLoginButton;
    
    private final IUserService userService = new UserService();
    
    @FXML
    public void initialize() {
        // Cacher le formulaire d'inscription au démarrage
        if (registerForm != null) {
            registerForm.setVisible(false);
            registerForm.setManaged(false);
        }
        
        // Initialiser la ComboBox des rôles avec seulement AGRICULTEUR et CLIENT
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll(Role.AGRICULTEUR, Role.CLIENT);
            roleComboBox.setValue(Role.CLIENT); // Valeur par défaut
        }
    }
    
    @FXML
    private void showRegisterForm() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        registerForm.setVisible(true);
        registerForm.setManaged(true);
        registerErrorLabel.setText("");
    }
    
    @FXML
    private void showLoginForm() {
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        errorLabel.setText("");
    }
    
    @FXML
    private void handleRegister() {
        // Vider les messages d'erreur précédents
        registerErrorLabel.setText("");
        
        // Récupérer les valeurs du formulaire
        String nom = registerNom.getText().trim();
        String prenom = registerPrenom.getText().trim();
        String email = registerEmail.getText().trim();
        String password = registerPassword.getText();
        String confirm = confirmPassword.getText();
        Role role = roleComboBox.getValue();
        
        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            registerErrorLabel.setText("Tous les champs sont obligatoires");
            return;
        }
        
        if (!password.equals(confirm)) {
            registerErrorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (!PasswordUtils.isValidEmail(email)) {
            registerErrorLabel.setText("Format d'email invalide");
            return;
        }
        
        // Vérifier si l'email existe déjà
        if (!userService.isEmailUnique(email)) {
            registerErrorLabel.setText("Cette adresse email est déjà utilisée");
            return;
        }
        
        // Créer un nouvel utilisateur
        User newUser = new User();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email);
        newUser.setPassword(password); // Le service se chargera du hashage
        newUser.setRole(role);
        newUser.setResetToken(null); // S'assurer que reset_token est NULL
        
        // Appel au service pour créer l'utilisateur
        User createdUser = userService.createUser(newUser);
        
        if (createdUser != null) {
            // Retourner au formulaire de connexion après inscription réussie
            showLoginForm();
            // Afficher un message de succès
            errorLabel.setText("Inscription réussie ! Veuillez vous connecter.");
            errorLabel.setTextFill(Paint.valueOf("#008000")); // Vert pour indiquer le succès
        } else {
            registerErrorLabel.setText("Erreur lors de l'inscription");
        }
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validation des champs vides
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        // Validation du format de l'email
        if (!PasswordUtils.isValidEmail(email)) {
            showError("Format d'adresse email invalide");
            return;
        }
        
        // Tentative de connexion avec le mot de passe non haché
        // Le service s'occupera de récupérer le sel et de hasher le mot de passe
        User user = userService.login(email, password);
        
        if (user != null) {
            try {
                // Charger le dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
                Parent root = loader.load();
                
                // Passer l'utilisateur connecté au contrôleur du dashboard
                DashboardController dashboardController = loader.getController();
                dashboardController.setCurrentUser(user);
                
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - Digifarm");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur lors du chargement de l'interface");
            }
        } else {
            showError("Email ou mot de passe incorrect");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 