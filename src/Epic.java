import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) { // конструктор
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtaskIds() { // получить подзадачу
        return subtaskIds;
    }

    public void addSubtaskId(int id) { // добавить подзадачу
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) { // удалить ид подзадачи
        subtaskIds.remove(Integer.valueOf(id));
    }

}