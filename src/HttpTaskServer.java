import adapters.DateTimeAdapter;
import adapters.DurationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controllers.TaskManager;
import exceptions.EpicNotFoundException;
import exceptions.SubtaskNotFoundException;
import exceptions.TaskIntersectException;
import exceptions.TaskNotFoundException;
import handlers.BaseHttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Managers;
import util.TaskProgress;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    private final TaskManager manager;
    private final HttpServer server;

    static final Path path = Paths.get("tasks.csv");

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/tasks/{id}", new TasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/epics/{id}", new EpicsHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/subtasks/{id}", new SubtasksHandler(manager));
        server.createContext("/subtasks/{epicId}/{subtaskId}", new SubtasksHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(manager));
    }

    public void start() {
        System.out.println("Starting TaskServer " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
            .create();

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

//        try {
//            if (!Files.exists(path)) {
//                Files.createFile(path);
//            }
//            manager = manager.createFileBackedTaskManager(path);
//        } catch (ManagerLoadFromFileException | IOException e) {
//            System.out.println("Ошибка создания файла");
//        }

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
                    taskManager.addTask(new Task(taskName, taskDescription, TaskProgress.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
                    break;
                case 2:
                    System.out.println("Чтобы создать Эпик, нужно ввести данные:");
                    System.out.println("Введите название эпика:");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String epicDescription = scanner.nextLine();
                    taskManager.addEpic(new Epic(epicName, epicDescription, TaskProgress.NEW));
                    break;
                case 3:
                    System.out.println("Введите id эпика, в который хотите добавить задачу:");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название подзадачи:");
                    String subTaskName = scanner.nextLine();
                    System.out.println("Введите описание подзадачи");
                    String subTaskDescription = scanner.nextLine();
                    taskManager.addSubtaskToEpic(
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
                    taskManager.updateTask(neededId,
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
                    taskManager.updateSubtask(
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
                    System.out.println(taskManager.getTasks());
                    break;
                case 7:
                    System.out.println(taskManager.getEpics());
                    break;
                case 8:
                    System.out.println(taskManager.getSubtasks());
                    break;
                case 9:
                    System.out.println("Введите id эпика, чьи задачи нужно вывести:");
                    int idOfEpic = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.getSubtasksOfEpic(idOfEpic);
                    System.out.println(taskManager.getSubtasksOfEpic(idOfEpic));
                    break;
                case 10:
                    System.out.println("Введите id задачи, которую вы хотите найти: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(taskManager.getTaskById(id));
                    break;
                case 11:
                    System.out.println("Введите id подзадачи: ");
                    int subId = scanner.nextInt();
                    taskManager.getSubtaskById(subId);
                    System.out.println(taskManager.getSubtaskById(subId));
                    break;
                case 12:
                    System.out.println("Введите id эпика, который вы хотите найти: ");
                    int findingEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(taskManager.getEpicById(findingEpicId));
                    break;
                case 13:
                    System.out.println("Введите id эпика, подзадачу которого вы хотите найти: ");
                    int findEpicId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите id подзадачи, которую вы хотите найти: ");
                    int findSubTaskId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(taskManager.getSubtaskInEpicById(findEpicId, findSubTaskId));
                    break;
                case 14:
                    System.out.println("Введите id задачи, которую вы хотите удалить: ");
                    int removingTaskId = scanner.nextInt();
                    taskManager.removeTaskById(removingTaskId);
                    break;
                case 15:
                    System.out.println("Введите id эпика, который вы хотите удалить: ");
                    int removingEpicId = scanner.nextInt();
                    taskManager.removeEpicById(removingEpicId);
                    break;
                case 16:
                    System.out.println("Введите id эпика, подзадачу в которм вы хотите удалить ");
                    int removingEpicSubtaskId = scanner.nextInt();
                    System.out.println("Введите id подзадачи, которую вы хотите удалить ");
                    int removingSubtaskId = scanner.nextInt();
                    taskManager.removeSubtaskById(removingEpicSubtaskId, removingSubtaskId);
                    break;
                case 17:
                    taskManager.removeAllTasks();
                    break;
                case 18:
                    taskManager.removeAllEpics();
                    break;
                case 19:
                    taskManager.removeAllSubtasks();
                    break;
                case 20:
                    System.out.println("Вввдите id эпика:");
                    int epicId2 = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.removeAllSubtasksOfEpic(epicId2);
                    System.out.println(taskManager.removeAllSubtasksOfEpic(epicId2));
                    break;
                case 21:
                    System.out.println(taskManager.getHistory());
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

    static class TasksHandler extends BaseHttpHandler {

        TaskManager manager;

        public TasksHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        protected void processGet(HttpExchange exchange) throws IOException {
            String response;

            String[] splitStrings = getTaskIdFromURI(exchange);

            if (splitStrings.length == 2) {
                response = manager.getTasks().stream()
                        .map(Task::toString)
                        .collect(Collectors.joining("\n"));

                sendSuccessText(exchange, response);
            }

            if (splitStrings.length == 3) {
                Optional<Integer> id = getById(exchange);

                if (id.isPresent()) {

                    try {
                        Task task = manager.getTaskById(id.get());
                        response = task.toString();
                        sendSuccessText(exchange, response);
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET).trim();

            Optional<Integer> id = getById(exchange);

            if (id.isEmpty()) {
                try {
                    final Task task = gson.fromJson(body, Task.class);
                    int idAdded = manager.addTask(task);
                    System.out.println("Создали задачу id=" + idAdded);
                    sendSuccessText(exchange, "Задача c id: " + idAdded + " создана");
                } catch (TaskIntersectException e) {
                    System.out.println("Задача пересекается с существующей");
                    sendHasIntersections(exchange);
                }
            } else {
                try {
                    final Task task = gson.fromJson(body, Task.class);
                    Task updatedTask = manager.updateTask(id.get(), task);
                    System.out.println("Задача обновлена: " + updatedTask);
                    sendSuccessText(exchange, "Задача успешно обновлена");
                } catch (TaskIntersectException e) {
                    System.out.println("Задача пересекается с существующей");
                    sendHasIntersections(exchange);
                }
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            Optional<Integer> deletingId = getById(exchange);
            deletingId.ifPresent(integer -> manager.removeTaskById(integer));
            sendSuccessText(exchange, "Задача успешно удалена");
        }
    }

    static class EpicsHandler extends BaseHttpHandler {

        TaskManager manager;

        public EpicsHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        protected void processGet(HttpExchange exchange) throws IOException {
            String response;

            String[] splitStrings = getTaskIdFromURI(exchange);

            if (splitStrings.length == 2) {
                response = manager.getEpics().stream()
                        .map(Epic::toString)
                        .collect(Collectors.joining("\n"));

                sendSuccessText(exchange, response);
            }

            if (splitStrings.length == 3) {
                Optional<Integer> epicId = getById(exchange);

                if (epicId.isPresent()) {

                    try {
                        Epic epic = manager.getEpicById(epicId.get());
                        response = gson.toJson(epic);
                        sendSuccessText(exchange, response);
                    } catch (EpicNotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            Optional<Integer> id = getById(exchange);

            if (id.isPresent()) {
                manager.addEpic(epic);
                sendSuccessText(exchange, "Эпик успешно добавлен");
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            Optional<Integer> deletingId = getById(exchange);
            deletingId.ifPresent(integer -> manager.removeEpicById(integer));
            sendSuccessText(exchange, "Эпик успешно удален");
        }
    }

    static class SubtasksHandler extends BaseHttpHandler {

        TaskManager manager;

        public SubtasksHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        protected void processGet(HttpExchange exchange) throws IOException {
            String response;

            String[] splitStrings = getTaskIdFromURI(exchange);

            if (splitStrings.length == 2) {
                response = manager.getSubtasks().stream()
                        .map(Subtask::toString)
                        .collect(Collectors.joining("\n"));
                sendSuccessText(exchange, response);
            }

            if (splitStrings.length == 3) {
                Optional<Integer> subtaskId = getById(exchange);
                if (subtaskId.isPresent()) {

                    try {
                        Subtask subtask = manager.getSubtaskById(subtaskId.get());
                        response = gson.toJson(subtask);
                        sendSuccessText(exchange, response);
                    } catch (SubtaskNotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
            }
        }

        @Override
        protected void processPost(HttpExchange exchange) throws IOException {
            String[] splitStrings = getTaskIdFromURI(exchange);

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            if (splitStrings.length == 4) {
                Optional<Integer> epicId = getById(exchange);

                if (epicId.isPresent()) {

                    Epic epic = manager.getEpicById(epicId.get());

                    if (epic == null) {
                        sendNotFound(exchange);
                        return;
                    }

                    Optional<Integer> id = Optional.of(Integer.parseInt(splitStrings[3]));

                    if (id.isEmpty()) {
                        try {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            int subtaskId = manager.addSubtaskToEpic(epicId.get(), subtask);
                            System.out.println("Подзадача добавлена: " + subtaskId);
                            sendSuccessText(exchange, "Подзадача c id: " + subtaskId + " добавлена");
                        } catch (TaskIntersectException e) {
                            System.out.println("Подзадача пересекается с существующей");
                            sendHasIntersections(exchange);
                        }
                    } else {
                        try {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            Subtask subtaskId = manager.updateSubtask(epicId.get(), id.get(), subtask);
                            System.out.println("Подзадача c id: " + subtaskId + " обновлена");
                        } catch (TaskIntersectException e) {
                            System.out.println("Подзадача пересекается с существующей");
                            sendHasIntersections(exchange);
                        }
                    }
                }
            }
        }

        @Override
        protected void processDelete(HttpExchange exchange) throws IOException {
            String[] splitStrings = getTaskIdFromURI(exchange);

            Optional<Integer> epicId = getById(exchange);
            if (epicId.isPresent()) {
                Optional<Integer> subtaskId = Optional.of(Integer.parseInt(splitStrings[3]));
                if (subtaskId.isPresent()) {
                    manager.removeSubtaskById(epicId.get(), subtaskId.get());
                    sendSuccessText(exchange, "Подзадача успешно удалена");
                }
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler {

        TaskManager manager;

        public HistoryHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        protected void processGet(HttpExchange exchange) throws IOException {
            String response = manager.getHistory().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            sendSuccessText(exchange, response);
        }
    }

    static class PrioritizedTasksHandler extends BaseHttpHandler {

        TaskManager manager;

        public PrioritizedTasksHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        protected void processGet(HttpExchange exchange) throws IOException {
            String response = manager.getPrioritizedTasks().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            sendSuccessText(exchange, response);
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