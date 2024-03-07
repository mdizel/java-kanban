import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    HistoryManager historyManager = taskManager.memHisManager;


    @Test
    public void getHistory() {
        Task task = new Task("Test first", "description first", Status.NEW);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        Task task2 = new Task(task.getId(), "Test changed", "description changed", Status.DONE);
        Task task3 = new Task("Test3", "description3", Status.IN_PROGRESS);
        taskManager.getTask(task.getId());
        taskManager.getTask(task1.getId());
        ArrayList<Task> recentlyOpenTasks = historyManager.getHistory();
        assertNotEquals(0, recentlyOpenTasks.size(), "Список задач пуст");
        assertEquals(2, recentlyOpenTasks.size(), "Неверное количество задач.");
        taskManager.changeTask(task2);
        assertEquals("Test first", recentlyOpenTasks.getFirst().getName(),
                "Задача в истории изменилась");
        taskManager.setTask(task3);
        taskManager.getTask(task3.getId());
        assertEquals(3, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.deleteTask(task.getId());
        assertEquals(2, historyManager.getHistory().size(), "Удаленная задача осталась в истории");
        assertFalse(historyManager.getHistory().contains(task), "Удаленная задача осталась в истории");

    }

    @Test
    public void add() {
        Task task = new Task("Test first", "description first", Status.NEW);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Status.NEW);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(3, historyManager.size(), "Неверное количество задач.");
    }
}
