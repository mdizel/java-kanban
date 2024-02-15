public class Managers {
    static HistoryManager historyManagerForTest = getDefaultHistory();
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static  HistoryManager getDefaultHistory(){
                return new InMemoryHistoryManager();
    }
}
