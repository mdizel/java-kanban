public class SubTask extends Task {

    private int parentsId;


    public SubTask(String name, String description, Status status, int parentsId) {
        super(name, description, status);
        this.parentsId = parentsId;
        typeOfTask = TypeOfTask.SUBTASK;
    }

    public SubTask(int id, String name, String description, Status status, int parentsId) {
        super(id, name, description, status);
        this.parentsId = parentsId;
        typeOfTask = TypeOfTask.SUBTASK;
    }

    public int getParentsId() {
        return parentsId;
    }

    public void setParentsId(int parentsId) {
        this.parentsId = parentsId;
    }

    @Override
    public String toString() {
        return String.format("%d;%s;%s;%s;%s;%s\n", id, typeOfTask, name, status, description, parentsId);
    }
}
