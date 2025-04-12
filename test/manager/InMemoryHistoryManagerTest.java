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
        assertEquals(2, history.size(), "В истории должно быть 2 записи");
        assertEquals(task, history.get(0), "Первая задача не совпадает");
        assertEquals(task, history.get(1), "Вторая задача не совпадает");
    }

    @Test
    void maxElementsHistory10Test() { // вместимость истории не больше 10 элементов
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Задача" + i, "Описание задачи", Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История содержит 10 элементов");
        assertEquals(2, history.get(0).getId(), "Первый элемент истории неверный");
        assertEquals(11, history.get(9).getId(), "Последний элемент истории неверный");
    }

}
