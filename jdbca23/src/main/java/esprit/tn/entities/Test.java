package esprit.tn.entities;

import java.util.Objects;

public class Test {
    private int id;
    private String heureDuTest;
    private String nomMatiere;
    private Type type;

    public Test() {
        // Constructeur par défaut
    }

    public Test(int id, String heureDuTest, String nomMatiere, Type type) {
        this.id = id;
        this.heureDuTest = heureDuTest;
        this.nomMatiere = nomMatiere;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeureDuTest() {
        return heureDuTest;
    }

    public void setHeureDuTest(String heureDuTest) {
        this.heureDuTest = heureDuTest;
    }

    public String getNomMatiere() {
        return nomMatiere;
    }

    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return id == test.id &&
                Objects.equals(heureDuTest, test.heureDuTest) &&
                Objects.equals(nomMatiere, test.nomMatiere) &&
                Objects.equals(type, test.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, heureDuTest, nomMatiere, type);
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", heureDuTest='" + heureDuTest + '\'' +
                ", nomMatiere='" + nomMatiere + '\'' +
                ", type=" + type +
                '}';
    }
} 