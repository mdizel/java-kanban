import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    public static Path path = Paths.get("D:", "projectIdea", "files", "data.csv");
    public static String fileName = String.valueOf(path);

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileManager() {
        return new FileBackedTaskManager(fileName);
    }
}
