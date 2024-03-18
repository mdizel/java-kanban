import java.io.File;

public class Main {
    static  String fileName = Managers.fileName;
   static File file = new File(fileName);
    public static void main(String[] args) {
  InputOutput inputOutput = new InputOutput();
        //inputOutput.test();
       FileBackedTaskManager fileManager=  FileBackedTaskManager.loadFromFile(file);
        Task task = new Task("Покрасить стены", "Нужно покрасить стены в коридоре в синий цвет.", Status.NEW);
        Task task1 = new Task("Прополоть картошку", "Огородные работы", Status.IN_PROGRESS);
        Task task2 = new Task("Заготовить дров", "Нужно привезти, напилить, нарубить ", Status.DONE);
        Task task4 = new Task(10001, "Покрасить стены", "Стены красим теперь красный цвет.", Status.DONE);
        Epic epic = new Epic("Поcтроить сарай", "Помещение для хоз нужд", Status.NEW);
        Epic epic3 = new Epic(10009, "Эпик для замены", "Им мы заменили эпик 10009", Status.DONE);
        SubTask subTask = new SubTask("Закупить материалы", "Заказать материалы для строительства сарая",
                Status.IN_PROGRESS, 10004);
        SubTask subTask1 = new SubTask("Договорится с бригадой", "Найти бригаду, обсудить стоимость и сроки",
                Status.NEW, 10004);
        Epic epic1 = new Epic("Сделать ремонт в комнате", "Косметический ремонт в комнате на втором этаже",
                Status.NEW);
        SubTask subTask2 = new SubTask("Выбрать обои", "Обдумать цветовую гамму, купить обои и клей ",
                Status.NEW, 10007);
        Epic epic2 = new Epic("Перебрать мотор в тракторе", "Полная переборка с расточкой", Status.DONE);
        SubTask subTask3 = new SubTask(10005, "Проточить коленвал", "Отвезти коленвал в расточку",
                Status.NEW, 10004);
        SubTask subTask4 = new SubTask(10006, "Залить фундамент", "Заказать бетон, вызвать бетоннасос",
                Status.NEW, 10004);
        SubTask subTask5 = new SubTask("Привезти доски", "Привезти доски с рынка", Status.NEW, 10009);
        SubTask subTask6 = new SubTask("Просто подзадача к эпику.", "Добавление по id эпика",
                Status.DONE, 10007);

        fileManager.setTask(task);
        fileManager.setTask(task1);
        fileManager.setTask(task2);
        fileManager.setEpic(epic);
        fileManager.setSubTask(subTask);
        fileManager.setSubTask(subTask1);
        fileManager.setEpic(epic1);
        fileManager.setSubTask(subTask2);
        fileManager.setEpic(epic2);

        System.out.println(fileManager.getTaskAndSubtasks());
        System.out.println(fileManager.getMemHisManager().getHistory());
    }
}
