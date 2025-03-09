package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {

            case GET -> {
                if (path.length == 3) {
                    getTaskById(exchange);
                } else {
                    getAllTasks(exchange);
                }
            }

            case POST -> {
                if (path.length == 3) {
                    updateTask(exchange);
                } else {
                    addTask(exchange);
                }
            }

            case DELETE -> deleteTask(exchange);

            default -> writeDefault(exchange);
        }
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getId(exchange);

        if (taskId.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(taskId.get());
            if (task.isPresent()) {
                writeResponse(exchange, gson.toJson(task.get()), 200);
            } else {
                writeTaskNotFound(exchange, taskId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        writeResponse(exchange, gson.toJson(tasks), 200);
    }

    private void addTask(HttpExchange exchange) throws IOException {
        Optional<Task> taskOpt = extractTask(exchange);

        if (taskOpt.isPresent() && !checkNull(taskOpt)) {
            Task task = taskOpt.get();
            try {
                taskManager.addTask(task);
                writeResponse(exchange, "Задача с id " + task.getId() + " добавлена!", 201);
                return;
            } catch (ManagerException e) {
                writeResponse(exchange, e.getMessage(), 406);
            }
        }

        writeIncorrectFormat(exchange);
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getId(exchange);

        if (taskId.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(taskId.get());
            if (task.isPresent()) {
                taskManager.removeTaskById(task.get().getId());
                writeResponse(exchange, "Задача с id " + taskId.get() + " удалена!", 200);
            } else {
                writeTaskNotFound(exchange, taskId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getId(exchange);

        if (taskId.isPresent()) {
            Optional<Task> oldTask = taskManager.getTaskById(taskId.get());
            Optional<Task> newTask = extractTask(exchange);

            if ((oldTask.isPresent()) && newTask.isPresent() && !checkNull(newTask)) {
                try {
                    taskManager.updateTask(newTask.get());
                    writeResponse(exchange, "Задача c id " + newTask.get().getId() + " обновлена!", 201);
                } catch (ManagerException e) {
                    writeResponse(exchange, e.getMessage(), 406);
                }

            } else {
                writeIncorrectFormat(exchange);
            }
        } else {
            writeIncorrectId(exchange);
        }
    }


    private Optional<Task> extractTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String taskAsString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(taskAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return Optional.ofNullable(gson.fromJson(jsonObject, Task.class));
    }

    private void writeTaskNotFound(HttpExchange exchange, int taskId) throws IOException {
        writeResponse(exchange, "Задача с id " + taskId + " не найдена!", 404);
    }
}
