package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    // Методы для задач
    void createTask(Task task); // создать задачу

    void updateTask(Task task); // обновить задачу

    ArrayList<Task> getAllTasks(); // получить все задачи

    Task getTaskById(int id) throws NotFoundException; // получить задачу по id

    void deleteTask(int id); // удалить задачу

    void deleteAllTasks(); // удалить все задачи

    // Методы для эпиков
    void createEpic(Epic epic); // создать эпик

    void updateEpic(Epic epic); // обновить эпик

    ArrayList<Epic> getAllEpics(); // получить все эпики

    Epic getEpicById(int id) throws NotFoundException; // получить эпик по id

    void deleteEpic(int id); // удалить эпик

    void deleteAllEpics(); // удалить все эпики вместе с подзадачами

    // Методы для подзадач
    void createSubtask(Subtask subtask); // создать подзадачу

    void updateSubtask(Subtask subtask); // обновить подзадачу

    ArrayList<Subtask> getAllSubtasks(); // получить все подзадачи

    Subtask getSubtaskById(int id) throws NotFoundException; // получить подзадачу по id

    void deleteSubtask(Integer id); // удалить подзадачу

    void deleteAllSubtasks(); //удалить все подзадачи

    ArrayList<Subtask> getEpicSubtasks(int epicId); // получить эпик к подзадаче

    ArrayList<Task> getHistory(); // получить историю

    List<Task> getPrioritizedTasks(); // сортировка всех задач по приоритету

}
