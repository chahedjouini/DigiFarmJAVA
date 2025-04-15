package esprit.tn.demo.entities.GestionMachine;
import java.util.ArrayList;
import java.util.List;

    public class Technicien {
        private int id;
        private String name;
        private String prenom;
        private String specialite;
        private String email;
        private int telephone;
        private String localisation;
        private Float latitude;
        private Float longitude;

        // One-to-Many relationship with Maintenance
        private List<Maintenance> maintenances = new ArrayList<>();

        // Constructors
        public Technicien() {}

        public Technicien(int id, String name, String prenom, String specialite,
                          String email, int telephone, String localisation,
                          Float latitude, Float longitude) {
            this.id = id;
            this.name = name;
            this.prenom = prenom;
            this.specialite = specialite;
            this.email = email;
            this.telephone = telephone;
            this.localisation = localisation;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Technicien(String name, String prenom, String specialite,
                          String email, int telephone, String localisation) {
            this.name = name;
            this.prenom = prenom;
            this.specialite = specialite;
            this.email = email;
            this.telephone = telephone;
            this.localisation = localisation;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getSpecialite() {
            return specialite;
        }

        public void setSpecialite(String specialite) {
            this.specialite = specialite;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getTelephone() {
            return telephone;
        }

        public void setTelephone(int telephone) {
            this.telephone = telephone;
        }

        public String getLocalisation() {
            return localisation;
        }

        public void setLocalisation(String localisation) {
            this.localisation = localisation;
        }

        public Float getLatitude() {
            return latitude;
        }

        public void setLatitude(Float latitude) {
            this.latitude = latitude;
        }

        public Float getLongitude() {
            return longitude;
        }

        public void setLongitude(Float longitude) {
            this.longitude = longitude;
        }

        // Relationship methods
        public List<Maintenance> getMaintenances() {
            return new ArrayList<>(maintenances);
        }


        @Override
        public String toString() {
            return "Technicien{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", prenom='" + prenom + '\'' +
                    ", specialite='" + specialite + '\'' +
                    ", email='" + email + '\'' +
                    ", telephone=" + telephone +
                    ", localisation='" + localisation + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", maintenances=" + maintenances.size() +
                    '}';
        }
    }

