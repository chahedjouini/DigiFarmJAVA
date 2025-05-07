package entities;

import java.time.LocalDate;

public class Facture {
    private int id;
    private LocalDate datef;
    private float prixt;
    private int cin;
    private String email;
    private Abonnement abonnement;

    public Facture() {
        this.datef = LocalDate.now(); // Par défaut : aujourd'hui
    }

    public Facture(LocalDate datef, float prixt, int cin, String email, Abonnement abonnement) {
        this.datef = datef;
        this.prixt = prixt;
        this.cin = cin;
        this.email = email;
        this.abonnement = abonnement;
        if (abonnement != null) {
            this.prixt = abonnement.getPrix(); // Auto-remplissage prix depuis abonnement
        }
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDatef() {
        return datef;
    }

    public void setDatef(LocalDate datef) {
        this.datef = datef;
    }

    public float getPrixt() {
        return prixt;
    }

    public void setPrixt(float prixt) {
        this.prixt = prixt;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Abonnement getAbonnement() {
        return abonnement;
    }

    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
        if (abonnement != null) {
            this.prixt = abonnement.getPrix(); // Recalcule automatiquement
        }
    }

    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", datef=" + datef +
                ", prixt=" + prixt +
                ", cin=" + cin +
                ", email='" + email + '\'' +
                ", abonnement=" + (abonnement != null ? abonnement.getId() : null) +
                '}';
    }
}
