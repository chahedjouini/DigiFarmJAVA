package entities;

import enums.BesoinsEngrais;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Culture {
    private int id;
    private String nom;
    private float surface;
    private LocalDate datePlantation;
    private LocalDate dateRecolte;
    private String region;
    private String typeCulture;
    private float densitePlantation;
    private float besoinsEau;
    private BesoinsEngrais besoinsEngrais;
    private float rendementMoyen;
    private float coutMoyen;
    private int idUser;
    private List<Etude> etudes = new ArrayList<>();

    public Culture() {
    }

    public Culture(int id, String nom, float surface, LocalDate datePlantation, LocalDate dateRecolte,
                   String region, String typeCulture, float densitePlantation, float besoinsEau,
                   BesoinsEngrais besoinsEngrais, float rendementMoyen, float coutMoyen, int idUser) {
        this.id = id;
        this.nom = nom;
        this.surface = surface;
        this.datePlantation = datePlantation;
        this.dateRecolte = dateRecolte;
        this.region = region;
        this.typeCulture = typeCulture;
        this.densitePlantation = densitePlantation;
        this.besoinsEau = besoinsEau;
        this.besoinsEngrais = besoinsEngrais;
        this.rendementMoyen = rendementMoyen;
        this.coutMoyen = coutMoyen;
        this.idUser = idUser;
    }

    public Culture(String nom, float surface, LocalDate datePlantation, LocalDate dateRecolte,
                   String region, String typeCulture, float densitePlantation, float besoinsEau,
                   BesoinsEngrais besoinsEngrais, float rendementMoyen, float coutMoyen, int idUser) {
        this.nom = nom;
        this.surface = surface;
        this.datePlantation = datePlantation;
        this.dateRecolte = dateRecolte;
        this.region = region;
        this.typeCulture = typeCulture;
        this.densitePlantation = densitePlantation;
        this.besoinsEau = besoinsEau;
        this.besoinsEngrais = besoinsEngrais;
        this.rendementMoyen = rendementMoyen;
        this.coutMoyen = coutMoyen;
        this.idUser = idUser;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public float getSurface() { return surface; }
    public void setSurface(float surface) { this.surface = surface; }

    public LocalDate getDatePlantation() { return datePlantation; }
    public void setDatePlantation(LocalDate datePlantation) { this.datePlantation = datePlantation; }

    public LocalDate getDateRecolte() { return dateRecolte; }
    public void setDateRecolte(LocalDate dateRecolte) { this.dateRecolte = dateRecolte; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getTypeCulture() { return typeCulture; }
    public void setTypeCulture(String typeCulture) { this.typeCulture = typeCulture; }

    public float getDensitePlantation() { return densitePlantation; }
    public void setDensitePlantation(float densitePlantation) { this.densitePlantation = densitePlantation; }

    public float getBesoinsEau() { return besoinsEau; }
    public void setBesoinsEau(float besoinsEau) { this.besoinsEau = besoinsEau; }

    public BesoinsEngrais getBesoinsEngrais() { return besoinsEngrais; }
    public void setBesoinsEngrais(BesoinsEngrais besoinsEngrais) { this.besoinsEngrais = besoinsEngrais; }

    public float getRendementMoyen() { return rendementMoyen; }
    public void setRendementMoyen(float rendementMoyen) { this.rendementMoyen = rendementMoyen; }

    public float getCoutMoyen() { return coutMoyen; }
    public void setCoutMoyen(float coutMoyen) { this.coutMoyen = coutMoyen; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

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
        return nom; // Seulement le nom
    }

}
