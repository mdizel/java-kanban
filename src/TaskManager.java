import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 10000;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public int createId() {
        id++;
        return id;
    }

      public void setTask(Task task) {                     // загружаем задачи
       id = createId();
        task.setId(id);
        tasks.put(id, task);
          }

    public void setEpic(Epic epic) {                        // загружаем эпики
        id = createId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void setSubTask(SubTask subTask) {                 // загружаем подзадачи
        id = createId();
        subTask.setId(id);
        int parentsId = subTask.getParentsId();
        addSubTaskToEpic(subTask, parentsId);
    }

    public ArrayList<Epic> getEpicsList() {                      //Выводим все эпики списком
        ArrayList<Epic> epicsList = new ArrayList<>(epics.values());
        return epicsList;
    }

    public ArrayList<Task> getTasksList() {                     //Выводим все задачи списком
        ArrayList<Task> tasksList = new ArrayList<>(tasks.values());
        return tasksList;
    }

    public ArrayList<SubTask> getSubtasksList() {                      //Выводим все подзадачи списком
        ArrayList<SubTask> subtasksList = new ArrayList<>(getAllSubtask().values());
        return subtasksList;
    }

    public void changeTask(Task task) {                                    // заменяем задачу
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        tasks.put(id, task);
    }

    public void changeEpic(Epic epic) {                                  // заменяем эпик
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        epics.put(id, epic);
    }

    public void changeSubTask(SubTask subTask) {                           // заменяем подзадачу
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

    public ArrayList<SubTask> getSubtaskFromEpicList(int epicId) {          //Подзадачи отдельного Эпика списком
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
        ArrayList<SubTask> subtaskFromEpicList = new ArrayList<>(subtasks.values());
        return subtaskFromEpicList;
    }


    public Task getTask(int id) {                                         // Получаем задачу по Id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return new Task("none", "none",Status.DONE);
        }
        return tasks.get(id);
    }

    public Epic getEpic(int id) {                                         // Получаем Эпик по Id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
        }
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {                                         // Получаем подзадачу по Id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return new SubTask("none", "none", Status.DONE, -1);
        }
        return getAllSubtask().get(id);
    }

    public void deleteAllTasks(){                                       //удаляем все задачи
        tasks.clear();
    }
    public void deleteAllEpics(){                                      //удаляем все эпики
        epics.clear();
    }

    public void deleteAllSubTasks(){                                   //удаляем все подзадачи
        for (Integer epicId : epics.keySet()) {
            HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
            subtasks.clear();
            epics.get(epicId).setSubtacks(subtasks);
        }
    }
    public void deleteTask(int id){                                          // Удаляем задачу по id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        tasks.remove(id);
    }

     public void deleteEpic(int id){                                          // Удаляем эпик по id
    if (!getTaskAndSubtasks().containsKey(id)) {
        System.out.println("Id " + id + " dont exist");
        return;
    }
    epics.remove(id);
    }

    public void deleteSubTask(int id){                                          // Удаляем подзадачу по id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        SubTask subTask = getAllSubtask().get(id);
        int parentsId = subTask.getParentsId();
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
        subtasks.remove(id);
        checkStatus(parentsId);
        Epic epic = epics.get(parentsId);
        epic.setSubtacks(subtasks);
    }

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
    public HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId) {  //все подзадачи эпика списком в 78 строке
        Epic epic = epics.get(parentsId);                                 //используется упрошения обращения к позадачам
        return epic.getSubtacks();
    }

    private HashMap<Integer, Task> getTaskAndSubtasks() {
        HashMap<Integer, Task> taskAndSubtasks = new HashMap<>();
        taskAndSubtasks.putAll(tasks);
        taskAndSubtasks.putAll(epics);
        taskAndSubtasks.putAll(getAllSubtask());
        return taskAndSubtasks;
    }
}


