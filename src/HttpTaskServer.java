import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
       private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        InputOutput inputOutput = new InputOutput();
        inputOutput.test();
        TaskManager taskManager = inputOutput.taskManager;
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/", new TaskHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
