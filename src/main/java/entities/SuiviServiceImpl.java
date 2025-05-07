package services;
import data.MyDataBase;

import entities.Animal;
import entities.Suivi;
import entities.Veterinaire;
import data.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuiviServiceImpl implements ISuiviService {

    private static final Logger logger = Logger.getLogger(SuiviServiceImpl.class.getName());
    private final Connection connection;
    private final IAnimalService animalService;
    private final VeterinaireService veterinaireService;

    public SuiviServiceImpl() {
        this.connection = MyDataBase.getInstance().getConnection();
        this.animalService = new AnimalServiceImpl();
        this.veterinaireService = new VeterinaireServiceImpl();
    }

    @Override
    public void addSuivi(Suivi s) {
        String query = "INSERT INTO suivi (id_animal, temperature, rythme_cardiaque, etat, id_client, analysis, veterinaire_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, s.getAnimal().getId());
            statement.setFloat(2, s.getTemperature());
            statement.setFloat(3, s.getRythmeCardiaque());
            statement.setString(4, s.getEtat());
            statement.setInt(5, s.getIdClient());
            statement.setString(6, s.getAnalysis());
            statement.setInt(7, s.getVeterinaire().getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    s.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding Suivi", e);
        }
    }

    @Override
    public void updateSuivi(Suivi s) {
        String query = "UPDATE suivi SET id_animal = ?, temperature = ?, rythme_cardiaque = ?, etat = ?, id_client = ?, analysis = ?, veterinaire_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, s.getAnimal().getId());
            statement.setFloat(2, s.getTemperature());
            statement.setFloat(3, s.getRythmeCardiaque());
            statement.setString(4, s.getEtat());
            statement.setInt(5, s.getIdClient());
            statement.setString(6, s.getAnalysis());
            statement.setInt(7, s.getVeterinaire().getId());
            statement.setInt(8, s.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating Suivi", e);
        }
    }

    @Override
    public void deleteSuivi(int id) {
        String query = "DELETE FROM suivi WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting Suivi with id: " + id, e);
        }
    }

    @Override
    public List<Suivi> getAllSuivis() {
        List<Suivi> suivis = new ArrayList<>();
        String query = "SELECT * FROM suivi";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Suivi suivi = new Suivi();
                suivi.setId(rs.getInt("id"));
                suivi.setTemperature(rs.getFloat("temperature"));
                suivi.setRythmeCardiaque(rs.getFloat("rythme_cardiaque"));
                suivi.setEtat(rs.getString("etat"));
                suivi.setIdClient(rs.getInt("id_client"));
                suivi.setAnalysis(rs.getString("analysis"));

                int animalId = rs.getInt("id_animal");
                Animal animal = animalService.getAnimalById(animalId);
                if (animal != null) {
                    suivi.setAnimal(animal);
                }

                int veterinaireId = rs.getInt("veterinaire_id");
                Veterinaire veterinaire = veterinaireService.getVeterinaireById(veterinaireId);
                if (veterinaire != null) {
                    suivi.setVeterinaire(veterinaire);
                } else {
                    logger.warning("Animal or Veterinaire not found for suivi id: " + suivi.getId());
                }

                suivis.add(suivi);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all Suivis", e);
        }
        return suivis;
    }

    // Nouvelle méthode pour récupérer un Suivi par ID
    public Suivi getSuiviById(int id) {
        String query = "SELECT * FROM suivi WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Suivi suivi = new Suivi();
                    suivi.setId(rs.getInt("id"));
                    suivi.setTemperature(rs.getFloat("temperature"));
                    suivi.setRythmeCardiaque(rs.getFloat("rythme_cardiaque"));
                    suivi.setEtat(rs.getString("etat"));
                    suivi.setIdClient(rs.getInt("id_client"));
                    suivi.setAnalysis(rs.getString("analysis"));

                    int animalId = rs.getInt("id_animal");
                    Animal animal = animalService.getAnimalById(animalId);
                    if (animal != null) {
                        suivi.setAnimal(animal);
                    }

                    int veterinaireId = rs.getInt("veterinaire_id");
                    Veterinaire veterinaire = veterinaireService.getVeterinaireById(veterinaireId);
                    if (veterinaire != null) {
                        suivi.setVeterinaire(veterinaire);
                    } else {
                        logger.warning("Animal or Veterinaire not found for suivi id: " + suivi.getId());
                    }

                    return suivi;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving Suivi with id: " + id, e);
        }
        return null;
    }
}