package handlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.stream.Collectors;

public class PrioritizedTasksHandler extends BaseHttpHandler {

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
