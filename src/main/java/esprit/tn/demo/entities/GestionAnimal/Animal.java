package esprit.tn.demo.entities.GestionAnimal;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Animal {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty type;
    private final StringProperty race;
    private final IntegerProperty age;
    private final FloatProperty poids;

    // Constructor
    public Animal(int id, String nom, String type, int age, float poids, String race) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.type = new SimpleStringProperty(type);
        this.age = new SimpleIntegerProperty(age);
        this.poids = new SimpleFloatProperty(poids);
        this.race = new SimpleStringProperty(race);
    }

    // JavaFX Property methods (these will be used in TableView bindings)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty raceProperty() {
        return race;
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public FloatProperty poidsProperty() {
        return poids;
    }

    // Regular getters (non-JavaFX)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getRace() {
        return race.get();
    }

    public void setRace(String race) {
        this.race.set(race);
    }

    public int getAge() {
        return age.get();
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public float getPoids() {
        return poids.get();
    }

    public void setPoids(float poids) {
        this.poids.set(poids);
    }
}
