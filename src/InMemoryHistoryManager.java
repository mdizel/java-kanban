import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node<Task>> idLink = new HashMap<>();
    Node<Task> first;
    Node<Task> last;
    static int size = 0;

    private void linkLast(Task task) {
        Node<Task> oldLast = last;
        Node<Task> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        idLink.put(task.getId(), newNode);
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    private ArrayList<Task> getTasks(Node<Task> head) {
        ArrayList<Task> recentlyOpenTasks = new ArrayList<>();
        if (head != null) {
            recentlyOpenTasks.add(head.data);
            Node<Task> node = head.next;
            while (node != null) {
                recentlyOpenTasks.add(node.data);
                node = node.next;
            }
        }
        return recentlyOpenTasks;
    }

    private void removeNode(Node<Task> node) {
        node.data = null;
        node.next = null;
        node.prev = null;
        size--;
    }

    @Override
    public void remove(int id) {
        if (!idLink.containsKey(id)) {
            return;
        }
        Node<Task> oldNode = idLink.get(id);
        if (oldNode.next == null && oldNode.prev == null) {
        } else if (oldNode.next == null) {
            oldNode.prev.next = null;
            last = oldNode.prev;
        } else if (oldNode.prev == null) {
            oldNode.next.prev = null;
            first = oldNode.next;
        } else {
            oldNode.prev.next = oldNode.next;
            oldNode.next.prev = oldNode.prev;
        }
        removeNode(oldNode);
        idLink.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks(first);
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (idLink.containsKey(id)) {
            remove(id);
        }
        linkLast(task);
    }
}

