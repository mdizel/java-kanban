import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public interface TaskManager {
    int createId();

    void setTask(Task task);

    void setEpic(Epic epic);

    void setSubTask(SubTask subTask);

    ArrayList<Epic> getEpicsList();

    ArrayList<Task> getTasksList();

    ArrayList<SubTask> getSubtasksList();

    void changeTask(Task task);

    void changeEpic(Epic epic);

    void changeSubTask(SubTask subTask);

    ArrayList<SubTask> getSubtaskFromEpicList(int epicId);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    void checkStatus(int parentsId);
    //void checkDuration(int parentsId);

   // void checkStartTime(int parentsId);

    HashMap<Integer, SubTask> getAllSubtask();

    HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId);

    void addSubTaskToEpic(SubTask subtask, int parentsId);

    LinkedHashMap<Integer, Task> getTaskAndSubtasks();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks(Map<Integer, Task> taskAndSubTasks);
}

