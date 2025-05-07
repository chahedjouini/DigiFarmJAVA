package services.GestionVente;

import entities.GestionVente.Commande;
import services.ICommandeService;
import data.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements ICommandeService<Commande> {

    private Connection cnx;

    public CommandeService() {
        cnx = MyDataBase.getInstance().getConnection();
    }


    @Override
    public List<Commande> getAll() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Récupère tous les champs de la base de données
                int id = rs.getInt("id");
                String statut = rs.getString("statut");
                float montantTotal = rs.getFloat("montant_total");
                String dateCommande = rs.getString("date_commande");

                // Crée un objet Commande avec les données récupérées
                Commande commande =  new Commande(id,statut, montantTotal,dateCommande);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving commandes: " + e.getMessage());
        }

        return commandes;
    }

    @Override
    public Commande getById(int id) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Récupère tous les champs de la base de données
                    String statut = rs.getString("statut");
                    float montantTotal = rs.getFloat("montant_total");
                    String dateCommande = rs.getString("date_commande");

                    // Crée et retourne un objet Commande avec les données récupérées
                    return new Commande(id,statut, montantTotal,dateCommande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving commande: " + e.getMessage());
        }
        return null;
    }
    public void add(Commande commande) {
        String sql = "INSERT INTO commande (statut, montant_total, date_commande) VALUES (?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, commande.getStatut());
            pst.setFloat(2, commande.getMontantTotal());
            pst.setString(3, commande.getDateCommande());
            pst.executeUpdate();
            System.out.println("✅ Commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }



}
