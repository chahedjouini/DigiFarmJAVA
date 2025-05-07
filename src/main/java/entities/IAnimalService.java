package services;

import entities.Animal;
import java.util.List;

public interface IAnimalService {
    void addAnimal(Animal animal);
    void updateAnimal(Animal animal);
    void deleteAnimal(int id);
    List<Animal> getAllAnimals();
    Animal getAnimalById(int id);
}
