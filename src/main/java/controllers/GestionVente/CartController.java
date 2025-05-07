package controllers.GestionVente;

import entities.GestionVente.Commande;
import entities.GestionVente.Produit;
import services.GestionVente.CommandeService;
import services.GestionVente.EmailService;
import services.GestionVente.PaiementService;
import services.GestionVente.PaiementService.LigneCommande;

import com.stripe.exception.StripeException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CartController {

    @FXML private VBox cartItemsBox;
    @FXML private TextField couponField;
    @FXML private Label subtotalLabel;

    public final List<CartItem> cartItems = new ArrayList<>();
    public double deliveryFee = 3.0;

    public void initialize() {
        System.out.println("🛒 CartController initialisé");
    }

    public void setProduits(List<Produit> produits) {
        cartItemsBox.getChildren().clear();
        for (Produit p : produits) {
            addItemToCart(p.getNom(), p.getPrix(), p.getImagePath());
        }
        updateSubtotal();
    }

    public void addItemToCart(String name, double price, String imagePath) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        Label nameLabel = new Label(name);
        Label priceLabel = new Label(String.format("%.2f USD", price));
        Label totalLabel = new Label();

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setPrefWidth(60);

        ImageView imageView = new ImageView();
        File imageFile = new File(imagePath != null ? imagePath : "");
        if (imageFile.exists()) {
            imageView.setImage(new Image(imageFile.toURI().toString()));
        } else {
            imageView.setImage(new Image("file:images/home.png"));
        }
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Button removeBtn = new Button("✕");

        CartItem item = new CartItem(name, price, quantitySpinner, totalLabel, row);
        cartItems.add(item);

        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            item.setQuantity(newVal);
            updateSubtotal();
        });

        removeBtn.setOnAction(e -> {
            cartItems.remove(item);
            cartItemsBox.getChildren().remove(row);
            updateSubtotal();
        });

        row.getChildren().addAll(imageView, nameLabel, priceLabel, quantitySpinner, totalLabel, removeBtn);
        cartItemsBox.getChildren().add(row);

        item.setQuantity(1);
    }

    public void updateSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotal();
        }
        subtotalLabel.setText(String.format("Subtotal: %.2f USD", subtotal + deliveryFee));
    }

    @FXML
    private void applyCoupon() {
        String code = couponField.getText().trim();
        if (code.equalsIgnoreCase("DISCOUNT10")) {
            deliveryFee = 0;
            updateSubtotal();
        }
    }

    static class CartItem {
        private final String name;
        private final double price;
        private final Spinner<Integer> quantitySpinner;
        private final Label totalLabel;
        private final HBox row;

        public CartItem(String name, double price, Spinner<Integer> spinner, Label totalLabel, HBox row) {
            this.name = name;
            this.price = price;
            this.quantitySpinner = spinner;
            this.totalLabel = totalLabel;
            this.row = row;
        }

        public void setQuantity(int quantity) {
            totalLabel.setText(String.format("%.2f USD", price * quantity));
        }

        public double getTotal() {
            return price * quantitySpinner.getValue();
        }

        public String getName() { return name; }
        public double getUnitPrice() { return price; }
        public int getQuantity() { return quantitySpinner.getValue(); }
    }

    @FXML
    private void placeOrder() {
        float montant = 0;
        List<LigneCommande> articles = new ArrayList<>();

        for (CartItem item : cartItems) {
            montant += item.getTotal();
            LigneCommande ligne = new LigneCommande(
                    item.getName(),
                    item.getUnitPrice(),
                    item.getQuantity()
            );
            articles.add(ligne);
        }

        montant += deliveryFee;

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Commande commande = new Commande();
        commande.setMontantTotal(montant);
        commande.setDateCommande(now);
        commande.setStatut("En cours");

        CommandeService commandeService = new CommandeService();
        commandeService.add(commande);

        try {
            PaiementService ps = new PaiementService();
            String url = ps.creerSessionPaiement(articles, deliveryFee);
            System.out.println("💳 Stripe URL = " + url);
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));

            sendEmailWithOrderDetails(commande, articles);
            loadSuccessView();

        } catch (StripeException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du paiement Stripe").show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur inattendue").show();
        }
    }

    private void sendEmailWithOrderDetails(Commande commande, List<LigneCommande> articles) {
        String to = "nourkooli@outlook.fr"; // TODO: rendre dynamique
        String subject = "Confirmation de votre commande";

        StringBuilder body = new StringBuilder("Merci pour votre commande !\n\n");
        body.append("🧾 Détails de la commande :\n");
        for (LigneCommande article : articles) {
            body.append("- ").append(article.getNomProduit())
                    .append(" x").append(article.getQuantite())
                    .append(" → ").append(article.getPrixUnitaire() * article.getQuantite()).append(" USD\n");
        }

        body.append("\n💰 Total : ").append(commande.getMontantTotal()).append(" USD\n");
        body.append("📅 Date : ").append(commande.getDateCommande());

        System.out.println("📧 Envoi de l'email à : " + to);
        EmailService emailService = new EmailService();
        emailService.sendOrderConfirmation(to, subject, body.toString());
    }

    private void loadSuccessView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/esprit/tn/demo/SuccessPaiement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Paiement réussi");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de succès").show();
        }
    }
}
