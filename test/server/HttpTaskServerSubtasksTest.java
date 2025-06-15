package server;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerSubtasksTest extends HttpTestBase {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;
    private Epic epic;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();

        epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void createSubtaskValidDataReturns201Test() throws Exception { // проверяем создание подзадачи (статус 201 и наличие в менеджере)
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    void createSubtaskInvalidEpicReturns404Test() throws Exception { // проверяем создание подзадачи с несуществующим эпиком (статус 404)
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, 999);
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void updateSubtaskValidDataReturns201Test() throws Exception { // проверяем обновление подзадачи (статус 201 и изменение статуса)
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).getStatus());
    }

    @Test
    void getSubtaskByIdValidIdReturns200Test() throws Exception { // проверяем получение подзадачи по ID (статус 200 и совпадение ID)
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);
        int id = manager.getAllSubtasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(id, responseSubtask.getId());
    }

    @Test
    void deleteSubtaskValidIdReturns200Test() throws Exception { // проверяем удаление подзадачи (статус 200 и отсутствие в менеджере)
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId());
        manager.createSubtask(subtask);
        int id = manager.getAllSubtasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }

}
