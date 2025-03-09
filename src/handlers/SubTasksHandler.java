package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubTasksHandler extends BaseHttpHandler {

    public SubTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {

            case GET -> {
                if (path.length == 3) {
                    getSubTaskById(exchange);
                } else {
                    getAllSubTasks(exchange);
                }
            }

            case POST -> {
                if (path.length == 3) {
                    updatedSubTask(exchange);
                } else {
                    addSubTask(exchange);
                }
            }

            case DELETE -> deleteSubTask(exchange);

            default -> writeDefault(exchange);
        }
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> subTaskId = getId(exchange);

        if (subTaskId.isPresent()) {
            Optional<SubTask> subTaskOpt = taskManager.getSubTaskById(subTaskId.get());
            if (subTaskOpt.isPresent()) {
                writeResponse(exchange, gson.toJson(subTaskOpt.get()), 200);
            } else {
                writeSubTaskNotFound(exchange, subTaskId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }
    }

    private void getAllSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        writeResponse(exchange, gson.toJson(subTasks), 200);
    }

    private void addSubTask(HttpExchange exchange) throws IOException {
        Optional<SubTask> subTaskOpt = extractSubTask(exchange);

        if (subTaskOpt.isPresent() && !checkNull(subTaskOpt)) {
            int epicId = subTaskOpt.get().getEpicId();
            Optional<Epic> epicOpt = taskManager.getEpicById(epicId);
            if (epicOpt.isPresent()) {
                SubTask subTask = subTaskOpt.get();
                try {
                    taskManager.addSubTask(subTask);
                    writeResponse(exchange, "Подзадача с id " + subTask.getId() + " добавлена!", 201);
                    return;
                } catch (ManagerException e) {
                    writeResponse(exchange, e.getMessage(), 406);
                }
            } else {
                writeResponse(exchange, "Эпик с id " + epicId + " не найден!", 404);
            }
        }

        writeIncorrectFormat(exchange);
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> subTaskId = getId(exchange);

        if (subTaskId.isPresent()) {
            Optional<SubTask> subTask = taskManager.getSubTaskById(subTaskId.get());
            if (subTask.isPresent()) {
                taskManager.removeSubTaskById(subTask.get().getId());
                writeResponse(exchange, "Подзадача с id " + subTaskId.get() + " удалена!", 200);
            } else {
                writeSubTaskNotFound(exchange, subTaskId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }
    }

    private void updatedSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> subTaskId = getId(exchange);

        if (subTaskId.isPresent()) {
            Optional<SubTask> newSubTask = extractSubTask(exchange);
            Optional<SubTask> oldSubTask = taskManager.getSubTaskById(subTaskId.get());

            if (oldSubTask.isEmpty()) {
                writeSubTaskNotFound(exchange, subTaskId.get());
                return;
            } else if (newSubTask.isEmpty() || checkNull(newSubTask)) {
                writeIncorrectFormat(exchange);
                return;
            }

            try {
                taskManager.updateSubTask(newSubTask.get());
                writeResponse(exchange, "Подзадача c id " + newSubTask.get().getId() + " обновлена!", 201);
            } catch (ManagerException e) {
                writeResponse(exchange, e.getMessage(), 406);
            }

        } else {
            writeIncorrectId(exchange);
        }
    }

    private Optional<SubTask> extractSubTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String subTaskAsString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(subTaskAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return Optional.ofNullable(gson.fromJson(jsonObject, SubTask.class));
    }

    private void writeSubTaskNotFound(HttpExchange exchange, int subTaskId) throws IOException {
        writeResponse(exchange, "Подзадача с id " + subTaskId + " не найдена!", 404);
    }
}
