import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Объект не создан");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager memHisManager = Managers.getDefaultHistory();
        assertNotNull(memHisManager, "Объект не создан");
    }
}