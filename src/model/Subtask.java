package model;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) { // конструктор
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask other) {
        super(other);
        this.epicId = other.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}
