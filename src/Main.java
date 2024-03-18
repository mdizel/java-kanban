import java.io.File;

public class Main {
    static  String fileName = Managers.fileName;
   static File file = new File(fileName);
    public static void main(String[] args) {
  InputOutput inputOutput = new InputOutput();
        inputOutput.test();
       // FileBackedTaskManager fileManager=  FileBackedTaskManager.loadFromFile(file);
        //System.out.println(fileManager.getTaskAndSubtasks());
        //System.out.println(fileManager.getMemHisManager().getHistory());
    }
}
