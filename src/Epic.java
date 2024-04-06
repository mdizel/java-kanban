import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, SubTask> subtacks = new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        typeOfTask = TypeOfTask.EPIC;
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        typeOfTask = TypeOfTask.EPIC;
    }

    public HashMap<Integer, SubTask> getSubtacks() {
        return subtacks;
    }

    public void setSubtacks(HashMap<Integer, SubTask> subtacks) {
        this.subtacks = subtacks;
    }
}
