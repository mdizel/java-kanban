import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

//Этот класс полностью создан для проверки работы программы.
public class InputOutput {
    String fileName = Managers.fileName;
    File file = new File(fileName);
    Scanner scanner = new Scanner(System.in);
    TaskManager taskManager;
    int count = 1;

    TaskManager managerChoose() {
        TaskManager manager;
        System.out.println("Выберите менеджера");
        System.out.println("1 — Менеджер в памяти");
        System.out.println("2 — Файловый менеджер");
        System.out.println("3 — Файловый менеджер с предварительной загрузкой из файла");
        String command = scanner.next();
        manager = switch (command) {
            case "2" -> Managers.getFileManager();
            case "3" -> FileBackedTaskManager.loadFromFile(file);
            default -> new InMemoryTaskManager();
        };
        return manager;
    }

    void print() {
        System.out.println("Список задач " + count);
        System.out.println(taskManager.getTasksList());
        System.out.println("________________________________________");
        System.out.println("Список эпиков");
        System.out.println(taskManager.getEpicsList());
        System.out.println("________________________________________");
        System.out.println("Список подзадач");
        System.out.println(taskManager.getSubtasksList());
        System.out.println("________________________________________");
        System.out.println("Сборный список задач");
        System.out.println(taskManager.getTaskAndSubtasks());
        count++;
    }

    public void test() {                      // Метод для теста программы
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = managerChoose();
        System.out.println("Список задач после загрузки из файла:");
        print();
        System.out.println("История после загрузки из файла:");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________________");
        taskManager.setTask(task);
        taskManager.setTask(task1);
        taskManager.setTask(task2);
        taskManager.setEpic(epic);
        taskManager.setSubTask(subTask);
        taskManager.setSubTask(subTask1);
        taskManager.setEpic(epic1);
        taskManager.setSubTask(subTask2);
        taskManager.setEpic(epic2);
        taskManager.setTask(taskTime);
        taskManager.setTask(taskTime2);
        taskManager.setTask(taskTime3);
        taskManager.setEpic(epicTime);
        taskManager.setSubTask(subTaskTime);
        taskManager.setSubTask(subTaskTime2);
        taskManager.setSubTask(subTaskTime3);
        taskManager.setSubTask(subTaskTime4);
        taskManager.setTask(taskTime4);
        System.out.println("________________________________________");
        System.out.println("Список после добавления задач");
        print();
        System.out.println("Получаем, задачу, эпик и подзадачу");
        System.out.println(taskManager.getTask(10001));
        System.out.println(taskManager.getEpic(10004));
        System.out.println(taskManager.getSubTask(10005));
        System.out.println("________________________________________");
        System.out.println(taskManager.getAllSubtask().get(10006));
        System.out.println("Заменяем задачу 10001 и Подзадачу 10005 и 10006");
        taskManager.changeTask(task4);
        taskManager.changeSubTask(subTask3);
        taskManager.changeSubTask(subTask4);
        print();
        System.out.println("Проверка ошибочных загрузок");
        System.out.println("Замена подзадачи без id ");
        taskManager.changeSubTask(subTask5);
        System.out.println("Замена эпика на подзадачу");
        taskManager.changeSubTask(subTask7);
        System.out.println("Замена задачи на подзадачу");
        taskManager.changeTask(subTask8);
        System.out.println("Замена эпика на задачу");
        taskManager.changeTask(task5);
        System.out.println("Замена подзадачи на задачу");
        taskManager.changeTask(task6);
        System.out.println("Замена подзадачи на эпик");
        taskManager.changeEpic(epic4);
        System.out.println("Замена задачи на эпик");
        taskManager.changeTask(epic5);
        System.out.println("________________________________________");
        System.out.println("Добавляем подзадачи к Эпикам 10009 и 10007");
        taskManager.setSubTask(subTask5);
        taskManager.setSubTask(subTask6);
        System.out.println("Список эпиков");
        System.out.println(taskManager.getEpicsList());
        System.out.println("Меняем эпик 10009");
        taskManager.changeEpic(epic3);
        System.out.println("Список эпиков");
        System.out.println("Удаляем задачу 10001, подзадачу 10005, эпик 10009");
        taskManager.deleteTask(10001);
        //taskManager.deleteEpic(10009);
        taskManager.deleteSubTask(10005);
        print();
        System.out.println("Получаем подзадачи Эпика 10004");
        System.out.println(taskManager.getSubtaskFromEpicList(10004));
        System.out.println("________________________________________");
        System.out.println("Получаем задачу по Id 10002");
        System.out.println(taskManager.getTask(10002));
        System.out.println("Получаем Эпик по Id 10007");
        System.out.println(taskManager.getEpic(10007));
        System.out.println("Получаем Подзадачу по Id 10008");
        System.out.println(taskManager.getSubTask(10008));
        System.out.println("_______________555_________________________");
        System.out.println("Получаем историю");
        taskManager.getEpic(10004);
        taskManager.getEpic(10007);
        taskManager.getSubTask(10006);
        taskManager.getTask(10002);
        taskManager.getTask(10002);
        taskManager.getEpic(10004);
        taskManager.getEpic(10007);
        taskManager.getSubTask(10006);
        taskManager.getTask(10002);
        taskManager.getTask(10002);
        taskManager.getTask(10003);
        System.out.println("Список из памяти HistoryManager");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________________");
        System.out.println("Список задач сортированных по времени");
        System.out.println(taskManager.getPrioritizedTasks());
        taskManager.changeTask(taskTime5);
        System.out.println("Список задач сортированных по времени после обновления задачи");
        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println("________________________________________");
        System.out.println(taskManager.getTasksList());
        System.out.println(gson.toJson(taskTime));
    }

