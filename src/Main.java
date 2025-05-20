import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

//import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

//        // Создаем задачу, эпик и подзадачи
//        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW);
//        taskManager.createTask(task1);
//
//        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
//        taskManager.createEpic(epic1);
//
//        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", Status.NEW, epic1.getId());
//        taskManager.createSubtask(subtask1);
//
//        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", Status.NEW, epic1.getId());
//        taskManager.createSubtask(subtask2);
//
//        // Вызываем методы просмотра истории
//        taskManager.getTaskById(task1.getId());
//        taskManager.getEpicById(epic1.getId());
//        taskManager.getSubtaskById(subtask1.getId());
//        taskManager.getSubtaskById(subtask2.getId());
//
//        // Печатаем историю - 4 просмотра
//        printAllTasks(taskManager);
//
//        // Удаляем задачу и снова смотрим историю - осталось 3 просмотра (1 эпик и 2 подзадачи)
//        taskManager.deleteTask(task1.getId());
//        printAllTasks(taskManager);
//
//        // Проверяем, сколько подзадач в эпике - 2
//        ArrayList<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic1.getId());
//        System.out.println("Сколько в эпике1 подзадач? " + epicSubtasks.size());
//
//        // Проверяем статус эпика - NEW
//        System.out.println("Статус эпика1 - " + epic1.getStatus());
//
//        // Обновляем статус подзадачи - IN_PROGRESS
//        Subtask updatedSubtask = new Subtask(subtask1.getName(), subtask1.getDescription(),
//                Status.IN_PROGRESS, subtask1.getEpicId());
//        updatedSubtask.setId(subtask1.getId());
//        taskManager.updateSubtask(updatedSubtask);
//        System.out.println("Статус эпика1 после обновления статуса подзадачи - " + epic1.getStatus());
//
//        // Завершаем все подзадачи - DONE
//        subtask1.setStatus(Status.DONE);
//        subtask2.setStatus(Status.DONE);
//        taskManager.updateSubtask(subtask1);
//        taskManager.updateSubtask(subtask2);
//        System.out.println("Статус эпика1 после завершения всех подзадач - " + epic1.getStatus());
//
//        // Проверяем получение эпика по ид - true
//        Epic foundEpic = taskManager.getEpicById(epic1.getId());
//        System.out.println("Эпик найден: " + (foundEpic != null)); //
//
//        // Проверяем получение истории - 5
//        System.out.println("История просмотров: " + taskManager.getHistory().size());
//
//        // Проверяем удаление всех подзадач - 0
//        taskManager.deleteAllSubtasks();
//        System.out.println("Подзадачи после удаления: " + taskManager.getAllSubtasks().size());

        System.out.println("\n=== Дополнительное задание. Реализуем пользовательский сценарий ===");

        Task task01 = new Task("Задача01", "Описание задачи01", Status.NEW);
        taskManager.createTask(task01);
        Task task02 = new Task("Задача02", "Описание задачи02", Status.NEW);
        taskManager.createTask(task02);
        Epic epic01 = new Epic("Эпик01", "Описание эпика01");
        taskManager.createEpic(epic01);
        Subtask subtask01 = new Subtask("Подзадача01", "Описание подзадачи01", Status.NEW, epic01.getId());
        taskManager.createSubtask(subtask01);
        Subtask subtask02 = new Subtask("Подзадача02", "Описание подзадачи02", Status.NEW, epic01.getId());
        taskManager.createSubtask(subtask02);
        Subtask subtask03 = new Subtask("Подзадача03", "Описание подзадачи03", Status.NEW, epic01.getId());
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
