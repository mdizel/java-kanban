import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    public void setTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        Task task1 = new Task("Test addNewTask2", "Test addNewTask2", Status.IN_PROGRESS);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        final int taskId = task.getId();
        task1.setId(taskId);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
       assertEquals(task, savedTask, "Задачи не совпадают.");
       assertEquals(task1, task, "Задачи с одинаковым Id не совпали");

        final List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
       assertEquals(task, tasks.getFirst(), "Задачи совпадают.");
           }


    @Test
    void setEpic() {

            Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.DONE);
        Epic epic1 = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
            taskManager.setEpic(epic);
            taskManager.setEpic(epic1);
            final int taskId = epic.getId();

            final Epic savedTask = taskManager.getEpic(taskId);

            assertNotNull(savedTask, "Эпик не найдена.");
            assertEquals(epic, savedTask, "Эпики не совпадают.");
            assertNotEquals(epic, epic1,"Эпики c разным id совпали." );
               epic1.setId(taskId);
            assertEquals(epic, epic1, "Эпики c одинаковым id  не совпали.");
            List<Epic> epics = taskManager.getEpicsList();

            assertNotNull(epics, "Эпики не возвращаются.");
            assertEquals(2, epics.size(), "Неверное количество эпиков.");
            assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
        }

    @Test
    void setSubTask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.DONE);
        taskManager.setEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,
                10001);
        SubTask subTask1 = new SubTask("Test addNewSubtask1", "Test addNewSubtask description1",
                Status.DONE, 10001);
        taskManager.setSubTask(subTask);
        taskManager.setSubTask(subTask1);
        final int taskId = subTask.getId();

        final SubTask savedTask = taskManager.getSubTask(taskId);

        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(subTask, savedTask, "Подзадачи не совпадают.");
        subTask1.setId(taskId);
        assertEquals(subTask, subTask1, "Подзадачи c одинаковым id  не совпали.");
        final List<SubTask> subTasks = taskManager.getSubtasksList();
        subTaskToSubTask(subTask);
        assertNull(taskManager.getEpic(subTask.getId()),"Подзадача подгружена сама к себе");
        assertNotNull(subTask, "Подзадачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }

        void subTaskToSubTask (SubTask subTask){
        int id = subTask.getId();
        taskManager.addSubTaskToEpic(subTask, id);
                  }

    @Test

    void changeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = task.getId();
        taskManager.setTask(task);
        Task task1 = new Task(10001,"Test addNewTaskNEW", "Test addNewTask descriptionNEW", Status.DONE);
        assertEquals("Test addNewTask", task.getName(), "Проблема с задачей до изменения");
        taskManager.changeTask(task1);
        final List<Task> tasks = taskManager.getTasksList();
        Task changedTask = tasks.get(taskId);
        assertEquals("Test addNewTaskNEW", changedTask.getName(), "Задача не поменялась.");
        assertEquals(1, tasks.size(), "Неверное количество подзадач.");

    }

    @Test
    public void getHistory(){

        Task task = new Task("Test first", "description first", Status.NEW);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW);
        Task task2 = new Task(10001,"Test changed", "description changed", Status.DONE);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        taskManager.getTask(10001);
        taskManager.getTask(10002);
        assertNotNull(taskManager.getHistory(), "Список задач пуст");
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество задач.");
        taskManager.changeTask(task2);
        assertEquals ("Test first", taskManager.getHistory().get(0).getName(),
                "Задача в истории изменилась");
        taskManager.getTask(10001);
        assertEquals(3, taskManager.getHistory().size(), "Неверное количество задач.");
    }

    @Test
    void getEpicsList() {
    }

    @Test
    void getTasksList() {
    }

    @Test
    void getSubtasksList() {
    }
}
