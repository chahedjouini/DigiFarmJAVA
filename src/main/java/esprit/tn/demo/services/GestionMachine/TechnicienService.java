package esprit.tn.demo.services.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Technicien;
import esprit.tn.demo.services.IService;
import esprit.tn.demo.tools.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicienService implements IService<Technicien> {
    private Connection cnx;

    public TechnicienService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Technicien technicien) {
        String sql = "INSERT INTO technicien (name, prenom, specialite, email, telephone, localisation, latitude, longitude) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, technicien.getName());
            pst.setString(2, technicien.getPrenom());
            pst.setString(3, technicien.getSpecialite());
            pst.setString(4, technicien.getEmail());
            pst.setInt(5, technicien.getTelephone());
            pst.setString(6, technicien.getLocalisation());
            pst.setFloat(7, technicien.getLatitude());
            pst.setFloat(8, technicien.getLongitude());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    technicien.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding technicien: " + e.getMessage());
        }
    }

    @Override
    public void update(Technicien technicien) {
        String sql = "UPDATE technicien SET name = ?, prenom = ?, specialite = ?, email = ?, telephone = ?, "
                + "localisation = ?, latitude = ?, longitude = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, technicien.getName());
            pst.setString(2, technicien.getPrenom());
            pst.setString(3, technicien.getSpecialite());
            pst.setString(4, technicien.getEmail());
            pst.setInt(5, technicien.getTelephone());
            pst.setString(6, technicien.getLocalisation());
            pst.setFloat(7, technicien.getLatitude());
            pst.setFloat(8, technicien.getLongitude());
            pst.setInt(9, technicien.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating technicien: " + e.getMessage());
        }
    }

    @Override
    public void delete(Technicien technicien) {
        String sql = "DELETE FROM technicien WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, technicien.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting technicien: " + e.getMessage());
        }
    }

    public List<Technicien> getAll() {
        List<Technicien> techniciens = new ArrayList<>();
        String sql = "SELECT * FROM technicien";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Technicien technicien = new Technicien(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("email"),
                        rs.getInt("telephone"),
                        rs.getString("localisation"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude")
                );
                techniciens.add(technicien);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving techniciens: " + e.getMessage());
        }

        return techniciens;
    }

    public Technicien getById(int id) {
        String sql = "SELECT * FROM technicien WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Technicien(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getString("email"),
                            rs.getInt("telephone"),
                            rs.getString("localisation"),
                            rs.getFloat("latitude"),
                            rs.getFloat("longitude")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving technicien: " + e.getMessage());
        }
        return null;
    }

    // Additional specialized methods
    public List<Technicien> getBySpecialite(String specialite) {
        List<Technicien> techniciens = new ArrayList<>();
        String sql = "SELECT * FROM technicien WHERE specialite = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, specialite);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Technicien technicien = new Technicien(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getString("email"),
                            rs.getInt("telephone"),
                            rs.getString("localisation"),
                            rs.getFloat("latitude"),
                            rs.getFloat("longitude")
                    );
                    techniciens.add(technicien);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving techniciens by specialite: " + e.getMessage());
        }

        return techniciens;
    }

    public List<Technicien> getByLocalisation(String localisation) {
        List<Technicien> techniciens = new ArrayList<>();
        String sql = "SELECT * FROM technicien WHERE localisation LIKE ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + localisation + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Technicien technicien = new Technicien(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getString("email"),
                            rs.getInt("telephone"),
                            rs.getString("localisation"),
                            rs.getFloat("latitude"),
                            rs.getFloat("longitude")
                    );
                    techniciens.add(technicien);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving techniciens by localisation: " + e.getMessage());
        }

        return techniciens;
    }
}
