package services.GestionMachine;

import entities.GestionMachine.Machine;
import services.IService;
import data.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineService implements IService<Machine> {
    private Connection cnx;

    public MachineService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(entities.GestionMachine.Machine machine) {
        String sql = "INSERT INTO machine (nom, type, date_achat, etat_pred, etat, owner_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, machine.getNom());
            pst.setString(2, machine.getType());
            pst.setDate(3, new java.sql.Date(machine.getDate_achat().getTime()));
            pst.setString(4, machine.getEtat_pred());
            pst.setString(5, machine.getEtat());
            if (machine.getOwner_id() == 0) {
                pst.setNull(6, Types.INTEGER);
            } else {
                pst.setInt(6, machine.getOwner_id());
            }
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    machine.setId_machine(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding machine: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public List<Machine> getMachinesByOwner(int ownerId) throws SQLException {
        List<Machine> result = new ArrayList<>();
        String sql = """
        SELECT * FROM machine WHERE owner_id = ?
    """;

        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, ownerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Machine machine = new Machine();

            machine.setNom(rs.getString("nom"));
            machine.setType(rs.getString("type"));
            machine.setDate_achat(rs.getDate("date_achat"));
            machine.setEtat(rs.getString("etat"));
            machine.setEtat_pred(rs.getString("etat_pred"));
            machine.setOwner_id(rs.getInt("owner_id"));

            // Add any additional fields your Machine entity might have
            // machine.setSomeField(rs.getXXX("some_field"));

            result.add(machine);
        }

        return result;
    }
    @Override
    public void update(Machine machine) {
        String sql = "UPDATE machine SET nom = ?, type = ?, date_achat = ?, etat_pred = ?, etat = ?, owner_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, machine.getNom());
            pst.setString(2, machine.getType());
            pst.setDate(3, new java.sql.Date(machine.getDate_achat().getTime()));
            pst.setString(4, machine.getEtat_pred());
            pst.setString(5, machine.getEtat());
            if (machine.getOwner_id() == 0) {
                pst.setNull(6, Types.INTEGER);
            } else {
                pst.setInt(6, machine.getOwner_id());
            }
            pst.setInt(7, machine.getId_machine());
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No machine found with ID: " + machine.getId_machine());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating machine: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<Machine> select() throws SQLException {
        return List.of();
    }


    public void delete(Machine machine) {
        String sql = "DELETE FROM machine WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, machine.getId_machine());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting machine: " + e.getMessage());
        }
    }


    public List<Machine> getAll() {
        List<Machine> machines = new ArrayList<>();
        String sql = "SELECT * FROM machine";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Machine machine = new Machine(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getDate("date_achat"),
                        rs.getString("etat_pred"),
                        rs.getString("etat"),
                        rs.getInt("owner_id")
                );
                machines.add(machine);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving machines: " + e.getMessage());
        }
        return machines;
    }


    public Machine getById(int id) {
        String sql = "SELECT * FROM machine WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Machine(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getDate("date_achat"),
                            rs.getString("etat_pred"),
                            rs.getString("etat"),
                            rs.getInt("owner_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving machine: " + e.getMessage());
        }
        return null;
    }

    public List<Machine> getByEtat(String etat) {
        List<Machine> machines = new ArrayList<>();
        String sql = "SELECT * FROM machine WHERE etat = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, etat);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Machine machine = new Machine(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getDate("date_achat"),
                            rs.getString("etat_pred"),
                            rs.getString("etat"),
                            rs.getInt("owner_id")
                    );
                    machines.add(machine);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving machines by state: " + e.getMessage());
        }
        return machines;
    }

    // Reporting methods for maintenance reports
    public List<Object[]> findTotalMaintenanceCostPerMachine() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT m.nom, COALESCE(SUM(mn.cout), 0) as total_cost " +
                "FROM machine m " +
                "LEFT JOIN maintenance mn ON m.id = mn.id_machine_id " +
                "GROUP BY m.id, m.nom";
        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("nom"), rs.getDouble("total_cost")});
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance cost: " + e.getMessage());
        }
        return results;
    }

    public List<Object[]> findMaintenanceFrequency() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT m.nom, COUNT(mn.id) as maintenance_count " +
                "FROM machine m " +
                "LEFT JOIN maintenance mn ON m.id = mn.id_machine_id " +
                "GROUP BY m.id, m.nom";
        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("nom"), rs.getInt("maintenance_count")});
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance frequency: " + e.getMessage());
        }
        return results;
    }
}