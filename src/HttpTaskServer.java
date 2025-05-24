import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controllers.FileBackedTaskManager;
import exceptions.ManagerLoadFromFileException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HttpTaskServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8080;

    static final Path path = Paths.get("tasks.csv");

    static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
            .registerTypeAdapter(Integer.class, new StringToIntAdapter())
            .registerTypeAdapter(TaskProgress.class, new EnumAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static Scanner scanner = new Scanner(System.in);

    public static FileBackedTaskManager fileBackedTaskManager;

    public static void main(String[] args) throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/hello", new HelloHandler());
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/tasks/{id}", new TasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/epics/{id}", new EpicsHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/subtasks/{id}", new SubtasksHandler());
        httpServer.createContext("/subtasks/{epicId}/{subtaskId}", new SubtasksHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler());

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(path);
        } catch (ManagerLoadFromFileException | IOException e) {
            System.out.println("Ошибка создания файла");
        }

        System.out.println("Поехали!");


        while (true) {
            printMenu();

            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    System.out.println("Чтобы создать задачу, нужно ввести данные:");
                    System.out.println("Введите название задачи");
                    String taskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String taskDescription = scanner.nextLine();
                    fileBackedTaskManager.addTask(new Task(taskName, taskDescription, TaskProgress.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
                    break;
                case 2:
                    System.out.println("Чтобы создать Эпик, нужно ввести данные:");
                    System.out.println("Введите название эпика:");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String epicDescription = scanner.nextLine();
                    fileBackedTaskManager.addEpic(new Epic(epicName, epicDescription, TaskProgress.NEW));
                    break;
                case 3:
                    System.out.println("Введите id эпика, в который хотите добавить задачу:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название подзадачи:");
                    String subTaskName = scanner.nextLine();
                    System.out.println("Введите описание подзадачи");
                    String subTaskDescription = scanner.nextLine();
                    fileBackedTaskManager.addSubtaskToEpic(
                            epicId,
                            new Subtask(
                                    subTaskName,
                                    subTaskDescription,
                                    TaskProgress.NEW,
                                    Duration.ofMinutes(1),
                                    LocalDateTime.now()
                            )
                    );
                    break;
                case 4:
                    System.out.println("Чтобы обновить задачу, нужно ввести данные:");
                    System.out.println("Введите id задачи, которую вы хотите изменить:");
                    int neededId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Поменяйте название задачи");
                    String updatedName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String updatedDescription = scanner.nextLine();
                    System.out.println("Введите новый статус задачи:");
                    String updatedStatus = scanner.nextLine();
                    fileBackedTaskManager.updateTask(neededId,
                            new Task(updatedName,
                                    updatedDescription,
                                    TaskProgress.valueOf(updatedStatus)
                            )
                    );
                    break;
                case 5:
                    System.out.println("Чтобы обновить подзадачу в эпике, нужно ввести данные:");
                    System.out.println("Введите id эпика, к которм вы хотите изменить задачу:");
                    int neededEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите id подзадачи в эпике, которую нужно обновить:");
                    int neededSubtaskId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Поменяйте название задачи");
                    String updatedSubTaskName = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String updatedSubtaskDescription = scanner.nextLine();
                    System.out.println("Введите новый статус задачи:");
                    String updatedSubtaskStatus = scanner.nextLine();
                    fileBackedTaskManager.updateSubtask(
                            neededEpicId,
                            neededSubtaskId,
                            new Subtask(updatedSubTaskName,
                                    updatedSubtaskDescription,
                                    TaskProgress.valueOf(updatedSubtaskStatus),
                                    Duration.ofMinutes(30),
                                    LocalDateTime.now()
                            )
                    );
                    break;
                case 6:
                    System.out.println(fileBackedTaskManager.getTasks());
                    break;
                case 7:
                    System.out.println(fileBackedTaskManager.getEpics());
                    break;
                case 8:
                    System.out.println(fileBackedTaskManager.getSubtasks());
                    break;
                case 9:
                    System.out.println("Введите id эпика, чьи задачи нужно вывести:");
                    int idOfEpic = scanner.nextInt();
                    scanner.nextLine();
                    fileBackedTaskManager.getSubtasksOfEpic(idOfEpic);
                    System.out.println(fileBackedTaskManager.getSubtasksOfEpic(idOfEpic));
                    break;
                case 10:
                    System.out.println("Введите id задачи, которую вы хотите найти: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(fileBackedTaskManager.getTaskById(id));
                    break;
                case 11:
                    System.out.println("Введите id подзадачи: ");
                    int subId = scanner.nextInt();
                    fileBackedTaskManager.getSubtaskById(subId);
                    System.out.println(fileBackedTaskManager.getSubtaskById(subId));
                    break;
                case 12:
                    System.out.println("Введите id эпика, который вы хотите найти: ");
                    int findingEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(fileBackedTaskManager.getEpicById(findingEpicId));
                    break;
                case 13:
                    System.out.println("Введите id эпика, подзадачу которого вы хотите найти: ");
                    int findEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите id подзадачи, которую вы хотите найти: ");
                    int findSubTaskId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(fileBackedTaskManager.getSubtaskInEpicById(findEpicId, findSubTaskId));
                    break;
                case 14:
                    System.out.println("Введите id задачи, которую вы хотите удалить: ");
                    int removingTaskId = scanner.nextInt();
                    fileBackedTaskManager.removeTaskById(removingTaskId);
                    break;
                case 15:
                    System.out.println("Введите id эпика, который вы хотите удалить: ");
                    int removingEpicId = scanner.nextInt();
                    fileBackedTaskManager.removeEpicById(removingEpicId);
                    break;
                case 16:
                    System.out.println("Введите id эпика, подзадачу в которм вы хотите удалить ");
                    int removingEpicSubtaskId = scanner.nextInt();
                    System.out.println("Введите id подзадачи, которую вы хотите удалить ");
                    int removingSubtaskId = scanner.nextInt();
                    fileBackedTaskManager.removeSubtaskById(removingEpicSubtaskId, removingSubtaskId);
                    break;
                case 17:
                    fileBackedTaskManager.removeAllTasks();
                    break;
                case 18:
                    fileBackedTaskManager.removeAllEpics();
                    break;
                case 19:
                    fileBackedTaskManager.removeAllSubtasks();
                    break;
                case 20:
                    System.out.println("Вввдите id эпика:");
                    int epicId2 = scanner.nextInt();
                    scanner.nextLine();
                    fileBackedTaskManager.removeAllSubtasksOfEpic(epicId2);
                    System.out.println(fileBackedTaskManager.removeAllSubtasksOfEpic(epicId2));
                    break;
                case 21:
                    System.out.println(fileBackedTaskManager.getHistory());
                    break;
                case 22:
                    System.out.println("Выход из программы");
                    return;
                default:
                    System.out.println("Такой команды нет");
                    break;
            }
        }
    }

    public static void printMenu() {
        System.out.println("Что хотите сделать?");
        System.out.println("1 - добавить задачу");
        System.out.println("2 - добавить эпик");
        System.out.println("3 - добавить задачу в эпик");
        System.out.println("4 - обновить задачу");
        System.out.println("5 - обновить поздадачу в эпике");
        System.out.println("6 - вывести список задач");
        System.out.println("7 - вывести список эпиков");
        System.out.println("8 - вывести список ВСЕХ подзадач");
        System.out.println("9 - вывести список подзадач эпика");
        System.out.println("10 - найти задачу по id");
        System.out.println("11 - найти подзадачу по id");
        System.out.println("12 - найти эпик по id");
        System.out.println("13 - найти подзадачу в эпике по id");
        System.out.println("14 - удалить задачу по id");
        System.out.println("15 - удалить эпик по id");
        System.out.println("16 - удалить подзадачу в эпике по id");
        System.out.println("17 - удалить все задачи");
        System.out.println("18 - удалить все эпики");
        System.out.println("19 - удалить все подзадачи");
        System.out.println("20 - удалить все подзадачи в эпике");
        System.out.println("21 - посмотреть историю");
        System.out.println("22 - выйти из программы");
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /hello запроса от клиента.");

            String response = "Hey! Glad to see you on my best server.";

            writeResponse(exchange, response, 200);
        }
    }

    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");

            String response;

            String method = exchange.getRequestMethod();

            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");

            switch (method) {
                case "GET":
                    if (splitStrings.length == 2) {
                        response = fileBackedTaskManager.getTasks().stream()
                                .map(Task::toString)
                                .collect(Collectors.joining("\n"));

                        writeResponse(exchange, response, 200);
                        break;
                    }

                    if (splitStrings.length == 3) {
                        Optional<Integer> id = getById(exchange);

                        if (id.isPresent()) {
                            Task task = fileBackedTaskManager.getTaskById(id.get());

                            if (task != null) {
                                response = task.toString();
                                writeResponse(exchange, response, 200);
                            } else {
                                response = "Запрашиваемая задача не существует";
                                writeResponse(exchange, response, 404);
                            }
                            break;
                        }
                    }
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Task task = gson.fromJson(body, Task.class);

                    if (fileBackedTaskManager.checkTasksIntersectionsByRuntime(task)) {
                        writeResponse(
                                exchange,
                                "Задача не может быть добавлена или обновлена. Есть пересечения по времени",
                                406
                        );
                    } else {
                        Optional<Integer> id = getById(exchange);
                        if (id.isEmpty()) {
                            fileBackedTaskManager.addTask(task);
                            writeResponse(exchange, "Задача успешно добавлена", 201);
                        } else {
                            fileBackedTaskManager.updateTask(id.get(), task);
                            writeResponse(exchange, "Задача успешно обновлена", 201);
                        }
                    }
                    break;
                case "DELETE":
                    Optional<Integer> deletingId = getById(exchange);
                    deletingId.ifPresent(integer -> fileBackedTaskManager.removeTaskById(integer));

                    writeResponse(exchange, "Задача успешно удалена", 200);
                    break;
            }
        }
    }

    static class EpicsHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /epics запроса от клиента.");

            String response;

            String method = exchange.getRequestMethod();

            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");

            switch (method) {
                case "GET":
                    if (splitStrings.length == 2) {
                        response = fileBackedTaskManager.getEpics().stream()
                                .map(Epic::toString)
                                .collect(Collectors.joining("\n"));

                        writeResponse(exchange, response, 200);
                        break;
                    }

                    if (splitStrings.length == 3) {
                        Optional<Integer> epicId = getById(exchange);

                        if (epicId.isPresent()) {
                            Epic epic = fileBackedTaskManager.getEpicById(epicId.get());

                            if (epic != null) {
                                response = gson.toJson(epic);
                                writeResponse(exchange, response, 200);
                            } else {
                                response = "Запрашиваемый эпик не существует";
                                writeResponse(exchange, response, 404);
                            }
                            break;
                        }
                    }
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Epic epic = gson.fromJson(body, Epic.class);
                    Optional<Integer> id = getById(exchange);

                    if (id.isPresent()) {
                        fileBackedTaskManager.addEpic(epic);
                        writeResponse(exchange, "Эпик успешно добавлен", 201);
                    }
                    break;
                case "DELETE":
                    Optional<Integer> deletingId = getById(exchange);
                    deletingId.ifPresent(integer -> fileBackedTaskManager.removeEpicById(integer));
                    writeResponse(exchange, "Эпик успешно удален", 200);
                    break;
            }
        }
    }

    static class SubtasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /subtasks запроса от клиента.");

            String response;

            String method = exchange.getRequestMethod();

            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");

            switch (method) {
                case "GET":
                    if (splitStrings.length == 2) {
                        response = fileBackedTaskManager.getSubtasks().stream()
                                .map(Subtask::toString)
                                .collect(Collectors.joining("\n"));
                        writeResponse(exchange, response, 200);
                        break;
                    }

                    if (splitStrings.length == 3) {
                        Optional<Integer> subtaskId = getById(exchange);
                        if (subtaskId.isPresent()) {
                            Subtask subtask = fileBackedTaskManager.getSubtaskById(subtaskId.get());

                            if (subtask != null) {
                                response = gson.toJson(subtask);
                                writeResponse(exchange, response, 200);
                            } else {
                                response = "Поздадачи по заданному id не существует";
                                writeResponse(exchange, response, 404);
                            }
                        }
                    }
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (splitStrings.length == 4) {
                        Optional<Integer> epicId = getById(exchange);

                        if (epicId.isPresent()) {
                            Epic epic = fileBackedTaskManager.getEpicById(epicId.get());
                            if (epic == null) {
                                response = "Запрашиваемого эпика не существует";
                                writeResponse(exchange, response, 404);
                                return;
                            }


                            if (fileBackedTaskManager.checkTasksIntersectionsByRuntime(subtask)) {
                                writeResponse(
                                        exchange,
                                        "Задача не может быть обновлена. Есть пересечение по времени",
                                        406);
                                break;
                            } else {
                                Optional<Integer> id = Optional.of(Integer.parseInt(splitStrings[3]));

                                if (id.isEmpty()) {
                                    fileBackedTaskManager.addSubtaskToEpic(epicId.get(), subtask);
                                    writeResponse(exchange, "Задача успешно добавлена", 201);
                                } else {
                                    fileBackedTaskManager.updateSubtask(epicId.get(), id.get(), subtask);
                                    writeResponse(exchange, "Задача успешно обновлена", 201);

                                }
                            }
                        }
                    }
                    break;
                case "DELETE":
                    Optional<Integer> epicId = getById(exchange);
                    if (epicId.isPresent()) {
                        Optional<Integer> subtaskId = Optional.of(Integer.parseInt(splitStrings[3]));
                        if (subtaskId.isPresent()) {
                            fileBackedTaskManager.removeSubtaskById(epicId.get(), subtaskId.get());
                            writeResponse(exchange, "Подзадача успешно удалена", 200);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = fileBackedTaskManager.getHistory().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            writeResponse(exchange, response, 200);
        }
    }

    static class PrioritizedTasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = fileBackedTaskManager.getPrioritizedTasks().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            writeResponse(exchange, response, 200);
        }
    }

    static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        byte[] body = responseString.getBytes(DEFAULT_CHARSET);

        exchange.sendResponseHeaders(responseCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    private static Optional<Integer> getById(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}