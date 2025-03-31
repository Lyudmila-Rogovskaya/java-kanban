public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) { // конструктор
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

}
