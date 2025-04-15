
package esprit.tn.demo.services.GestionMachine;

import esprit.tn.demo.entities.GestionMachine.Machine;
import esprit.tn.demo.services.IService;
import esprit.tn.demo.tools.MyDataBase;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class MachineService implements IService<Machine> {
    private Connection cnx;

    public MachineService() {
        cnx = MyDataBase.getInstance().getConnection();
    }


    @Override
    public void add(Machine machine) {
        String sql = "INSERT INTO machine (nom, type, date_achat, etat_pred, etat, owner_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, machine.getNom());
            pst.setString(2, machine.getType());
            pst.setDate(3, new java.sql.Date(machine.getDate_achat().getTime()));
            pst.setString(4, machine.getEtat_pred());
            pst.setString(5, machine.getEtat());

            // Handle owner_id - either use the provided value or a default
            if (machine.getOwner_id() == 0) { // Assuming 0 means not set
                pst.setNull(6, Types.INTEGER); // Set as NULL if allowed
                // OR use a default value:
                // pst.setInt(6, 1); // Default owner ID
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
            throw new RuntimeException(e); // Consider rethrowing for better error handling
        }
    }

    @Override
    public void update(Machine machine) {
        String sql = "UPDATE machine SET nom = ?, type = ?, date_achat = ?, etat_pred = ?, etat = ?, owner_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
          // Validate before update
            pst.setString(1, machine.getNom());
            pst.setString(2, machine.getType());
            pst.setDate(3, new java.sql.Date(machine.getDate_achat().getTime()));
            pst.setString(4, machine.getEtat_pred());
            pst.setString(5, machine.getEtat());

            // Handle owner_id the same way as in add()
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

    // Additional methods specific to Machine if needed
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
}