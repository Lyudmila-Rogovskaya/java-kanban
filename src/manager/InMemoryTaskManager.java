package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextId = 1;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    // Методы для задач
    @Override
    public void createTask(Task task) { // создать задачу

        Task newTask = new Task(
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getStartTime(),
                task.getDuration()
        );
        newTask.setId(nextId++);

        if (hasIntersections(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующей");
        }

        task.setId(nextId++);
        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) { // обновить задачу
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }

            if (hasIntersections(task)) {
                prioritizedTasks.add(oldTask);
                throw new TimeConflictException("Обновление задачи приводит к пересечению по времени");
            }

            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() { // получить все задачи
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) { // получить задачу по id
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTask(int id) { // удалить задачу
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }
    }

    @Override
    public void deleteAllTasks() { // удалить все задачи
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }
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
            return;
        }
        if (hasIntersections(epic)) {
            throw new TimeConflictException("Обновление эпика приводит к пересечению");
        }

        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        updateEpicStatus(savedEpic);
    }

    @Override
    public ArrayList<Epic> getAllEpics() { // получить все эпики
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) { // получить эпик по id
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteEpic(int id) { // удалить эпик
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    historyManager.remove(subtaskId);
                    if (subtask.getStartTime() != null) {
                        prioritizedTasks.remove(subtask);
                    }
                }
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() { // удалить все эпики вместе с подзадачами
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }
        subtasks.clear();

        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
    }

    // Методы для подзадач
    @Override
    public void createSubtask(Subtask subtask) { // создать подзадачу
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        if (hasIntersections(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени с существующей");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTime(epic);

        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) { // обновить подзадачу
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.remove(oldSubtask);
            }

            if (hasIntersections(subtask)) {
                if (oldSubtask.getStartTime() != null) {
                    prioritizedTasks.add(oldSubtask);
                }
                throw new TimeConflictException("Обновление подзадачи приводит к пересечению по времени");
            }

            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() { // получить все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(int id) { // получить подзадачу по id
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer id) { // удалить подзадачу
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllSubtasks() { //удалить все подзадачи
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    protected void updateEpicStatus(Epic epic) { // обновить статус эпика у подзадачи
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = epicSubtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean allNew = epicSubtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);

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

        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Task> getHistory() { // получить историю
        return new ArrayList<>(historyManager.getHistory());
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
        } else {
            epic.updateTime(subtasks);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasIntersections(Task task) { // проверка пересечения задачи с любой другой
        if (task.getStartTime() == null) {
            return false;
        }

        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(t -> t.getStartTime() != null &&
                        start.isBefore(t.getEndTime()) &&
                        t.getStartTime().isBefore(end));
    }

}
