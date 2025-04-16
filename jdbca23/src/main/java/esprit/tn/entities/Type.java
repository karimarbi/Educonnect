package esprit.tn.entities;

public class Type {
    private int id;
    private String mode;

    public Type() {
    }

    public Type(int id, String mode) {
        this.id = id;
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                '}';
    }
} 