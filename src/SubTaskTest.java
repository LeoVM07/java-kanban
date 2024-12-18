import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        final int TEST_EPIC_ID = 1;
        SubTask subTask = new SubTask("Task", "Description", TEST_EPIC_ID);
        Assertions.assertNotNull(subTask.getStatus());
    }


}