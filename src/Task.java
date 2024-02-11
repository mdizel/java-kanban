import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected TypeOfTask typeOfTask;
    protected int id;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        typeOfTask = TypeOfTask.TASK;
    }
    public Task(int id, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task)) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
