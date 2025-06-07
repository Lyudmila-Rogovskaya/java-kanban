package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            Path tempPath = Files.createTempFile("tasks", ".csv");
            tempFile = tempPath.toFile();
            return new FileBackedTaskManager(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    void saveAndLoadEmptyFileTest() { // сохранение и загрузка пустого файла
        FileBackedTaskManager manager = createTaskManager();
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пустой");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустой");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустой");
    }

    @Test
    void saveAndLoadMultipleTaskTest() { // сохранение и загрузка нескольких задач
        FileBackedTaskManager manager = createTaskManager();
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
        FileBackedTaskManager manager = createTaskManager();
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Начальный статус эпика");

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.DONE, epic.getId());
        manager.createSubtask(subtask);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус эпика после добавления DONE подзадачи");
    }

    @Test
    void throwExceptionSavingFileTest() { // исключения при сохранении файла
        File invalidFile = new File("/invalid/path/tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("Task", "Description", Status.NEW);

        assertThrows(ManagerSaveException.class, () -> manager.createTask(task), "Должно быть исключение при сохранении");
    }

    @Test
    void throwWhenLoadingFileTest() { // исключения при загрузке файла
        createTaskManager();
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile), "Не должно быть исключения при загрузке");
    }

}
