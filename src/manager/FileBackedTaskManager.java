package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager { // логика сохранения в файл

    private final File file;
    private boolean isLoading = false;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() { // сохранить текущее состояние всех задач, подзадач и эпиков в CSV
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(CSVTaskFormat.toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(CSVTaskFormat.toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(CSVTaskFormat.toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) { // восстановить из файла
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.isLoading = true;

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = CSVTaskFormat.fromString(line);
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                }

                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }
            }

            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }

            for (Task task : manager.getAllTasks()) {
                manager.prioritizedTasks.add(task);
            }

            for (Subtask subtask : manager.getAllSubtasks()) {
                manager.prioritizedTasks.add(subtask);
            }

            for (Epic epic : manager.getAllEpics()) {
                List<Subtask> subtasks = manager.getEpicSubtasks(epic.getId());
                epic.updateTime(subtasks);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + file.getName(), e);
        } finally {
            manager.isLoading = false;
        }
        return manager;
    }

    // Методы для задач
    @Override
    public void createTask(Task task) { // создать задачу
        super.createTask(task);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void updateTask(Task task) { // обновить задачу
        super.updateTask(task);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteTask(int id) { // удалить задачу
        super.deleteTask(id);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteAllTasks() { // удалить все задачи
        super.deleteAllTasks();
        if (!isLoading) {
            save();
        }
    }

    // Методы для эпиков
    @Override
    public void createEpic(Epic epic) { // создать эпик
        super.createEpic(epic);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void updateEpic(Epic epic) { // обновить эпик
        super.updateEpic(epic);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteEpic(int id) { // удалить эпик
        super.deleteEpic(id);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteAllEpics() { // удалить все эпики вместе с подзадачами
        super.deleteAllEpics();
        if (!isLoading) {
            save();
        }
    }

    // Методы для подзадач
    @Override
    public void createSubtask(Subtask subtask) { // создать подзадачу
        super.createSubtask(subtask);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновить подзадачу
        super.updateSubtask(subtask);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteSubtask(Integer id) { // удалить подзадачу
        super.deleteSubtask(id);
        if (!isLoading) {
            save();
        }
    }

    @Override
    public void deleteAllSubtasks() { //удалить все подзадачи
        super.deleteAllSubtasks();
        if (!isLoading) {
            save();
        }
    }

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("\n=== Дополнительное задание к ТЗ-7. Реализуем пользовательский сценарий ===");

        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task01 = new Task("Задача01", "Описание задачи01", Status.NEW);
        manager.createTask(task01);
        Task task02 = new Task("Задача02", "Описание задачи02", Status.NEW);
        manager.createTask(task02);
        Epic epic01 = new Epic("Эпик01", "Описание эпика01");
        manager.createEpic(epic01);
        Subtask subtask01 = new Subtask("Подзадача01", "Описание подзадачи01", Status.NEW, epic01.getId());
        manager.createSubtask(subtask01);
        Subtask subtask02 = new Subtask("Подзадача02", "Описание подзадачи02", Status.NEW, epic01.getId());
        manager.createSubtask(subtask02);
        Subtask subtask03 = new Subtask("Подзадача03", "Описание подзадачи03", Status.NEW, epic01.getId());
        manager.createSubtask(subtask03);
        Epic epic02 = new Epic("Эпик02", "Описание эпика02");
        manager.createEpic(epic02);

        manager.save();

        System.out.println("\nДанные загружены из файла");
    }

}
