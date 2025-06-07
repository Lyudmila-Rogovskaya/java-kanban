package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
    }

    @Test
    void epicEqualityByIdTest() { // равенство эпиков по id
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        epic1.setId(1);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void epicTimeCalculationTest() { // расчёт времени эпика
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.DONE, epicId,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.DONE, epicId,
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), savedEpic.getStartTime(),
                "Начальное время эпика должно соответствовать самой ранней подзадаче");
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 30), savedEpic.getEndTime(),
                "Время завершения эпика должно соответствовать времени окончания самой поздней подзадачи");
        assertEquals(Duration.ofMinutes(90), savedEpic.getDuration(),
                "Продолжительность эпика должна быть равна сумме продолжительности подзадач");
    }

    @Test
    void epicStatusAllSubtasksNewTest() { // расчёт статуса эпика - граничные условия - все подзадачи со статусом NEW
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус должен быть NEW");
    }

    @Test
    void epicStatusAllSubtasksDoneTest() { // расчёт статуса эпика - граничные условия - все подзадачи со статусом DONE
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус должен быть DONE");
    }

    @Test
    void epicStatusSubtasksNewAndDoneTest() { // расчёт статуса эпика - граничные условия - подзадачи со статусом NEW и DONE
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusAllSubtasksInProgress() { // расчёт статуса эпика - граничные условия - подзадачи со статусом IN_PROGRESS
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");
    }

}
