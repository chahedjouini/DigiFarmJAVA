package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    // Longueur du sel en octets
    private static final int SALT_LENGTH = 16;
    
    // Méthode pour générer un sel aléatoire
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // Méthode pour hasher un mot de passe avec sel, format de retour "sel:hash"
    public static String hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + ":" + hash;
    }
    
    // Méthode pour hasher un mot de passe avec un sel fourni
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Ajouter d'abord le sel au digest
            digest.update(Base64.getDecoder().decode(salt));
            // Puis ajouter le mot de passe
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
    
    // Méthode de compatibilité pour l'ancien système de hashage (sans sel)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
    
    // Méthode pour vérifier un mot de passe
    public static boolean verifyPassword(String password, String storedHash) {
        // Vérifier si le hash est au format "sel:hash"
        if (storedHash.contains(":")) {
            String[] parts = storedHash.split(":", 2);
            String salt = parts[0];
            String hash = parts[1];
            
            // Hasher le mot de passe saisi avec le sel extrait
            String calculatedHash = hashPassword(password, salt);
            return calculatedHash.equals(hash);
        } else {
            // Fallback pour les anciens comptes sans sel
            return hashPassword(password).equals(storedHash);
        }
    }
    
    // Vérifier si un mot de passe est valide (au moins 8 caractères et contient au moins un chiffre)
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Vérifier qu'il y a au moins un chiffre
        return password.matches(".*\\d.*");
    }
    
    // Vérifier si une adresse email est valide
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Expression régulière simple pour valider le format de l'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
} 