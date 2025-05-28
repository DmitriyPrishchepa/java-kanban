package handlers;

import adapters.DateTimeAdapter;
import adapters.DurationAdapter;
import adapters.EnumAdapter;
import adapters.StringIntAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.TaskProgress;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                methodNotProvided(exchange);
        }
    }


    protected void processGet(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void sendSuccessText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {

        String response = "По вашему запросу ничего не найдно";

        exchange.sendResponseHeaders(404, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendHasIntersections(HttpExchange exchange) throws IOException {

        String response = "Добавление или обновление недоступно, так как есть пересечения по времени";

        exchange.sendResponseHeaders(406, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void methodNotProvided(HttpExchange exchange) throws IOException {

        String response = "No such method";

        exchange.sendResponseHeaders(501, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "Запрашиываемый метод не доступен";

        exchange.getResponseHeaders().add("Allow", "GET, POST, DELETE");
        exchange.sendResponseHeaders(405, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }

    protected String[] getTaskIdFromURI(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        return path.split("/");
    }

    public Optional<Integer> getById(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            if (pathParts.length == 3) {
                return Optional.of(Integer.parseInt(pathParts[2]));
            } else {
                return Optional.empty();
            }
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public Gson getGsonBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
                .registerTypeAdapter(TaskProgress.class, new EnumAdapter())
                .registerTypeAdapter(Integer.class, new StringIntAdapter())
                .create();
    }
}
