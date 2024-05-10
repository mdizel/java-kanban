import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected TypeOfTask typeOfTask;
    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        typeOfTask = TypeOfTask.TASK;
    }

    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        typeOfTask = TypeOfTask.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        typeOfTask = TypeOfTask.TASK;
    }

    public Task(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        typeOfTask = TypeOfTask.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    protected LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getDurationValue() {
        if (duration != null) {
            return duration.toMinutes();
        }
        return 0;
    }

    public String getStartTimeValue() {
        if (startTime != null) {
            return startTime.format(FORMATTER);
        }
        return null;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        if (!(object instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s\n", id, typeOfTask, name, status, description,
                getDurationValue(),
                getStartTimeValue());
    }
}
