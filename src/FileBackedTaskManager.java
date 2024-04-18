import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    private static final Charset CHARSET = Charset.forName("windows-1251");
    private static final String TITLE = "id,type,name,status,description,epic,duration,startTime\n";

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private void save() {
        try (FileWriter writer = new FileWriter(fileName, CHARSET);
             BufferedWriter bufWriter = new BufferedWriter(writer)) {
            LinkedHashMap<Integer, Task> allTaskAndSubtasks = getTaskAndSubtasks();
            List<Task> history = memHisManager.getHistory();
            bufWriter.write(TITLE);
            for (Integer id : allTaskAndSubtasks.keySet()) {
                String task = allTaskAndSubtasks.get(id).toString();
                bufWriter.write(task);
            }
            bufWriter.write("     \n");
            for (Task task : history) {
                bufWriter.write(task.getId() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.", e);
        }
    }

    static Task taskFromString(String value) {
        String[] splTask = value.split(",");
        int id = Integer.parseInt(splTask[0]);
        Status status = Status.valueOf(splTask[3]);
        Duration duration = null;
        LocalDateTime startTime = null;
        TypeOfTask type = TypeOfTask.valueOf(splTask[1]);
        if (!splTask[6].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(splTask[5]));
            startTime = LocalDateTime.parse(splTask[6], FORMATTER);
        }
        if (type == TypeOfTask.EPIC) {
            return new Epic(id, splTask[2], splTask[4], status);
                    } else if (type == TypeOfTask.SUBTASK) {
            int parentsId = Integer.parseInt(splTask[7]);
            if (startTime != null) {
                return new SubTask(id, splTask[2], splTask[4], status, parentsId, duration, startTime);
            } else {
                return new SubTask(id, splTask[2], splTask[4], status, parentsId);
            }
        }
        if (startTime != null) {
            return new Task(id, splTask[2], splTask[4], status, duration, startTime);
        } else {
            return new Task(id, splTask[2], splTask[4], status);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        String fileName = file.toString();
        FileBackedTaskManager fileManagerloaded = new FileBackedTaskManager(fileName);
        try (FileReader reader = new FileReader(fileName, CHARSET);
             BufferedReader bufReader = new BufferedReader(reader)) {
            HashMap<Integer, Task> tasks = fileManagerloaded.tasks;
            HashMap<Integer, Epic> epics = fileManagerloaded.epics;
            HistoryManager memHisManager = fileManagerloaded.memHisManager;
            boolean isHistoryBegin = false;
            int count = 0;
            while (bufReader.ready()) {
                String line = bufReader.readLine();
                if (count == 0) {
                    count++;
                    continue;
                }
                if (line.isBlank()) {
                    isHistoryBegin = true;
                    continue;
                }
                if (!isHistoryBegin) {
                    Task task = taskFromString(line);
                    if (task.getTypeOfTask() == TypeOfTask.EPIC) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task.getTypeOfTask() == TypeOfTask.TASK) {
                        tasks.put(task.getId(), task);
                    } else {
                        SubTask subTask = (SubTask) task;
                        int parentsId = ((SubTask) task).getParentsId();
                        fileManagerloaded.addSubTaskToEpic(subTask, parentsId);
                    }
                } else {
                    int id = Integer.parseInt(line);
                    Task task = fileManagerloaded.getTaskAndSubtasks().get(id);
                    memHisManager.add(task);
                }
            }
            fileManagerloaded.setLastId();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.", e);
        }
        return fileManagerloaded;
    }

    private void setLastId() {
        this.id = Collections.max(getTaskAndSubtasks().keySet());
    }

    @Override
    public void setTask(Task task) {
        super.setTask(task);
        save();
    }

    @Override
    public void setEpic(Epic epic) {
        super.setEpic(epic);
        save();
    }

    @Override
    public void setSubTask(SubTask subTask) {
        super.setSubTask(subTask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public boolean changeTask(Task task) {
        boolean isSuccess = super.changeTask(task);
        save();
        return isSuccess;
    }

    @Override
    public boolean changeEpic(Epic epic) {
        boolean isSuccess = super.changeEpic(epic);
        save();
        return isSuccess;
    }

    @Override
    public boolean changeSubTask(SubTask subTask) {
        boolean isSuccess = super.changeSubTask(subTask);
        save();
        return isSuccess;
    }

    @Override
    public boolean deleteTask(int id) {
        boolean isSuccess = super.deleteTask(id);
        save();
        return isSuccess;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean isSuccess = super.deleteEpic(id);
        save();
        return isSuccess;
    }

    @Override
    public boolean deleteSubTask(int id) {
        boolean isSuccess = super.deleteSubTask(id);
        save();
        return isSuccess;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }
}
