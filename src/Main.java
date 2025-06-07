import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        System.out.println("\n=== Дополнительное задание к ТЗ-6. Реализуем пользовательский сценарий ===");

        Task task01 = new Task("Задача01", "Описание задачи01", Status.NEW);
        taskManager.createTask(task01);
        Task task02 = new Task("Задача02", "Описание задачи02", Status.NEW);
        taskManager.createTask(task02);
        Epic epic01 = new Epic("Эпик01", "Описание эпика01");
        taskManager.createEpic(epic01);
        Subtask subtask01 = new Subtask("Подзадача01", "Описание подзадачи01", Status.NEW, epic01.getId(), null, null);
        taskManager.createSubtask(subtask01);
        Subtask subtask02 = new Subtask("Подзадача02", "Описание подзадачи02", Status.NEW, epic01.getId(), null, null);
        taskManager.createSubtask(subtask02);
        Subtask subtask03 = new Subtask("Подзадача03", "Описание подзадачи03", Status.NEW, epic01.getId(), null, null);
        taskManager.createSubtask(subtask03);
        Epic epic02 = new Epic("Эпик02", "Описание эпика02");
        taskManager.createEpic(epic02);

        taskManager.getTaskById(task01.getId());
        taskManager.getEpicById(epic01.getId());
        taskManager.getSubtaskById(subtask03.getId());
        taskManager.getSubtaskById(subtask02.getId());

        System.out.println("\n=== После первых запросов ===");
        printAllTasks(taskManager);

        taskManager.getSubtaskById(subtask03.getId());
        taskManager.getEpicById(epic01.getId());

        System.out.println("\n=== После повторных запросов ===");
        printAllTasks(taskManager);

        taskManager.getTaskById(task01.getId());
        taskManager.getSubtaskById(subtask03.getId());
        taskManager.getEpicById(epic01.getId());

        System.out.println("\n=== После финальных запросов ===");
        printAllTasks(taskManager);

        taskManager.deleteTask(task01.getId());
        System.out.println("\n=== После удаления задачи01 ===");
        printAllTasks(taskManager);

        taskManager.deleteEpic(epic01.getId());
        System.out.println("\n=== После удаления эпика01 ===");
        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) { // печать всех задач

        System.out.println("\n=== Текущее состояние менеджера ===");

        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
