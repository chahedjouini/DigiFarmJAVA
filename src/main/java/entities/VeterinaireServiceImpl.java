package services;

import data.MyDataBase;
import entities.Veterinaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeterinaireServiceImpl implements VeterinaireService {

    private static final Logger logger = Logger.getLogger(VeterinaireServiceImpl.class.getName());
    private final Connection connection;

    public VeterinaireServiceImpl() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void addVeterinaire(Veterinaire v) {
        String query = "INSERT INTO veterinaire (nom, num_tel, email, adresse_cabine) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, v.getNom());
            statement.setInt(2, v.getnum_tel());
            statement.setString(3, v.getEmail());
            statement.setString(4, v.getadresse_cabine());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    v.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding Veterinaire", e);
        }
    }

    @Override
    public void updateVeterinaire(Veterinaire v) {
        String query = "UPDATE veterinaire SET nom = ?, num_tel = ?, email = ?, adresse_cabine = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, v.getNom());
            statement.setInt(2, v.getnum_tel());
            statement.setString(3, v.getEmail());
            statement.setString(4, v.getadresse_cabine());
            statement.setInt(5, v.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating Veterinaire", e);
        }
    }

    @Override
    public void deleteVeterinaire(int id) {
        String query = "DELETE FROM veterinaire WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting Veterinaire with id: " + id, e);
        }
    }

    @Override
    public List<Veterinaire> getAllVeterinaires() {
        List<Veterinaire> vets = new ArrayList<>();
        String query = "SELECT * FROM veterinaire";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                int num_tel = rs.getInt("num_tel");
                String email = rs.getString("email");
                String adresse_cabine = rs.getString("adresse_cabine");

                Veterinaire vet = new Veterinaire(id, nom, num_tel, email, adresse_cabine);
                vets.add(vet);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all Veterinaires", e);
        }
        return vets;
    }

    @Override
    public Veterinaire getVeterinaireById(int id) {
        String query = "SELECT * FROM veterinaire WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    int num_tel = rs.getInt("num_tel");
                    String email = rs.getString("email");
                    String adresse_cabine = rs.getString("adresse_cabine"); // Correction ici

                    return new Veterinaire(id, nom, num_tel, email, adresse_cabine);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving Veterinaire with id: " + id, e);
        }
        return null;
    }
}