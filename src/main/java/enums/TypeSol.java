package enums;

public enum TypeSol {
    ARGILEUX("ARGILEUX"),
    SABLEUX("SABLEUX"),
    LIMONEUX("LIMONEUX");

    private final String label;

    TypeSol(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
