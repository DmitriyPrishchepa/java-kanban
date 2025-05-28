package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.TaskIntersectException;
import exceptions.TaskNotFoundException;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class TasksHandler extends BaseHttpHandler {

    TaskManager manager;
    Gson gson = getGsonBuilder();

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

        String[] splitStrings = getTaskIdFromURI(exchange);

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (splitStrings.length == 2) {
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
            int id = Integer.parseInt(splitStrings[2]);

            try {
                final Task task = gson.fromJson(body, Task.class);
                try {
                    Task updatedTask = manager.updateTask(id, task);
                    System.out.println("Задача обновлена: " + updatedTask);
                    sendSuccessText(exchange, "Задача успешно обновлена");
                } catch (TaskNotFoundException e) {
                    System.out.println("Задача не найдена");
                    sendNotFound(exchange);
                }
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