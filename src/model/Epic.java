package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) { // конструктор
        super(name, description, Status.NEW, null, Duration.ZERO);
    }

    public Epic(Epic other) {
        super(other);
        this.subtaskIds.addAll(other.subtaskIds);
        this.endTime = other.endTime;
    }

    public ArrayList<Integer> getSubtaskIds() { // получить подзадачу
        return subtaskIds;
    }

    public void addSubtaskId(int id) { // добавить подзадачу
        subtaskIds.add(id);
    }

    public void removeSubtaskId(Integer id) { // удалить ид подзадачи
        subtaskIds.remove(id);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void updateTime(List<Subtask> subtasks) { // расчёт времени эпика
        if (subtasks == null || subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
            return;
        }

        this.duration = subtasks.stream()
                .map(subtask -> subtask.getDuration() != null ? subtask.getDuration() : Duration.ZERO)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
