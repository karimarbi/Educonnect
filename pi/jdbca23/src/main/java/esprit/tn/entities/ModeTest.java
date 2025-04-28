package esprit.tn.entities;

public enum ModeTest {
    PRESENTIELLE("présentielle"),
    EN_LIGNE("en ligne");

    private final String value;

    ModeTest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ModeTest fromString(String text) {
        for (ModeTest mode : ModeTest.values()) {
            if (mode.value.equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Mode inconnu: " + text);
    }

    @Override
    public String toString() {
        return value;
    }
}
