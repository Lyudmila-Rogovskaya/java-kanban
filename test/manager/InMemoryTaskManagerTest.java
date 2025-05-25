package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addAndSearchForDifferentTypesOfTasksByIdTest() { // добавление и поиск задач разных типов по id
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);
        Task savedTask = taskManager.getAllTasks().get(0);

        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Epic savedEpic = taskManager.getAllEpics().get(0);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getAllSubtasks().get(0);

        assertNotNull(taskManager.getTaskById(savedTask.getId()), "Задача не найдена по id");
        assertNotNull(taskManager.getEpicById(savedEpic.getId()), "Эпик не найден по id");
        assertNotNull(taskManager.getSubtaskById(savedSubtask.getId()), "Подзадача не найдена по id");
    }

    @Test
    void tasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflictTest() { // конфликт задач заданным и сгенерированным id
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        taskManager.createTask(task1);
        Task savedTask1 = taskManager.getAllTasks().get(0);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        taskManager.createTask(task2);
        Task savedTask2 = taskManager.getAllTasks().get(1);
        assertNotEquals(savedTask1.getId(), savedTask2.getId(), "id должны быть разными");
    }

    @Test
    void epicCannotBeSubtaskAndSubtaskCannotBeEpicTest() { // эпик не может быть своей подзадачей и в обратную сторону
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        assertNotEquals(subtask.getId(), epic.getId(), "ID подзадачи не должен совпадать с ID эпика");
    }

    @Test
    void subtaskDoesNotReferenceNonExistentEpicTest() { // подзадача не ссылается на несуществующий эпик
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, 999);
        taskManager.createSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадача не должна быть создана");
    }

    @Test
    void addToHistoryTest() { // добавление в историю
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().get(0).getId();
        taskManager.getTaskById(taskId);

        ArrayList<Task> history = taskManager.getHistory();

        assertFalse(history.isEmpty(), "История не пустая");
        assertEquals(taskId, history.get(0).getId(), "ID задачи в истории совпадает");

    }

    @Test
    void managersCreatesAndReturnsNonNullObjectsTest() { // менеджер создает и возвращает не null объекты
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не проинициализирован");
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не проинициализирован");
    }

    @Test
    void immutabilityOfFieldsWhenAddingTest() { // неизменность полей задачи при добавлении

        Task originalTask = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(originalTask);
        Task savedTask = taskManager.getAllTasks().get(0);
        originalTask.setName("Новое название задачи");
        originalTask.setDescription("Новое описание задачи");
        originalTask.setStatus(Status.DONE);
        assertEquals("Задача", savedTask.getName(), "Название изменилось");
        assertEquals("Описание задачи", savedTask.getDescription(), "Описание изменилось");
        assertEquals(Status.NEW, savedTask.getStatus(), "Статус изменился");
    }

    @Test
    void addedTaskRetainPreviousVersionAndDataTest() { // удаляются предыдущие записи при добавлении дубликатов
        Task originalTask = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(originalTask);
        int taskId = taskManager.getAllTasks().get(0).getId();
        taskManager.getTaskById(taskId);

        Task updatedTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updatedTask.setId(taskId);
        taskManager.updateTask(updatedTask);
        taskManager.getTaskById(taskId);

        Task currentTask = taskManager.getTaskById(taskId);
        assertEquals("Обновленная задача", currentTask.getName(), "Сохраняется имя обновленной " +
                "задачи");

        List<Task> histori = taskManager.getHistory();
        assertEquals(1, histori.size(), "В истории должна быть 1 запись");
        assertEquals(updatedTask.getName(), histori.get(0).getName(), "После обновления задачи история " +
                "содержит актуальную версию, а не оригинальную");
    }

    @Test
    void updateTaskTest() { // обновление полей задачи
        Task originalTask = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(originalTask);

        Task updatedTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updatedTask.setId(originalTask.getId());

        taskManager.updateTask(updatedTask);
        Task savedTask = taskManager.getTaskById(originalTask.getId());

        assertEquals("Обновленная задача", updatedTask.getName(), "Название задачи изменилось");
        assertEquals("Обновленное описание", updatedTask.getDescription(), "Описание задачи " +
                "изменилось");
        assertEquals(Status.DONE, updatedTask.getStatus(), "Статус задачи обновился");
    }

    @Test
    void deleteTaskTest() { // удаление задачи
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        taskManager.createTask(task1);
        Task savedTask1 = taskManager.getAllTasks().get(0);

        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        taskManager.createTask(task2);
        Task savedTask2 = taskManager.getAllTasks().get(1);

        assertEquals(2, taskManager.getAllTasks().size(), "В списке 2 задачи");

        taskManager.deleteTask(savedTask1.getId());
        ArrayList<Task> tasksAfterDeletion = taskManager.getAllTasks();

        assertEquals(1, tasksAfterDeletion.size(), "Осталась 1 задача");
        assertNull(taskManager.getTaskById(savedTask1.getId()), "Удаленная задача не доступна по id");
        assertNotNull(taskManager.getTaskById(savedTask2.getId()), "Оставшаяся задача доступна по id");
        assertEquals(savedTask2.getId(), tasksAfterDeletion.get(0).getId(), "В списке осталась Задача2");
    }

    @Test
    void deleteAllTaskTest() { // удаление всех задач
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач пуст");
    }

    @Test
    void getEpicSubtaskTest() { // получение подзадачи к эпику
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());

        assertEquals(1, subtasks.size(), "В эпике 1 подзадача");
        assertEquals(subtask.getId(), subtasks.get(0).getId(), "Id подзадачи не совпадает");
    }

    @Test
    void updatingEpicStatusTheSubtaskStatusChangesTest() { // обновление статуса эпика при изменении статуса подзадач
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask2);

        Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        savedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask1);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика IN_PROGRESS");

        Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2.getId());
        savedSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask2);

        savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика DONE");
    }

    @Test
    void deleteSubtaskRemoveFromEpicTest() { // удаление подзадач из эпика
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());
        ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertTrue(subtasks.isEmpty(), "Список подзадач эпика пустой");
    }

    @Test
    void addTaskHistoryTest() { // задача добавляется в историю просмотров
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);
        Task savedTask = taskManager.getAllTasks().get(0);
        taskManager.getTaskById(savedTask.getId());
        taskManager.getTaskById(savedTask.getId());

        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "В истории должна быть 1 запись");
        assertEquals(savedTask, history.get(0), "Задача в истории не совпадает с исходной");
    }

    @Test
    void deleteAllSubtaskClearsEpicSubtaskListTest() { // удаление всех подзадач очищает список подзадач эпика
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask2);

        taskManager.deleteAllSubtasks();
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(savedEpic.getSubtaskIds().isEmpty(), "Все подзадачи эпика должны удалены");
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика после удаления всех подзадач - NEW");
    }

    @Test
    void deleteEpicDeleteSubtaskTest() { // удаление эпика удаляет его подзадачи
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epic.getId());
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи удалены при удалении эпика");
    }

    @Test
    void changingStatusSubtaskWithoutCallingUpdateSubtaskNotAffectEpicTest() { // изменение статуса подзадачи без вызова updateSubtask() не влияет на эпик
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        savedSubtask.setStatus(Status.DONE);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика не изменился без вызова updateSubtask");
    }

}
