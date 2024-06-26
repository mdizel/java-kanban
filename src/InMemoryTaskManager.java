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
    public void setTask(Task task) throws TaskTimeException {
        id = createId();
        task.setId(id);
        if (task.getStartTime() != null && checkTaskCrossing(task)) {
            throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + task.getName() + "\""
                    + " конфликтуют со сроками других задач");
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            tasksTimeSorted.add(task);
        }
    }


    @Override
    public void setEpic(Epic epic) {
        id = createId();
        epic.setId(id);
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            tasksTimeSorted.add(epic);
        }
    }

    @Override
    public void setSubTask(SubTask subTask) {
        id = createId();
        subTask.setId(id);
        int parentsId = subTask.getParentsId();
        if (subTask.getStartTime() != null && checkTaskCrossing(subTask)) {
            throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + subTask.getName() + "\""
                    + " конфликтуют со сроками других задач");
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
    public boolean changeTask(Task task) {
        if (task instanceof Epic || task instanceof SubTask) {
            System.out.println("Wrong object type");
            return false;
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
        }
        if (task.getStartTime() != null) {
            tasksTimeSorted.remove(tasks.get(id));
            if (checkTaskCrossing(task)) {
                throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + task.getName() + "\""
                        + " конфликтуют со сроками других задач");
            }
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            tasksTimeSorted.add(task);
        }
        return true;
    }


    @Override
    public boolean changeEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
        }
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            tasksTimeSorted.remove(epics.get(id));
            tasksTimeSorted.add(epic);
        }
        return true;
    }

    @Override
    public boolean changeSubTask(SubTask subTask) {
        int id = subTask.getId();
        if (!getAllSubtask().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
        }
        int parentsId = (subTask.getParentsId());
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(parentsId);
        if (subTask.getStartTime() != null) {
            tasksTimeSorted.remove(subtasks.get(id));
            if (checkTaskCrossing(subTask)) {
                throw new TaskTimeException("Сроки исполнения задачи: " + "\"" + subTask.getName() + "\""
                        + " конфликтуют со сроками других задач");
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
        return true;
    }


    @Override
    public ArrayList<SubTask> getSubtaskFromEpicList(int epicId) {
        HashMap<Integer, SubTask> subtasks = getSubtaskFromEpic(epicId);
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
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
    public Epic getEpic(int id) {
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
    public SubTask getSubTask(int id) {
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
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            memHisManager.remove(taskId);
            if (tasks.get(taskId).getStartTime() != null) {
                tasksTimeSorted.remove(tasks.get(taskId));
            }
        }
        tasks.clear();

    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            memHisManager.remove(epicId);
            if (epics.get(epicId).getStartTime() != null) {
                tasksTimeSorted.remove(epics.get(epicId));
            }
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
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
    public boolean deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
        }
        if (tasks.get(id).getStartTime() != null) {
            tasksTimeSorted.remove(tasks.get(id));
        }
        tasks.remove(id);
        memHisManager.remove(id);
        return true;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
        }
        if (epics.get(id).getStartTime() != null) {
            tasksTimeSorted.remove(epics.get(id));
        }
        epics.remove(id);
        memHisManager.remove(id);
        return true;
    }

    @Override
    public boolean deleteSubTask(int id) {
        if (!getAllSubtask().containsKey(id)) {
            System.out.println("Id " + id + " dont exist");
            return false;
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
        return true;
    }

    protected void checkDuration(int parentsId) {
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


    protected void checkStartTime(int parentsId) {
        Epic epic = epics.get(parentsId);
        ArrayList<SubTask> subtasks = getSubtaskFromEpicList(parentsId);
        if (!subtasks.isEmpty()) {
            LocalDateTime startTime = (subtasks.stream()
                    .filter(subTask -> subTask.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime))
                    .map(Task::getStartTime)
                    .orElse(epic.getStartTime()));
            epic.setStartTime(startTime);
        }
    }

    @Override
    public void checkStatus(int parentsId) {
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

    public void addSubTaskToEpic(SubTask subtask, int parentsId) {
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

    public HashMap<Integer, SubTask> getAllSubtask() {
        HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
        for (Epic epic : epics.values()) {
            if (!epic.getSubtacks().isEmpty()) {
                allSubtasks.putAll(epic.getSubtacks());
            }
        }
        return allSubtasks;
    }

    public HashMap<Integer, SubTask> getSubtaskFromEpic(int parentsId) {
        Epic epic = epics.get(parentsId);
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


