package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.SubtaskNotFoundException;
import exceptions.TaskIntersectException;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubtasksHandler extends BaseHttpHandler {
    TaskManager manager;
    Gson gson = getGsonBuilder();

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
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        int epicId = 1;

        if (getById(exchange).isPresent()) {
            epicId = getById(exchange).get();
        }

        Epic epic = manager.getEpicById(epicId);

        System.out.println(splitStrings.length);

        if (splitStrings.length == 3) {
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                int subtaskId = manager.addSubtaskToEpic(epic.getId(), subtask);
                System.out.println("Подзадача добавлена: " + subtaskId);
                sendSuccessText(exchange, "Подзадача c id: " + subtaskId + " добавлена");
            } catch (TaskIntersectException e) {
                System.out.println("Подзадача пересекается с существующей");
                sendHasIntersections(exchange);
            }
        } else {
            int id = Optional.of(Integer.parseInt(splitStrings[4])).get();

            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                Subtask subtaskId = manager.updateSubtask(epic.getId(), id, subtask);
                System.out.println("Подзадача c id: " + subtaskId + " обновлена");
            } catch (TaskIntersectException e) {
                System.out.println("Подзадача пересекается с существующей");
                sendHasIntersections(exchange);
            }
        }
    }


    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        String[] splitStrings = getTaskIdFromURI(exchange);

        int epicId = Integer.parseInt(splitStrings[2]);
        int subtaskId = Integer.parseInt(splitStrings[3]);
        manager.removeSubtaskById(epicId, subtaskId);
        sendSuccessText(exchange, "Подзадача успешно удалена");
    }
}
