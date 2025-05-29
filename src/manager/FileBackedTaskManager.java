package manager;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager { // логика сохранения в файл

    private final File file;
    private boolean isLoading = false;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() { // сохранить текущее состояние всех задач, подзадач и эпиков в CSV
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName(), e);
        }
    }

    protected String toString(Task task) { // преобразовать задачу в строку CSV
        if (task instanceof Epic) {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "EPIC",
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.join(",",
                    String.valueOf(subtask.getId()),
                    "SUBTASK",
                    subtask.getName(),
                    subtask.getStatus().toString(),
                    subtask.getDescription(),
                    String.valueOf(subtask.getEpicId())
            );
        } else {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "TASK",
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        }
    }

    private static Task fromString(String value) { // создать задачи из строки
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
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

                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
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

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicStatus(epic);
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

}
