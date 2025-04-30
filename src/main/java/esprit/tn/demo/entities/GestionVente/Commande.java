package esprit.tn.demo.entities.GestionVente;

import esprit.tn.demo.entities.GestionVente.CommandeDetail;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Commande {
    private final IntegerProperty id;
    private final StringProperty statut;
    private final FloatProperty montantTotal;
    private final StringProperty dateCommande;
    private ObservableList<CommandeDetail> commandeDetails;
    // Constructor
    public Commande(int id, String statut, float montantTotal, String dateCommande) {
        this.id = new SimpleIntegerProperty(id);
        this.statut = new SimpleStringProperty(statut);
        this.montantTotal = new SimpleFloatProperty(montantTotal);
        this.dateCommande = new SimpleStringProperty(dateCommande);  // Format: "yyyy-MM-dd HH:mm:ss"
    }



    // JavaFX Property methods (these will be used in TableView bindings)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty statutProperty() {
        return statut;
    }

    public FloatProperty montantTotalProperty() {
        return montantTotal;
    }

    public StringProperty dateCommandeProperty() {
        return dateCommande;
    }

    // Regular getters (non-JavaFX)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getStatut() {
        return statut.get();
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    public float getMontantTotal() {
        return montantTotal.get();
    }

    public void setMontantTotal(float montantTotal) {
        this.montantTotal.set(montantTotal);
    }

    public String getDateCommande() {
        return dateCommande.get();
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande.set(dateCommande);
    }
}
