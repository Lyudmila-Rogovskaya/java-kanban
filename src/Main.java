import manager.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // Создаем эпик
        Epic epic = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic);

        // Добавляем 2 подзадачи
        Subtask subtask1 = new Subtask("Подзадача1", "", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Проверяем, сколько подзадач в эпике - 2
        ArrayList<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic.getId());
        System.out.println("Сколько в эпике1 подзадач? " + epicSubtasks.size());

        // Проверяем статус эпика - NEW
        System.out.println("Статус эпика1 - " + epic.getStatus());

        // Обновляем статус подзадачи - IN_PROGRESS
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        System.out.println("Статус эпика1 после обновления статуса подзадачи - " + epic.getStatus());

        // Завершаем все подзадачи - DONE
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        System.out.println("Статус эпика1 после завершения всех подзадач - " + epic.getStatus());

        // Проверка получения всех задач - 3
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW);
        Task task3 = new Task("Задача3", "Описание задачи3", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println("Сколько добавлено задач? " + taskManager.getAllTasks().size());

        // Проверка удаления по ид - 2
        taskManager.deleteTask(task1.getId());
        System.out.println("Осталось задач после удаления 1 задачи - " + taskManager.getAllTasks().size());

        // Проверка получения эпика по ид - true
        Epic foundEpic = taskManager.getEpicById(epic.getId());
        System.out.println("Эпик найден: " + (foundEpic != null)); //

        // Проверка удаления всех подзадач - 0
        taskManager.deleteAllSubtasks();
        System.out.println("Подзадачи после удаления: " + taskManager.getAllSubtasks().size());
    }

}
