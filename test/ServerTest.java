import adapters.DateTimeAdapter;
import adapters.DurationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TaskProgress;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServerTest {

    static TaskManager manager = new InMemoryTaskManager();
    static HttpTaskServer server;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
            .create();

    static {
        try {
            server = new HttpTaskServer(manager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void startServer() {
        server.start();
    }

    @AfterAll
    public static void shutDown() {
        server.stop();
    }

    @Test
    void createTask() throws IOException, InterruptedException {

        Task task = new Task(
                "task1",
                "descr1",
                TaskProgress.NEW,
                Duration.ofMinutes(1),
                LocalDateTime.now()
        );

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        System.out.println(response);

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("task1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    void getTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        System.out.println(tasksFromManager);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        List<Task> tasksFromManager = manager.getTasks();

        System.out.println(tasksFromManager.getFirst());

        assertEquals(200, response.statusCode());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {

        Task newTask = new Task(
                "task2",
                "descr2",
                TaskProgress.NEW,
                Duration.ofMinutes(1),
                LocalDateTime.now()
        );

        newTask.setId(1);

        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        System.out.println(tasksFromManager);

        assertEquals("task2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }
//
//    @Test
//    void deleteTask() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/tasks/1");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .DELETE()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Task> tasksFromManager = manager.getTasks();
//
//        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
//    }
//
//
//    //--------------------------------------------------------
//
//
//    @Test
//    void createEpic() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/epics");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        String jsonBody = "{" +
//                "\"name\": \"name1\n" +
//                "\"description\": \"descr1\n" +
//                "\"id\": \"1\n" +
//                "\"status\": \"NEW\n" +
//                "\"duration\": \"PT2H30M\n" +
//                "\"startTime\": \"2023-10-05T10:00:00\n" +
//                "}";
//
//        HttpRequest request = requestBuilder
//                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        System.out.println(response);
//
//        List<Epic> epicsFromManager = manager.getEpics();
//
//        assertNotNull(epicsFromManager, "Эпики не возвращаются");
//        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
//    }
//
//    @Test
//    void getEpics() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/epics");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Epic> epicsFromManager = manager.getEpics();
//
//        assertNotNull(epicsFromManager, "Задачи не возвращаются");
//        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
//    }
//
//    @Test
//    void getEpicById() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/epics/1");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//    }
//
//    @Test
//    void deleteEpic() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/epics/1");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .DELETE()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Epic> epicsFromManager = manager.getEpics();
//
//        assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");
//    }
//
//
//    //-------------------------------------------------------------
//
//
//    @Test
//    void createSubtask() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/subtasks");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        String jsonBody = "{" +
//                "\"name\": \"name1\n" +
//                "\"description\": \"descr1\n" +
//                "\"id\": \"1\n" +
//                "\"status\": \"NEW\n" +
//                "\"duration\": \"PT2H30M\n" +
//                "\"startTime\": \"2023-10-05T10:00:00\n" +
//                "}";
//
//        HttpRequest request = requestBuilder
//                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        System.out.println(response);
//
//        List<Subtask> subtasksFromManager = manager.getSubtasks();
//
//        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
//        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
//    }
//
//    @Test
//    void getSubtasks() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/subtasks");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Subtask> subtasksFromManager = manager.getSubtasks();
//
//        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
//        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
//    }
//
//    @Test
//    void getSubtaskById() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/subtask/1");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//    }
//
//    @Test
//    void deleteSubtask() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/subtasks/1");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .DELETE()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Subtask> subtasksFromManager = manager.getSubtasks();
//
//        assertEquals(0, subtasksFromManager.size(), "Некорректное количество подзадач");
//    }
//
//
//    //-------------------------------------------------------
//
//    @Test
//    void getHistory() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/history");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Task> historyFromManager = manager.getHistory();
//
//        assertNotNull(historyFromManager, "Задачи истории не возвращаются");
//        assertEquals(1, historyFromManager.size(), "Некорректное количество задач в истории");
//    }
//
//    @Test
//    void getPrioritized() throws IOException, InterruptedException {
//        URI uri = URI.create("http://localhost:8080/prioritized");
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//        HttpRequest request = requestBuilder
//                .GET()
//                .uri(uri)
//                .version(HttpClient.Version.HTTP_1_1)
//                .header("Accept", "application/json")
//                .build();
//
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
//
//        HttpResponse<String> response = client.send(request, handler);
//
//        assertEquals(200, response.statusCode());
//
//        List<Task> prioritizedFromManager = manager.getPrioritizedTasks();
//
//        assertNotNull(prioritizedFromManager, "Приоритетные задачт не возвращаются");
//        assertEquals(1, prioritizedFromManager.size(), "Некорректное количество приоритетных задач");
//    }
}
