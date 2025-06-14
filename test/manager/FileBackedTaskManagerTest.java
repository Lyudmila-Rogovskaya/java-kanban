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
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пустой");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустой");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустой");
    }

    @Test
    void saveAndLoadMultipleTaskTest() { // сохранение и загрузка нескольких задач
        Task task = new Task("Задача", "Описание задачи", Status.IN_PROGRESS);
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks(),
                "Списки задач не совпадают");
        assertEquals(taskManager.getAllEpics(), loadedManager.getAllEpics(),
                "Списки эпиков не совпадают");
        assertEquals(taskManager.getAllSubtasks(), loadedManager.getAllSubtasks(),
                "Списки подзадач не совпадают");
    }

    @Test
    void epicStatusUpdateSubtaskTest() { // статус эпика после добавления подзадачи
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Начальный статус эпика");

        Subtask subtask = new Subtask("Подзадача", "Описание", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус эпика после добавления DONE подзадачи");
    }

    @Test
    void throwExceptionSavingFileTest() { // исключения при сохранении файла
        File invalidFile = new File("/invalid/path/tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("Task", "Description", Status.NEW);

        assertThrows(ManagerSaveException.class, () -> manager.createTask(task),
                "Должно быть исключение при сохранении");
    }

    @Test
    void throwWhenLoadingFileTest() { // исключения при загрузке файла
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile),
                "Не должно быть исключения при загрузке");
    }

}
