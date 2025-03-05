import managers.Managers;
import managers.TaskManager;
import tasks.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;


class TaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        TaskManager testManager = Managers.getDefault();
        Task task = new Task("Tasks.Task", "Description", Duration.ofMinutes(0), LocalDateTime.now());
        testManager.addTask(task);
        Assertions.assertNotNull(task.getStatus());
    }

}