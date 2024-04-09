import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected int id = 10000;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> tasksTimeSorted =
            new TreeSet<>((task1, task2) -> {
                if (!task1.getStartTime().isEqual(task2.getStartTime())) {
                    return task1.getStartTime().compareTo(task2.getStartTime());
                } else {
                    return task1.getTypeOfTask().compareTo(task2.getTypeOfTask());
                }
            });
    protected HistoryManager memHisManager = Managers.getDefaultHistory();

    @Override
    public Set<Task> getPrioritizedTasks() {
        return tasksTimeSorted;
    }

    private boolean checkTaskCrossing(Task newTask) {
        LocalDateTime startTime = newTask.getStartTime();
        LocalDateTime endTime = newTask.getEndTime();
        Set<Task> tasksTimeSorted = getPrioritizedTasks();
        Optional<Task> crossTask = tasksTimeSorted.stream()
                .filter(task -> startTime.isAfter(task.getStartTime()) && startTime.isBefore(task.getEndTime()) ||
                        startTime.isEqual(task.getStartTime()) ||
                        startTime.isEqual(task.getEndTime()) ||
                        endTime.isAfter(task.getStartTime()) && endTime.isBefore(task.getEndTime()) ||
                        endTime.isEqual(task.getStartTime()) ||
                        endTime.isEqual(task.getEndTime()) ||
                        task.getStartTime().isAfter(startTime) && task.getStartTime().isBefore(endTime) ||
                        task.getEndTime().isAfter(startTime) && task.getEndTime().isBefore(endTime))
                .findFirst();
        return crossTask.isPresent();
    }

    public List<Task> getHistory() {

        return memHisManager.getHistory();
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
        if (task.getStartTime() != null) {
            if (checkTaskCrossing(task)) {
                //System.out.println("Сроки исполнения задачи " + task.getName() + " конфликтует со сроками других задач");
                return;
            }
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            tasksTimeSorted.add(task);
        }
    }


    @Override
    public void setEpic(Epic epic) {                        // загружаем эпики
        id = createId();
        epic.setId(id);
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            tasksTimeSorted.add(epic);
        }
    }

    @Override
    public void setSubTask(SubTask subTask) {                 // загружаем подзадачи
        id = createId();
        subTask.setId(id);
        int parentsId = subTask.getParentsId();
        if (subTask.getStartTime() != null) {
            if (checkTaskCrossing(subTask)) {
                System.out.println("Сроки исполнения подзадачи " + subTask.getName() + " конфликтует со сроками других задач");
                return;
            }
        }
        addSubTaskToEpic(subTask, parentsId);
        if (subTask.getStartTime() != null) {
            tasksTimeSorted.add(subTask);
            Epic epic = epics.get(subTask.getParentsId());
            tasksTimeSorted.add(epic);
        }
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
    public void changeTask(Task task) {                                 // заменяем задачу
        if (task instanceof Epic || task instanceof SubTask) {
            System.out.println("Wrong object type");
            return;
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        if (task.getStartTime() != null) {
            tasksTimeSorted.remove(tasks.get(id));
            try {
                if (checkTaskCrossing(task)) {
                    throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + task.getName() + "\""
                            + " конфликтуют со сроками других задач");
                }
            } catch (TaskTimeException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            tasksTimeSorted.add(task);
        }
    }


    @Override
    public void changeEpic(Epic epic) {                        // заменяем эпик
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return;
        }
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            tasksTimeSorted.remove(epics.get(id));
            tasksTimeSorted.add(epic);
        }
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
        if (subTask.getStartTime() != null) {
            tasksTimeSorted.remove(subtasks.get(id));
            try {
                if (checkTaskCrossing(subTask)) {
                    throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + subTask.getName() + "\""
                            + " конфликтуют со сроками других задач");
                }
            } catch (TaskTimeException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        addSubTaskToEpic(subTask, parentsId);
        subtasks.put(id, subTask);
        if (subTask.getStartTime() != null) {
            tasksTimeSorted.add(subTask);
            Epic epic = epics.get(subTask.getParentsId());
            tasksTimeSorted.add(epic);
        }
        checkStatus(parentsId);
        checkDuration(parentsId);
        checkStartTime(parentsId);
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
        for (Integer taskId : tasks.keySet()) {
            memHisManager.remove(taskId);
            if (tasks.get(taskId).getStartTime() != null) {
                tasksTimeSorted.remove(tasks.get(taskId));
            }
        }
        tasks.clear();

    }

    @Override
    public void deleteAllEpics() {                                      //удаляем все эпики
        for (Integer epicId : epics.keySet()) {
            memHisManager.remove(epicId);
            if (epics.get(epicId).getStartTime() != null) {
                tasksTimeSorted.remove(epics.get(epicId));
            }
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {                                   //удаляем все подзадачи
        for (Integer epicId : epics.keySet()) {
            HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
            for (Integer subTaskId : subtasks.keySet()) {
                memHisManager.remove(subTaskId);
                if (subtasks.get(subTaskId).getStartTime() != null) {
                    tasksTimeSorted.remove(subtasks.get(subTaskId));
                }
            }
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
        if (tasks.get(id).getStartTime() != null) {
            tasksTimeSorted.remove(tasks.get(id));
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
        if (epics.get(id).getStartTime() != null) {
            tasksTimeSorted.remove(epics.get(id));
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
        if (subtasks.get(id).getStartTime() != null) {
            tasksTimeSorted.remove(subtasks.get(id));
        }
        subtasks.remove(id);
        memHisManager.remove(id);
        checkStatus(parentsId);
        checkDuration(parentsId);
        checkStartTime(parentsId);
        Epic epic = epics.get(parentsId);
        epic.setSubtacks(subtasks);
    }

    protected void checkDuration(int parentsId) {                 // проверка продолжительности
        Epic epic = epics.get(parentsId);
        ArrayList<SubTask> subtasks = getSubtaskFromEpicList(parentsId);
        Duration duration = Duration.ofMinutes(0);
        for (SubTask subTask : subtasks) {
            if (subTask.getDuration() != null) {
                duration = duration.plus(subTask.getDuration());
            }
        }
        epic.setDuration(duration);
    }


    protected void checkStartTime(int parentsId) {                // проверка времени начала
        Epic epic = epics.get(parentsId);
        ArrayList<SubTask> subtasks = getSubtaskFromEpicList(parentsId);
        if (!subtasks.isEmpty()) {
            LocalDateTime startTime = (subtasks.stream()
                    .filter(subTask -> subTask.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime))
                    .map(Task::getStartTime)
                    .orElse(epic.getStartTime()));  //если эпик не имеет подзаданий, но время старта задан при его создании
            epic.setStartTime(startTime);
        }
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
        checkDuration(parentsId);
        checkStartTime(parentsId);
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


