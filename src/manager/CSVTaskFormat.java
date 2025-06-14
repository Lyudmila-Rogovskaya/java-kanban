package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVTaskFormat {

    public static String toString(Task task) { // преобразовать задачу в строку CSV

        return new StringBuilder()
                .append(task.getId()).append(',')
                .append(task.getType().name()).append(',')
                .append(task.getName()).append(',')
                .append(task.getStatus().name()).append(',')
                .append(task.getDescription()).append(',')
                .append(task.getStartTime() != null ? task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "").append(',')
                .append(task.getDuration() != null ? task.getDuration().toMinutes() : "").append(',')
                .append(task instanceof Subtask ? ((Subtask) task).getEpicId() : "")
                .toString();
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = !fields[5].isEmpty() ? LocalDateTime.parse(fields[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        Duration duration = !fields[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                Subtask subtask = new Subtask(name, description, status, epicId, startTime, duration);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

}
