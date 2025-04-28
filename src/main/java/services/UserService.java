package services;

import entities.User;
import enums.Role;
import tools.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    // Singleton pattern pour éviter les instances multiples
    private static UserService instance;
    private final List<User> users = new ArrayList<>();
    private int lastId = 0;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    // Méthodes CRUD
    public User createUser(String nom, String prenom, String email, String password, Role role) {
        User user = new User();
        user.setId(++lastId);
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setPassword(PasswordHasher.hashPassword(password)); // Hashage du mot de passe
        user.setRole(role);

        users.add(user);
        return user;
    }

    public User updateUser(int id, String nom, String prenom, String email, Role role) {
        User user = getUserById(id);
        if (user != null) {
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setRole(role);
        }
        return user;
    }

    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
    }

    public User getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Méthode d'authentification
    public User authenticate(String email, String password) {
        String hashedPassword = PasswordHasher.hashPassword(password);
        return users.stream()
                .filter(u -> u.getEmail().equals(email)
                        && u.getPassword().equals(hashedPassword))
                .findFirst()
                .orElse(null);
    }

    // Vérifie si l'email existe déjà
    public boolean emailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }
}