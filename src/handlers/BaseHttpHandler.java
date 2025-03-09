package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;

    BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(response.getBytes());
        }
        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected boolean checkNull(Optional<? extends Task> task) {
        return task.toString().contains("null");
    }

    protected void writeIncorrectFormat(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Некорректный формат задачи", 400);
    }

    protected void writeIncorrectId(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Некорректный формат id", 400);
    }

    protected void writeDefault(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Неподдерживаемый метод!", 405);
    }


}
