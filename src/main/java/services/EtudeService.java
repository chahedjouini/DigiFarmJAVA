package services;

import utils.MailUtil;
import entities.Culture;
import entities.Etude;
import entities.Expert;
import enums.Climat;
import enums.TypeSol;
import data.MyDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtudeService implements IService<Etude> {
    Connection connection;

    public EtudeService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Etude etude) throws SQLException {
        String sql = "INSERT INTO etude (date_r, culture_id, expert_id, id_user_id, climat, type_sol, irrigation, fertilisation, prix, rendement, precipitations, main_oeuvre) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(etude.getDateR()));
        ps.setInt(2, etude.getCulture().getId());
        ps.setInt(3, etude.getExpert().getId());
        ps.setInt(4, etude.getCulture().getIdUser());
        ps.setString(5, etude.getClimat().toString());
        ps.setString(6, etude.getTypeSol().toString());
        ps.setBoolean(7, etude.isIrrigation());
        ps.setBoolean(8, etude.isFertilisation());
        ps.setFloat(9, etude.getPrix());
        ps.setFloat(10, etude.getRendement());
        ps.setFloat(11, etude.getPrecipitations());
        ps.setFloat(12, etude.getMainOeuvre());
        ps.executeUpdate();

        ExpertService expertService = new ExpertService();
        expertService.markAsUnavailable(etude.getExpert().getId());

        sendEmailToExpert(etude.getExpert(), etude);
    }

    private void sendEmailToExpert(Expert expert, Etude etude) {
        if (expert.getEmail() == null || expert.getEmail().isEmpty()) {
            System.err.println("Expert has no email address.");
            return;
        }

        String subject = "Nouvelle étude assignée";
        String content = "Bonjour " + expert.getNom() + " " + expert.getPrenom() + ",\n\n" +
                "Vous avez une nouvelle étude assignée. Détails de l'étude :\n" +
                "Date de réalisation: " + etude.getDateR() + "\n" +
                "Culture: " + etude.getCulture().getNom() + "\n" +
                "Climat: " + etude.getClimat() + "\n" +
                "Type de Sol: " + etude.getTypeSol() + "\n\n" +
                "Merci de bien vouloir procéder à l'exécution de cette étude.\n\n" +
                "Cordialement,\n" +
                "Chef de Departement d'etude Yassine Abidi,\n" +
                "L'équipe DigiFarm";

        try {
            MailUtil.sendEmail(expert.getEmail(), subject, content);
            System.out.println("Email sent successfully to: " + expert.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while sending email to expert: " + expert.getEmail());
        }
    }

    @Override
    public void update(Etude etude) throws SQLException {
        String sql = "UPDATE etude SET " +
                "date_r = ?, " +
                "culture_id = ?, " +
                "expert_id = ?, " +
                "climat = ?, " +
                "type_sol = ?, " +
                "irrigation = ?, " +
                "fertilisation = ?, " +
                "prix = ?, " +
                "rendement = ?, " +
                "precipitations = ?, " +
                "main_oeuvre = ? " +
                "WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(etude.getDateR()));
        ps.setInt(2, etude.getCulture().getId());
        ps.setInt(3, etude.getExpert().getId());
        ps.setString(4, etude.getClimat().toString());
        ps.setString(5, etude.getTypeSol().toString());
        ps.setBoolean(6, etude.isIrrigation());
        ps.setBoolean(7, etude.isFertilisation());
        ps.setFloat(8, etude.getPrix());
        ps.setFloat(9, etude.getRendement());
        ps.setFloat(10, etude.getPrecipitations());
        ps.setFloat(11, etude.getMainOeuvre());
        ps.setInt(12, etude.getId());

        try {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to update Etude: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "SELECT expert_id FROM etude WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int expertId = rs.getInt("expert_id");
            ExpertService expertService = new ExpertService();
            expertService.markAsAvailable(expertId);
        }

        String deleteSql = "DELETE FROM etude WHERE id = ?";
        PreparedStatement deletePs = connection.prepareStatement(deleteSql);
        deletePs.setInt(1, id);
        deletePs.executeUpdate();
    }

    @Override
    public List<Etude> select() throws SQLException {
        List<Etude> etudes = new ArrayList<>();
        String sql = """
            SELECT e.*, 
                   c.id AS culture_id, c.nom AS culture_nom, c.region AS culture_region, 
                   ex.id AS expert_id, ex.nom AS expert_nom, ex.prenom AS expert_prenom
            FROM etude e
            JOIN culture c ON e.culture_id = c.id
            JOIN expert ex ON e.expert_id = ex.id
            """;

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Expert expert = new Expert();
            expert.setId(rs.getInt("expert_id"));
            expert.setNom(rs.getString("expert_nom"));
            expert.setPrenom(rs.getString("expert_prenom"));

            Culture culture = new Culture();
            culture.setId(rs.getInt("culture_id"));
            culture.setNom(rs.getString("culture_nom"));
            culture.setRegion(rs.getString("culture_region"));

            Etude etude = new Etude();
            etude.setId(rs.getInt("id"));
            etude.setDateR(rs.getDate("date_r").toLocalDate());
            etude.setCulture(culture);
            etude.setExpert(expert);

            try {
                etude.setClimat(Climat.valueOf(rs.getString("climat").toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠ Erreur Climat non reconnu : " + rs.getString("climat"));
                continue;
            }

            try {
                etude.setTypeSol(TypeSol.valueOf(rs.getString("type_sol").toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠ Erreur TypeSol non reconnu : " + rs.getString("type_sol"));
                continue;
            }

            etude.setIrrigation(rs.getBoolean("irrigation"));
            etude.setFertilisation(rs.getBoolean("fertilisation"));
            etude.setPrix(rs.getFloat("prix"));
            etude.setRendement(rs.getFloat("rendement"));
            etude.setPrecipitations(rs.getFloat("precipitations"));
            etude.setMainOeuvre(rs.getFloat("main_oeuvre"));

            etudes.add(etude);
        }

        return etudes;
    }

    public Map<String, Integer> getStatisticsByClimat() throws SQLException {
        Map<String, Integer> climatStats = new HashMap<>();
        String sql = "SELECT climat, COUNT(*) AS count FROM etude GROUP BY climat";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String climat = rs.getString("climat");
            int count = rs.getInt("count");
            climatStats.put(climat, count);
        }

        return climatStats;
    }

    public Map<String, Integer> getStatisticsByTypeSol() throws SQLException {
        Map<String, Integer> typeSolStats = new HashMap<>();
        String sql = "SELECT type_sol, COUNT(*) AS count FROM etude GROUP BY type_sol";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String typeSol = rs.getString("type_sol");
            int count = rs.getInt("count");
            typeSolStats.put(typeSol, count);
        }

        return typeSolStats;
    }

    public Map<String, Double> getStatistics() throws SQLException {
        Map<String, Double> statistics = new HashMap<>();

        String priceSql = "SELECT AVG(prix) FROM etude";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(priceSql);
        if (rs.next()) {
            statistics.put("price", rs.getDouble(1));
        }

        String rendementSql = "SELECT AVG(rendement) FROM etude";
        rs = stmt.executeQuery(rendementSql);
        if (rs.next()) {
            statistics.put("rendement", rs.getDouble(1));
        }

        String mainOeuvreSql = "SELECT AVG(main_oeuvre) FROM etude";
        rs = stmt.executeQuery(mainOeuvreSql);
        if (rs.next()) {
            statistics.put("mainOeuvre", rs.getDouble(1));
        }

        return statistics;
    }

}
