import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    public static Path path = Paths.get("D:", "projectIdea", "files", "test.csv");
    public static String fileName = String.valueOf(path);
    File file = new File(fileName);
    FileBackedTaskManager fileManager = new FileBackedTaskManager(fileName);

    @Test
    void save() {
        Task task = new Task("Task test", "Task description", Status.NEW);
        Epic epic = new Epic("Epic test", "Epic description", Status.IN_PROGRESS);
        Task task2 = new Task("Task2 ", "NewTask2", Status.DONE);
        fileManager.setTask(task);
        fileManager.setTask(epic);
        fileManager.setTask(task2);
        assertEquals(3, fileManager.getTaskAndSubtasks().size(), "Неверное количество задач.");
        fileManager.save();
        assertTrue(Files.exists(path), "Файл не создан");
    }

    @Test
    void taskFromString() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        String taskToString = task.toString();
        taskToString = taskToString.replace("\n", "");
        Task loadedTask = FileBackedTaskManager.taskFromString(taskToString);
        assertEquals(task, loadedTask, "Неверно загружен ID задачи.");
        assertEquals(task.getName(), loadedTask.getName(), "Неверно загружено имя задачи.");
        assertEquals(task.description, loadedTask.description, "Неверно загружено описание задачи.");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Неверно загружен статус задачи.");
        assertEquals(task.getTypeOfTask(), loadedTask.getTypeOfTask(), "Неверно загружено тип задачи.");
    }

    @Test
    void loadFromFile() {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(3, taskManager.getTaskAndSubtasks().size(), "Неверное количество задач.");
    }
}