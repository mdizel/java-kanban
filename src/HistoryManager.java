import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    int size();

    void add(Task task);

    void remove(int id);
}
