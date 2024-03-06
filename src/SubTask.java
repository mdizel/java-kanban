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
        return typeOfTask +
                "  Код: " + id +
                "  К эпику N " + parentsId +
                "  Название: " + name +
                "  Описание: " + description +
                "  Статус: " + status + "\n";
    }
}
