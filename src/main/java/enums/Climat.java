package enums;

public enum Climat {
    SEC("sec"),
    HUMIDE("humide"),
    TEMPERE("tempéré");

    private final String label;

    Climat(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
