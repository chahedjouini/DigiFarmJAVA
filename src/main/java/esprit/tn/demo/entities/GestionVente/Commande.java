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

        private IntegerProperty id;
        private StringProperty statut;
        private FloatProperty montantTotal;
        private StringProperty dateCommande;

        public Commande() {
            this.id = new SimpleIntegerProperty();
            this.statut = new SimpleStringProperty();
            this.montantTotal = new SimpleFloatProperty();
            this.dateCommande = new SimpleStringProperty();
        }

        public Commande(int id, String statut, float montantTotal, String dateCommande) {
            this.id = new SimpleIntegerProperty(id);
            this.statut = new SimpleStringProperty(statut);
            this.montantTotal = new SimpleFloatProperty(montantTotal);
            this.dateCommande = new SimpleStringProperty(dateCommande);
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
