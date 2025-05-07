package controllers.GestionMachine;

import entities.GestionMachine.Machine;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.GestionMachine.MachineService;
import controllers.FrontboardController;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherMachineF implements Initializable, FrontboardController.UserAwareController {

    @FXML private GridPane machineGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label outputLabel;

    private final MachineService machineService = new MachineService();
    private ObservableList<Machine> machineList;
    private Machine selectedMachine;
    private User currentUser;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSortComboBox();
        setupSearch();
    }

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        loadMachines();
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll(
                "Nom (A-Z)",
                "Nom (Z-A)",
                "Date d'achat (récent)",
                "Date d'achat (ancien)",
                "État (A-Z)",
                "État prédit (A-Z)"
        );
        sortComboBox.setValue("Nom (A-Z)");
        sortComboBox.setOnAction(e -> sortMachines());
    }

    private void sortMachines() {
        if (machineList == null) return;

        switch (sortComboBox.getValue()) {
            case "Nom (A-Z)":
                machineList.sort(Comparator.comparing(Machine::getNom, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Nom (Z-A)":
                machineList.sort((m1, m2) -> String.CASE_INSENSITIVE_ORDER.compare(m2.getNom(), m1.getNom()));
                break;
            case "Date d'achat (récent)":
                machineList.sort(Comparator.comparing(Machine::getDate_achat).reversed());
                break;
            case "Date d'achat (ancien)":
                machineList.sort(Comparator.comparing(Machine::getDate_achat));
                break;
            case "État (A-Z)":
                machineList.sort(Comparator.comparing(Machine::getEtat, String.CASE_INSENSITIVE_ORDER));
                break;
            case "État prédit (A-Z)":
                machineList.sort(Comparator.comparing(Machine::getEtat_pred, String.CASE_INSENSITIVE_ORDER));
                break;
        }

        filterMachines(searchField.getText());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterMachines(newValue);
        });
    }

    private void filterMachines(String searchText) {
        machineGrid.getChildren().clear();
        int row = 0;
        int col = 0;

        for (Machine machine : machineList) {
            if (matchesSearch(machine, searchText)) {
                VBox card = createMachineCard(machine);
                machineGrid.add(card, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    private boolean matchesSearch(Machine machine, String searchText) {
        if (searchText == null || searchText.isEmpty()) return true;
        searchText = searchText.toLowerCase();

        return machine.getNom().toLowerCase().contains(searchText) ||
                machine.getType().toLowerCase().contains(searchText) ||
                machine.getEtat().toLowerCase().contains(searchText) ||
                dateFormat.format(machine.getDate_achat()).contains(searchText);
    }

    private VBox createMachineCard(Machine machine) {
        VBox card = new VBox(10);
        card.getStyleClass().add("machine-card");

        Label title = new Label(machine.getNom());
        title.getStyleClass().add("card-title");

        VBox content = new VBox(5);
        content.getStyleClass().add("card-content");

        addCardField(content, "Type", machine.getType());
        addCardField(content, "Date achat", dateFormat.format(machine.getDate_achat()));
        addCardField(content, "État", machine.getEtat());
        addCardField(content, "État prédit", machine.getEtat_pred());
        addCardField(content, "Propriétaire", String.valueOf(machine.getOwner_id()));

        card.getChildren().addAll(title, content);

        card.setOnMouseClicked(e -> {
            selectedMachine = machine;
            updateCardSelection(card);
        });

        return card;
    }

    private void addCardField(VBox container, String label, String value) {
        HBox field = new HBox(5);
        Label labelNode = new Label(label + ":");
        labelNode.getStyleClass().add("field-label");
        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("field-value");
        field.getChildren().addAll(labelNode, valueNode);
        container.getChildren().add(field);
    }

    private void updateCardSelection(VBox selectedCard) {
        machineGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-card"));
        selectedCard.getStyleClass().add("selected-card");
    }

    private void loadMachines() {
        try {
            List<Machine> list;

            if (currentUser != null) {
                if (currentUser.getRole() == Role.ADMIN) {
                    list = machineService.getAll(); // admin voit tout
                } else {
                    list = machineService.getMachinesByOwner(currentUser.getId());
                }
            } else {
                list = machineService.getAll(); // admin par défaut
            }

            machineList = FXCollections.observableArrayList(list);
            filterMachines("");
        } catch (Exception e) {
            showAlert("Erreur", "Chargement échoué : " + e.getMessage());
        }
    }

    @FXML
    private void onAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/AjoutMachine.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter Machine");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadMachines();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'ajout.");
        }
    }

    @FXML
    private void onModifier() {
        if (selectedMachine == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une machine.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/modify-machine.fxml"));
            Parent root = loader.load();

            ModifyMachine controller = loader.getController();
            controller.setMachineData(selectedMachine);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Machine");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadMachines());

            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Chargement de la modification impossible.");
        }
    }

    @FXML
    private void onSupprimer() {
        if (selectedMachine == null) {
            showAlert("Sélection requise", "Veuillez sélectionner une machine.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la machine");
        alert.setContentText("Voulez-vous vraiment supprimer cette machine ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                machineService.delete(selectedMachine);
                loadMachines();
            } catch (Exception e) {
                showAlert("Erreur", "Suppression échouée : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onRefresh() {
        loadMachines();
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}