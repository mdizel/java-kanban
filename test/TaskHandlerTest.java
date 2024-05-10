import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskHandlerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    String path;
    String requestType;
    String taskJson;
    Gson gson;
    TaskManager taskManager;
    HttpServer httpServer;
    LocalDateTime time1 = LocalDateTime.parse("2024-04-01T09:10:00");
    LocalDateTime time2 = LocalDateTime.parse("2024-04-01T11:20:00");
    LocalDateTime time3 = LocalDateTime.parse("2024-04-01T14:05:00");
    LocalDateTime time4 = LocalDateTime.parse("2024-04-02T15:41:00");
    LocalDateTime time5 = LocalDateTime.parse("2024-04-02T20:50:00");
    LocalDateTime time6 = LocalDateTime.parse("2024-04-03T09:20:00");
    Duration duration1 = Duration.ofMinutes(60);
    Duration duration2 = Duration.ofMinutes(119);
    Duration duration3 = Duration.ofMinutes(250);

    @BeforeEach
    void createManagers() throws IOException {
        taskManager = Managers.getDefault();
        httpServer = HttpTaskServer.startServer(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @AfterEach
    void closeServer() {
        httpServer.stop(2);
    }

    @Test
    void httpTasksTest() {
        path = "http://localhost:8080/tasks";
        requestType = "GET";
        Task task = new Task("Test addNewTask", "description", Status.NEW, duration1, time1);
        Task task1 = new Task("Test addNewTask2", "description2", Status.IN_PROGRESS, duration2, time2);
        Task task2 = new Task("Test Task3", "------", Status.IN_PROGRESS, duration3, time3);
        Task task3 = new Task("Добавленная задача", "-----", Status.NEW, duration2, time4);
        Task task5 = new Task("Наложение по времени", "-----", Status.NEW, duration3, time4);
        taskManager.setTask(task);
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        taskJson = gson.toJson(task);
        int id = task1.getId();
        int id2 = task2.getId();
        Task task4 = new Task(id2, "Обновленная задача", "-----", Status.NEW, duration2, time5);
        TaskClient taskClient = new TaskClient(client);
        String jsonSavedTasks = gson.toJson(taskManager.getTasksList());
        String httpResponseTasks = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedTasks, httpResponseTasks, "Список задач из http ответа не совпадает"); // GET
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
        System.out.println("----------------------------------");
        System.out.println(jsonSavedTasks);
        path = String.format("http://localhost:8080/tasks/%s", id);
        System.out.println("----------------------------------");
        String jsonSavedTask = gson.toJson(taskManager.getTask(id));
        String httpResponseTask = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedTask, httpResponseTask, "Задача из http ответа не совпадает"); // GET ID
        System.out.println(httpResponseTask);
        path = "http://localhost:8080/history";
        String jsonSavedHistory = gson.toJson(taskManager.getHistory());
        String httpResponseHistory = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedHistory, httpResponseHistory, "История из http ответа не совпадает"); // history
        path = "http://localhost:8080/prioritized";
        String jsonSavedPrioritized = gson.toJson(taskManager.getPrioritizedTasks());
        String httpResponsePrioritized = taskClient.getTaskData(path, requestType, taskJson);       // prioritized
        assertEquals(jsonSavedPrioritized, httpResponsePrioritized, "Список по приоритету из http ответа не совпадает");
        path = String.format("http://localhost:8080/tasks/%s", "notId");
        System.out.println("Ответ при не числовом Id");
        assertTrue(taskClient.getTaskData(path, requestType, taskJson).contains("404"),
                "Не возвращается код 404 при некорректном Id");                       // WRONG ID
        path = String.format("http://localhost:8080/tasks/%s", id);
        requestType = "DELETE";
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(2, taskManager.getTasksList().size(), "Задача не удалилась");  // DELITE
        System.out.println("----------------------------------");
        System.out.println(taskManager.getTasksList());
        requestType = "POST";
        path = "http://localhost:8080/tasks";
        taskJson = gson.toJson(task3);
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(3, taskManager.getTasksList().size(), "Задача не добавилась"); // POST
        System.out.println("----------------------------------");
        System.out.println(taskManager.getTasksList());
        taskJson = gson.toJson(task4);
        taskClient.getTaskData(path, requestType, taskJson);
        System.out.println("----------------------------------");
        System.out.println(taskManager.getTasksList());
        Task taskNew = taskManager.getTask(id2);
        String taskNewName = taskNew.getName();
        assertEquals("Обновленная задача", taskNewName, "Задача не обновилась"); // POST ID
        taskJson = gson.toJson(task5);
        taskClient.getTaskData(path, requestType, taskJson);
        assertTrue(taskClient.getTaskData(path, requestType, taskJson).contains("406"), // наложение по времени
                "Не возвращается код 406 при наложении времени");
        System.out.println("----------------------------------");
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
    }

    @Test
    void httpEpicTest() {
        path = "http://localhost:8080/epics";
        requestType = "GET";
        Epic epic = new Epic("Test addNewEpic", "description", Status.NEW);
        Epic epic1 = new Epic("Test addNewEpic2", "description2", Status.IN_PROGRESS);
        Epic epic2 = new Epic("Test Epic3", "------", Status.IN_PROGRESS);
        Epic epic3 = new Epic("Добавленный эпик", "-----", Status.NEW);
        taskManager.setEpic(epic);
        taskManager.setEpic(epic1);
        taskManager.setEpic(epic2);
        SubTask subTaskTime = new SubTask("Подзадача к эпику со временем1.", "_описание_",
                Status.IN_PROGRESS, epic.getId(), duration2, time1);
        SubTask subTaskTime2 = new SubTask("Подзадача к эпику со временем2.", "_описание_",
                Status.IN_PROGRESS, epic.getId(), duration1, time2);
        SubTask subTaskTime3 = new SubTask("Подзадача к эпику со временем3.", "_описание_",
                Status.IN_PROGRESS, epic1.getId(), duration1, time3);
        SubTask subTaskTime4 = new SubTask("Подзадача к эпику со временем4.", "_описание_",
                Status.NEW, epic1.getId(), duration1, time4);
        SubTask subTaskTime5 = new SubTask("Подзадача к эпику со временем5.", "_описание_",
                Status.NEW, epic2.getId(), duration1, time5);
        SubTask subTaskTime6 = new SubTask("Подзадача к эпику со временем6.", "_описание_",
                Status.NEW, epic2.getId(), duration3, time6);
        taskManager.setSubTask(subTaskTime);
        taskManager.setSubTask(subTaskTime2);
        taskManager.setSubTask(subTaskTime3);
        taskManager.setSubTask(subTaskTime4);
        taskManager.setSubTask(subTaskTime5);
        taskManager.setSubTask(subTaskTime6);
        taskJson = gson.toJson(epic);
        int id = epic1.getId();
        int id2 = epic2.getId();
        Epic epic4 = new Epic(id2, "Обновленный эпик", "-----", Status.NEW);
        TaskClient taskClient = new TaskClient(client);
        String jsonSavedEpics = gson.toJson(taskManager.getEpicsList());
        String httpResponseEpics = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedEpics, httpResponseEpics, "Список эпиков из http ответа не совпадает"); // GET
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
        System.out.println("----------------------------------");
        System.out.println(jsonSavedEpics);
        path = String.format("http://localhost:8080/epics/%s", id);
        System.out.println("----------------------------------");
        String jsonSavedTask = gson.toJson(taskManager.getEpic(id));
        String httpResponseEpic = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedTask, httpResponseEpic, "Задача из http ответа не совпадает"); // GET ID
        System.out.println(httpResponseEpic);
        path = String.format("http://localhost:8080/epics/%s", "notId");
        System.out.println("Ответ при не числовом Id");
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
        assertTrue(taskClient.getTaskData(path, requestType, taskJson).contains("404"),
                "Не возвращается код 404 при некорректном Id");                       // WRONG ID
        path = String.format("http://localhost:8080/epics/%s", id);
        requestType = "DELETE";
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(2, taskManager.getEpicsList().size(), "Задача не удалилась");  // DELITE
        System.out.println("----------------------------------");
        System.out.println(taskManager.getEpicsList());
        requestType = "POST";
        path = "http://localhost:8080/epics";
        taskJson = gson.toJson(epic3);
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(3, taskManager.getEpicsList().size(), "Задача не добавилась"); // POST
        System.out.println("----------------------------------");
        System.out.println(taskManager.getEpicsList());
        taskJson = gson.toJson(epic4);
        taskClient.getTaskData(path, requestType, taskJson);
        System.out.println("----------------------------------");
        System.out.println(taskManager.getEpicsList());
        Task epicNew = taskManager.getEpic(id2);
        String epicNewName = epicNew.getName();
        assertEquals("Обновленный эпик", epicNewName, "Эпик не обновился"); // POST ID
    }

    @Test
    void httpSubTasksTest() {
        path = "http://localhost:8080/subtasks";
        requestType = "GET";
        Epic epic = new Epic("Test addNewEpic", "description", Status.NEW);
        taskManager.setEpic(epic);
        SubTask subtask = new SubTask("Test addNewSubTask", "description", Status.NEW, epic.getId(),
                duration1, time1);
        SubTask subtask1 = new SubTask("Test addNewSubTask2", "description2", Status.IN_PROGRESS,
                epic.getId(), duration2, time2);
        SubTask subtask2 = new SubTask("Test SubTask3", "------", Status.IN_PROGRESS, epic.getId(),
                duration3, time3);
        SubTask subtask3 = new SubTask("Добавленная подзадача", "-----", Status.NEW, epic.getId(),
                duration2, time4);
        SubTask subtask5 = new SubTask("Наложение по времени", "-----", Status.NEW, epic.getId(),
                duration3, time4);
        taskManager.setSubTask(subtask);
        taskManager.setSubTask(subtask1);
        taskManager.setSubTask(subtask2);
        taskJson = gson.toJson(subtask);
        int id = subtask1.getId();
        int id2 = subtask2.getId();
        SubTask subtask4 = new SubTask(id2, "Обновленная подзадача", "-----", Status.NEW, epic.getId(),
                duration2, time5);
        TaskClient taskClient = new TaskClient(client);
        String jsonSavedSubTasks = gson.toJson(taskManager.getSubtasksList());
        String httpResponseTasks = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedSubTasks, httpResponseTasks, "Список подзадач из http ответа не совпадает");// GET
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
        System.out.println("----------------------------------");
        System.out.println(jsonSavedSubTasks);
        path = String.format("http://localhost:8080/subtasks/%s", id);
        System.out.println("----------------------------------");
        String jsonSavedSubTask = gson.toJson(taskManager.getSubTask(id));
        String httpResponseTask = taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(jsonSavedSubTask, httpResponseTask, "Подзадача из http ответа не совпадает"); // GET ID
        System.out.println(httpResponseTask);
        path = String.format("http://localhost:8080/subtasks/%s", "notId");
        assertTrue(taskClient.getTaskData(path, requestType, taskJson).contains("404"),
                "Не возвращается код 404 при некорректном Id");                       // WRONG ID
        path = String.format("http://localhost:8080/subtasks/%s", id);
        requestType = "DELETE";
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(2, taskManager.getSubtasksList().size(), "Подзадача не удалилась");  // DELITE
        System.out.println("----------------------------------");
        System.out.println(taskManager.getSubtasksList());
        requestType = "POST";
        path = "http://localhost:8080/subtasks";
        taskJson = gson.toJson(subtask3);
        taskClient.getTaskData(path, requestType, taskJson);
        assertEquals(3, taskManager.getSubtasksList().size(), "Подадача не добавилась"); // POST
        System.out.println("----------------------------------");
        System.out.println(taskManager.getSubtasksList());
        taskJson = gson.toJson(subtask4);
        taskClient.getTaskData(path, requestType, taskJson);
        System.out.println("----------------------------------");
        System.out.println(taskManager.getSubtasksList());
        SubTask subtaskNew = taskManager.getSubTask(id2);
        String sutaskNewName = subtaskNew.getName();
        assertEquals("Обновленная подзадача", sutaskNewName, "Подзадача не обновилась"); // POST ID
        taskJson = gson.toJson(subtask5);
        taskClient.getTaskData(path, requestType, taskJson);
        assertTrue(taskClient.getTaskData(path, requestType, taskJson).contains("406"), // наложение по времени
                "Не возвращается код 406 при наложении времени");
        System.out.println("----------------------------------");
        System.out.println(taskClient.getTaskData(path, requestType, taskJson));
    }
}