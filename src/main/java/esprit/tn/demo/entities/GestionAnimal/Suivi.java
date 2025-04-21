package esprit.tn.demo.entities.GestionAnimal;

import javafx.beans.property.*;

public class Suivi {
    private final IntegerProperty id;
    private final ObjectProperty<Animal> animal;
    private final FloatProperty temperature;
    private final FloatProperty rythmeCardiaque;
    private final StringProperty etat;
    private final IntegerProperty idClient;
    private final StringProperty analysis;
    private final ObjectProperty<Veterinaire> veterinaire;

    public Suivi() {
        this.id = new SimpleIntegerProperty();
        this.animal = new SimpleObjectProperty<>();
        this.temperature = new SimpleFloatProperty();
        this.rythmeCardiaque = new SimpleFloatProperty();
        this.etat = new SimpleStringProperty();
        this.idClient = new SimpleIntegerProperty();
        this.analysis = new SimpleStringProperty();
        this.veterinaire = new SimpleObjectProperty<>();
    }

    public Suivi(int id, Animal animal, float temperature, float rythmeCardiaque, String etat, int idClient, String analysis, Veterinaire veterinaire) {
        this.id = new SimpleIntegerProperty(id);
        this.animal = new SimpleObjectProperty<>(animal);
        this.temperature = new SimpleFloatProperty(temperature);
        this.rythmeCardiaque = new SimpleFloatProperty(rythmeCardiaque);
        this.etat = new SimpleStringProperty(etat);
        this.idClient = new SimpleIntegerProperty(idClient);
        this.analysis = new SimpleStringProperty(analysis);
        this.veterinaire = new SimpleObjectProperty<>(veterinaire);
    }

    // Property methods for TableView binding
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<Animal> animalProperty() { return animal; }
    public FloatProperty temperatureProperty() { return temperature; }
    public FloatProperty rythmeCardiaqueProperty() { return rythmeCardiaque; }
    public StringProperty etatProperty() { return etat; }
    public IntegerProperty idClientProperty() { return idClient; }
    public StringProperty analysisProperty() { return analysis; }
    public ObjectProperty<Veterinaire> veterinaireProperty() { return veterinaire; }

    // Getters
    public int getId() { return id.get(); }
    public Animal getAnimal() { return animal.get(); }
    public float getTemperature() { return temperature.get(); }
    public float getRythmeCardiaque() { return rythmeCardiaque.get(); }
    public String getEtat() { return etat.get(); }
    public int getIdClient() { return idClient.get(); }
    public String getAnalysis() { return analysis.get(); }
    public Veterinaire getVeterinaire() { return veterinaire.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setAnimal(Animal animal) { this.animal.set(animal); }
    public void setTemperature(float temperature) { this.temperature.set(temperature); }
    public void setRythmeCardiaque(float rythmeCardiaque) { this.rythmeCardiaque.set(rythmeCardiaque); }
    public void setEtat(String etat) { this.etat.set(etat); }
    public void setIdClient(int idClient) { this.idClient.set(idClient); }
    public void setAnalysis(String analysis) { this.analysis.set(analysis); }
    public void setVeterinaire(Veterinaire veterinaire) { this.veterinaire.set(veterinaire); }
}
