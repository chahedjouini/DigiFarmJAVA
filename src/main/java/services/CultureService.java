package services;

import entities.Culture;
import entities.Etude;
import entities.Expert;
import enums.BesoinsEngrais;
import enums.Climat;
import enums.TypeSol;
import data.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CultureService implements IService<Culture> {
    Connection connection;

    public CultureService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Culture culture) throws SQLException {
        String sql = "INSERT INTO culture (nom, surface, date_plantation, date_recolte, region, type_culture, densite_plantation, besoins_eau, besoins_engrais, rendement_moyen, cout_moyen, id_user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, culture.getNom());
        ps.setFloat(2, culture.getSurface());
        ps.setDate(3, Date.valueOf(culture.getDatePlantation()));
        ps.setDate(4, Date.valueOf(culture.getDateRecolte()));
        ps.setString(5, culture.getRegion());
        ps.setString(6, culture.getTypeCulture());
        ps.setFloat(7, culture.getDensitePlantation());
        ps.setFloat(8, culture.getBesoinsEau());
        ps.setString(9, culture.getBesoinsEngrais().toString());
        ps.setFloat(10, culture.getRendementMoyen());
        ps.setFloat(11, culture.getCoutMoyen());
        ps.setInt(12, culture.getIdUser());
        ps.executeUpdate();
    }

    @Override
    public void update(Culture culture) throws SQLException {
        String sql = "UPDATE culture SET nom=?, surface=?, date_plantation=?, date_recolte=?, region=?, type_culture=?, densite_plantation=?, besoins_eau=?, besoins_engrais=?, rendement_moyen=?, cout_moyen=?, id_user_id=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, culture.getNom());
        ps.setFloat(2, culture.getSurface());
        ps.setDate(3, Date.valueOf(culture.getDatePlantation()));
        ps.setDate(4, Date.valueOf(culture.getDateRecolte()));
        ps.setString(5, culture.getRegion());
        ps.setString(6, culture.getTypeCulture());
        ps.setFloat(7, culture.getDensitePlantation());
        ps.setFloat(8, culture.getBesoinsEau());
        ps.setString(9, culture.getBesoinsEngrais().toString());
        ps.setFloat(10, culture.getRendementMoyen());
        ps.setFloat(11, culture.getCoutMoyen());
        ps.setInt(12, culture.getIdUser());
        ps.setInt(13, culture.getId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String deleteEtudeSql = "DELETE FROM etude WHERE culture_id = ?";
        PreparedStatement psDeleteEtude = connection.prepareStatement(deleteEtudeSql);
        psDeleteEtude.setInt(1, id);
        psDeleteEtude.executeUpdate();

        String deleteCultureSql = "DELETE FROM culture WHERE id = ?";
        PreparedStatement psDeleteCulture = connection.prepareStatement(deleteCultureSql);
        psDeleteCulture.setInt(1, id);
        psDeleteCulture.executeUpdate();
    }

    @Override
    public List<Culture> select() throws SQLException {
        List<Culture> cultures = new ArrayList<>();
        String sqlCulture = "SELECT * FROM culture";
        Statement stmtCulture = connection.createStatement();
        ResultSet rsCulture = stmtCulture.executeQuery(sqlCulture);

        while (rsCulture.next()) {
            Culture culture = new Culture();
            int cultureId = rsCulture.getInt("id");

            culture.setId(cultureId);
            culture.setNom(rsCulture.getString("nom"));
            culture.setSurface(rsCulture.getFloat("surface"));
            culture.setDatePlantation(rsCulture.getDate("date_plantation").toLocalDate());
            culture.setDateRecolte(rsCulture.getDate("date_recolte").toLocalDate());
            culture.setRegion(rsCulture.getString("region"));
            culture.setTypeCulture(rsCulture.getString("type_culture"));
            culture.setDensitePlantation(rsCulture.getFloat("densite_plantation"));
            culture.setBesoinsEau(rsCulture.getFloat("besoins_eau"));

            // Conversion sécurisée de BesoinsEngrais
            String engraisStr = rsCulture.getString("besoins_engrais").toLowerCase();
            for (BesoinsEngrais be : BesoinsEngrais.values()) {
                if (be.toString().equalsIgnoreCase(engraisStr)) {
                    culture.setBesoinsEngrais(be);
                    break;
                }
            }

            culture.setRendementMoyen(rsCulture.getFloat("rendement_moyen"));
            culture.setCoutMoyen(rsCulture.getFloat("cout_moyen"));
            culture.setIdUser(rsCulture.getInt("id_user_id"));

            // Charger les études associées
            String sqlEtude = """
            SELECT e.*, ex.id AS expert_id, ex.nom AS expert_nom, ex.prenom AS expert_prenom
            FROM etude e
            JOIN expert ex ON e.expert_id = ex.id
            WHERE e.culture_id = ?
            """;
            PreparedStatement psEtude = connection.prepareStatement(sqlEtude);
            psEtude.setInt(1, cultureId);
            ResultSet rsEtude = psEtude.executeQuery();

            while (rsEtude.next()) {
                Expert expert = new Expert();
                expert.setId(rsEtude.getInt("expert_id"));
                expert.setNom(rsEtude.getString("expert_nom"));
                expert.setPrenom(rsEtude.getString("expert_prenom"));

                Etude etude = new Etude();
                etude.setId(rsEtude.getInt("id"));
                etude.setDateR(rsEtude.getDate("date_r").toLocalDate());
                etude.setExpert(expert);
                etude.setCulture(culture);
                etude.setIrrigation(rsEtude.getBoolean("irrigation"));
                etude.setFertilisation(rsEtude.getBoolean("fertilisation"));
                etude.setPrix(rsEtude.getFloat("prix"));
                etude.setRendement(rsEtude.getFloat("rendement"));
                etude.setPrecipitations(rsEtude.getFloat("precipitations"));
                etude.setMainOeuvre(rsEtude.getFloat("main_oeuvre"));

                // Conversion sécurisée de Climat
                try {
                    etude.setClimat(Climat.valueOf(rsEtude.getString("climat").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    etude.setClimat(null); // ou log
                }

                // Conversion sécurisée de TypeSol
                try {
                    String solStr = rsEtude.getString("type_sol").toUpperCase();
                    for (TypeSol ts : TypeSol.values()) {
                        if (ts.toString().equalsIgnoreCase(solStr)) {
                            etude.setTypeSol(ts);
                            break;
                        }
                    }
                } catch (Exception e) {
                    etude.setTypeSol(null); // ou log
                }

                culture.getEtudes().add(etude);
            }

            cultures.add(culture);
        }

        return cultures;
    }

}
