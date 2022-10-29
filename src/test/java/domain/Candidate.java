package domain;
import java.util.List;

public class Candidate {
    private boolean readyForWork;
    private List<String> languages;
    private String job;
    private String name;
    private Integer age;

    public boolean isReadyForWork() {
        return readyForWork;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }
    public Integer getAge(){
        return age;
    }
}
