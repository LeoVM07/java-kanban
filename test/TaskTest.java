import Managers.Managers;
import Managers.TaskManager;
import Tasks.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


class TaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        TaskManager testManager = Managers.getDefault();
        Task task = new Task("Tasks.Task", "Description");
        testManager.addTask(task);
        Assertions.assertNotNull(task.getStatus());
    }

}