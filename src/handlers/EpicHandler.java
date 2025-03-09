package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case GET -> {
                if (path.length == 3) {
                    getEpicById(exchange);
                } else if (path.length == 4 && path[3].equals("subtasks")) {
                    getEpicSubTasks(exchange);
                } else {
                    getAllEpics(exchange);
                }
            }
            case POST -> {
                if (path.length == 3) {
                    updateEpic(exchange);
                } else {
                    addEpic(exchange);
                }
            }
            case DELETE -> deleteEpic(exchange);
            default -> writeDefault(exchange);
        }
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getId(exchange);

        if (epicId.isPresent()) {
            Optional<Epic> epic = taskManager.getEpicById(epicId.get());
            if (epic.isPresent()) {
                writeResponse(exchange, gson.toJson(epic.get()), 200);
            } else {
                writeEpicNotFound(exchange, epicId.get());
            }
        } else {
            writeIncorrectFormat(exchange);
        }

    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        writeResponse(exchange, gson.toJson(epics), 200);
    }

    private void getEpicSubTasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getId(exchange);

        if (epicId.isPresent()) {
            Optional<Epic> epicOpt = taskManager.getEpicById(epicId.get());

            if (epicOpt.isPresent()) {
                ArrayList<Integer> subTasks = epicOpt.get().getSubTasksId();
                if (subTasks.isEmpty()) {
                    writeResponse(exchange, "Список подзадач пуст!", 404);
                } else {
                    writeResponse(exchange, gson.toJson(epicOpt.get().getSubTasksId()), 200);
                }
            } else {
                writeEpicNotFound(exchange, epicId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        Optional<Epic> epicOpt = extractEpic(exchange);

        if (epicOpt.isPresent() && !checkNull(epicOpt)) {
            taskManager.addEpic(epicOpt.get());
            writeResponse(exchange, "Эпик с id " + epicOpt.get().getId() + " добавлен!", 201);
            return;
        }

        writeIncorrectFormat(exchange);
    }

    private void updateEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getId(exchange);

        if (epicId.isPresent()) {
            if (taskManager.getEpicById(epicId.get()).isPresent()) {
                Optional<Epic> newEpic = extractEpic(exchange);
                if (newEpic.isPresent()) {
                    taskManager.updateEpic(newEpic.get());
                    writeResponse(exchange, "Эпик с id " + epicId.get() + " обновлён!", 201);
                } else {
                    writeIncorrectFormat(exchange);
                }
            } else {
                writeEpicNotFound(exchange, epicId.get());
            }
        } else {
            writeIncorrectId(exchange);
        }

    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getId(exchange);

        if (epicId.isPresent()) {
            Optional<Epic> epicOpt = taskManager.getEpicById(epicId.get());
            if (epicOpt.isPresent()) {
                taskManager.removeEpicById(epicOpt.get().getId());
                writeResponse(exchange, "Эпик с id " + epicId.get() + " удалён!", 200);
            } else {
                writeEpicNotFound(exchange, epicId.get());
            }
        } else {
            writeIncorrectFormat(exchange);
        }
    }

    private Optional<Epic> extractEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String epicAsString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(epicAsString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return Optional.ofNullable(gson.fromJson(jsonObject, Epic.class));
    }

    private void writeEpicNotFound(HttpExchange exchange, int epicId) throws IOException {
        writeResponse(exchange, "Эпик с id " + epicId + " не найден!", 404);
    }
}
