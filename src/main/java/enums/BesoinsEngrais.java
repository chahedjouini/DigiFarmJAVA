package enums;

public enum BesoinsEngrais {
    AZOTE("Azote"),
    PHOSPHORE("Phosphore"),
    POTASSIUM("Potassium"),
    NPK("NPK"),
    COMPOST("Compost"),
    FUMIER("Fumier"),
    UREE("Urée");

    private final String label;

    BesoinsEngrais(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
