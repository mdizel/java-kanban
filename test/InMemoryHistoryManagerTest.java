import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();

    @Test
    void getHistory() {
        Task task = new Task("Test first", "description first", Status.NEW);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW);
        Task task2 = new Task(10001, "Test changed", "description changed", Status.DONE);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        taskManager.getTask(10001);
        taskManager.getTask(10002);
        assertNotNull(historyManager.getHistory(), "Список задач пуст");
        assertEquals(2, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.changeTask(task2);
        assertEquals("Test first", historyManager.getHistory().get(0).getName(),
                "Задача в истории изменилась");
        taskManager.getTask(10001);
        assertEquals(3, historyManager.getHistory().size(), "Неверное количество задач.");
    }
}
