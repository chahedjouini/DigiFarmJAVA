package esprit.tn.demo.services.GestionAnimal;

import esprit.tn.demo.entities.GestionAnimal.Veterinaire;
import java.util.ArrayList;
import java.util.List;

public class VeterinaireServiceImpl implements VeterinaireService {
    private final List<Veterinaire> veterinaires = new ArrayList<>();

    @Override
    public void addVeterinaire(Veterinaire v) {
        veterinaires.add(v);
    }

    @Override
    public void updateVeterinaire(Veterinaire v) {
        for (int i = 0; i < veterinaires.size(); i++) {
            if (veterinaires.get(i).getId() == v.getId()) {
                veterinaires.set(i, v);
                return;
            }
        }
    }

    @Override
    public void deleteVeterinaire(int id) {
        veterinaires.removeIf(v -> v.getId() == id);
    }

    @Override
    public List<Veterinaire> getAllVeterinaires() {
        return new ArrayList<>(veterinaires);
    }

    @Override
    public Veterinaire getVeterinaireById(int id) {
        return veterinaires.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }
}
