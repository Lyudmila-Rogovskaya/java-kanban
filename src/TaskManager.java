import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    // Методы для задач
    public Task createTask(Task task) { // создать задачу
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) { // обновить задачу
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public ArrayList<Task> getAllTasks() { // получить все задачи
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int id) { // получить задачу по id
        return tasks.get(id);
    }

    public void deleteTask(int id) { // удалить задачу
        tasks.remove(id);
    }

    public void deleteAllTasks() { // удалить все задачи
        tasks.clear();
    }

    // Методы для эпиков
    public Epic createEpic(Epic epic) { // создать эпик
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) { // обновить эпик
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    public ArrayList<Epic> getAllEpics() { // получить все эпики
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(int id) { // получить эпик по id
        return epics.get(id);
    }

    public void deleteEpic(int id) { // удалить эпик
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtasks::remove);
        }
    }

    public void deleteAllEpics() { // удалить все эпики вместе с подзадачами
        epics.clear();
        subtasks.clear();
    }

    // Методы для подзадач
    public Subtask createSubtask(Subtask subtask) { // создать подзадачу
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
        return subtask;
    }

    public void updateSubtask(Subtask subtask) { // обновить подзадачу
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    public ArrayList<Subtask> getAllSubtasks() { // получить все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(int id) { // получить подзадачу по id
        return subtasks.get(id);
    }

    public void deleteSubtasks() { // удалить подзадачу
        subtasks.clear();
    }

    public void deleteAllSubtasks() { //удалить все подзадачи
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) { // обновить статус эпика у подзадачи
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) { // получить эпик к подзадаче
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> result = new ArrayList<>();

        if (epic == null) {
            return result; // возвращаем пустой лист
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

}
