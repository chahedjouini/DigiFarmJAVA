package entities;

public class MaintenanceRecord {
    private String dateEntretien;
    private double cout;
    private int temperature;
    private int humidite;
    private double consoCarburant;
    private double consoEnergie;
    private String status;
    private int idMachine;

    public String getDateEntretien() {
        return dateEntretien;
    }

    public void setDateEntretien(String dateEntretien) {
        this.dateEntretien = dateEntretien;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidite() {
        return humidite;
    }

    public void setHumidite(int humidite) {
        this.humidite = humidite;
    }

    public double getConsoCarburant() {
        return consoCarburant;
    }

    public void setConsoCarburant(double consoCarburant) {
        this.consoCarburant = consoCarburant;
    }

    public double getConsoEnergie() {
        return consoEnergie;
    }

    public void setConsoEnergie(double consoEnergie) {
        this.consoEnergie = consoEnergie;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
        this.idMachine = idMachine;
    }
}