    Task task = new Task("Покрасить стены", "Нужно покрасить стены в коридоре в синий цвет.", Status.NEW);
    Task task1 = new Task("Прополоть картошку", "Огородные работы", Status.IN_PROGRESS);
    Task task2 = new Task("Заготовить дров", "Нужно привезти напилить нарубить ", Status.DONE);
    Task task4 = new Task(10001, "Покрасить стены", "Стены красим теперь красный цвет.", Status.DONE);
    Epic epic = new Epic("Поcтроить сарай", "Помещение для хоз нужд", Status.NEW);
    Epic epic3 = new Epic(10009, "Эпик для замены", "Им мы заменили эпик 10009", Status.DONE);
    SubTask subTask = new SubTask("Закупить материалы", "Заказать материалы для строительства сарая",
            Status.IN_PROGRESS, 10004);
    SubTask subTask1 = new SubTask("Договорится с бригадой", "Найти бригаду",
            Status.NEW, 10004);
    Epic epic1 = new Epic("Сделать ремонт в комнате", "Косметический ремонт в комнате на втором этаже",
            Status.NEW);
    SubTask subTask2 = new SubTask("Выбрать обои", "Обдумать цветовую гамму купить обои и клей ",
            Status.NEW, 10007);
    Epic epic2 = new Epic("Перебрать мотор в тракторе", "Полная переборка с расточкой", Status.DONE);
    SubTask subTask3 = new SubTask(10005, "Проточить коленвал", "Отвезти коленвал в расточку",
            Status.NEW, 10004);
    SubTask subTask4 = new SubTask(10006, "Залить фундамент", "Заказать бетон вызвать бетоннасос",
            Status.NEW, 10004);
    SubTask subTask5 = new SubTask("Привезти доски", "Привезти доски с рынка", Status.NEW, 10007);
    SubTask subTask6 = new SubTask("Просто подзадача к эпику.", "Добавление по id эпика",
            Status.DONE, 10007);
    SubTask subTask7 = new SubTask(10007, "Ошибочная подзадача для эпика",
            "--------", Status.DONE, 10004);
    SubTask subTask8 = new SubTask(10002, "Ошибочная подзадача для задачи", "-------", Status.DONE,
            10001);
    Task task5 = new Task(10004, "Ошибочная задача для Эпика", "-----", Status.DONE);

    Task task6 = new Task(10005, "Ошибочная задача для Подзадачи", "-----", Status.DONE);

    Epic epic4 = new Epic(10006, "Ошибочный Эпик для Подзадачи", "--------", Status.NEW);
    Epic epic5 = new Epic(10003, "Ошибочный Эпик для Задачи", "--------", Status.NEW);
    Task taskTime = new Task("Задача со временем 1", "----1", Status.NEW, Duration.ofMinutes(120),
            LocalDateTime.parse("2024-04-01T08:00:00"));
    Task taskTime5 = new Task(10010, "Задача со временем для замены", "-----", Status.NEW,
            Duration.ofMinutes(119), LocalDateTime.parse("2024-04-06T11:20:00"));
    Task taskTime2 = new Task("Задача со временем 2", "----2", Status.NEW, Duration.ofMinutes(180),
            LocalDateTime.parse("2024-04-01T11:00:00"));
    Task taskTime3 = new Task("Задача со временем 3", "----3", Status.NEW, Duration.ofMinutes(240),
            LocalDateTime.parse("2024-04-08T07:00:00"));
    Task taskTime4 = new Task("Задача со временем наложение", "----4", Status.NEW, Duration.ofMinutes(50),
            LocalDateTime.parse("2024-04-01T07:00:00"));
    Epic epicTime = new Epic("Эпик со временем 1", "----э1", Status.NEW);
    SubTask subTaskTime = new SubTask("Подзадача к эпику 10007 со временем1.", "______сб1",
            Status.IN_PROGRESS, 10007, Duration.ofMinutes(60), LocalDateTime.parse("2024-04-01T18:15:00"));
    SubTask subTaskTime2 = new SubTask("Подзадача к эпику 10007 со временем2.", "______сб2",
            Status.IN_PROGRESS, 10007, Duration.ofMinutes(240), LocalDateTime.parse("2024-04-02T21:55:00"));
    SubTask subTaskTime3 = new SubTask("Подзадача к эпику epicTime со временем3.", "______сб3",
            Status.IN_PROGRESS, 10013, Duration.ofMinutes(300), LocalDateTime.parse("2024-04-07T09:50:00"));
    SubTask subTaskTime4 = new SubTask("Подзадача к эпику epicTime со временем3.2.", "______сб3",
            Status.IN_PROGRESS, 10013, Duration.ofMinutes(20), LocalDateTime.parse("2024-04-05T07:20:00"));
}

