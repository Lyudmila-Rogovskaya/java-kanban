package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

    @Test
    void shouldCheckSubtaskHasEpicTest() { // наличие связанного эпика
        T manager = createTaskManager();
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        assertNotNull(manager.getEpicById(epic.getId()), "Эпик должен существовать");
        assertEquals(epic.getId(), subtask.getEpicId(), "Id эпика должно совпадать");
    }

    @Test
    void shouldCheckEpicStatusCalculationTest() { // расчёт статуса на основании состояния подзадач
        T manager = createTaskManager();
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.DONE, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
    }

    @Test
    void shouldDetectTimeOverlapsTest() { // проверка пересечения временных интервалов задач
        T manager = createTaskManager();
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW, time, duration);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW, time.plusMinutes(30), duration);

        manager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> manager.createTask(task2),
                "Должно быть пересечение интервалов");
    }

    @Test
    void shouldPreventTimeConflictsTest() { // пересечение интервалов
        T manager = createTaskManager();
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);

        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW, start, duration);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW, start.plusMinutes(30), duration);

        manager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> manager.createTask(task2),
                "Должно быть пересечение интервалов");
    }

    @Test
    void prioritizedTasksOrderTest() { // проверка порядка выполнения приоритетных задач
        T manager = createTaskManager();
        LocalDateTime now = LocalDateTime.now();

        Task earlyTask = new Task("Ранняя задача", "Описание ранней задачи",
                Status.NEW, now, Duration.ofHours(1));
        Task secondaryTask = new Task("Поздняя задача", "Описание поздней задачи",
                Status.NEW, now.plusHours(2), Duration.ofHours(1));

        manager.createTask(secondaryTask);
        manager.createTask(earlyTask);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(now, prioritized.get(0).getStartTime(), "Первой должна быть ранняя задача");
        assertEquals(now.plusHours(2), prioritized.get(1).getStartTime(), "Второй должна быть поздняя задача");
    }

}
