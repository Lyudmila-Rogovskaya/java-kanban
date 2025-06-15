package ru.yandex.javacourse.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TimeConflictException;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseTaskHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException { // обрабатываем запросы для задач
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException, NotFoundException { // возвращаем задачи/задачу по ID
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/tasks")) {
            sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
        } else if (path.matches("/tasks/\\d+")) {
            int id = extractId(path);
            Task task = taskManager.getTaskById(id);
            if (task == null) throw new NotFoundException("Задача не найдена");
            sendText(exchange, gson.toJson(task), 200);
        } else {
            sendNotFound(exchange, "Неверный путь запроса");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, TimeConflictException { // создаем/обновляем задачу
        Task task = gson.fromJson(readRequestBody(exchange), Task.class);
        if (task.getId() == 0) {
            taskManager.createTask(task);
            sendText(exchange, gson.toJson(task), 201);
        } else {
            taskManager.updateTask(task);
            sendText(exchange, gson.toJson(task), 201);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException { // удаляем задачу
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/tasks/\\d+")) {
            int id = extractId(path);
            taskManager.deleteTask(id);
            sendText(exchange, "Задача удалена", 200);
        } else {
            sendNotFound(exchange, "Неверный путь для удаления");
        }
    }

}
