package ru.yandex.javacourse.schedule.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler { // отправляем JSON-текст с указанным статусом
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException { // отправляем ошибку 404 (не найдено)
        String response = "{\"error\":\"" + message + "\"}";
        sendText(exchange, response, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException { // отправляем ошибку 406 (конфликт времени)
        String response = "{\"error\":\"" + message + "\"}";
        sendText(exchange, response, 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException { // отправляем ошибку 400 (некорректный запрос)
        String response = "{\"error\":\"" + message + "\"}";
        sendText(exchange, response, 400);
    }

}
