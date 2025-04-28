package entities;

import enums.Climat;
import enums.TypeSol;

import java.time.LocalDate;

public class Etude {
    private int id;
    private LocalDate dateR;
    private Culture culture;
    private Expert expert;
    private Climat climat;
    private TypeSol typeSol;
    private boolean irrigation;
    private boolean fertilisation;
    private float prix;
    private float rendement;
    private float precipitations;
    private float mainOeuvre;

    public Etude() {
    }

    public Etude(int id, LocalDate dateR, Culture culture, Expert expert, Climat climat, TypeSol typeSol,
                 boolean irrigation, boolean fertilisation, float prix, float rendement,
                 float precipitations, float mainOeuvre) {
        this.id = id;
        this.dateR = dateR;
        this.culture = culture;
        this.expert = expert;
        this.climat = climat;
        this.typeSol = typeSol;
        this.irrigation = irrigation;
        this.fertilisation = fertilisation;
        this.prix = prix;
        this.rendement = rendement;
        this.precipitations = precipitations;
        this.mainOeuvre = mainOeuvre;
    }

    public Etude(LocalDate dateR, Culture culture, Expert expert, Climat climat, TypeSol typeSol,
                 boolean irrigation, boolean fertilisation, float prix, float rendement,
                 float precipitations, float mainOeuvre) {
        this.dateR = dateR;
        this.culture = culture;
        this.expert = expert;
        this.climat = climat;
        this.typeSol = typeSol;
        this.irrigation = irrigation;
        this.fertilisation = fertilisation;
        this.prix = prix;
        this.rendement = rendement;
        this.precipitations = precipitations;
        this.mainOeuvre = mainOeuvre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateR() { return dateR; }
    public void setDateR(LocalDate dateR) { this.dateR = dateR; }

    public Culture getCulture() { return culture; }
    public void setCulture(Culture culture) { this.culture = culture; }

    public Expert getExpert() { return expert; }
    public void setExpert(Expert expert) { this.expert = expert; }

    public Climat getClimat() { return climat; }
    public void setClimat(Climat climat) { this.climat = climat; }

    public TypeSol getTypeSol() { return typeSol; }
    public void setTypeSol(TypeSol typeSol) { this.typeSol = typeSol; }

    public boolean isIrrigation() { return irrigation; }
    public void setIrrigation(boolean irrigation) { this.irrigation = irrigation; }

    public boolean isFertilisation() { return fertilisation; }
    public void setFertilisation(boolean fertilisation) { this.fertilisation = fertilisation; }

    public float getPrix() { return prix; }
    public void setPrix(float prix) { this.prix = prix; }

    public float getRendement() { return rendement; }
    public void setRendement(float rendement) { this.rendement = rendement; }

    public float getPrecipitations() { return precipitations; }
    public void setPrecipitations(float precipitations) { this.precipitations = precipitations; }

    public float getMainOeuvre() { return mainOeuvre; }
    public void setMainOeuvre(float mainOeuvre) { this.mainOeuvre = mainOeuvre; }

    @Override
    public String toString() {
        return "Etude{" +
                "id=" + id +
                ", dateR=" + dateR +
                ", culture=" + culture +
                ", expert=" + expert +
                ", climat=" + climat +
                ", typeSol=" + typeSol +
                ", irrigation=" + irrigation +
                ", fertilisation=" + fertilisation +
                ", prix=" + prix +
                ", rendement=" + rendement +
                ", precipitations=" + precipitations +
                ", mainOeuvre=" + mainOeuvre +
                '}';
    }
}
