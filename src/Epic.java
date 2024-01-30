import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, SubTask> subtacks = new HashMap<>();

    public Epic(String name, String description){
        super(name, description);
        typeOfTask = TypeOfTask.EPIC;
            }

    public HashMap<Integer, SubTask> getSubtacks() {
        return subtacks;
    }
    public void setSubtacks(HashMap<Integer, SubTask> subtacks) {
        this.subtacks = subtacks;
    }



    @Override
    public String toString() {
        return  typeOfTask +
                "    Код: " + id + ",\n" +
                "    Название: " + name + ",\n" +
                "    Описание: " + description + ",\n"+
                "    Статус: " + status +"\n" +
        "          "  + subtacks+"\n";
    }
}