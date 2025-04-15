
package esprit.tn.demo.services.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Maintenance;
import esprit.tn.demo.services.IService;
import esprit.tn.demo.tools.MyDataBase;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class MaintenanceService implements IService<Maintenance> {
    private Connection cnx;

    public MaintenanceService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Maintenance maintenance) {
        String sql = "INSERT INTO maintenance (description, date_maintenance, type, etat, id_machine, id_technicien) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, maintenance.getDescription());
            pst.setDate(2, new java.sql.Date(maintenance.getDate_maintenance().getTime()));
            pst.setString(3, maintenance.getType());
            pst.setString(4, maintenance.getEtat());
            pst.setInt(5, maintenance.getId_machine());
            pst.setInt(6, maintenance.getId_technicien());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    maintenance.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding maintenance: " + e.getMessage());
        }
    }

    @Override
    public void update(Maintenance maintenance) {
        String sql = "UPDATE maintenance SET description = ?, date_maintenance = ?, type = ?, etat = ?, id_machine = ?, id_technicien = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, maintenance.getDescription());
            pst.setDate(2, new java.sql.Date(maintenance.getDate_maintenance().getTime()));
            pst.setString(3, maintenance.getType());
            pst.setString(4, maintenance.getEtat());
            pst.setInt(5, maintenance.getId_machine());
            pst.setInt(6, maintenance.getId_technicien());
            pst.setInt(7, maintenance.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating maintenance: " + e.getMessage());
        }
    }

    @Override
    public void delete(Maintenance maintenance) {
        String sql = "DELETE FROM maintenance WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, maintenance.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting maintenance: " + e.getMessage());
        }
    }

    public List<Maintenance> getAll() {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Maintenance maintenance = new Maintenance(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDate("date_maintenance"),
                        rs.getString("type"),
                        rs.getString("etat"),
                        rs.getInt("id_machine"),
                        rs.getInt("id_technicien")
                );
                maintenances.add(maintenance);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenances: " + e.getMessage());
        }

        return maintenances;
    }

    public Maintenance getById(int id) {
        String sql = "SELECT * FROM maintenance WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Maintenance(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getDate("date_maintenance"),
                            rs.getString("type"),
                            rs.getString("etat"),
                            rs.getInt("id_machine"),
                            rs.getInt("id_technicien")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance: " + e.getMessage());
        }
        return null;
    }

    // Additional specialized methods
    public List<Maintenance> getByMachineId(int machineId) {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE id_machine = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, machineId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Maintenance maintenance = new Maintenance(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getDate("date_maintenance"),
                            rs.getString("type"),
                            rs.getString("etat"),
                            rs.getInt("id_machine"),
                            rs.getInt("id_technicien")
                    );
                    maintenances.add(maintenance);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenances by machine ID: " + e.getMessage());
        }

        return maintenances;
    }

    public List<Maintenance> getByTechnicienId(int technicienId) {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE id_technicien = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, technicienId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Maintenance maintenance = new Maintenance(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getDate("date_maintenance"),
                            rs.getString("type"),
                            rs.getString("etat"),
                            rs.getInt("id_machine"),
                            rs.getInt("id_technicien")
                    );
                    maintenances.add(maintenance);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenances by technicien ID: " + e.getMessage());
        }

        return maintenances;
    }
}