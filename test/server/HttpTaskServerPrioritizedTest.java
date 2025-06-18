package server;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerPrioritizedTest extends HttpTestBase {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();

        Task earlyTask = new Task("Ранняя задача", "Описание ранней задачи", Status.NEW,
                LocalDateTime.now(), Duration.ofHours(1));

        Task lateTask = new Task("Поздняя задача", "Описание поздней задачи", Status.NEW,
                LocalDateTime.now().plusHours(2), Duration.ofHours(1));

        manager.createTask(lateTask);
        manager.createTask(earlyTask);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void getPrioritizedTasksValidRequestReturns200Test() throws Exception { // проверяем запрос списка (статус 200 и количество задач)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    void prioritizedOrderCorrectTimeOrderTest() throws Exception { // проверяем порядок задач (ранняя задача должна быть первой)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertEquals("Ранняя задача", tasks[0].getName());
        assertEquals("Поздняя задача", tasks[1].getName());
    }

    @Test
    void prioritizedTasksIncludesAllTypesTest() throws Exception { // проверяем наличие задач всех типов (Task, Subtask) в списке
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.NEW, epic.getId(),
                LocalDateTime.now().plusHours(3), Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(3, tasks.length);
    }

}
