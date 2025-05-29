package model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) { // конструктор
        super(name, description, Status.NEW);
    }

    public Epic(Epic other) {
        super(other);
        this.subtaskIds.addAll(other.subtaskIds);
        this.setId(other.getId());
        this.setStatus(other.getStatus());
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

}
