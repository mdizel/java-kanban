import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface TaskManager {
    int createId();

    HistoryManager getMemHisManager();

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

    HashMap<Integer, SubTask> getAllSubtask();

    HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId);

    void addSubTaskToEpic(SubTask subtask, int parentsId);

    LinkedHashMap<Integer, Task> getTaskAndSubtasks();
}

