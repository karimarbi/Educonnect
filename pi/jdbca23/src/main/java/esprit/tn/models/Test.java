package esprit.tn.models;

public class Test {
    private int id;
    private String trainerName;
    private String testTime;
    private String subjectName;

    public Test() {
    }

    public Test(String trainerName, String testTime, String subjectName) {
        this.trainerName = trainerName;
        this.testTime = testTime;
        this.subjectName = subjectName;
    }

    public Test(int id, String trainerName, String testTime, String subjectName) {
        this.id = id;
        this.trainerName = trainerName;
        this.testTime = testTime;
        this.subjectName = subjectName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
} 