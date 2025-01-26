import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        TaskManager testManager = Managers.getDefault();
        Epic testEpic = new Epic("Epic", "Description");
        testManager.addEpic(testEpic);
        SubTask testSubTask = new SubTask("SubTask", "Description", testEpic.getId());
        testManager.addSubTask(testSubTask);
        Assertions.assertNotNull(testSubTask.getStatus());
    }


}