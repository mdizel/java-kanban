import java.util.HashMap;

public class TaskManager {
    private static int id = 10000;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    private int lastEpicId = 0;

    public static int getId() {
        id++;
        return id;
    }

    public void setAnyTask(Object object) {          //Загружаем задачи, эпики загружаются с подзадачами, если они идут
        if (object instanceof Epic) {                  //за эпиками, иначе подзадачи загружаются к определенному эпику
            int id = ((Epic) object).getId();
            lastEpicId = id;
            ((Epic) object).setStatus(Status.NEW);
            epics.put(id, (Epic) object);
        } else if (object instanceof SubTask) {
            int parentsId = ((SubTask) object).getParentsId();
            if (parentsId != 0) {
                parentsId = ((SubTask) object).getParentsId();
            } else {
                parentsId = lastEpicId;
                ((SubTask) object).setParentsId(lastEpicId);
            }
            addSubTaskToEpic((SubTask) object, parentsId);
        } else if (object instanceof Task) {
            int id = ((Task) object).getId();
            lastEpicId = 0;
            tasks.put(id, (Task) object);
            ((Task) object).setStatus(Status.NEW);
        } else {
            System.out.println("Error: wrong object type. " + object.getClass() + " don't expected here.");
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTaskAndSubtasks() {                   //Получаем одноуровневый список всех задач
        HashMap<Integer, Task> taskAndSubtasks = new HashMap<>();
        taskAndSubtasks.putAll(tasks);
        taskAndSubtasks.putAll(epics);
        taskAndSubtasks.putAll(getAllSubtask());
        return taskAndSubtasks;
    }

    public void changeTask(int id, Task task) {                         //заменяем задачу
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        task.setId(id);
        if (task instanceof SubTask) {
            Task oldTask = getTaskAndSubtasks().get(id);
            int parentsId = ((SubTask) oldTask).getParentsId();
            ((SubTask) task).setParentsId(parentsId);
            HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
            subtasks.put(id, (SubTask) task);
            subtasks.get(id).setStatus(Status.NEW);
            checkStatus(parentsId);
            Epic epic = epics.get(parentsId);
            epic.setSubtacks(subtasks);
        } else if (task instanceof Epic) {
            epics.put(id, (Epic) task);
            epics.get(id).setStatus(Status.NEW);
        } else {
            tasks.put(id, task);
            tasks.get(id).setStatus(Status.NEW);
        }
    }

    public void addSubTaskToEpic(SubTask subtask, int parentsId) {        // Добавляем подзадачу к Эпику
        if (!epics.containsKey(parentsId)) {
            System.out.println("For subtask named: \"" + subtask.getName() + "\" hasn't any Epics.");
            return;
        }
        int id = subtask.getId();
        Epic epic = epics.get(parentsId);
        subtask.setParentsId(parentsId);
        subtask.setStatus(Status.NEW);
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

    public HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId) {      //Выводим подзадачи отдельного Эпика
        Epic epic = epics.get(parentsId);
        return epic.getSubtacks();
    }

    public Task getTask(int id) {                                            // Получаем задачу по Id
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
        }
        return getTaskAndSubtasks().get(id);
    }

    public void deleteAllTask(TypeOfTask type) {                      // удаляем все задачи по типу
        if (type == TypeOfTask.SUBTASK) {
            for (Integer epicId : epics.keySet()) {
                HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
                subtasks.clear();
                epics.get(epicId).setSubtacks(subtasks);
            }
        } else if (type == TypeOfTask.EPIC) {
            epics.clear();
        } else {
            tasks.clear();
        }
    }

    public void deleteTask(int id) {                                          // Удаляем задачу
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        if (getTaskAndSubtasks().get(id).typeOfTask == TypeOfTask.SUBTASK) {
            Task task = getTaskAndSubtasks().get(id);
            int parentsId = ((SubTask) task).getParentsId();
            HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
            subtasks.remove(id);
            checkStatus(parentsId);
            Epic epic = epics.get(parentsId);
            epic.setSubtacks(subtasks);
        } else if (getTaskAndSubtasks().get(id).typeOfTask == TypeOfTask.EPIC) {
            epics.remove(id);
        } else {
            tasks.remove(id);
        }
    }

    public void setChangeStatus(int id, Status newStatus) {                  //Меняем статус статус задачи
        if (!getTaskAndSubtasks().containsKey(id)) {
            System.out.println("Error: incorrect Id");
        } else {
            Task task = getTaskAndSubtasks().get(id);
            if (task.getTypeOfTask() == TypeOfTask.SUBTASK) {
                task.setStatus(newStatus);
                int parentsId = ((SubTask) task).getParentsId();
                checkStatus(parentsId);
            } else if (task.getTypeOfTask() == TypeOfTask.TASK) {
                task.setStatus(newStatus);
            } else {
                System.out.println("You can't change Epic status");
            }
        }
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
}


