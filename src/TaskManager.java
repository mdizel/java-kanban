import java.util.*;

public interface TaskManager {
    int createId();

    void setTask(Task task);

    void setEpic(Epic epic);

    void setSubTask(SubTask subTask);

    ArrayList<Epic> getEpicsList();

    ArrayList<Task> getTasksList();

    ArrayList<SubTask> getSubtasksList();

    boolean changeTask(Task task);

    boolean changeEpic(Epic epic);

    boolean changeSubTask(SubTask subTask);

    ArrayList<SubTask> getSubtaskFromEpicList(int epicId);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    boolean deleteTask(int id);

    boolean deleteEpic(int id);

    boolean deleteSubTask(int id);

    void checkStatus(int parentsId);

    HashMap<Integer, SubTask> getAllSubtask();

    HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId);

    void addSubTaskToEpic(SubTask subtask, int parentsId);

    LinkedHashMap<Integer, Task> getTaskAndSubtasks();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}

