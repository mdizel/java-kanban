import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    private final IOException e;

    public ManagerSaveException(String message, IOException e) {
        super(message);
        this.e = e;
    }
}
