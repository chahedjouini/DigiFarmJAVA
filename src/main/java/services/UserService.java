package services;

import entities.User;
import enums.Role;
import data.MyDataBase;
import utils.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService implements IService<User> {
    private static User currentUser = null;
    private static UserService instance;
    private final Connection connection;

    private UserService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void add(User user) throws SQLException {
        // Hasher le mot de passe avec un sel, format "sel:hash"
        String hashedPassword = PasswordUtils.hashPasswordWithSalt(user.getPassword());
        
        // Stocker le mot de passe haché et le reset_token à NULL
        String query = "INSERT INTO user (nom, prenom, adresse_mail, password, role, reset_token) VALUES (?, ?, ?, ?, ?, NULL)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, hashedPassword); // Mot de passe au format "sel:hash"
            pstmt.setString(5, user.getRole().name());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
            
            // Mettre à jour l'objet utilisateur avec le mot de passe haché
            user.setPassword(hashedPassword);
            user.setResetToken(null); // reset_token est explicitement NULL
        }
    }

    @Override
    public void update(User user) throws SQLException {
        // Vérifier si le mot de passe a été modifié
        String passwordQuery = "";
        boolean updatePassword = user.getPassword() != null && !user.getPassword().isEmpty();
        
        if (updatePassword) {
            // Si le mot de passe est modifié, utiliser la méthode avec sel intégré
            String hashedPassword = PasswordUtils.hashPasswordWithSalt(user.getPassword());
            passwordQuery = "password = ?, ";
            user.setPassword(hashedPassword);
        }
        
        String query = "UPDATE user SET nom = ?, prenom = ?, adresse_mail = ?, " + passwordQuery + "role = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            
            int paramIndex = 4;
            if (updatePassword) {
                pstmt.setString(paramIndex++, user.getPassword());
            }
            
            pstmt.setString(paramIndex++, user.getRole().name());
            pstmt.setInt(paramIndex, user.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<User> select() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public User createUser(User user) {
        try {
            add(user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User updateUser(User user) {
        try {
            update(user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteUser(int id) {
        try {
            delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        try {
            return select();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public User login(String email, String password) {
        String query = "SELECT * FROM user WHERE adresse_mail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    
                    // Vérifier le mot de passe
                    if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                        currentUser = user;
                        return currentUser;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void logout() {
        // Déconnecter l'utilisateur
        currentUser = null;
    }

    public boolean isEmailUnique(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE adresse_mail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Méthode pour générer un token de réinitialisation de mot de passe
    public boolean generateResetToken(String email) {
        // Générer un nouvel UUID pour la réinitialisation
        String token = UUID.randomUUID().toString().replace("-", "");
        
        // Mettre à jour le token dans la base de données
        String query = "UPDATE user SET reset_token = ? WHERE adresse_mail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, token);
            pstmt.setString(2, email);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("adresse_mail"));
        user.setPassword(rs.getString("password"));
        user.setResetToken(rs.getString("reset_token"));
        
        // Adaptation des rôles selon la base de données
        String roleStr = rs.getString("role");
        Role role;
        
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            // Si le rôle n'est pas dans l'enum, on le traite comme CLIENT par défaut
            role = Role.CLIENT;
        }
        user.setRole(role);
        
        return user;
    }
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE adresse_mail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getResetToken(String email) {
        String query = "SELECT reset_token FROM user WHERE adresse_mail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("reset_token");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // Hasher le nouveau mot de passe
        String hashedPassword = PasswordUtils.hashPasswordWithSalt(newPassword);

        // Trouver l'utilisateur avec ce token et mettre à jour son mot de passe
        String query = "UPDATE user SET password = ?, reset_token = NULL WHERE reset_token = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, token);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String query = "SELECT id FROM user WHERE reset_token = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Si un utilisateur est trouvé, le token est valide
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



}