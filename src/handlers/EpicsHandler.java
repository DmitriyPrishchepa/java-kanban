package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import exceptions.EpicNotFoundException;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicsHandler extends BaseHttpHandler {

    TaskManager manager;
    Gson gson = getGsonBuilder();

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
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Epic epic = gson.fromJson(body, Epic.class);
        manager.addEpic(epic);
        sendSuccessText(exchange, "Эпик успешно добавлен");

    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> deletingId = getById(exchange);
        deletingId.ifPresent(integer -> manager.removeEpicById(integer));
        sendSuccessText(exchange, "Эпик успешно удален");
    }
}