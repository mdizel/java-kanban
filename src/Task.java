public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected TypeOfTask typeOfTask;
    protected int id;

    public Task(String name, String description) {
        id = TaskManager.getId();                           // Метод по проставлению ID и его логика находятся в
        this.name = name;                                   // TaskManager. Здесь он только вызывается.
        this.description = description;
        typeOfTask = TypeOfTask.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypeOfTask getTypeOfTask() {
        return typeOfTask;
    }

    @Override
    public String toString() {
        return typeOfTask +
                "    Код: " + id + ",\n" +
                "    Название: " + name + ",\n" +
                "    Описание: " + description + ",\n" +
                "    Статус: " + status + "\n";
    }
}
