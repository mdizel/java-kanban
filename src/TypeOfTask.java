public enum TypeOfTask {
    TASK("Задача"),
    EPIC("Эпик"),
    SUBTASK("Подзадача");

    @Override
    public String toString() {
        return title;
    }
    final String title;

    TypeOfTask(String title) {
        this.title = title;
    }
}

