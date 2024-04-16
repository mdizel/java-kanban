import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import java.io.IOException;
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

    public TaskHandler(TaskManager taskManager){
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
            if (requestMethod.equals("GET") && pathSplit.length == 2){
                //writeResponse(exchange, gson.toJson("Что то вышло"), 200);
                writeResponse(exchange, gson.toJson(taskManager.getTasksList()), 200);
            } else if (requestMethod.equals("GET") && pathSplit.length == 3){
                Optional<Integer> idOpt = getId(pathSplit);
                if (idOpt.isEmpty()) {
                    writeResponse(exchange, "Wrong task Id", 404);
                    return;
                }
                int id = idOpt.get();
                Task task = taskManager.getTask(id);
                if(task == null){
                    writeResponse(exchange, "Task not found", 404);
                    return;
                }
                writeResponse(exchange, gson.toJson(task), 200);
            }
    }

    private void handleEpicRequest(HttpExchange exchange) throws IOException{

    }

    private void handleSubtaskRequest(HttpExchange exchange) throws IOException{

    }

    private void handleHistoryRequest(HttpExchange exchange) throws IOException{

    }

    private void handlePrioritizedRequest(HttpExchange exchange) throws IOException{

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

    /*private Endpoints getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts[1].equals("tasks")) {
            return Endpoints.TASKS;
        }
        if (pathParts[1].equals("epics")) {
            return Endpoints.EPICS;
        }

        return Endpoints.UNKNOWN;
    }*/

  /*  private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }*/
}
