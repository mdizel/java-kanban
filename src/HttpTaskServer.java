import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        InputOutput inputOutput = new InputOutput();
        inputOutput.test();
        TaskManager taskManager = inputOutput.taskManager;
        httpServer = startServer(taskManager);
        httpServer.stop(2);
    }

    public static HttpServer startServer(TaskManager taskManager) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/", new TaskHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        return httpServer;
    }
}
