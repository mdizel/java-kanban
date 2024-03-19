import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 10000;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HistoryManager memHisManager = Managers.getDefaultHistory();

    public HistoryManager getMemHisManager() {
        return memHisManager;
    }

    public void addRecentlyOpenTasks(Task task) {
        memHisManager.add(task);
    }

    @Override
    public int createId() {
        id++;
        return id;
    }

    @Override
    public void setTask(Task task) {                     // загружаем задачи
        id = createId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void setEpic(Epic epic) {                        // загружаем эпики
        id = createId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void setSubTask(SubTask subTask) {                 // загружаем подзадачи
        id = createId();
        subTask.setId(id);
        int parentsId = subTask.getParentsId();
        addSubTaskToEpic(subTask, parentsId);
    }

    @Override
    public ArrayList<Epic> getEpicsList() {                      //Выводим все эпики списком
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getTasksList() {                     //Выводим все задачи списком
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubtasksList() {                      //Выводим все подзадачи списком
        return new ArrayList<>(getAllSubtask().values());
    }

    @Override
    public void changeTask(Task task) {                     // заменяем задачу
        if (task instanceof Epic || task instanceof SubTask) {
            System.out.println("Wrong object type");
            return;
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public void changeEpic(Epic epic) {                        // заменяем эпик
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        epics.put(id, epic);
    }

    @Override
    public void changeSubTask(SubTask subTask) {                // заменяем подзадачу
        int id = subTask.getId();
        if (!getAllSubtask().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        int parentsId = (subTask.getParentsId());
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
        subtasks.put(id, subTask);
        checkStatus(parentsId);
    }

    @Override
    public ArrayList<SubTask> getSubtaskFromEpicList(int epicId) {          //Подзадачи отдельного Эпика списком
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {                                         // Получаем задачу по Id
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            addRecentlyOpenTasks(task);
            return task;
        } else {
            System.out.println("Id " + id + " dont exist");
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {                                         // Получаем Эпик по Id
        if (epics.containsKey(id)) {
            Task epic = epics.get(id);
            addRecentlyOpenTasks(epic);
            return epics.get(id);
        } else {
            System.out.println("Id " + id + " dont exist");
            return null;
        }
    }

    @Override
    public SubTask getSubTask(int id) {                                         // Получаем подзадачу по Id
        if (getAllSubtask().containsKey(id)) {
            Task subTask = getAllSubtask().get(id);
            addRecentlyOpenTasks(subTask);
            return getAllSubtask().get(id);
        } else {
            System.out.println("Id " + id + " dont exist");
            return null;
        }
    }

    @Override
    public void deleteAllTasks() {                                       //удаляем все задачи
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {                                      //удаляем все эпики
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {                                   //удаляем все подзадачи
        for (Integer epicId : epics.keySet()) {
            HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
            subtasks.clear();
            epics.get(epicId).setSubtacks(subtasks);
        }
    }

    @Override
    public void deleteTask(int id) {                                          // Удаляем задачу по id
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        tasks.remove(id);
        memHisManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {                                          // Удаляем эпик по id
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        epics.remove(id);
        memHisManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {                                          // Удаляем подзадачу по id
        if (!getAllSubtask().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        SubTask subTask = getAllSubtask().get(id);
        int parentsId = subTask.getParentsId();
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
        subtasks.remove(id);
        memHisManager.remove(id);
        checkStatus(parentsId);
        Epic epic = epics.get(parentsId);
        epic.setSubtacks(subtasks);
    }

    @Override
    public void checkStatus(int parentsId) {                           // Проверка статуса эпика
        int count = 0;
        Epic epic = epics.get(parentsId);
        HashMap<Integer, SubTask> subtasks = epic.getSubtacks();
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask subTask : subtasks.values()) {
                if (subTask.getStatus() == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                } else if (subTask.getStatus() == Status.NEW) {
                    epic.setStatus(Status.NEW);
                    count++;
                } else {
                    epic.setStatus(Status.DONE);
                }
            }
            if (subtasks.size() > count && count != 0) {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
    // доп методы

    public void addSubTaskToEpic(SubTask subtask, int parentsId) {            // Добавляем подзадачу к Эпику
        if (!epics.containsKey(parentsId)) {
            System.out.println("For subtask named: \"" + subtask.getName() + "\" wrong Epic Id.");
            return;
        }
        int id = subtask.getId();
        Epic epic = epics.get(parentsId);
        subtask.setParentsId(parentsId);
        HashMap<Integer, SubTask> subtasks = epic.getSubtacks();
        subtasks.put(id, subtask);
        epic.setSubtacks(subtasks);
        checkStatus(parentsId);
    }

    public HashMap<Integer, SubTask> getAllSubtask() {                 // Выводим подзадачи всех Эпиков
        HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            if (!epic.getSubtacks().isEmpty()) {
                allSubtasks.putAll(epic.getSubtacks());
            }
        }
        return allSubtasks;
    }


    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId) {
        Epic epic = epics.get(parentsId);                                 //используется упрошения обращения к позадачам
        return epic.getSubtacks();
    }


    public LinkedHashMap<Integer, Task> getTaskAndSubtasks() {
        LinkedHashMap<Integer, Task> taskAndSubtasks = new LinkedHashMap<>();
        taskAndSubtasks.putAll(tasks);
        taskAndSubtasks.putAll(epics);
        taskAndSubtasks.putAll(getAllSubtask());
        return taskAndSubtasks;
    }
}


