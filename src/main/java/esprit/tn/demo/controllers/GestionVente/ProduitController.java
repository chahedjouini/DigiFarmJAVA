package esprit.tn.demo.controllers.GestionVente;

import esprit.tn.demo.entities.GestionVente.Produit;
import esprit.tn.demo.services.GestionVente.ProduitService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class ProduitController {

    @FXML private TextField typeField, referenceField, nomField, descriptionField, prixField, stockField, imageField;
    @FXML private Button saveButton, updateButton, deleteButton;

    @FXML private TableView<Produit> produitTable;
    @FXML private TableColumn<Produit, String> nomColumn;
    @FXML private TableColumn<Produit, String> typeColumn;
    @FXML private TableColumn<Produit, String> referenceColumn;
    @FXML private TableColumn<Produit, Float> prixColumn;
    @FXML private TableColumn<Produit, Integer> stockColumn;

    @FXML private Label typeErrorLabel, referenceErrorLabel, nomErrorLabel,
            descriptionErrorLabel, prixErrorLabel, stockErrorLabel;

    private ObservableList<Produit> produitsList;
    private ProduitService produitService;

    @FXML
    public void initialize() {
        produitService = new ProduitService();

        // Configuration des colonnes du TableView
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        referenceColumn.setCellValueFactory(cellData -> cellData.getValue().referenceProperty());
        prixColumn.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());

        loadProduits();

        // Sélection d'un élément → remplissage des champs
        produitTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void resetErrorLabels() {
        typeErrorLabel.setVisible(false);
        referenceErrorLabel.setVisible(false);
        nomErrorLabel.setVisible(false);
        descriptionErrorLabel.setVisible(false);
        prixErrorLabel.setVisible(false);
        stockErrorLabel.setVisible(false);
    }

    public boolean validateFields() {
        resetErrorLabels();
        boolean isValid = true;

        if (typeField.getText().trim().isEmpty()) {
            typeErrorLabel.setText("Le type de produit est requis.");
            typeErrorLabel.setVisible(true);
            isValid = false;
        }
        if (referenceField.getText().trim().isEmpty()) {
            referenceErrorLabel.setText("La référence est obligatoire.");
            referenceErrorLabel.setVisible(true);
            isValid = false;
        }
        if (nomField.getText().trim().isEmpty()) {
            nomErrorLabel.setText("Le nom du produit est requis.");
            nomErrorLabel.setVisible(true);
            isValid = false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            descriptionErrorLabel.setText("La description est requise.");
            descriptionErrorLabel.setVisible(true);
            isValid = false;
        }
        if (prixField.getText().trim().isEmpty()) {
            prixErrorLabel.setText("Le prix unitaire est obligatoire.");
            prixErrorLabel.setVisible(true);
            isValid = false;
        } else if (!prixField.getText().matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            prixErrorLabel.setText("Le prix doit être un nombre positif (ex: 12.34).");
            prixErrorLabel.setVisible(true);
            isValid = false;
        }

        if (stockField.getText().trim().isEmpty()) {
            stockErrorLabel.setText("Le stock est obligatoire.");
            stockErrorLabel.setVisible(true);
            isValid = false;
        } else if (!stockField.getText().matches("^[0-9]+$")) {
            stockErrorLabel.setText("Le stock doit être un entier positif.");
            stockErrorLabel.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void handleAddProduit() {
        if (!validateFields()) return;

        try {
            String type = typeField.getText().trim();
            String reference = referenceField.getText().trim();
            String nom = nomField.getText().trim();
            String description = descriptionField.getText().trim();
            float prix = Float.parseFloat(prixField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            String image = imageField.getText().trim();

            if (prix <= 0 || stock < 0) {
                showError("Le prix doit être > 0 et le stock ≥ 0.");
                return;
            }

            Produit produit = new Produit(type, reference, nom, description, prix, stock);
            produitService.add(produit);
            loadProduits();
            clearFields();
            showSuccess("Produit ajouté avec succès !");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void updateProduit() {
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showError("Veuillez sélectionner un produit.");
            return;
        }
        if (!validateFields()) return;

        try {
            selectedProduit.setType(typeField.getText().trim());
            selectedProduit.setReference(referenceField.getText().trim());
            selectedProduit.setNom(nomField.getText().trim());
            selectedProduit.setDescription(descriptionField.getText().trim());
            selectedProduit.setPrix(Float.parseFloat(prixField.getText().trim()));
            selectedProduit.setStock(Integer.parseInt(stockField.getText().trim()));

            produitService.update(selectedProduit);
            loadProduits();
            clearFields();
            showSuccess("Produit mis à jour !");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void deleteProduit() {
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showError("Veuillez sélectionner un produit.");
            return;
        }

        try {
            produitService.delete(selectedProduit);
            loadProduits();
            clearFields();
            showSuccess("Produit supprimé !");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    private void loadProduits() {
        try {
            List<Produit> produits = produitService.getAll();
            produitsList = FXCollections.observableArrayList(produits);
            produitTable.setItems(produitsList);
        } catch (Exception e) {
            showError("Chargement échoué : " + e.getMessage());
        }
    }

    public void populateFields(Produit produit) {
        typeField.setText(produit.getType());
        referenceField.setText(produit.getReference());
        nomField.setText(produit.getNom());
        descriptionField.setText(produit.getDescription());
        prixField.setText(String.valueOf(produit.getPrix()));
        stockField.setText(String.valueOf(produit.getStock()));
    }

    private void clearFields() {
        typeField.clear();
        referenceField.clear();
        nomField.clear();
        descriptionField.clear();
        prixField.clear();
        stockField.clear();
        imageField.clear();
        produitTable.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    private void showSuccess(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    @FXML
    private void handleOpenProduitClient() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/esprit/tn/demo/views/produit-client.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Produit Client");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            showError("Erreur ouverture interface client : " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenCommande() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/esprit/tn/demo/views/commande-view.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Liste des Commandes");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            showError("Erreur ouverture commandes : " + e.getMessage());
        }
    }
}
