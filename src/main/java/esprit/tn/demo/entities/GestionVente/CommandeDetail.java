package esprit.tn.demo.entities.GestionVente;


import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CommandeDetail {
    private final IntegerProperty id;
    private final FloatProperty prixUnitaire;
    private final FloatProperty montantTotal;
    private final IntegerProperty quantite;

    private final Commande commande;
    private final Produit produit;

    // Constructor
    public CommandeDetail(int id, float prixUnitaire, float montantTotal, int quantite, Commande commande, Produit produit) {
        this.id = new SimpleIntegerProperty(id);
        this.prixUnitaire = new SimpleFloatProperty(prixUnitaire);
        this.montantTotal = new SimpleFloatProperty(montantTotal);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.commande = commande;
        this.produit = produit;
    }

    // JavaFX Property methods (these will be used in TableView bindings)
    public IntegerProperty idProperty() {
        return id;
    }

    public FloatProperty prixUnitaireProperty() {
        return prixUnitaire;
    }

    public FloatProperty montantTotalProperty() {
        return montantTotal;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    // Regular getters (non-JavaFX)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public float getPrixUnitaire() {
        return prixUnitaire.get();
    }

    public void setPrixUnitaire(float prixUnitaire) {
        this.prixUnitaire.set(prixUnitaire);
    }

    public float getMontantTotal() {
        return montantTotal.get();
    }

    public void setMontantTotal(float montantTotal) {
        this.montantTotal.set(montantTotal);
    }

    public int getQuantite() {
        return quantite.get();
    }

    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    public Commande getCommande() {
        return commande;
    }

    public Produit getProduit() {
        return produit;
    }
}
