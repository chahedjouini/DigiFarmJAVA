package services;

import data.MyDataBase;
import entities.Abonnement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService implements IService<Abonnement> {

    private final Connection connection;

    public AbonnementService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Abonnement abonnement) throws SQLException {
        String sql = "INSERT INTO abonnement (idc, nom, prenom, numero, typeabb, dureeabb, prix) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, abonnement.getIdc());
        ps.setString(2, abonnement.getNom());
        ps.setString(3, abonnement.getPrenom());
        ps.setInt(4, abonnement.getNumero());
        ps.setString(5, abonnement.getTypeabb());
        ps.setInt(6, abonnement.getDureeabb());
        ps.setFloat(7, abonnement.getPrix());
        ps.executeUpdate();
    }

    @Override
    public void update(Abonnement abonnement) throws SQLException {
        String sql = "UPDATE abonnement SET idc=?, nom=?, prenom=?, numero=?, typeabb=?, dureeabb=?, prix=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, abonnement.getIdc());
        ps.setString(2, abonnement.getNom());
        ps.setString(3, abonnement.getPrenom());
        ps.setInt(4, abonnement.getNumero());
        ps.setString(5, abonnement.getTypeabb());
        ps.setInt(6, abonnement.getDureeabb());
        ps.setFloat(7, abonnement.getPrix());
        ps.setInt(8, abonnement.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM abonnement WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Abonnement> select() throws SQLException {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnement";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Abonnement abonnement = new Abonnement();
            abonnement.setId(rs.getInt("id"));
            abonnement.setIdc(rs.getInt("idc"));  // id_user
            abonnement.setNom(rs.getString("nom"));
            abonnement.setPrenom(rs.getString("prenom"));
            abonnement.setNumero(rs.getInt("numero"));
            abonnement.setTypeabb(rs.getString("typeabb"));
            abonnement.setDureeabb(rs.getInt("dureeabb"));
            abonnement.setPrix(rs.getFloat("prix"));

            abonnements.add(abonnement);
        }

        return abonnements;
    }
}
