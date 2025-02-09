import Managers.Managers;
import Managers.TaskManager;
import Managers.HistoryManager;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    public void historyShouldNotBeEmptyAfterGetTaskById() {
        Task task = new Task("Tasks.Task", "Description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(historyManager.getHistory(), "Ни одна задача не попала в историю!");

    }

}