package enums;

public enum Dispo {
    DISPONIBLE("disponible"),
    NON_DISPONIBLE("non disponible");

    private final String label;

    Dispo(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
