package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (method.equals(HttpMethod.GET) && path.length == 2) {
            getPrioritizedTasks(exchange);
        }

        writeDefault(exchange);
    }

    public void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        writeResponse(exchange, gson.toJson(prioritizedTasks), 200);
    }
}
