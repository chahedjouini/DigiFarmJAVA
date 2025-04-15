package esprit.tn.demo.entities.GestionMachine;
import java.util.Date;
import esprit.tn.demo.entities.GestionMachine.Technicien;
    public class Maintenance {
        private int id;
        private String description;
        private Date date_maintenance;
        private String type;
        private String etat;
        private int id_machine;  // Foreign key to Machine
        private int id_technicien;  // Foreign key to Technicien

        // Constructors
        public Maintenance() {}

        public Maintenance(int id, String description, Date date_maintenance,
                           String type, String etat, int id_machine, int id_technicien) {
            this.id = id;
            this.description = description;
            this.date_maintenance = date_maintenance;
            this.type = type;
            this.etat = etat;
            this.id_machine = id_machine;
            this.id_technicien = id_technicien;
        }

        public Maintenance(String description, Date date_maintenance,
                           String type, String etat, int id_machine, int id_technicien) {
            this.description = description;
            this.date_maintenance = date_maintenance;
            this.type = type;
            this.etat = etat;
            this.id_machine = id_machine;
            this.id_technicien = id_technicien;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate_maintenance() {
            return date_maintenance;
        }

        public void setDate_maintenance(Date date_maintenance) {
            this.date_maintenance = date_maintenance;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }

        public int getId_machine() {
            return id_machine;
        }

        public void setId_machine(int id_machine) {
            this.id_machine = id_machine;
        }

        public int getId_technicien() {
            return id_technicien;
        }

        public void setId_technicien(int id_technicien) {
            this.id_technicien = id_technicien;
        }

        // Relationship management methods


        @Override
        public String toString() {
            return "Maintenance{" +
                    "id=" + id +
                    ", description='" + description + '\'' +
                    ", date_maintenance=" + date_maintenance +
                    ", type='" + type + '\'' +
                    ", etat='" + etat + '\'' +
                    ", id_machine=" + id_machine +
                    ", id_technicien=" + id_technicien +
                    '}';
        }
    }
