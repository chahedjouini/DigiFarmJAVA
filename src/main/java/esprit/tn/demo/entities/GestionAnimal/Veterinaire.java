package esprit.tn.demo.entities.GestionAnimal;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Veterinaire {

    private IntegerProperty id;
    private StringProperty nom;
    private IntegerProperty num_tel;
    private StringProperty email;
    private StringProperty adresse_cabine;

    public Veterinaire() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.num_tel = new SimpleIntegerProperty();
        this.email = new SimpleStringProperty();
        this.adresse_cabine = new SimpleStringProperty();
    }

    public Veterinaire(int id, String nom, int num_tel, String email, String adresse_cabine) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.num_tel = new SimpleIntegerProperty(num_tel);
        this.email = new SimpleStringProperty(email);
        this.adresse_cabine = new SimpleStringProperty(adresse_cabine);
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

    public IntegerProperty num_telProperty() {
        return num_tel;
    }

    public int getnum_tel() {
        return num_tel.get();
    }

    public void setnum_tel(int num_tel) {
        this.num_tel.set(num_tel);
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

    public StringProperty adresse_cabineProperty() {
        return adresse_cabine;
    }

    public String getadresse_cabine() {
        return adresse_cabine.get();
    }

    public void setadresse_cabine(String adresse_cabine) {
        this.adresse_cabine.set(adresse_cabine);
    }

    @Override
    public String toString() {
        return nom + " (" + email + ")";
    }
}
