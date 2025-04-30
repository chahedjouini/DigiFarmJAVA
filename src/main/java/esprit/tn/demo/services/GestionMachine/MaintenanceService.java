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
        String sql = "INSERT INTO maintenance (id_machine_id, id_technicien_id, date_entretien, cout, " +
                "temperature, humidite, conso_carburant, conso_energie, status, etat_pred) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, maintenance.getId_machine_id());
            pst.setObject(2, maintenance.getId_technicien_id(), Types.INTEGER);
            pst.setDate(3, new java.sql.Date(maintenance.getDate_entretien().getTime()));
            pst.setDouble(4, maintenance.getCout());
            pst.setObject(5, maintenance.getTemperature(), Types.INTEGER);
            pst.setObject(6, maintenance.getHumidite(), Types.INTEGER);
            pst.setObject(7, maintenance.getConso_carburant(), Types.DOUBLE);
            pst.setObject(8, maintenance.getConso_energie(), Types.DOUBLE);
            pst.setString(9, maintenance.getStatus());
            pst.setString(10, maintenance.getEtat_pred());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    maintenance.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding maintenance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Maintenance maintenance) {
        String sql = "UPDATE maintenance SET id_machine_id = ?, id_technicien_id = ?, date_entretien = ?, " +
                "cout = ?, temperature = ?, humidite = ?, conso_carburant = ?, conso_energie = ?, " +
                "status = ?, etat_pred = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, maintenance.getId_machine_id());
            pst.setObject(2, maintenance.getId_technicien_id(), Types.INTEGER);
            pst.setDate(3, new java.sql.Date(maintenance.getDate_entretien().getTime()));
            pst.setDouble(4, maintenance.getCout());
            pst.setObject(5, maintenance.getTemperature(), Types.INTEGER);
            pst.setObject(6, maintenance.getHumidite(), Types.INTEGER);
            pst.setObject(7, maintenance.getConso_carburant(), Types.DOUBLE);
            pst.setObject(8, maintenance.getConso_energie(), Types.DOUBLE);
            pst.setString(9, maintenance.getStatus());
            pst.setString(10, maintenance.getEtat_pred());
            pst.setInt(11, maintenance.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating maintenance: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }


    public List<Maintenance> getAll() {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                maintenances.add(mapResultSetToMaintenance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenances: " + e.getMessage());
            e.printStackTrace();
        }
        return maintenances;
    }


    public Maintenance getById(int id) {
        String sql = "SELECT * FROM maintenance WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaintenance(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Maintenance> getByMachineId(int machineId) {
        return getMaintenancesByField("id_machine_id", machineId);
    }

    public List<Maintenance> getByTechnicienId(int technicienId) {
        return getMaintenancesByField("id_technicien_id", technicienId);
    }

    private List<Maintenance> getMaintenancesByField(String fieldName, int value) {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE " + fieldName + " = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, value);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    maintenances.add(mapResultSetToMaintenance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenances by " + fieldName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return maintenances;
    }

    private Maintenance mapResultSetToMaintenance(ResultSet rs) throws SQLException {
        Maintenance maintenance = new Maintenance();
        maintenance.setId(rs.getInt("id"));
        maintenance.setId_machine_id(rs.getInt("id_machine_id"));
        maintenance.setId_technicien_id(rs.getObject("id_technicien_id") != null ? rs.getInt("id_technicien_id") : null);
        maintenance.setDate_entretien(rs.getDate("date_entretien"));
        maintenance.setCout(rs.getDouble("cout"));
        maintenance.setTemperature(rs.getObject("temperature") != null ? rs.getInt("temperature") : null);
        maintenance.setHumidite(rs.getObject("humidite") != null ? rs.getInt("humidite") : null);
        maintenance.setConso_carburant(rs.getObject("conso_carburant") != null ? rs.getDouble("conso_carburant") : null);
        maintenance.setConso_energie(rs.getObject("conso_energie") != null ? rs.getDouble("conso_energie") : null);
        maintenance.setStatus(rs.getString("status"));
        maintenance.setEtat_pred(rs.getString("etat_pred"));
        return maintenance;
    }

    // Reporting method for maintenance cost over time
    public List<Object[]> findMaintenanceCostOverTime() {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(date_entretien, '%Y-%m-%d') as date_entretien, COALESCE(SUM(cout), 0) as total_cost " +
                "FROM maintenance " +
                "GROUP BY date_entretien " +
                "ORDER BY date_entretien";
        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{rs.getString("date_entretien"), rs.getDouble("total_cost")});
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance cost over time: " + e.getMessage());
        }
        return results;
    }
}