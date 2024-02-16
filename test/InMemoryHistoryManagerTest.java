import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    InMemoryTaskManager taskManager= new InMemoryTaskManager();
    HistoryManager historyManager = taskManager.memHisManager;

    @Test
    public void getHistory(){
        Task task = new Task("Test first", "description first", Status.NEW);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        Task task2 = new Task(task.getId(),"Test changed", "description changed", Status.DONE);
        taskManager.getTask(task.getId());
        taskManager.getTask(task1.getId());
        assertNotEquals(0, historyManager.getHistory().size(), "Список задач пуст");
        assertEquals(2, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.changeTask(task2);
        assertEquals ("Test first", historyManager.getHistory().get(0).getName(),
                "Задача в истории изменилась");
        taskManager.getTask(task.getId());
        assertEquals(3, historyManager.getHistory().size(), "Неверное количество задач.");
    }
}
