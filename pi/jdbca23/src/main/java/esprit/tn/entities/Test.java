package esprit.tn.entities;

public class Test {
    private Integer id;
    private Integer heureDuTest;
    private String nomMatiere;
    private String nomFormateur;
    private String nomSalle;
    private Double coefficient;

    // Constructeur par défaut
    public Test() {
    }

    // Constructeur pour la création
    public Test(Integer heureDuTest, String nomMatiere, String nomFormateur, String nomSalle, Double coefficient) {
        setHeureDuTest(heureDuTest);
        setNomMatiere(nomMatiere);
        setNomFormateur(nomFormateur);
        setNomSalle(nomSalle);
        setCoefficient(coefficient);
    }

    // Constructeur pour la modification
    public Test(Integer id, Integer heureDuTest, String nomMatiere, String nomFormateur, String nomSalle, Double coefficient) {
        this(heureDuTest, nomMatiere, nomFormateur, nomSalle, coefficient);
        this.id = id;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getHeureDuTest() {
        return heureDuTest;
    }

    public String getNomMatiere() {
        return nomMatiere;
    }

    public String getNomFormateur() {
        return nomFormateur;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    // Setters avec validation
    public void setId(Integer id) {
        this.id = id;
    }

    public void setHeureDuTest(Integer heureDuTest) {
        if (heureDuTest == null || heureDuTest < 8 || heureDuTest > 18) {
            throw new IllegalArgumentException("L'heure du test doit être comprise entre 8 et 18");
        }
        this.heureDuTest = heureDuTest;
    }

    public void setNomMatiere(String nomMatiere) {
        if (nomMatiere == null || nomMatiere.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la matière ne peut pas être vide");
        }
        this.nomMatiere = nomMatiere.trim();
    }

    public void setNomFormateur(String nomFormateur) {
        if (nomFormateur == null || nomFormateur.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du formateur ne peut pas être vide");
        }
        this.nomFormateur = nomFormateur.trim();
    }

    public void setNomSalle(String nomSalle) {
        if (nomSalle == null || nomSalle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }
        this.nomSalle = nomSalle.trim();
    }

    public void setCoefficient(Double coefficient) {
        if (coefficient == null || coefficient < 0.5 || coefficient > 5.0) {
            throw new IllegalArgumentException("Le coefficient doit être compris entre 0.5 et 5.0");
        }
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return String.format("Test{id=%d, heure=%dh, matière='%s', formateur='%s', salle='%s', coefficient=%.1f}",
                id, heureDuTest, nomMatiere, nomFormateur, nomSalle, coefficient);
    }
}