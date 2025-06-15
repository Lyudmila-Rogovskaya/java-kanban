package ru.yandex.javacourse.schedule.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import manager.TimeConflictException;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseTaskHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException { // обрабатываем запросы для эпиков
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

    private void handleGet(HttpExchange exchange) throws IOException { // возвращаем эпики/подзадачи по ID
        String path = exchange.getRequestURI().getPath();
        try {
            if (path.equals("/epics")) {
                sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
            } else if (path.matches("/epics/\\d+/subtasks")) {
                int epicId = extractId(path.replace("/subtasks", ""));
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    sendNotFound(exchange, "Эпик не найден");
                } else {
                    List<Subtask> subtasks = taskManager.getEpicSubtasks(epicId);
                    sendText(exchange, gson.toJson(subtasks), 200);
                }
            } else if (path.matches("/epics/\\d+")) {
                int id = extractId(path);
                Epic epic = taskManager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange, "Эпик не найден");
                } else {
                    sendText(exchange, gson.toJson(epic), 200);
                }
            } else {
                sendNotFound(exchange, "Неверный путь запроса");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException, NotFoundException { // создаем/обновляем эпик
        Epic epic = gson.fromJson(readRequestBody(exchange), Epic.class);
        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
            sendText(exchange, gson.toJson(epic), 201);
        } else {
            taskManager.updateEpic(epic);
            sendText(exchange, gson.toJson(epic), 201);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException, NotFoundException { // удаляем эпик
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/epics/\\d+")) {
            int id = extractId(path);
            taskManager.deleteEpic(id);
            sendText(exchange, "Эпик удален", 200);
        } else {
            sendNotFound(exchange, "Неверный путь для удаления");
        }
    }

    protected int extractId(String path) { // извлекаем ID из URL
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

}
