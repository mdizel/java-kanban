import java.util.ArrayList;
import java.util.HashMap;

/*Этот класс полностью создан для проверки работы программы, пока не стал убирать его, но
по заданию его функционал не нужен. Также убрал сюда некоторые неиспользуемые обработки
public class InputOutput {
    TaskManager taskManager = new TaskManager();
        public void test() {         // Метод для теста программы

            taskManager.setAnyTask(task);
        System.out.println("Пробуем импортировать Подзадачу к которой нет эпика");
        taskManager.setAnyTask(task1);
        taskManager.setAnyTask(task2);
        taskManager.setAnyTask(epic);
        taskManager.setAnyTask(subTask);
        taskManager.setAnyTask(subTask1);
        taskManager.setAnyTask(epic1);
        taskManager.setAnyTask(subTask2);
        taskManager.setAnyTask(epic2);
        System.out.println("Пробуем импортировать объект другого класса");
        taskManager.setAnyTask(taskManager);
        // Печатаем
        System.out.println("Список всех задач и подзазач");
        System.out.println(taskManager.getTaskAndSubtasks());
        System.out.println("Конец списка задач и подзадач");
        System.out.println("______1111__________________________________");
            System.out.println(taskManager.getEpicsList());
            System.out.println("________________________________________");
        System.out.println("Печатаем задачи с сортировкой.");
        System.out.println("Задания со статусом NEW ");
        getAllTaskFromMap(sortAllTask(Status.NEW));
        System.out.println("Задания со статусом IN_PROGRESS ");
        getAllTaskFromMap(sortAllTask(Status.IN_PROGRESS));
        System.out.println("Задания со статусом IN_DONE ");
        getAllTaskFromMap(sortAllTask(Status.DONE));
        System.out.println("Конец списка задач с сортировкой");
        System.out.println("________________________________________");
        System.out.println("Меняем статусы");
        taskManager.setChangeStatus(10005, Status.DONE);
        taskManager.setChangeStatus(10001, Status.DONE);
        taskManager.setChangeStatus(10006, Status.DONE);
        taskManager.setChangeStatus(10008, Status.DONE);
        System.out.println("Пробуем импортировать Подзадачу к которой нет эпика");
        taskManager.setAnyTask(subTask6);
        System.out.println("Список после 1-го изменения статусов");
        System.out.println("Задания со статусом NEW ");
        getAllTaskFromMap(sortAllTask(Status.NEW));
        System.out.println("Задания со статусом IN_PROGRESS ");
        getAllTaskFromMap(sortAllTask(Status.IN_PROGRESS));
        System.out.println("Задания со статусом IN_DONE ");
        getAllTaskFromMap(sortAllTask(Status.DONE));
        System.out.println("Конец списка после 1-го изменения статусов");
        System.out.println("________________________________________");
        taskManager.setChangeStatus(10005, Status.IN_PROGRESS);
        taskManager.setChangeStatus(10006, Status.IN_PROGRESS);
        taskManager.setChangeStatus(10008, Status.NEW);
        taskManager.setChangeStatus(10002, Status.IN_PROGRESS);
        taskManager.setChangeStatus(10004, Status.NEW);

        System.out.println("Список после 2-го изменения статусов");
        System.out.println("Задания со статусом NEW ");
        getAllTaskFromMap(sortAllTask(Status.NEW));
        System.out.println("Задания со статусом IN_PROGRESS ");
        getAllTaskFromMap(sortAllTask(Status.IN_PROGRESS));
        System.out.println("Задания со статусом IN_DONE ");
        getAllTaskFromMap(sortAllTask(Status.DONE));
        System.out.println("Конец списка после 2-го изменения статусов");
        System.out.println("________________________________________");
        System.out.println("Загружаем новую подзадачу задачу или заменяем старую, удаляем");
        taskManager.addSubTaskToEpic(subTask3, 10009);
        taskManager.addSubTaskToEpic(subTask4, 10004);
            taskManager.addSubTaskToEpic(subTask7, 10009);
        taskManager.changeTask(10005, subTask5, Status.NEW);
        taskManager.deleteTask(10002);
        taskManager.deleteTask(10006);
        System.out.println("Задания со статусом NEW ");
        getAllTaskFromMap(sortAllTask(Status.NEW));
        System.out.println("Задания со статусом IN_PROGRESS ");
        getAllTaskFromMap(sortAllTask(Status.IN_PROGRESS));
        System.out.println("Задания со статусом IN_DONE ");
        getAllTaskFromMap(sortAllTask(Status.DONE));
        System.out.println("Конец списка после изменения.");
        System.out.println("________________________________________");
        System.out.println("Получаем все подзадачи");
        System.out.println(taskManager.getAllSubtask());
        System.out.println("________________________________________");
        System.out.println("Получаем подзадачи Эпика 10004");
        System.out.println(taskManager.getSubtaskFromEpic(10004));
        System.out.println("________________________________________");
        System.out.println("Печатаем task And Epics");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println("________________________________________");
        System.out.println("Получаем задачу по Id");
        System.out.println(taskManager.getTask(10002));
        System.out.println("Получаем задачу по Id 10007");
        System.out.println(taskManager.getTask(10007));
        System.out.println("Получаем задачу по Id 10004");
        System.out.println(taskManager.getTask(10004));
        System.out.println("________________________________________");
        System.out.println("Удаляем задачи по типу");
        taskManager.deleteAllTask(TypeOfTask.TASK);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println("________________________________________");
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println(taskManager.getSubtaskFromEpicList(10004));
    }
    Task task = new Task("Покрасить стены", "Нужно покрасить стены в коридоре в зеленый цвет.");
    Task task1 = new Task("Прополоть картошку", "Огородные работы");
    Task task2 = new Task("Заготовить дров", "Нужно привезти, напилить, нарубить ");
    Epic epic = new Epic("Поcтроить сарай", "Помещение для хоз нужд");
    SubTask subTask = new SubTask("Закупить материалы", "Заказать материалы для строительства сарая");
    SubTask subTask1 = new SubTask("Договорится с бригадой", "Найти бригаду, обсудить стоимость и сроки");
    Epic epic1 = new Epic("Сделать ремонт в комнате", "Косметический ремонт в комнате на втором этаже");
    SubTask subTask2 = new SubTask("Выбрать обои", "Обдумать цветовую гамму, купить обои и клей ");
    Epic epic2 = new Epic("Перебрать мотор в тракторе", "Полная переборка с расточкой");
    SubTask subTask3 = new SubTask("Проточить коленвал", "Отвезти коленвал в расточку");
    SubTask subTask4 = new SubTask("Залить фундамент", "Заказать бетон, вызвать бетоннасос");
    SubTask subTask5 = new SubTask("Привезти доски", "Привезти доски с рынка");
    SubTask subTask6 = new SubTask("Просто подзадача к эпику...", "Добавление подзадачи к эпику по id эпика", 10007);
    SubTask subTask7 = new SubTask("Ещеодна подзадача к эпику...", "Добавление2 подзадачи к эпику по id эпика", 10009);
    public HashMap<Integer, Task> sortAllTask(Status status) { // Возможно сортировка не лишняя, но в задании про нее нет
        HashMap<Integer, Task> allNewTasks;
        HashMap<Integer, Task> allProgresTasks;
        HashMap<Integer, Task> allDoneTasks;
        allNewTasks = new HashMap<>();
        allProgresTasks = new HashMap<>();
        allDoneTasks = new HashMap<>();
        for (Integer taskId : taskManager.getTasks().keySet()) {
            Task task = taskManager.getTasks().get(taskId);
            if (task.getStatus() == Status.NEW) {
                allNewTasks.put(taskId, task);
            } else if (task.getStatus() == Status.IN_PROGRESS) {
                allProgresTasks.put(taskId, task);
            } else {
                allDoneTasks.put(taskId, task);
            }
        }
        for (Integer epicId : taskManager.getEpics().keySet()) {
            Epic epic = taskManager.getEpics().get(epicId);
            if (epic.getStatus() == Status.NEW) {
                allNewTasks.put(epicId, epic);
            } else if (epic.getStatus() == Status.IN_PROGRESS) {
                allProgresTasks.put(epicId, epic);
            } else {
                allDoneTasks.put(epicId, epic);
            }
        }
        if (status == Status.NEW) return allNewTasks;
        if (status == Status.IN_PROGRESS) return allProgresTasks;
        return allDoneTasks;

    }

    public String getShortTaskInfo(Task task) {   //Для наглядного вывода задач  в том числе и эпика с подзадачами
        StringBuilder taskInfo = new StringBuilder("None");
        String subtaskInfo;
        if (task.getTypeOfTask() == TypeOfTask.TASK) {
            taskInfo = new StringBuilder(task.getId() + "\n" +
                    task.getName() + "\n" +
                    task.getStatus());
        } else if (task.getTypeOfTask() == TypeOfTask.EPIC) {
            HashMap<Integer, SubTask> subtasks = ((Epic) task).getSubtacks();

            taskInfo = new StringBuilder(task.getId() + "\n" +
                    task.getName() + "\n" +
                    task.getStatus() + "\n \n");
            int i = 1;
            for (Integer subTaskKey : subtasks.keySet()) {
                subtaskInfo = "Подзадача " + i + "\n" +
                        subTaskKey + "\n" +
                        subtasks.get(subTaskKey).getName() + "\n" +
                        subtasks.get(subTaskKey).getStatus() + "\n";
                taskInfo.append(subtaskInfo);
                i++;
            }
        }
        return taskInfo.toString();
    }

    public void getAllTaskFromMap(HashMap<Integer, Task> hashMap) { //Представление всех элементов списков
        for (Integer key : hashMap.keySet()) {
            System.out.println(getShortTaskInfo(hashMap.get(key))); //если заменить sout на какой- нибудь метод для вывода
        }                                                           // куда-либо, возможно будет полезно
    }
}*/
