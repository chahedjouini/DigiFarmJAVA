package entities;

import java.util.ArrayList;
import java.util.List;

public class Abonnement {
    private int id;
    private int idc;
    private String nom;
    private String prenom;
    private int numero;
    private String typeabb;
    private int dureeabb;
    private float prix;

    private List<Facture> factures = new ArrayList<>();

    public Abonnement() {}

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdc() {
        return idc;
    }

    public void setIdc(int idc) {
        this.idc = idc;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTypeabb() {
        return typeabb;
    }

    public void setTypeabb(String typeabb) {
        this.typeabb = typeabb;
    }

    public int getDureeabb() {
        return dureeabb;
    }

    public void setDureeabb(int dureeabb) {
        this.dureeabb = dureeabb;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public List<Facture> getFactures() {
        return factures;
    }

    public void setFactures(List<Facture> factures) {
        this.factures = factures;
    }

    public void addFacture(Facture facture) {
        this.factures.add(facture);
    }

    public void removeFacture(Facture facture) {
        this.factures.remove(facture);
    }

    public void calculerPrix() {
        float prixParMois;
        switch (this.typeabb.toLowerCase()) {
            case "bronze" -> prixParMois = 10.0f;
            case "silver" -> prixParMois = 15.0f;
            case "gold" -> prixParMois = 20.0f;
            default -> prixParMois = 10.0f;
        }

        int multiplicateur;
        switch (this.dureeabb) {
            case 6 -> multiplicateur = 6;
            case 12 -> multiplicateur = 12;
            default -> multiplicateur = 1;
        }

        this.prix = prixParMois * multiplicateur;
    }

    @Override
    public String toString() {
        return nom;
    }
}
