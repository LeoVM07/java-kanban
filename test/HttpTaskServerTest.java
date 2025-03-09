import com.google.gson.Gson;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    HttpTaskServer httpTaskServer = new HttpTaskServer();
    TaskManager manager = httpTaskServer.manager;
    Gson gson = httpTaskServer.gson;
    HttpClient client = HttpClient.newHttpClient();
    String hostPath = "http://localhost:8080/";


    public HttpTaskServerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() {
        httpTaskServer.manager.clearTasks();
        httpTaskServer.manager.clearEpics();
        httpTaskServer.manager.clearSubTasks();
        httpTaskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stopServer();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Description", Task.Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        URI url = URI.create(hostPath + "tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Task.Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task1);
        Task task2 = new Task("Edited name", "Description", Task.Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        task2.setId(1);

        String taskJson = httpTaskServer.gson.toJson(task2);
        URI url = URI.create(hostPath + "tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Edited name", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(10));
        manager.addTask(task1);
        manager.addTask(task2);

        URI url = URI.create(hostPath + "tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String expectedTask = gson.toJson(task2);
        String actualTask = response.body();

        Assertions.assertEquals(200, response.statusCode(), "Код статуса должен быть 200");
        Assertions.assertEquals(expectedTask, actualTask, "Возвращается не та задача");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(task1);

        URI url = URI.create(hostPath + "tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код должен быть 200");

        List<Task> tasksFromManager = manager.getAllTasks();
        Assertions.assertEquals(0, tasksFromManager.size(), "В списке не должно остаться задач");
    }

    @Test
    public void testResponseCodeShouldBe400ForGetTaskById() throws IOException, InterruptedException {
        URI url = URI.create(hostPath + "tasks/DirectedByRobertBWeide");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Код должен быть 400");
    }

    @Test
    public void testResponseCodeShouldBe404ForGetTaskById() throws IOException, InterruptedException {
        URI url = URI.create(hostPath + "tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Код должен быть 404");
    }

    @Test
    public void testResponseCodeShouldBe406ForPostTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(10));
        manager.addTask(task2);
        Task task3 = new Task("Task 3", "Description 3", Task.Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(5));
        String taskJson = gson.toJson(task3);

        URI url = URI.create(hostPath + "tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(), "Код должен быть 406");

        task3.setId(1);
        taskJson = gson.toJson(task3);
        url = URI.create(hostPath + "tasks/1");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(), "Код должен быть 406");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        String epicJson = gson.toJson(epic);

        URI url = URI.create(hostPath + "epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректный размер списка эпиков");
        assertEquals("Epic name", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic name", "Epic description");
        manager.addEpic(epic1);
        Epic epic2 = new Epic("Epic name edited", "Epic description");
        epic2.setId(1);

        String epicJson = gson.toJson(epic2);

        URI url = URI.create(hostPath + "epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректный размер списка эпиков");
        assertEquals("Epic name edited", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);

        URI url = URI.create(hostPath + "epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код статуса должен быть 200");

        List<Epic> epicsFromManager = manager.getAllEpics();
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректный размер списка эпиков");
        Assertions.assertEquals("Epic name", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1, Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask);

        URI url = URI.create(hostPath + "epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код статуса должен быть 200");

        Epic epicFromManager = manager.getEpicById(1).get();
        assertEquals(1, epicFromManager.getSubTasksId().size(), "Некорректный размер списка подзадач");
        assertEquals(2, epicFromManager.getSubTasksId().get(0), "Некорректный id подзадачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);

        URI url = URI.create(hostPath + "epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(0, epicsFromManager.size(), "Некорректный размер списка эпиков");
    }

    @Test
    public void testResponseCodeShouldBe400ForGetEpicById() throws IOException, InterruptedException {
        URI url = URI.create(hostPath + "epics/easilyBlocked");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Код должен быть 400");
    }

    @Test
    public void testResponseCodeShouldBe404ForGetEpicById() throws IOException, InterruptedException {
        URI url = URI.create(hostPath + "epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Код должен быть 404");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1, Duration.ofMinutes(3), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create(hostPath + "subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask", "Description", 1, Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask updated", "Description", 1, Duration.ofMinutes(10), LocalDateTime.now());
        subTask2.setId(2);

        String subTaskJson = gson.toJson(subTask2);

        URI url = URI.create(hostPath + "subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код статуса должен быть 201");

        List<SubTask> tasksFromManager = manager.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask updated", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1,
                Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask);

        URI url = URI.create(hostPath + "subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса должен быть 200");

        String subTaskExpected = gson.toJson(subTask);
        String subTaskActual = response.body();

        assertEquals(subTaskExpected, subTaskActual, "Подзадачи не равны");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1,
                Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask);

        URI url = URI.create(hostPath + "subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса должен быть 200");

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();

        assertEquals(0, subTasksFromManager.size(), "Список подзадач должен быть пуст");
    }

    @Test
    public void testResponseCodeShouldBe404ForGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1,
                Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask);

        URI url = URI.create(hostPath + "subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Код статуса должен быть 404");
    }

    @Test
    public void testResponseCodeShouldBe400ForGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Description", 1,
                Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask);

        URI url = URI.create(hostPath + "subtasks/ohMyShoulder");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Код статуса должен быть 400");
    }

    @Test
    public void testResponseCodeShouldBe406ForPostSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description", 1,
                Duration.ofMinutes(3), LocalDateTime.now());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask 2", "Description", 1,
                Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(3));
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "Description", 1,
                Duration.ofMinutes(40), LocalDateTime.now().minusMinutes(3));
        String subTaskJson = gson.toJson(subTask3);

        URI url = URI.create(hostPath + "subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Код статуса должен быть 406");

        subTask3.setId(2);
        subTaskJson = gson.toJson(subTask3);
        url = URI.create(hostPath + "subtasks/2");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Код статуса должен быть 406");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Task.Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 12, 12, 12, 0));
        Task task2 = new Task("Task 2", "Description", Task.Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 12, 12, 12, 30));
        Task task3 = new Task("Task 3", "Description", Task.Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 12, 12, 12, 40));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.getTaskById(3);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);

        URI url = URI.create(hostPath + "history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseExpected = "[{\"name\":\"Task 3\",\"description\":\"Description\",\"id\":3,\"status\":\"NEW\",\"duration\":\"60\",\"startTime\":\"12:40 | 12.12.2025\"},{\"name\":\"Task 2\",\"description\":\"Description\",\"id\":2,\"status\":\"NEW\",\"duration\":\"10\",\"startTime\":\"12:30 | 12.12.2025\"},{\"name\":\"Task 1\",\"description\":\"Description\",\"id\":1,\"status\":\"NEW\",\"duration\":\"30\",\"startTime\":\"12:00 | 12.12.2025\"}]";
        String bodyActual = response.body();

        Assertions.assertEquals(200, response.statusCode(), "Код статуса должен быть 200");
        Assertions.assertEquals(responseExpected, bodyActual, "Тело ответа отличается от ожидаемого");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Task.Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 12, 12, 12, 0));
        Task task2 = new Task("Task 2", "Description", Task.Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 12, 12, 11, 40));
        Task task3 = new Task("Task 3", "Description", Task.Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 12, 12, 12, 40));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        URI url = URI.create(hostPath + "prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseExpected = "[{\"name\":\"Task 2\",\"description\":\"Description\",\"id\":2,\"status\":\"NEW\",\"duration\":\"10\",\"startTime\":\"11:40 | 12.12.2025\"},{\"name\":\"Task 1\",\"description\":\"Description\",\"id\":1,\"status\":\"NEW\",\"duration\":\"30\",\"startTime\":\"12:00 | 12.12.2025\"},{\"name\":\"Task 3\",\"description\":\"Description\",\"id\":3,\"status\":\"NEW\",\"duration\":\"60\",\"startTime\":\"12:40 | 12.12.2025\"}]";
        String bodyActual = response.body();

        Assertions.assertEquals(200, response.statusCode(), "Код статуса должен быть 200");
        Assertions.assertEquals(responseExpected, bodyActual, "Тело ответа отличается от ожидаемого");
    }

}
