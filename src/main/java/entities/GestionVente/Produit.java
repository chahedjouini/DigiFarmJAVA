package entities.GestionVente;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Produit {
    private final IntegerProperty id;
    private final StringProperty type;
    private final StringProperty reference;
    private final StringProperty nom;
    private final StringProperty description;
    private final FloatProperty prix;
    private final IntegerProperty stock;
    private String imagePath;
    private boolean selected;

    //    private final StringProperty image;
    private ObservableList<CommandeDetail> commandeDetails;
    // Constructor
    public Produit(int id, String type, String reference, String nom, String description, float prix, int stock) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.reference = new SimpleStringProperty(reference);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.prix = new SimpleFloatProperty(prix);
        this.stock = new SimpleIntegerProperty(stock);
        //  this.image = new SimpleStringProperty(image);
    }

    // Overloaded constructor without ID (new)
    public Produit(String type, String reference, String nom, String description, float prix, Integer stock) {
        this.id = new SimpleIntegerProperty(0); // Default value for ID, can be changed later
        this.type = new SimpleStringProperty(type);
        this.reference = new SimpleStringProperty(reference);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.prix = new SimpleFloatProperty(prix);
        this.stock = new SimpleIntegerProperty(stock);
    }


    // JavaFX Property methods (these will be used in TableView bindings)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty referenceProperty() {
        return reference;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public FloatProperty prixProperty() {
        return prix;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

//    public StringProperty imageProperty() {
//        return image;
//    }

    // Regular getters (non-JavaFX)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getReference() {
        return reference.get();
    }

    public void setReference(String reference) {
        this.reference.set(reference);
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public float getPrix() {
        return prix.get();
    }

    public void setPrix(float prix) {
        this.prix.set(prix);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }
    public String getImagePath() { return imagePath; }        // 👈 nouveau getter
    public void setImagePath(String imagePath) { this.imagePath = imagePath; } // 👈 setter

    public boolean isSelected() { return selected; }          // 👈 nouveau getter
    public void setSelected(boolean selected) { this.selected = selected; }
//    public String getImage() {
//        return image.get();
//    }
//
//    public void setImage(String image) {
//        this.image.set(image);
//    }
}

