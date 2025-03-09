package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (httpMethod.equals(HttpMethod.GET) && path.length == 2) {
            getHistory(exchange);
        }
        writeDefault(exchange);
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getTaskHistory();
        writeResponse(exchange, gson.toJson(history), 200);
    }
}
