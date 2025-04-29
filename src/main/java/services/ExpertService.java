package services;

import entities.Expert;
import enums.Dispo;
import data.MyDataBase;
import utils.MailUtil;  // Ensure this import is correct

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

        sendWelcomeEmail(expert);
    }

    private void sendWelcomeEmail(Expert expert) {
        if (expert.getEmail() == null || expert.getEmail().isEmpty()) {
            System.err.println("Expert has no email address.");
            return;
        }

        String subject = "Bienvenue sur DigiFarm";
        String content = "Bonjour " + expert.getNom() + " " + expert.getPrenom() + ",\n\n" +
                "Bienvenue sur DigiFarm ! Nous sommes ravis de vous avoir parmi nous.\n\n" +
                "N'hésitez pas à nous contacter si vous avez des questions.\n\n" +
                "Cordialement,\n" +
                "L'équipe DigiFarm";

        try {
            // Send the email
            MailUtil.sendEmail(expert.getEmail(), subject, content);
            System.out.println("Welcome email sent successfully to: " + expert.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while sending welcome email to expert: " + expert.getEmail());
        }
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

    public void markAsUnavailable(int expertId) throws SQLException {
        String sql = "UPDATE expert SET dispo = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, Dispo.NON_DISPONIBLE.toString());
        ps.setInt(2, expertId);
        ps.executeUpdate();
    }

    public void markAsAvailable(int expertId) throws SQLException {
        String sql = "UPDATE expert SET dispo = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, Dispo.DISPONIBLE.toString());
        ps.setInt(2, expertId);
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM expert WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
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

            String dispoStr = rs.getString("dispo");
            if (dispoStr != null) {
                expert.setDispo(Dispo.valueOf(dispoStr.toUpperCase().replace(" ", "_")));
            }

            experts.add(expert);
        }
        return experts;
    }

    public Expert getOne(int id) throws SQLException {
        String req = "SELECT * FROM expert WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(req);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Expert e = new Expert();
            e.setId(rs.getInt("id"));
            e.setNom(rs.getString("nom"));
            e.setPrenom(rs.getString("prenom"));
            return e;
        }
        return null;
    }
}
