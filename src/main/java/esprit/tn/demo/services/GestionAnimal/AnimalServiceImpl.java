package esprit.tn.demo.services.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalServiceImpl implements IAnimalService {

    private Connection connection;

    public AnimalServiceImpl() {
        // Use the MyDataBase class to get the DB connection
        this.connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void addAnimal(Animal animal) {
        String query = "INSERT INTO animal (nom, type, race, age, poids) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, animal.getNom());
            statement.setString(2, animal.getType());
            statement.setString(3, animal.getRace());
            statement.setInt(4, animal.getAge());
            statement.setFloat(5, animal.getPoids());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated ID
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    animal.setId(generatedKeys.getInt(1)); // Set the generated ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAnimal(Animal updatedAnimal) {
        String query = "UPDATE animal SET nom = ?, type = ?, race = ?, age = ?, poids = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, updatedAnimal.getNom());
            statement.setString(2, updatedAnimal.getType());
            statement.setString(3, updatedAnimal.getRace());
            statement.setInt(4, updatedAnimal.getAge());
            statement.setFloat(5, updatedAnimal.getPoids());
            statement.setInt(6, updatedAnimal.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAnimal(int id) {
        String query = "DELETE FROM animal WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Animal> getAllAnimals() {
        List<Animal> animalList = new ArrayList<>();
        String query = "SELECT * FROM animal";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String type = resultSet.getString("type");
                String race = resultSet.getString("race");
                int age = resultSet.getInt("age");
                float poids = resultSet.getFloat("poids");

                Animal animal = new Animal(id, nom, type, age, poids, race);
                animalList.add(animal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animalList;
    }

    @Override
    public Animal getAnimalById(int id) {
        String query = "SELECT * FROM animal WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    String type = resultSet.getString("type");
                    String race = resultSet.getString("race");
                    int age = resultSet.getInt("age");
                    float poids = resultSet.getFloat("poids");
                    return new Animal(id, nom, type, age, poids, race);
                } else {
                    throw new RuntimeException("Animal avec ID " + id + " non trouvé.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
