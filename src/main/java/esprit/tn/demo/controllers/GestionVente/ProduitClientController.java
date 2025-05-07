package esprit.tn.demo.controllers.GestionVente;

import esprit.tn.demo.entities.GestionVente.Produit;
import esprit.tn.demo.services.GestionVente.ProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProduitClientController {

    @FXML
    private FlowPane produitFlowPane;

    @FXML
    private Label paginationLabel;

    private final ProduitService produitService = new ProduitService();
    private final int itemsPerPage = 6;
    private int currentPage = 1;

    private List<Produit> allProduits;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<Produit> produitsAffiches = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            allProduits = produitService.getAll();
            updateProduitAffichage();
        } catch (Exception e) {
            e.printStackTrace();
            produitFlowPane.getChildren().add(new Label("Erreur de chargement des produits : " + e.getMessage()));
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #fafafa;");
        card.setPrefWidth(200);

        // Checkbox
        CheckBox selectCheckBox = new CheckBox();
        checkBoxes.add(selectCheckBox);
        produitsAffiches.add(produit);

        // Image
        ImageView imageView = new ImageView();
        String imagePath = produit.getImagePath();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image("file:images/home.png"));
            }
        } else {
            imageView.setImage(new Image("file:images/home.png"));
        }
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        // Labels
        Label nomLabel = new Label(produit.getNom());
        nomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label refLabel = new Label("Référence: " + produit.getReference());

        Label descLabel = new Label(produit.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40);

        Label prixLabel = new Label(String.format("%.0f usd", produit.getPrix()));
        prixLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        card.getChildren().addAll(imageView, nomLabel, refLabel, descLabel, prixLabel, selectCheckBox);
        return card;
    }

    @FXML
    private void commander() {
        try {
            System.out.println(" Étape 1 : Tentative de chargement de CartView.fxml");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/CartView.fxml"));
            Parent root = loader.load(); // Étape 2 : chargement

            CartController cartController = loader.getController();

            // Étape 3 : récupérer les produits sélectionnés
            List<Produit> produitsSelectionnes = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    produitsSelectionnes.add(produitsAffiches.get(i));
                }
            }

            // Étape 4 : injecter les produits dans le panier
            cartController.setProduits(produitsSelectionnes);

            // Étape 5 : afficher la vue du panier
            Stage stage = new Stage();
            stage.setTitle("Panier");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de CartView.fxml");
            e.printStackTrace();
        }
    }


    private void updateProduitAffichage() {
        produitFlowPane.getChildren().clear();
        checkBoxes.clear();
        produitsAffiches.clear();

        if (allProduits == null || allProduits.isEmpty()) {
            paginationLabel.setText("Aucun produit trouvé");
            return;
        }

        int total = allProduits.size();
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, total);

        List<Produit> pageProduits = allProduits.subList(fromIndex, toIndex);
        for (Produit produit : pageProduits) {
            produitFlowPane.getChildren().add(createProductCard(produit));
        }

        paginationLabel.setText(String.format("Affichage %d–%d sur %d produits", fromIndex + 1, toIndex, total));
    }

    @FXML
    private void pageSuivante() {
        if (currentPage * itemsPerPage < allProduits.size()) {
            currentPage++;
            updateProduitAffichage();
        }
    }

    @FXML
    private void pagePrecedente() {
        if (currentPage > 1) {
            currentPage--;
            updateProduitAffichage();
        }
    }

    @FXML
    private void ouvrirCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/ListCommande.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de l'interface des commandes.").showAndWait();
        }
    }

}