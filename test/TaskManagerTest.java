import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void createManagers() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void setTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        Task task1 = new Task("Test addNewTask2", "Test addNewTask2", Status.IN_PROGRESS);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        Task task2 = new Task(task.getId(), "New task", "-----------", Status.IN_PROGRESS);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task2, task, "Задачи с одинаковым Id не совпали");
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
        assertNotEquals(epic, epic1, "Эпики c разным id совпали.");
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
                epic.getId());
        SubTask subTask1 = new SubTask("Test addNewSubtask1", "Test addNewSubtask description1",
                Status.DONE, epic.getId());
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
        assertNull(taskManager.getEpic(subTask.getId()), "Подзадача подгружена сама к себе");
        assertNotNull(subTask, "Подзадачи не возвращаются.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }

    void subTaskToSubTask(SubTask subTask) {
        int id = subTask.getId();
        taskManager.addSubTaskToEpic(subTask, id);
    }

    @Test
    void changeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.setTask(task);
        Task task1 = new Task(task.getId(), "Test addNewTaskNEW", "Test addNewTask descriptionNEW", Status.DONE);
        assertEquals("Test addNewTask", task.getName(), "Проблема с задачей до изменения");
        taskManager.changeTask(task1);
        final List<Task> tasks = taskManager.getTasksList();
        Task changedTask = tasks.getFirst();
        assertEquals("Test addNewTaskNEW", changedTask.getName(), "Задача не поменялась.");
        assertEquals(1, tasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void checkStatusAndTime() {
        Status status1 = Status.NEW;
        Status status2 = Status.IN_PROGRESS;
        Status status3 = Status.DONE;
        LocalDateTime TimeOfFirstSubt = LocalDateTime.parse("2024-04-01T09:10:00");
        LocalDateTime startTime3 = LocalDateTime.parse("2024-04-01T11:20:00");
        LocalDateTime earliestTime = LocalDateTime.parse("2024-04-01T06:30:00");
        LocalDateTime startTime4 = LocalDateTime.parse("2024-04-02T19:41:00");
        LocalDateTime startTime5 = LocalDateTime.parse("2024-04-02T20:50:00");
        LocalDateTime lastestTime = LocalDateTime.parse("2024-04-01T19:40:00");
        Duration duration1 = Duration.ofMinutes(1440);
        Duration duration2 = Duration.ofMinutes(119);
        Duration duration3 = Duration.ofMinutes(250);
        Duration duration4 = Duration.ofMinutes(60);
        Epic epic = new Epic("Эпик со временем 1", "----э1", status1);
        taskManager.setEpic(epic);
        SubTask subTaskTime = new SubTask("Подзадача к эпику со временем1.", "_описание_",
                status1, epic.getId(), duration2, TimeOfFirstSubt);
        SubTask subTaskTime2 = new SubTask("Подзадача к эпику со временем2.", "_описание_",
                status1, epic.getId(), duration3, startTime3);
        SubTask subTaskTime3 = new SubTask("Подзадача к эпику со временем3.", "_описание_",
                status1, epic.getId(), duration3, earliestTime);
        SubTask subTaskTime4 = new SubTask("Подзадача к эпику со временем4.", "_описание_",
                status3, epic.getId(), duration1, lastestTime);
        SubTask subTaskTime5 = new SubTask("Подзадача к эпику со временем5.", "_описание_",
                status1, epic.getId(), duration4, startTime4);
        SubTask subTaskTime6 = new SubTask("Подзадача к эпику со временем6.", "_описание_",
                status2, epic.getId(), duration4, startTime5);
        taskManager.setSubTask(subTaskTime);
        taskManager.setSubTask(subTaskTime2);
        taskManager.setSubTask(subTaskTime3);
        Duration epicDuration = Duration.between(TimeOfFirstSubt, subTaskTime2.getEndTime());
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус при всех подзадачах NEW");
        assertEquals(epic.getStartTime(), TimeOfFirstSubt, "Неверное расчетное время старта эпика");
        assertEquals(epic.getDuration(), epicDuration, "Неверный расчетный срок исполнения эпика");
        List<SubTask> subTasks = taskManager.getSubtasksList();
        assertEquals(2, subTasks.size(), "Подгружена подзадача с конфликтом по времени исполнения");
        subTaskTime.setStatus(Status.DONE);
        subTaskTime2.setStatus(Status.DONE);
        subTaskTime3.setStatus(Status.DONE);
        taskManager.setSubTask(subTaskTime4);
        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус при всех подзадачах DONE");
        taskManager.setSubTask(subTaskTime5);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус,есть невыполенные задачи");
        subTasks = taskManager.getSubtasksList();
        assertEquals(4, subTasks.size(), "Подгружены не все подзадачи");
        subTaskTime4.setStatus(Status.IN_PROGRESS);
        subTaskTime4.setStatus(Status.IN_PROGRESS);
        taskManager.setSubTask(subTaskTime6);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус при задачах в процессе исполнения");
    }
}
