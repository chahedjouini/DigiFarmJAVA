package services;

import entities.Culture;
import entities.Etude;
import entities.Expert;
import enums.Climat;
import enums.TypeSol;
import data.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void update(Etude etude) throws SQLException {
        String sql = "UPDATE etude SET date_r=?, culture_id=?, expert_id=?, id_user_id=?, climat=?, type_sol=?, irrigation=?, fertilisation=?, prix=?, rendement=?, precipitations=?, main_oeuvre=? WHERE id=?";
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
        ps.setInt(13, etude.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM etude WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
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
}
