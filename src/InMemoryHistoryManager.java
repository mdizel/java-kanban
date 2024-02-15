import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    final static int MAX_HISTORY_SIZE = 10;
    ArrayList<Task> recentlyOpenTasks = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {

        return getRecentlyOpenTasks();
    }

    public ArrayList<Task> getRecentlyOpenTasks() {
        return recentlyOpenTasks;
    }

    @Override
    public void add(Task task) {
        if (recentlyOpenTasks.size() < MAX_HISTORY_SIZE) {
            recentlyOpenTasks.add(task);
        } else {
            recentlyOpenTasks.removeFirst();
            recentlyOpenTasks.add(task);
        }
    }
}
