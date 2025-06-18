package server;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTasksTest extends HttpTestBase {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testCreateTaskTest() throws Exception { // проверяем создание задачи (статус 201, наличие и имя задачи)
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Задача", tasks.get(0).getName());
    }

    @Test
    void testGetTaskTest() throws Exception { // проверяем получение задачи по ID (статус 200 и совпадение ID)
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        manager.createTask(task);
        int id = manager.getAllTasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(id, responseTask.getId());
    }

    @Test
    void testDeleteTaskTest() throws Exception { // проверяем удаление задачи (статус 200 и пустой список задач)
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        manager.createTask(task);
        int id = manager.getAllTasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testTimeConflictTest() throws Exception { // проверяем конфликт времени задач (статус 406 и отсутствие дублирования)
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Задача1", "Описание задачи1", Status.NEW, now, Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи2", Status.NEW,
                now.plusMinutes(30), Duration.ofHours(1));
        String json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

}
