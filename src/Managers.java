public class Managers {
    static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static  HistoryManager getDefaultHistory(){
                return historyManager;
    }
}
