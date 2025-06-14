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
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        task2.setId(2);
        task3 = new Task("Задача3", "Описание задачи3", Status.NEW);
        task3.setId(3);
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
    void addIdenticalTasksInHistoryTest() { // добавление в историю одинаковых задач
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна быть 1 запись");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с исходной");
    }

    @Test
    void addTasksInHistoryTest() { // добавление задач в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(List.of(task1, task2, task3), historyManager.getHistory(), "История сохраняет порядок" +
                "добавления задач");
    }

    @Test
    void addingRepetitiveTasksTest() { // добавление повторяющихся задач
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        assertEquals(List.of(task1, task3, task2), historyManager.getHistory(), "При повторном добавлении " +
                "задача должна перемещаться в конец истории");
    }

    @Test
    void deleteTaskFromBeginningTest() { // удаление задачи из начала истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        assertEquals(List.of(task2, task3), historyManager.getHistory(), "После удаления задачи из начала " +
                "истории порядок корректируется");
    }

    @Test
    void deleteTaskFromMiddleTest() { // удаление задачи из середины истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        assertEquals(List.of(task1, task3), historyManager.getHistory(), "После удаления задачи из средины " +
                "истории остаются нужные задачи");
    }

    @Test
    void deleteTaskFromEndTest() { // удаление задачи с конца истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);
        assertEquals(List.of(task1, task2), historyManager.getHistory(), "После удаления задачи с конца " +
                "истории остаются нужные задачи");
    }

    @Test
    void removeAllTasksTest() { // удаление всех задач
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(3);
        assertTrue(historyManager.getHistory().isEmpty(), "После удаления задач история пустая");
    }

    @Test
    void removeFromEmptyHistoryTest() { // удаление задач из пустой истории
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty(), "После удаления задач из пустой истории " +
                "возвращается список без ошибок");
    }

    @Test
    void shouldHandleEmptyHistoryTest() { // для всех методов интерфейса - граничные условия - пустая история задач
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldHandleDuplicatesTest() { // для всех методов интерфейса - граничные условия - дублирование
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Дубликаты не должны создаваться");
    }

}
