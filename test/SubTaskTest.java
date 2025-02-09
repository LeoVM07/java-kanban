import Managers.Managers;
import Managers.TaskManager;
import Tasks.Epic;
import Tasks.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    public void checkTaskStatusIfNotSetAtCreation() {
        TaskManager testManager = Managers.getDefault();
        Epic testEpic = new Epic("Tasks.Epic", "Description");
        testManager.addEpic(testEpic);
        SubTask testSubTask = new SubTask("Tasks.SubTask", "Description", testEpic.getId());
        testManager.addSubTask(testSubTask);
        Assertions.assertNotNull(testSubTask.getStatus());
    }


}