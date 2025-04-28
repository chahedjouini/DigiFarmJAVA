package services;

import data.MyDataBase;
import entities.Facture;
import entities.Abonnement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FactureService implements IService<Facture> {

    private final Connection connection;

    public FactureService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Facture facture) throws SQLException {
        String sql = "INSERT INTO facture (abonnement_id, datef, prixt, cin, email) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, facture.getAbonnement().getId());
        ps.setDate(2, Date.valueOf(facture.getDatef()));

        ps.setFloat(3, facture.getPrixt());
        ps.setInt(4, facture.getCin());
        ps.setString(5, facture.getEmail());
        ps.executeUpdate();
    }

    @Override
    public void update(Facture facture) throws SQLException {
        String sql = "UPDATE facture SET abonnement_id = ?, datef = ?, prixt = ?, cin = ?, email = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, facture.getAbonnement().getId());
        ps.setDate(2, Date.valueOf(facture.getDatef()));
        ps.setFloat(3, facture.getPrixt());
        ps.setInt(4, facture.getCin());
        ps.setString(5, facture.getEmail());
        ps.setInt(6, facture.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM facture WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Facture> select() throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String sql = """
            SELECT f.*, a.id AS ab_id, a.nom AS ab_nom, a.typeabb, a.dureeabb, a.prix
            FROM facture f
            JOIN abonnement a ON f.abonnement_id = a.id
        """;

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            // Créer l’abonnement minimal
            Abonnement abonnement = new Abonnement();
            abonnement.setId(rs.getInt("ab_id"));
            abonnement.setNom(rs.getString("ab_nom"));
            abonnement.setTypeabb(rs.getString("typeabb"));
            abonnement.setDureeabb(rs.getInt("dureeabb"));
            abonnement.setPrix(rs.getFloat("prix"));

            // Créer la facture
            Facture facture = new Facture();
            facture.setId(rs.getInt("id"));
            facture.setDatef(rs.getDate("datef").toLocalDate());
            facture.setPrixt(rs.getFloat("prixt"));
            facture.setCin(rs.getInt("cin"));
            facture.setEmail(rs.getString("email"));
            facture.setAbonnement(abonnement);

            factures.add(facture);
        }

        return factures;
    }
}
