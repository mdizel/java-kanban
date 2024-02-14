import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
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
        if (recentlyOpenTasks.size() < 10) {
            recentlyOpenTasks.add(task);
        } else {
            recentlyOpenTasks.removeFirst();
            recentlyOpenTasks.add(task);
        }
    }
}
