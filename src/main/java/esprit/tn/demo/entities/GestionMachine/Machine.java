package esprit.tn.demo.entities.GestionMachine;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import esprit.tn.demo.entities.GestionMachine.Maintenance;

    public class Machine {
        private int id_machine;
        private String nom;
        private String type;
        private Date date_achat;
        private String etat_pred;
        private String etat;
        private int owner_id;
        // One-to-Many relationship with Maintenance
        private List<Maintenance> maintenances = new ArrayList<>();
        public Machine() {}

        public Machine(int id_machine, String nom, String type, Date date_achat,
                       String etat_pred, String etat, int owner_id) {
            this.id_machine = id_machine;
            this.nom = nom;
            this.type = type;
            this.date_achat = date_achat;
            this.etat_pred = etat_pred;
            this.etat = etat;
            this.owner_id = owner_id;
        }

        public Machine(String nom, String type, Date date_achat,
                       String etat_pred, String etat, int owner_id) {
            this.nom = nom;
            this.type = type;
            this.date_achat = date_achat;
            this.etat_pred = etat_pred;
            this.etat = etat;
            this.owner_id = owner_id;
        }

        // Getters and Setters
        public int getId_machine() {
            return id_machine;
        }

        public void setId_machine(int id_machine) {
            this.id_machine = id_machine;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getDate_achat() {
            return date_achat;
        }

        public void setDate_achat(Date date_achat) {
            this.date_achat = date_achat;
        }

        public String getEtat_pred() {
            return etat_pred;
        }

        public void setEtat_pred(String etat_pred) {
            this.etat_pred = etat_pred;
        }

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }

        public int getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(int owner_id) {
            this.owner_id = owner_id;
        }

        @Override
        public String toString() {
            return "Machine{" +
                    "id_machine=" + id_machine +
                    ", nom='" + nom + '\'' +
                    ", type='" + type + '\'' +
                    ", date_achat=" + date_achat +
                    ", etat_pred='" + etat_pred + '\'' +
                    ", etat='" + etat + '\'' +
                    ", owner_id=" + owner_id +
                    '}';
        }
    }

