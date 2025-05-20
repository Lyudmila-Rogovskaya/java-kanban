package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyNullTest() { // получение пустой и не пустой истории
        ArrayList<Task> emptyHistory = historyManager.getHistory();
        assertTrue(emptyHistory.isEmpty(), "История должна быть пустой");
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        ArrayList<Task> nonEmptyHistory = historyManager.getHistory();
        assertFalse(nonEmptyHistory.isEmpty(), "История НЕ должна быть пустой");
        assertEquals(1, nonEmptyHistory.size(), "В истории должно быть 1 запись");
    }

    @Test
    void addTasksInHistoryTest() { // добавление в историю одинаковых задач
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна быть 1 запись");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с исходной");
    }

    @Test
    void historyOrderAndRemoveTest() { // порядок и удаление задач
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Задача3", "Описание задачи3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory(), "История сохраняет порядок" +
                "добавления задач");

        historyManager.add(task2);
        assertEquals(List.of(task1, task3, task2), historyManager.getHistory(), "При повторном добавлении " +
                "задача должна перемещаться в конец истории");

        historyManager.remove(1);
        assertEquals(List.of(task3, task2), historyManager.getHistory(), "После удаления задачи из начала " +
                "истории порядок корректируется");

        historyManager.remove(3);
        assertEquals(List.of(task2), historyManager.getHistory(), "После удаления задачи из средины истории " +
                "остается нужная задача");

        historyManager.remove(2);
        assertTrue(historyManager.getHistory().isEmpty(), "После удаления последней задачи история пустая");
    }

}
