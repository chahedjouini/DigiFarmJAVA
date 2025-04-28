package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import entities.User;
import enums.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import utils.EmailSender;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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
    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private Button sendGreetingsButton;

    // Labels pour les statistiques
    @FXML private Label adminStatsLabel;
    @FXML private Label clientStatsLabel;
    @FXML private Label agriculteurStatsLabel;
    @FXML private Label totalStatsLabel;

    private final UserService userService = UserService.getInstance();
    private User currentUser;
    private ObservableList<User> userList;
    private FilteredList<User> filteredUserList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (userTable != null) {
            setupTableColumns();
        }

        if (roleFilterComboBox != null) {
            setupRoleFilter();
        }

        loadUserData();
    }

    private void setupRoleFilter() {
        // Options pour le filtre (Tous + les différents rôles)
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "Tous", "ADMIN", "CLIENT", "AGRICULTEUR"
        );
        roleFilterComboBox.setItems(filterOptions);
        roleFilterComboBox.setValue("Tous"); // Valeur par défaut
    }

    @FXML
    private void handleRoleFilter() {
        if (filteredUserList == null) return;

        String selectedFilter = roleFilterComboBox.getValue();

        if (selectedFilter == null || selectedFilter.equals("Tous")) {
            // Pas de filtre, montrer tous les utilisateurs
            filteredUserList.setPredicate(user -> true);
        } else {
            // Filtrer par le rôle sélectionné
            Role selectedRole = Role.valueOf(selectedFilter);
            filteredUserList.setPredicate(user -> user.getRole() == selectedRole);
        }
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

        // Activer le tri sur les colonnes
        if (idColumn != null) idColumn.setSortable(true);
        if (nomColumn != null) nomColumn.setSortable(true);
        if (prenomColumn != null) prenomColumn.setSortable(true);
        if (emailColumn != null) emailColumn.setSortable(true);
        if (roleColumn != null) roleColumn.setSortable(true);

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

            // Créer une liste filtrée
            filteredUserList = new FilteredList<>(userList, p -> true);

            // Utiliser une SortedList qui enveloppe la FilteredList
            SortedList<User> sortedData = new SortedList<>(filteredUserList);

            // Vérifier si userTable existe avant de l'utiliser
            if (userTable != null) {
                // Lier le comparateur de la SortedList au comparateur de la TableView
                sortedData.comparatorProperty().bind(userTable.comparatorProperty());

                // Utiliser la SortedList comme items de la TableView
                userTable.setItems(sortedData);

                userTable.refresh();
            }

            if (messageLabel != null) {
                messageLabel.setText("");
            }

            // Appliquer le filtre actuel (si déjà sélectionné)
            if (roleFilterComboBox != null && roleFilterComboBox.getValue() != null) {
                handleRoleFilter();
            }

            // Mettre à jour les statistiques par rôle
            updateRoleStats();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    // Méthode pour calculer les statistiques par rôle
    private void updateRoleStats() {
        if (userList == null || userList.isEmpty()) return;

        // Calculer le nombre d'utilisateurs par rôle
        long adminCount = userList.stream().filter(user -> user.getRole() == Role.ADMIN).count();
        long clientCount = userList.stream().filter(user -> user.getRole() == Role.CLIENT).count();
        long agriculteurCount = userList.stream().filter(user -> user.getRole() == Role.AGRICULTEUR).count();

        // Mettre à jour les labels de statistiques dans l'interface
        if (adminStatsLabel != null) {
            adminStatsLabel.setText("ADMIN: " + adminCount);
        }
        if (clientStatsLabel != null) {
            clientStatsLabel.setText("CLIENT: " + clientCount);
        }
        if (agriculteurStatsLabel != null) {
            agriculteurStatsLabel.setText("AGRICULTEUR: " + agriculteurCount);
        }

        // Calculer le total
        if (totalStatsLabel != null) {
            totalStatsLabel.setText("Total: " + userList.size());
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
    private void handleSortById() {
        if (userTable != null && idColumn != null) {
            userTable.getSortOrder().clear();
            userTable.getSortOrder().add(idColumn);
            // Alterner entre ASC et DESC si déjà trié par cette colonne
            if (idColumn.getSortType() == TableColumn.SortType.ASCENDING) {
                idColumn.setSortType(TableColumn.SortType.DESCENDING);
            } else {
                idColumn.setSortType(TableColumn.SortType.ASCENDING);
            }
        }
    }

    @FXML
    private void handleSortByName() {
        if (userTable != null && nomColumn != null) {
            userTable.getSortOrder().clear();
            userTable.getSortOrder().add(nomColumn);
            // Alterner entre ASC et DESC si déjà trié par cette colonne
            if (nomColumn.getSortType() == TableColumn.SortType.ASCENDING) {
                nomColumn.setSortType(TableColumn.SortType.DESCENDING);
            } else {
                nomColumn.setSortType(TableColumn.SortType.ASCENDING);
            }
        }
    }

    @FXML
    public void handleSendGreetings() {
        // Récupérer les utilisateurs filtrés actuellement affichés
        ObservableList<User> usersToSend = userTable.getItems();

        // Vérifier s'il y a des utilisateurs à qui envoyer
        if (usersToSend.isEmpty()) {
            showError("Aucun utilisateur à qui envoyer les voeux !");
            return;
        }

        // Demander confirmation avant l'envoi massif
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation d'envoi");
        confirmDialog.setHeaderText("Envoyer les voeux Aid Idhha Mubarak");

        String message = "Vous êtes sur le point d'envoyer un message de voeux à ";
        if (roleFilterComboBox != null && !roleFilterComboBox.getValue().equals("Tous")) {
            message += "tous les " + roleFilterComboBox.getValue() + "S (" + usersToSend.size() + " utilisateurs).";
        } else {
            message += "tous les utilisateurs (" + usersToSend.size() + " au total).";
        }

        confirmDialog.setContentText(message + "\nVoulez-vous continuer ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Créer un thread pour l'envoi des emails (pour ne pas bloquer l'interface)
            new Thread(() -> {
                int successCount = 0;
                int errorCount = 0;

                // Désactiver le bouton d'envoi pendant le traitement
                javafx.application.Platform.runLater(() -> {
                    if (messageLabel != null) {
                        messageLabel.setText("Envoi des emails en cours...");
                        messageLabel.setStyle("-fx-text-fill: blue;");
                    }
                    if (sendGreetingsButton != null) {
                        sendGreetingsButton.setDisable(true);
                    }
                });

                // Envoyer à chaque utilisateur
                for (User user : usersToSend) {
                    boolean success = EmailSender.sendGreetingsEmail(
                            user.getEmail(),
                            user.getNom(),
                            user.getPrenom()
                    );

                    if (success) {
                        successCount++;
                    } else {
                        errorCount++;
                    }

                    // Mettre à jour le message de progression tous les 5 emails
                    if ((successCount + errorCount) % 5 == 0 || (successCount + errorCount) == usersToSend.size()) {
                        final int currentSuccess = successCount;
                        final int currentError = errorCount;
                        javafx.application.Platform.runLater(() -> {
                            if (messageLabel != null) {
                                messageLabel.setText("Envoi en cours... " + (currentSuccess + currentError) +
                                        "/" + usersToSend.size() + " emails envoyés.");
                            }
                        });
                    }

                    // Petite pause pour ne pas surcharger le serveur SMTP
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Afficher le résultat final
                final int finalSuccess = successCount;
                final int finalError = errorCount;

                javafx.application.Platform.runLater(() -> {
                    if (finalError == 0) {
                        showSuccess("Tous les messages ont été envoyés avec succès ! (" + finalSuccess + " emails)");
                    } else {
                        showError("Terminé avec " + finalSuccess + " emails envoyés et " + finalError + " erreurs.");
                    }

                    // Réactiver le bouton
                    if (sendGreetingsButton != null) {
                        sendGreetingsButton.setDisable(false);
                    }
                });
            }).start();
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
        // Récupérer les utilisateurs filtrés actuellement affichés
        ObservableList<User> usersToExport = userTable.getItems();

        // Création du document
        Document document = new Document();

        try {
            // Choisir une taille de page personnalisée (A4)
            document.setPageSize(PageSize.A4);
            document.setMargins(36, 72, 108, 180); // Marges : gauche, droite, haut, bas

            // Chemin vers le dossier Téléchargements de l'utilisateur
            String home = System.getProperty("user.home");

            // Ajouter le filtre au nom du fichier s'il est activé
            String fileName = "Liste des utilisateurs";
            if (roleFilterComboBox != null && !roleFilterComboBox.getValue().equals("Tous")) {
                fileName += " - " + roleFilterComboBox.getValue();
            }
            fileName += ".pdf";

            String downloadPath = Paths.get(home, "Downloads", fileName).toString();

            // Création d'un writer qui écrit le document dans un fichier
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(downloadPath));
            document.open(); // Ouvre le document pour écrire

            // Ajouter un titre avec couleur (vert)
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(34, 139, 34)); // Vert (DarkGreen)
            Paragraph title = new Paragraph("Liste des utilisateurs de DigiFarm", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ajouter une information sur le filtre si activé
            if (roleFilterComboBox != null && !roleFilterComboBox.getValue().equals("Tous")) {
                Font filterFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, new BaseColor(0, 0, 139)); // Bleu foncé
                Paragraph filterInfo = new Paragraph("Filtre appliqué: " + roleFilterComboBox.getValue(), filterFont);
                filterInfo.setAlignment(Element.ALIGN_CENTER);
                document.add(filterInfo);
            }

            // Ajouter les statistiques
            Font statsFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(0, 102, 204)); // Bleu

            if (userList != null && !userList.isEmpty()) {
                long adminCount = userList.stream().filter(user -> user.getRole() == Role.ADMIN).count();
                long clientCount = userList.stream().filter(user -> user.getRole() == Role.CLIENT).count();
                long agriculteurCount = userList.stream().filter(user -> user.getRole() == Role.AGRICULTEUR).count();

                Paragraph statsInfo = new Paragraph();
                statsInfo.add(new Chunk("Statistiques: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                statsInfo.add(new Chunk("ADMIN: " + adminCount + " | ", statsFont));
                statsInfo.add(new Chunk("CLIENT: " + clientCount + " | ", statsFont));
                statsInfo.add(new Chunk("AGRICULTEUR: " + agriculteurCount + " | ", statsFont));
                statsInfo.add(new Chunk("Total: " + userList.size(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

                statsInfo.setAlignment(Element.ALIGN_CENTER);
                document.add(statsInfo);
            }

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

            // Ajouter les données des utilisateurs filtrés
            for (User user : usersToExport) {
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