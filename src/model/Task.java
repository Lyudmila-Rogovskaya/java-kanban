package model;

import java.util.Objects;

public class Task {

    private String name;
    private String description; // описание задачи
    private Status status;
    private int id;

    public Task(String name, String description, Status status) { // конструктор
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Task other) {
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
        this.id = other.id;
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

}
