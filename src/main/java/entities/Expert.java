package entities;

import enums.Dispo;
import java.util.ArrayList;
import java.util.List;

public class Expert {
    private int id;
    private String nom;
    private String prenom;
    private int tel;
    private String email;
    private String zone;
    private Dispo dispo;
    private List<Etude> etudes = new ArrayList<>();

    public Expert() {
    }

    public Expert(int id, String nom, String prenom, int tel, String email, String zone, Dispo dispo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.email = email;
        this.zone = zone;
        this.dispo = dispo;
    }

    public Expert(String nom, String prenom, int tel, String email, String zone, Dispo dispo) {
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.email = email;
        this.zone = zone;
        this.dispo = dispo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public int getTel() { return tel; }
    public void setTel(int tel) { this.tel = tel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public Dispo getDispo() { return dispo; }
    public void setDispo(Dispo dispo) { this.dispo = dispo; }

    public List<Etude> getEtudes() { return etudes; }
    public void setEtudes(List<Etude> etudes) { this.etudes = etudes; }

    public void addEtude(Etude etude) {
        this.etudes.add(etude);
    }

    public void removeEtude(Etude etude) {
        this.etudes.remove(etude);
    }


    @Override
    public String toString() {
        return nom + " " + prenom; // Nom complet de l’expert
    }

}
