package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
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

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;


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
    @FXML
    public void handleExportPdf() {
        // Création du document
        Document document = new Document();

        try {
            // Choisir une taille de page personnalisée (A4)
            document.setPageSize(PageSize.A4);
            document.setMargins(36, 72, 108, 180); // Marges : gauche, droite, haut, bas

            // Chemin vers le dossier Téléchargements de l'utilisateur
            String home = System.getProperty("user.home");
            String downloadPath = Paths.get(home, "Downloads", "Liste des utilisateurs.pdf").toString();

            // Création d'un writer qui écrit le document dans un fichier
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(downloadPath));
            document.open(); // Ouvre le document pour écrire

            // Ajouter un titre avec couleur (vert)
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(34, 139, 34)); // Vert (DarkGreen)
            Paragraph title = new Paragraph("Liste des utilisateurs de DigiFarm", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ajouter un espace
            document.add(new Chunk("\n"));

            // Création du tableau avec des couleurs de fond (bleu ciel)
            PdfPTable table = new PdfPTable(5); // 5 colonnes : ID, Nom, Prénom, Email, Rôle
            table.setWidthPercentage(100); // Remplir la largeur de la page

            // Définir une couleur d'arrière-plan pour les en-têtes de colonnes (bleu ciel)
            BaseColor skyBlue = new BaseColor(135, 206, 250); // Bleu ciel

            // Ajouter les en-têtes de colonnes avec une couleur de fond (bleu ciel)
            PdfPCell cell = new PdfPCell(new Phrase("ID"));
            cell.setBackgroundColor(skyBlue);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Nom"));
            cell.setBackgroundColor(skyBlue);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Prénom"));
            cell.setBackgroundColor(skyBlue);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Email"));
            cell.setBackgroundColor(skyBlue);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Rôle"));
            cell.setBackgroundColor(skyBlue);
            table.addCell(cell);

            // Ajouter les données des utilisateurs (exemple avec un tableau de données fictif)
            for (User user : userTable.getItems()) { // userTable est ton TableView
                // Ajouter les données dans le tableau
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getNom());
                table.addCell(user.getPrenom());
                table.addCell(user.getEmail());
                table.addCell(user.getRole().toString());
            }

            // Ajouter le tableau au document
            document.add(table);

            // Fermer le document
            document.close();

            // Afficher un message de succès
            showSuccess("PDF exporté avec succès dans votre dossier Téléchargements !");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'exportation du PDF : " + e.getMessage());
        }
    }







}