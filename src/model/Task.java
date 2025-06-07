package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private String description; // описание задачи
    private Status status;
    private int id;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) { // конструктор
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status) { // конструктор
        this(name, description, status, null, Duration.ZERO);
    }

    public Task(Task other) { // конструктор
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
        this.id = other.id;
        this.startTime = other.startTime;
        this.duration = other.duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plus(duration) : null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы объектов
        Task task = (Task) obj; // открываем доступ к полям другого объекта
        return (id == task.id); // проверяем все поля
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

}
