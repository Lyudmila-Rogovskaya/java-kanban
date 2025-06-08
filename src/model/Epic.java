package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) { // конструктор
        super(name, description, Status.NEW, null, Duration.ZERO);
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

        LocalDateTime minStart = LocalDateTime.MAX;
        LocalDateTime maxEnd = LocalDateTime.MIN;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            Duration duration = subtask.getDuration() != null ? subtask.getDuration() : Duration.ZERO;
            totalDuration = totalDuration.plus(duration);

            if (subtask.getStartTime() != null) {
                if (subtask.getStartTime().isBefore(minStart)) {
                    minStart = subtask.getStartTime();
                }

                LocalDateTime endTime = subtask.getEndTime();
                if (endTime != null && endTime.isAfter(maxEnd)) {
                    maxEnd = endTime;
                }
            }
        }

        this.startTime = minStart.equals(LocalDateTime.MAX) ? null : minStart;
        this.endTime = maxEnd.equals(LocalDateTime.MIN) ? null : maxEnd;
        this.duration = totalDuration;

    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
