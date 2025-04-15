package services;

import entities.Expert;
import enums.Dispo;
import data.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpertService implements IService<Expert> {
    Connection connection;

    public ExpertService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Expert expert) throws SQLException {
        String sql = "INSERT INTO expert (nom, prenom, tel, email, zone, dispo) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, expert.getNom());
        ps.setString(2, expert.getPrenom());
        ps.setInt(3, expert.getTel());
        ps.setString(4, expert.getEmail());
        ps.setString(5, expert.getZone());
        ps.setString(6, expert.getDispo().toString());
        ps.executeUpdate();
    }

    @Override
    public void update(Expert expert) throws SQLException {
        String sql = "UPDATE expert SET nom=?, prenom=?, tel=?, email=?, zone=?, dispo=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, expert.getNom());
        ps.setString(2, expert.getPrenom());
        ps.setInt(3, expert.getTel());
        ps.setString(4, expert.getEmail());
        ps.setString(5, expert.getZone());
        ps.setString(6, expert.getDispo().toString());
        ps.setInt(7, expert.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM expert WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Expert> select() throws SQLException {
        List<Expert> experts = new ArrayList<>();
        String sql = "SELECT * FROM expert";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Expert expert = new Expert();
            expert.setId(rs.getInt("id"));
            expert.setNom(rs.getString("nom"));
            expert.setPrenom(rs.getString("prenom"));
            expert.setTel(rs.getInt("tel"));
            expert.setEmail(rs.getString("email"));
            expert.setZone(rs.getString("zone"));
            expert.setDispo(Dispo.valueOf(rs.getString("dispo").toUpperCase().replace(" ", "_")));
            experts.add(expert);
        }
        return experts;
    }
}
