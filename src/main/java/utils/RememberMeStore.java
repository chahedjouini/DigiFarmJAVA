package utils;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

public class RememberMeStore {
    private static final Path FILE = Paths.get(System.getProperty("user.home"), ".digifarm_user.json");
    
    public static void save(RememberedUser u) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(FILE))) {
            out.writeObject(u);
        } catch (IOException ignored) {
            System.err.println("Erreur lors de la sauvegarde des données utilisateur: " + ignored.getMessage());
        }
    }

    public static Optional<RememberedUser> load() {
        if (!Files.exists(FILE)) return Optional.empty();
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(FILE))) {
            return Optional.ofNullable((RememberedUser) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement des données utilisateur: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (IOException ignored) {
            System.err.println("Erreur lors de la suppression des données utilisateur: " + ignored.getMessage());
        }
    }

    // DTO pour stocker les informations de l'utilisateur
    public static class RememberedUser implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final int id;
        private final String nom;
        private final String prenom;
        private final String role;

        public RememberedUser(int id, String nom, String prenom, String role) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public String getRole() {
            return role;
        }
    }
}
