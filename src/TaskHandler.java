import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    TaskManager taskManager;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathSplit = requestPath.split("/");
        String pathBeginning = pathSplit[1];
        switch (pathBeginning) {
            case "tasks": {
                handleTaskRequest(exchange);
                break;
            }
            case "epics": {
                handleEpicRequest(exchange);
                break;
            }
            case "subtasks": {
                handleSubtaskRequest(exchange);
                break;
            }
            case "history": {
                handleHistoryRequest(exchange);
                break;
            }
            case "prioritized": {
                handlePrioritizedRequest(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Bad request", 404);
        }
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();
        if (pathSplit.length == 3) {
            Optional<Integer> idOpt = getId(pathSplit);
            if (idOpt.isEmpty()) {
                writeResponse(exchange, "Wrong task Id", 404);
                return;
            }
            int id = idOpt.get();
            Task task = taskManager.getTask(id);
            if (task == null) {
                writeResponse(exchange, "Task Id " + id + " not found", 404);
                return;
            }
            if (requestMethod.equals("GET")) {
                writeResponse(exchange, gson.toJson(task), 200);
            } else if (requestMethod.equals("DELETE")) {
                taskManager.deleteTask(id);
                writeResponse(exchange, "Задача " + id + " успешно удалена", 200);
            }
        } else if (pathSplit.length == 2) {
            if (requestMethod.equals("GET")) {
                writeResponse(exchange, gson.toJson(taskManager.getTasksList()), 200);
            } else if (requestMethod.equals("POST")) {
                Optional<Task> taskOpt = parseBody(exchange.getRequestBody());
                if (taskOpt.isEmpty()) {
                    writeResponse(exchange, "Empty request", 404);
                    return;
                }
                try {
                    Task taskNew = taskOpt.get();
                    if (taskNew.getId() == 0) {
                        taskManager.setTask(taskNew);
                        writeResponse(exchange, "Задача успешно добавлена", 201);
                    } else {
                        if (!taskManager.changeTask(taskNew)) {
                            writeResponse(exchange, "Task Id " + taskNew.getId() + " not found", 404);
                            return;
                        }
                        writeResponse(exchange, "Задача " + taskNew.getId() + " успешно обновлена",
                                201);
                    }
                } catch (TaskTimeException e) {
                    writeResponse(exchange, e.getMessage(), 406);
                }
            }
        } else {
            writeResponse(exchange, "Bad request", 404);
        }
    }

    private void handleEpicRequest(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();
        if (pathSplit.length == 3 || pathSplit.length == 4) {
            Optional<Integer> idOpt = getId(pathSplit);
            if (idOpt.isEmpty()) {
                writeResponse(exchange, "Wrong Epic Id", 404);
                return;
            }
            int id = idOpt.get();
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                writeResponse(exchange, "Epic Id " + id + " not found", 404);
                return;
            }
            if (requestMethod.equals("GET")) {
                if (pathSplit.length == 3) {
                    writeResponse(exchange, gson.toJson(epic), 200);
                } else if (pathSplit[3].equals("subtasks")) {
                    writeResponse(exchange, gson.toJson(taskManager.getSubtaskFromEpicList(id)), 200);
                }
            } else if (requestMethod.equals("DELETE")) {
                taskManager.deleteEpic(id);
                writeResponse(exchange, "Эпик " + id + " успешно удален", 200);
            }
        } else if (pathSplit.length == 2) {
            if (requestMethod.equals("GET")) {
                writeResponse(exchange, gson.toJson(taskManager.getEpicsList()), 200);
            } else if (requestMethod.equals("POST")) {
                Optional<Epic> epicOpt = parseEpicBody(exchange.getRequestBody());
                if (epicOpt.isEmpty()) {
                    writeResponse(exchange, "Empty request", 404);
                    return;
                }
                try {
                    Epic epicNew = epicOpt.get();
                    if (epicNew.getId() == 0) {
                        taskManager.setEpic(epicNew);
                        writeResponse(exchange, "Эпик успешно добавлен", 201);
                    } else {
                        if (!taskManager.changeEpic(epicNew)) {
                            writeResponse(exchange, "Epic Id " + epicNew.getId() + " not found", 404);
                            return;
                        }
                        writeResponse(exchange, "Эпик " + epicNew.getId() + " успешно обновлен",
                                201);
                    }
                } catch (TaskTimeException e) {
                    writeResponse(exchange, e.getMessage(), 406);
                }
            }
        } else {
            writeResponse(exchange, "Bad request", 404);
        }
    }


    private void handleSubtaskRequest(HttpExchange exchange) throws IOException {
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();
        if (pathSplit.length == 3) {
            Optional<Integer> idOpt = getId(pathSplit);
            if (idOpt.isEmpty()) {
                writeResponse(exchange, "Wrong SubTask Id", 404);
                return;
            }
            int id = idOpt.get();
            SubTask subTask = taskManager.getSubTask(id);
            if (subTask == null) {
                writeResponse(exchange, "Task Id " + id + " not found", 404);
                return;
            }
            if (requestMethod.equals("GET")) {
                writeResponse(exchange, gson.toJson(subTask), 200);
            } else if (requestMethod.equals("DELETE")) {
                taskManager.deleteSubTask(id);
                writeResponse(exchange, "Подзадача " + id + " успешно удалена", 200);
            }
        } else if (pathSplit.length == 2) {
            if (requestMethod.equals("GET")) {
                writeResponse(exchange, gson.toJson(taskManager.getSubtasksList()), 200);
            } else if (requestMethod.equals("POST")) {
                Optional<SubTask> subTaskOpt = parseSubtaskBody(exchange.getRequestBody());
                if (subTaskOpt.isEmpty()) {
                    writeResponse(exchange, "Empty request", 404);
                    return;
                }
                try {
                    SubTask subTaskNew = subTaskOpt.get();
                    if (subTaskNew.getId() == 0) {
                        taskManager.setSubTask(subTaskNew);
                        writeResponse(exchange, "Подзадача успешно добавлена", 201);
                    } else {
                        if (!taskManager.changeSubTask(subTaskNew)) {
                            writeResponse(exchange, "SubTask Id " + subTaskNew.getId() + " not found",
                                    404);
                            return;
                        }
                        writeResponse(exchange, "Подзадача " + subTaskNew.getId() + " успешно обновлена",
                                201);
                    }
                } catch (TaskTimeException e) {
                    writeResponse(exchange, e.getMessage(), 406);
                }
            }
        } else {
            writeResponse(exchange, "Bad request", 404);
        }
    }


    private void handleHistoryRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
    }

    private void handlePrioritizedRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Optional<Integer> getId(String[] pathSplit) {
        try {
            return Optional.of(Integer.parseInt(pathSplit[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Optional<Task> parseBody(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            return Optional.empty();
        }
        Task task = gson.fromJson(jsonElement, Task.class);
        return Optional.of(task);
    }

    private Optional<Epic> parseEpicBody(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            return Optional.empty();
        }
        Epic epic = gson.fromJson(jsonElement, Epic.class);
        return Optional.of(epic);
    }

    private Optional<SubTask> parseSubtaskBody(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), DEFAULT_CHARSET);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            return Optional.empty();
        }
        SubTask subTask = gson.fromJson(jsonElement, SubTask.class);
        return Optional.of(subTask);
    }
}
