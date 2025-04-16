package esprit.tn.demo.entities.GestionAnimal;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Veterinaire {

    private IntegerProperty id;
    private StringProperty nom;
    private IntegerProperty numTel;
    private StringProperty email;
    private StringProperty adresseCabine;

    public Veterinaire() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.numTel = new SimpleIntegerProperty();
        this.email = new SimpleStringProperty();
        this.adresseCabine = new SimpleStringProperty();
    }

    public Veterinaire(int id, String nom, int numTel, String email, String adresseCabine) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.numTel = new SimpleIntegerProperty(numTel);
        this.email = new SimpleStringProperty(email);
        this.adresseCabine = new SimpleStringProperty(adresseCabine);
    }

    // Getters and setters with Property methods
    public IntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public IntegerProperty numTelProperty() {
        return numTel;
    }

    public int getNumTel() {
        return numTel.get();
    }

    public void setNumTel(int numTel) {
        this.numTel.set(numTel);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty adresseCabineProperty() {
        return adresseCabine;
    }

    public String getAdresseCabine() {
        return adresseCabine.get();
    }

    public void setAdresseCabine(String adresseCabine) {
        this.adresseCabine.set(adresseCabine);
    }

    @Override
    public String toString() {
        return getNom();
    }
}
