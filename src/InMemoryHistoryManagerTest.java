import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    public void historyShouldNotBeEmptyAfterGetTaskById() {
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        Assertions.assertNotNull(historyManager.getHistory(), "Ни одна задача не попала в историю!");

    }


}