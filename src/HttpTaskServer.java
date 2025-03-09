import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import handlers.adapters.DurationAdapter;
import handlers.adapters.LocalDateTimeAdapter;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    TaskManager manager;
    final static int PORT = 8080;
    Gson gson;
    HttpServer server;

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefault();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/epics", new EpicHandler(manager, gson));
        server.createContext("/subtasks", new SubTasksHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        System.out.println("Сервер запущен. Порт: " + PORT);
        httpTaskServer.server.start();

    }

    public void startServer() {
        server.start();
    }

    public void stopServer() {
        server.stop(1);
    }
}
