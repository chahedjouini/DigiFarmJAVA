package enums;

public enum TypeSol {
    ARGILEUX("agrileux"),
    SABLEUX("sableux"),
    LIMONEUX("limoneux");

    private final String label;

    TypeSol(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
