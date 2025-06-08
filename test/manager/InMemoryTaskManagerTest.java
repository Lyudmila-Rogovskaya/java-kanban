package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void managersCreatesAndReturnsNonNullObjectsTest() { // менеджер создает и возвращает не null объекты
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не проинициализирован");
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не проинициализирован");
    }

}
