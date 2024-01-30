public class SubTask extends Task {

    private int parentsId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name, String description, int parentsId) {
        super(name, description);
        this.parentsId = parentsId;
    }

    public int getParentsId() {
        return parentsId;
    }

    public void setParentsId(int parentsId) {
        this.parentsId = parentsId;
        typeOfTask = TypeOfTask.SUBTASK;
    }

    @Override
    public String toString() {
        return typeOfTask +
                "    Код: " + id + ",\n" +
                "    Подзадача эпика N " + parentsId + ",\n" +
                "    Название: " + name + ",\n" +
                "    Описание: " + description + ",\n" +
                "    Статус: " + status + "\n";
    }
}
