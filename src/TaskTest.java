import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


class TaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        Task task = new Task("Task", "Description");
        Assertions.assertNotNull(task.getStatus());
    }

}