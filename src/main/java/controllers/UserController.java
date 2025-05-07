package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import utils.EmailSender;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    // ========== FXML Components ==========
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
    @FXML private Label adminStatsLabel;
    @FXML private Label clientStatsLabel;
    @FXML private Label agriculteurStatsLabel;
    @FXML private Label totalStatsLabel;
    @FXML private TextField searchField;

    // ========== Service & Data ==========
    private final UserService userService = UserService.getInstance();
    private User currentUser;
    private ObservableList<User> userList;
    private FilteredList<User> filteredUserList;

    // ========== Initialization Methods ==========
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (userTable != null) {
            setupTableColumns();
        }

        if (roleFilterComboBox != null) {
            setupRoleFilter();
        }

        // Initialiser le champ de recherche avec un écouteur de texte vide pour éviter les erreurs
        if (searchField != null) {
            searchField.setText("");
        }

        loadUserData();
    }

    private void setupRoleFilter() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "Tous", "ADMIN", "CLIENT", "AGRICULTEUR"
        );
        roleFilterComboBox.setItems(filterOptions);
        roleFilterComboBox.setValue("Tous");
    }

    private void setupTableColumns() {
        // Setup column value factories
        if (idColumn != null) idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nomColumn != null) nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        if (prenomColumn != null) prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        if (emailColumn != null) emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (roleColumn != null) roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Enable column sorting
        if (idColumn != null) idColumn.setSortable(true);
        if (nomColumn != null) nomColumn.setSortable(true);
        if (prenomColumn != null) prenomColumn.setSortable(true);
        if (emailColumn != null) emailColumn.setSortable(true);
        if (roleColumn != null) roleColumn.setSortable(true);

        // Setup actions column with edit and delete buttons
        if (actionsColumn != null) {
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");
                private final HBox pane = new HBox(5, editButton, deleteButton);

                {
                    // Style buttons
                    editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-font-size: 11px;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 11px;");

                    pane.setPadding(new Insets(5));

                    // Set button actions
                    editButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        handleEditUser(user);
                    });

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

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
        }
    }

    // ========== Data Loading & Filtering ==========
    private void loadUserData() {
        try {
            // Get all users
            List<User> users = userService.getAllUsers();
            userList = FXCollections.observableArrayList(users);

            // Create filtered list
            filteredUserList = new FilteredList<>(userList, p -> true);

            // Create sorted list from filtered list
            SortedList<User> sortedData = new SortedList<>(filteredUserList);

            if (userTable != null) {
                // Bind sorted list comparator to table comparator
                sortedData.comparatorProperty().bind(userTable.comparatorProperty());
                userTable.setItems(sortedData);
                userTable.refresh();
            }

            if (messageLabel != null) {
                messageLabel.setText("");
            }

            // Apply current filter if selected
            if (roleFilterComboBox != null && roleFilterComboBox.getValue() != null) {
                handleRoleFilter();
            }

            // Update role statistics
            updateRoleStats();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    private void handleRoleFilter() {
        if (filteredUserList == null) return;

        String selectedFilter = roleFilterComboBox.getValue();
        String searchText = searchField != null ? searchField.getText().toLowerCase().trim() : "";

        filteredUserList.setPredicate(user -> {
            // Vérifie d'abord le filtre de rôle
            boolean matchesRole = (selectedFilter == null || selectedFilter.equals("Tous")) ||
                    (user.getRole() == Role.valueOf(selectedFilter));

            // Si pas de texte de recherche, on applique seulement le filtre de rôle
            if (searchText.isEmpty()) {
                return matchesRole;
            }

            // Sinon, on vérifie à la fois le rôle et la correspondance au texte
            if (!matchesRole) {
                return false;
            }

            String nom = user.getNom() != null ? user.getNom().toLowerCase() : "";
            String prenom = user.getPrenom() != null ? user.getPrenom().toLowerCase() : "";
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";

            return nom.contains(searchText) ||
                    prenom.contains(searchText) ||
                    email.contains(searchText);
        });
    }

    private void updateRoleStats() {
        if (userList == null || userList.isEmpty()) return;

        // Calculate user count by role
        long adminCount = userList.stream().filter(user -> user.getRole() == Role.ADMIN).count();
        long clientCount = userList.stream().filter(user -> user.getRole() == Role.CLIENT).count();
        long agriculteurCount = userList.stream().filter(user -> user.getRole() == Role.AGRICULTEUR).count();

        // Update statistics labels
        if (adminStatsLabel != null) {
            adminStatsLabel.setText("ADMIN: " + adminCount);
        }
        if (clientStatsLabel != null) {
            clientStatsLabel.setText("CLIENT: " + clientCount);
        }
        if (agriculteurStatsLabel != null) {
            agriculteurStatsLabel.setText("AGRICULTEUR: " + agriculteurCount);
        }

        // Update total count
        if (totalStatsLabel != null) {
            totalStatsLabel.setText("Total: " + userList.size());
        }
    }

    // ========== User Management Operations ==========
    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    @FXML
    private void handleAdd() {
        // Method added for compatibility with AfficherUser.fxml
        handleAddUser();
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

            // Update table when window closes
            stage.setOnHidden(e -> loadUserData());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    // ========== UI Action Handlers ==========
    @FXML
    private void handleRefresh() {
        loadUserData();
    }

    @FXML
    private void handleSortById() {
        if (userTable != null && idColumn != null) {
            userTable.getSortOrder().clear();
            userTable.getSortOrder().add(idColumn);
            // Toggle between ASC and DESC if already sorted by this column
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
            // Toggle between ASC and DESC if already sorted by this column
            if (nomColumn.getSortType() == TableColumn.SortType.ASCENDING) {
                nomColumn.setSortType(TableColumn.SortType.DESCENDING);
            } else {
                nomColumn.setSortType(TableColumn.SortType.ASCENDING);
            }
        }
    }

    @FXML
    private void handleLogout() {
        userService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Find a valid window to use
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

    // ========== Email Functionality ==========
    @FXML
    public void handleSendGreetings() {
        // Get currently filtered users
        ObservableList<User> usersToSend = userTable.getItems();

        // Check if there are users to send to
        if (usersToSend.isEmpty()) {
            showError("Aucun utilisateur à qui envoyer les voeux !");
            return;
        }

        // Ask for confirmation before mass sending
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
            // Create a thread for sending emails (to avoid blocking the UI)
            new Thread(() -> {
                int successCount = 0;
                int errorCount = 0;

                // Disable the send button during processing
                javafx.application.Platform.runLater(() -> {
                    if (messageLabel != null) {
                        messageLabel.setText("Envoi des emails en cours...");
                        messageLabel.setStyle("-fx-text-fill: blue;");
                    }
                    if (sendGreetingsButton != null) {
                        sendGreetingsButton.setDisable(true);
                    }
                });

                // Send to each user
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

                    // Update progress message every 5 emails
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

                    // Small pause to avoid overloading the SMTP server
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Display final result
                final int finalSuccess = successCount;
                final int finalError = errorCount;

                javafx.application.Platform.runLater(() -> {
                    if (finalError == 0) {
                        showSuccess("Tous les messages ont été envoyés avec succès ! (" + finalSuccess + " emails)");
                    } else {
                        showError("Terminé avec " + finalSuccess + " emails envoyés et " + finalError + " erreurs.");
                    }

                    // Reactivate the button
                    if (sendGreetingsButton != null) {
                        sendGreetingsButton.setDisable(false);
                    }
                });
            }).start();
        }
    }

    // ========== PDF Export Functionality ==========
    @FXML
    public void handleExportPdf() {
        // Get currently filtered users
        ObservableList<User> usersToExport = userTable.getItems();

        // Create document
        Document document = new Document();

        try {
            // Set custom page size (A4)
            document.setPageSize(PageSize.A4);
            document.setMargins(36, 72, 108, 180); // Margins: left, right, top, bottom

            // Path to user's Downloads folder
            String home = System.getProperty("user.home");

            // Add filter to filename if active
            String fileName = "Liste des utilisateurs";
            if (roleFilterComboBox != null && !roleFilterComboBox.getValue().equals("Tous")) {
                fileName += " - " + roleFilterComboBox.getValue();
            }
            fileName += ".pdf";

            String downloadPath = Paths.get(home, "Downloads", fileName).toString();

            // Create writer that writes the document to a file
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(downloadPath));
            document.open(); // Open document for writing

            // Add title with color (green)
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(34, 139, 34)); // Dark Green
            Paragraph title = new Paragraph("Liste des utilisateurs de DigiFarm", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add filter information if active
            if (roleFilterComboBox != null && !roleFilterComboBox.getValue().equals("Tous")) {
                Font filterFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, new BaseColor(0, 0, 139)); // Dark Blue
                Paragraph filterInfo = new Paragraph("Filtre appliqué: " + roleFilterComboBox.getValue(), filterFont);
                filterInfo.setAlignment(Element.ALIGN_CENTER);
                document.add(filterInfo);
            }

            // Add statistics
            Font statsFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(0, 102, 204)); // Blue

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

            // Add space
            document.add(new Chunk("\n"));

            // Create table with background color (sky blue)
            PdfPTable table = new PdfPTable(5); // 5 columns: ID, Name, First Name, Email, Role
            table.setWidthPercentage(100); // Fill page width

            // Set background color for column headers (sky blue)
            BaseColor skyBlue = new BaseColor(135, 206, 250); // Sky blue

            // Add column headers with background color (sky blue)
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

            // Add filtered user data
            for (User user : usersToExport) {
                // Add data to table
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getNom());
                table.addCell(user.getPrenom());
                table.addCell(user.getEmail());
                table.addCell(user.getRole().toString());
            }

            // Add table to document
            document.add(table);

            // Close document
            document.close();

            // Show success message
            showSuccess("PDF exporté avec succès dans votre dossier Téléchargements !");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'exportation du PDF : " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        if (filteredUserList == null) return;

        String searchText = searchField.getText().toLowerCase().trim();

        // Si le champ de recherche est vide, on applique uniquement le filtre par rôle
        if (searchText.isEmpty()) {
            handleRoleFilter(); // Réapplique uniquement le filtre par rôle
            return;
        }

        // Récupère le filtre de rôle actuel
        String selectedFilter = roleFilterComboBox.getValue();
        Role selectedRole = (selectedFilter != null && !selectedFilter.equals("Tous"))
                ? Role.valueOf(selectedFilter)
                : null;

        // Applique à la fois le filtre de rôle et le filtre de recherche
        filteredUserList.setPredicate(user -> {
            // Vérifie d'abord si l'utilisateur correspond au filtre de rôle
            boolean matchesRole = (selectedRole == null) || (user.getRole() == selectedRole);

            // Si pas de correspondance de rôle, on rejette directement
            if (!matchesRole) {
                return false;
            }

            // Sinon on vérifie la correspondance avec le texte de recherche
            String nom = user.getNom() != null ? user.getNom().toLowerCase() : "";
            String prenom = user.getPrenom() != null ? user.getPrenom().toLowerCase() : "";
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";

            // Cherche dans le nom, prénom ou email
            return nom.contains(searchText) ||
                    prenom.contains(searchText) ||
                    email.contains(searchText);
        });
    }

    // ========== UI Feedback Methods ==========
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