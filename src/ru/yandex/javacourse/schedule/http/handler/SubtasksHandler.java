package ru.yandex.javacourse.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TimeConflictException;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseTaskHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException { // обрабатываем запросы для подзадач
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

    private void handleGet(HttpExchange exchange) throws IOException, NotFoundException { // возвращаем подзадачи/подзадачу по ID
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/subtasks")) {
            sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
        } else if (path.matches("/subtasks/\\d+")) {
            int id = extractId(path);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) throw new NotFoundException("Подзадача не найдена");
            sendText(exchange, gson.toJson(subtask), 200);
        } else {
            sendNotFound(exchange, "Неверный путь запроса");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException { // создаем/обновляем подзадачу
        try {
            Subtask subtask = gson.fromJson(readRequestBody(exchange), Subtask.class);
            if (subtask == null) {
                sendBadRequest(exchange, "Неверный формат подзадачи");
                return;
            }

            if (taskManager.getEpicById(subtask.getEpicId()) == null) {
                sendNotFound(exchange, "Эпик не найден");
                return;
            }

            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException { // удаляем подзадачу
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/subtasks/\\d+")) {
            int id = extractId(path);
            taskManager.deleteSubtask(id);
            sendText(exchange, "Подзадача удалена", 200);
        } else {
            sendNotFound(exchange, "Неверный путь для удаления");
        }
    }

}
