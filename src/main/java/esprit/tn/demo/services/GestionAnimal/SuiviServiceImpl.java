package esprit.tn.demo.services.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Suivi;
import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import esprit.tn.demo.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuiviServiceImpl implements ISuiviService {

    private static final Logger logger = Logger.getLogger(SuiviServiceImpl.class.getName());
    private final Connection connection;
    private final VeterinaireServiceImpl veterinaireService = new VeterinaireServiceImpl();

    public SuiviServiceImpl() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void addSuivi(Suivi suivi) {
        String query = "INSERT INTO suivi (id_animal, temperature, rythme_cardiaque, etat, id_client, analysis, veterinaire_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, suivi.getAnimal().getId());
            statement.setFloat(2, suivi.getTemperature());
            statement.setFloat(3, suivi.getRythmeCardiaque());
            statement.setString(4, suivi.getEtat());
            statement.setInt(5, suivi.getIdClient());
            statement.setString(6, suivi.getAnalysis());
            statement.setInt(7, suivi.getVeterinaire().getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    suivi.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding suivi", e);
        }
    }

    @Override
    public void updateSuivi(Suivi updatedSuivi) {
        String query = "UPDATE suivi SET id_animal = ?, temperature = ?, rythme_cardiaque = ?, etat = ?, id_client = ?, analysis = ?, veterinaire_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, updatedSuivi.getAnimal().getId());
            statement.setFloat(2, updatedSuivi.getTemperature());
            statement.setFloat(3, updatedSuivi.getRythmeCardiaque());
            statement.setString(4, updatedSuivi.getEtat());
            statement.setInt(5, updatedSuivi.getIdClient());
            statement.setString(6, updatedSuivi.getAnalysis());
            statement.setInt(7, updatedSuivi.getVeterinaire().getId());
            statement.setInt(8, updatedSuivi.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating suivi", e);
        }
    }

    @Override
    public void deleteSuivi(int id) {
        String query = "DELETE FROM suivi WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting suivi with id: " + id, e);
        }
    }

    @Override
    public List<Suivi> getAllSuivis() {
        List<Suivi> suivis = new ArrayList<>();
        String query = "SELECT * FROM suivi";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int animalId = resultSet.getInt("id_animal");
                float temperature = resultSet.getFloat("temperature");
                float rythmeCardiaque = resultSet.getFloat("rythme_cardiaque");
                String etat = resultSet.getString("etat");
                int idClient = resultSet.getInt("id_client");
                String analysis = resultSet.getString("analysis");
                int vetId = resultSet.getInt("veterinaire_id");

                Animal animal = getAnimalById(animalId);
                Veterinaire vet = veterinaireService.getVeterinaireById(vetId);

                if (animal != null && vet != null) {
                    Suivi suivi = new Suivi(id, animal, temperature, rythmeCardiaque, etat, idClient, analysis, vet);
                    suivis.add(suivi);
                } else {
                    logger.warning("Animal or Veterinaire not found for suivi id: " + id);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all suivis", e);
        }
        return suivis;
    }

    @Override
    public Suivi getSuiviById(int id) {
        String query = "SELECT * FROM suivi WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int animalId = resultSet.getInt("id_animal");
                    float temperature = resultSet.getFloat("temperature");
                    float rythmeCardiaque = resultSet.getFloat("rythme_cardiaque");
                    String etat = resultSet.getString("etat");
                    int idClient = resultSet.getInt("id_client");
                    String analysis = resultSet.getString("analysis");
                    int vetId = resultSet.getInt("veterinaire_id");

                    Animal animal = getAnimalById(animalId);
                    Veterinaire vet = veterinaireService.getVeterinaireById(vetId);

                    if (animal != null && vet != null) {
                        return new Suivi(id, animal, temperature, rythmeCardiaque, etat, idClient, analysis, vet);
                    } else {
                        logger.warning("Animal or Veterinaire not found for suivi id: " + id);
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving suivi with id: " + id, e);
        }
        return null;
    }

    private Animal getAnimalById(int animalId) {
        String query = "SELECT * FROM animal WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, animalId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    String type = resultSet.getString("type");
                    String race = resultSet.getString("race");
                    int age = resultSet.getInt("age");
                    float poids = resultSet.getFloat("poids");

                    return new Animal(animalId, nom, type, age, poids, race);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving animal with id: " + animalId, e);
        }
        return null;
    }
}
