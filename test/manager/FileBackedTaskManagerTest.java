package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

        assertEquals(manager.getAllTasks(), loadedManager.getAllTasks(), "Списки задач не совпадают");
        assertEquals(manager.getAllEpics(), loadedManager.getAllEpics(), "Списки эпиков не совпадают");
        assertEquals(manager.getAllSubtasks(), loadedManager.getAllSubtasks(), "Списки подзадач не совпадают");
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
