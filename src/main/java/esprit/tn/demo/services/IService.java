package esprit.tn.demo.services;

import esprit.tn.demo.entities.GestionAnimal.Animal;
import java.util.List;

public interface IService <T> {
    void add(T t);
    void update(T t);
    void delete(T t);

}
