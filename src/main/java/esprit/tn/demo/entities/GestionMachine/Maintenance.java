package esprit.tn.demo.entities.GestionMachine;

import java.util.Date;

public class Maintenance {
    private int id;
    private int id_machine_id;  // Foreign key to Machine
    private Integer id_technicien_id;  // Nullable foreign key to Technicien
    private Date date_entretien;
    private double cout;
    private Integer temperature;  // Nullable
    private Integer humidite;     // Nullable
    private Double conso_carburant;  // Nullable
    private Double conso_energie;    // Nullable
    private String status;
    private String etat_pred;       // Nullable

    // Constructors
    public Maintenance() {}

    public Maintenance(int id, int id_machine_id, Integer id_technicien_id,
                       Date date_entretien, double cout, Integer temperature,
                       Integer humidite, Double conso_carburant, Double conso_energie,
                       String status, String etat_pred) {
        this.id = id;
        this.id_machine_id = id_machine_id;
        this.id_technicien_id = id_technicien_id;
        this.date_entretien = date_entretien;
        this.cout = cout;
        this.temperature = temperature;
        this.humidite = humidite;
        this.conso_carburant = conso_carburant;
        this.conso_energie = conso_energie;
        this.status = status;
        this.etat_pred = etat_pred;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_machine_id() {
        return id_machine_id;
    }

    public void setId_machine_id(int id_machine_id) {
        this.id_machine_id = id_machine_id;
    }

    public Integer getId_technicien_id() {
        return id_technicien_id;
    }

    public void setId_technicien_id(Integer id_technicien_id) {
        this.id_technicien_id = id_technicien_id;
    }

    public Date getDate_entretien() {
        return date_entretien;
    }

    public void setDate_entretien(Date date_entretien) {
        this.date_entretien = date_entretien;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidite() {
        return humidite;
    }

    public void setHumidite(Integer humidite) {
        this.humidite = humidite;
    }

    public Double getConso_carburant() {
        return conso_carburant;
    }

    public void setConso_carburant(Double conso_carburant) {
        this.conso_carburant = conso_carburant;
    }

    public Double getConso_energie() {
        return conso_energie;
    }

    public void setConso_energie(Double conso_energie) {
        this.conso_energie = conso_energie;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEtat_pred() {
        return etat_pred;
    }

    public void setEtat_pred(String etat_pred) {
        this.etat_pred = etat_pred;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + id +
                ", id_machine_id=" + id_machine_id +
                ", id_technicien_id=" + id_technicien_id +
                ", date_entretien=" + date_entretien +
                ", cout=" + cout +
                ", temperature=" + temperature +
                ", humidite=" + humidite +
                ", conso_carburant=" + conso_carburant +
                ", conso_energie=" + conso_energie +
                ", status='" + status + '\'' +
                ", etat_pred='" + etat_pred + '\'' +
                '}';
    }
}