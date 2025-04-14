package esprit.tn.demo.services.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import esprit.tn.demo.services.IService;
import esprit.tn.demo.tools.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalService implements IService<Animal> {
    private Connection cnx;

    public AnimalService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Animal animal) {
        String sql = "INSERT INTO animal (nom, type, age, poids, race) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, animal.getNom());
            pst.setString(2, animal.getType());
            pst.setInt(3, animal.getAge());
            pst.setFloat(4, animal.getPoids());
            pst.setString(5, animal.getRace());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    animal.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding animal: " + e.getMessage());
        }
    }

    @Override
    public void update(Animal animal) {
        String sql = "UPDATE animal SET nom = ?, type = ?, age = ?, poids = ?, race = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, animal.getNom());
            pst.setString(2, animal.getType());
            pst.setInt(3, animal.getAge());
            pst.setFloat(4, animal.getPoids());
            pst.setString(5, animal.getRace());
            pst.setInt(6, animal.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating animal: " + e.getMessage());
        }
    }

    @Override
    public void delete(Animal animal) {
        String sql = "DELETE FROM animal WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, animal.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting animal: " + e.getMessage());
        }
    }

    public List<Animal> getAll() {
        List<Animal> animals = new ArrayList<>();
        String sql = "SELECT * FROM animal";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Animal animal = new Animal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getInt("age"),
                        rs.getFloat("poids"),
                        rs.getString("race")
                );
                animals.add(animal);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving animals: " + e.getMessage());
        }

        return animals;
    }

    public Animal getById(int id) {
        String sql = "SELECT * FROM animal WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Animal(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getInt("age"),
                            rs.getFloat("poids"),
                            rs.getString("race")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving animal: " + e.getMessage());
        }
        return null;
    }
}