import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    public void save() {
        Charset charset = Charset.forName("windows-1251");
        try (FileWriter writer = new FileWriter(fileName, charset);
             BufferedWriter bufWriter = new BufferedWriter(writer)) {
            LinkedHashMap<Integer, Task> allTaskAndSubtasks = getTaskAndSubtasks();
            HistoryManager historyManager = getMemHisManager();
            ArrayList<Task> history = historyManager.getHistory();
            bufWriter.write("id;type;name;status;description;epic\n");
            for (Integer id : allTaskAndSubtasks.keySet()) {
                String task = allTaskAndSubtasks.get(id).toString();
                bufWriter.write(task);
            }
            bufWriter.write("     \n");
            for (Task task : history) {
                bufWriter.write(task.getId() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

    public static Task taskFromString(String value) {
        String[] splTask = value.split(";");
        int id = Integer.parseInt(splTask[0]);
        Status status = Status.valueOf(splTask[3]);
        TypeOfTask type = TypeOfTask.valueOf(splTask[1]);
        if (type == TypeOfTask.EPIC) {
            return new Epic(id, splTask[2], splTask[4], status);
        } else if (type == TypeOfTask.SUBTASK) {
            int parentsId = Integer.parseInt(splTask[5]);
            return new SubTask(id, splTask[2], splTask[4], status, parentsId);
        }
        return new Task(id, splTask[2], splTask[4], status);
    }

    static FileBackedTaskManager loadFromFile(File file) {
        String fileName = file.toString();
        FileBackedTaskManager fileManagerloaded = new FileBackedTaskManager(fileName);
        Charset charset = Charset.forName("windows-1251");
        try (FileReader reader = new FileReader(fileName, charset);
             BufferedReader bufReader = new BufferedReader(reader)) {
            HashMap<Integer, Task> tasks = new HashMap<>();
            HashMap<Integer, Epic> epics = new HashMap<>();
            HistoryManager memHisManager = fileManagerloaded.getMemHisManager();
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
                        fileManagerloaded.setEpics(epics);
                    } else if (task.getTypeOfTask() == TypeOfTask.TASK) {
                        tasks.put(task.getId(), task);
                        fileManagerloaded.setTasks(tasks);
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
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return fileManagerloaded;
    }

    void setLastId() {
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
    public void changeTask(Task task) {
        super.changeTask(task);
        save();
    }

    @Override
    public void changeEpic(Epic epic) {
        super.changeEpic(epic);
        save();
    }

    @Override
    public void changeSubTask(SubTask subTask) {
        super.changeSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
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
