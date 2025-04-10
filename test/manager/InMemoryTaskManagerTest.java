package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        assertNotNull(taskManager.getTaskById(savedTask.getId()));
        assertNotNull(taskManager.getEpicById(savedEpic.getId()));
        assertNotNull(taskManager.getSubtaskById(savedSubtask.getId()));
    }

    @Test
    void TasksWithTheSpecifiedIdAndTheGeneratedIdDoNotConflictTest() { // конфликт задач заданным и сгенерированным id
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        taskManager.createTask(task1);
        Task savedTask1 = taskManager.getAllTasks().get(0);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        taskManager.createTask(task2);
        Task savedTask2 = taskManager.getAllTasks().get(1);
        assertNotEquals(savedTask1.getId(), savedTask2.getId(), "id должны быть разными");
    }

    @Test
    void EpicCannotBeSubtaskAndSubtaskCannotBeEpicTest() { // эпик не может быть своей подзадачей и в обратную сторону
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        assertNotEquals(subtask.getId(), epic.getId(), "ID подзадачи не должен совпадать с ID эпика");
    }

    @Test
    void SubtaskDoesNotReferenceNonExistentEpicTest() { // подзадача не ссылается на несуществующий эпик
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, 999);
        taskManager.createSubtask(subtask);
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадача не должна быть создана");
    }

    @Test
    void addToHistoryTest() { // добавление в историю
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "История должна содержать 1 задачу");
    }

    @Test
    void ManagersCreatesAndReturnsNonNullObjectsTest() { // менеджер создает и возвращает не null объекты
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не проинициализирован");
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не проинициализирован");
    }

    @Test
    void ImmutabilityOfFieldsWhenAddingTest() { // неизменность полей задачи при добавлении

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
    void AddedTaskRetainPreviousVersionAndDataTest() { //добавленная задача сохраняет предыдущую версию и данные
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        Task updatedTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);
        assertEquals("Описание задачи", taskManager.getHistory().get(0).getDescription());
    }

}
