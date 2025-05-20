package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 1;

    // Методы для задач
    @Override
    public void createTask(Task task) { // создать задачу
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(nextId++);
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateTask(Task task) { // обновить задачу
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() { // получить все задачи
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) { // получить задачу по id
        Task original = tasks.get(id);
        if (original != null) {
            Task copy = new Task(original);
            historyManager.add(copy);
            return copy;
        }
        return null;
    }

    @Override
    public void deleteTask(int id) { // удалить задачу
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() { // удалить все задачи
        tasks.clear();
    }

    // Методы для эпиков
    @Override
    public void createEpic(Epic epic) { // создать эпик
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) { // обновить эпик
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            updateEpicStatus(savedEpic);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() { // получить все эпики
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) { // получить эпик по id
        Epic original = epics.get(id);
        if (original != null) {
            Epic copy = new Epic(original);
            historyManager.add(copy);
            return copy;
        }
        return null;
    }

    @Override
    public void deleteEpic(int id) { // удалить эпик
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() { // удалить все эпики вместе с подзадачами
        epics.clear();
        subtasks.clear();
    }

    // Методы для подзадач
    @Override
    public void createSubtask(Subtask subtask) { // создать подзадачу
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновить подзадачу
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() { // получить все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) { // получить подзадачу по id
        Subtask original = subtasks.get(id);
        if (original != null) {
            Subtask copy = new Subtask(original);
            historyManager.add(copy);
            return copy;
        }
        return null;
    }

    @Override
    public void deleteSubtask(Integer id) { // удалить подзадачу
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
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

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) { // получить эпик к подзадаче
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> result = new ArrayList<>();

        if (epic == null) {
            return result;
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Task> getHistory() { // получить историю
        return new ArrayList<>(historyManager.getHistory());
    }

}
