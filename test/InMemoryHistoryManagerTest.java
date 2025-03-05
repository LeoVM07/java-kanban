import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryHistoryManagerTest {
    Task task1;
    Task task2;
    Task task3;
    Epic epic1;
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;
    private TaskManager taskManager;

    @BeforeEach
    void createNecessaryObjectsForTests() {
        this.taskManager = Managers.getDefault();

        task1 = new Task("task1", "description1", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        task2 = new Task("task2", "description2", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.now());
        task3 = new Task("task3", "description3", Task.Status.DONE,
                Duration.ofMinutes(20), LocalDateTime.now());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        epic1 = new Epic("epic1", "description1");
        epic2 = new Epic("epic2", "description2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subTask1 = new SubTask("subTask1", "description1", epic1.getId(),
                Duration.ofMinutes(69), LocalDateTime.now());
        subTask2 = new SubTask("subTask2", "description2", epic1.getId(),
                Duration.ofMinutes(17), LocalDateTime.now());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
    }


    @Test
    public void historyShouldNotBeEmptyAfterGetTaskById() {
        taskManager.getTaskById(task1.getId());
        Assertions.assertNotNull(taskManager.getTaskHistory(), "Ни одна задача не попала в историю!");
    }

    @Test
    public void historyShouldBeEmpty() {
        Assertions.assertTrue(taskManager.getTaskHistory().isEmpty(), "История должна быть пустой!");
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.clearTasks();
        Assertions.assertTrue(taskManager.getTaskHistory().isEmpty(), "История должна быть пустой!");
    }

    @Test
    public void shouldNotBeDuplicatesInHistory() {
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(1, taskManager.getTaskHistory().size(), "В истории не должно быть " +
                "дубликатов!");
    }

    @Test
    public void historyShouldBeInProperOrderAfterRemovingTasks() {
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.removeTaskById(task1.getId());
        Assertions.assertEquals(taskManager.getTaskHistory().get(1), epic2, "Некорректный порядок задач!");

        taskManager.removeTaskById(task2.getId());
        Assertions.assertEquals(taskManager.getTaskHistory().get(0), epic2, "Некорректный порядок задач!");

        taskManager.removeTaskById(task3.getId());
        Assertions.assertEquals(taskManager.getTaskHistory().get(1), subTask1, "Некорректный порядок задач!");
    }

}