package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveAndLoadEmptyFileTest() { // сохранение и загрузка пустого файла
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пустой");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустой");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустой");
    }

    @Test
    void saveAndLoadMultipleTaskTest() { // сохранение и загрузка нескольких задач
        Task task = new Task("Задача", "Описание задачи", Status.IN_PROGRESS);
        manager.createTask(task);
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.DONE, epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadedTasks = loadedManager.getAllTasks();
        assertEquals(1, loadedTasks.size(), "Должна быть одна задача после загрузки");

        Task loadedTask = loadedTasks.get(0);
        assertEquals(task.getName(), loadedTask.getName(), "Название задачи должно совпадать после загрузки");
        assertEquals(task.getDescription(), loadedTask.getDescription(), "Описание задачи должно совпадать " +
                "после загрузки");
        assertEquals(task.getStatus(), loadedTask.getStatus(), "Статус задачи должен совпадать после загрузки");

        List<Epic> loadedEpics = loadedManager.getAllEpics();
        assertEquals(1, loadedEpics.size(), "Должен быть один эпик после загрузки");

        Epic loadedEpic = loadedEpics.get(0);
        assertEquals(epic.getName(), loadedEpic.getName(), "Название эпика должно совпадать после загрузки");
        assertEquals(1, loadedEpic.getSubtaskIds().size(), "Эпик должен содержать ровно одну " +
                "подзадачу после загрузки");
        assertEquals(Status.DONE, loadedEpic.getStatus(), "Статус эпика должен быть DONE после загрузки");

        List<Subtask> loadedSubtasks = loadedManager.getAllSubtasks();
        assertEquals(1, loadedSubtasks.size(), "Должна быть одна подзадача после загрузки");

        Subtask loadedSubtask = loadedSubtasks.get(0);
        assertEquals(subtask.getName(), loadedSubtask.getName(), "Название подзадачи должно совпадать " +
                "после загрузки");
        assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId(), "Id эпика подзадачи должно совпадать " +
                "после загрузки");
    }

    @Test
    void epicStatusUpdateSubtaskTest() { // статус эпика после добавления подзадачи
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Начальный статус эпика");

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.DONE, epic.getId());
        manager.createSubtask(subtask);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус эпика после добавления DONE подзадачи");
    }

}
