import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


class InMemoryTaskManagerTest {
    private TaskManager managerTest;

    @BeforeEach
    void createNewTaskManagerForEachTest() {
        this.managerTest = Managers.getDefault();
    }

    @Test
    void shouldNotBeNullAndEqualsForTasksTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task);
        int taskId = task.getId();
        final Task savedTask = managerTest.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void taskListShouldNotBeNullAndEqualsTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task);

        final List<Task> tasks = managerTest.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldNotBeNullAndEqualsForEpicsTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();
        final Task savedEpic = managerTest.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void epicListShouldNotBeNullAndEqualsTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);

        final List<Epic> epics = managerTest.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldNotBNullAndEqualsForSubTasksTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epic.getId());
        managerTest.addSubTask(subTask);
        int subTaskId = subTask.getId();
        final Task savedSubTask = managerTest.getSubTaskById(subTaskId);

        assertNotNull(savedSubTask, "Сабтаск не найден.");
        assertEquals(subTask, savedSubTask, "Сабтаски не совпадают.");
    }

    @Test
    void subTaskListShouldNotBeNullAndEqualsTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epic.getId());
        managerTest.addSubTask(subTask);

        final List<SubTask> subTasks = managerTest.getAllSubTasks();
        assertNotNull(subTasks, "Сабтаски не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество сабтасков.");
        assertEquals(subTask, subTasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void taskByIdAndTaskFromHistoryShouldBeEqual() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task);
        managerTest.getTaskById(task.getId());
        Task taskInHistory = managerTest.getTaskHistory().get(0);
        assertEquals(task, taskInHistory, "Созданная задача отличается от задачи в истории");
    }

    @Test
    void newTaskAndTaskWithGeneratedIdShouldNotConflictWTFDoesThatEvenMean() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task1);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        task2.setId(1);
        managerTest.addTask(task2);
        Assertions.assertNotEquals(task1, task2, "ID задач не должен совпадать!");
    }

    @Test
    void allFieldsShouldBeEqualButItIsImpossibleBecauseTheIDIsGeneratedIndividuallyForEachTaskWhenTaskManagerAddsIt() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task);
        int taskId = task.getId();
        Assertions.assertEquals(task.getName(), managerTest.getTaskById(taskId).getName());
        Assertions.assertEquals(task.getDescription(), managerTest.getTaskById(taskId).getDescription());
        Assertions.assertEquals(task.getStatus(), managerTest.getTaskById(taskId).getStatus());

    }

    @Test
    void shouldReturnDifferentTasksById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW);
        managerTest.addTask(task);
        int taskId = task.getId();

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epicId);
        managerTest.addSubTask(subTask);
        int subTaskId = subTask.getId();

        assertNotNull(managerTest.getTaskById(taskId), "Задача не найдена по ID!");
        assertNotNull(managerTest.getEpicById(epicId), "Эпик не найден по ID!");
        assertNotNull(managerTest.getSubTaskById(subTaskId), "Подзадача не найдена по ID!");
    }

    @Test
    void shouldReturnExactEpicSubTasks() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epicId);
        managerTest.addSubTask(subTask);
        assertNotNull(managerTest.getExactEpicSubTasks(epicId), "Подзадачи для эпика не найдены!");
    }

    @Test
    void shouldNotShowWrongSubTaskId () {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", epicId);
        managerTest.addSubTask(subTask);
        managerTest.removeSubTaskById(subTask.getId());
        boolean isEmptySubTasks = managerTest.getExactEpicSubTasks(epicId).isEmpty();
        assertTrue(isEmptySubTasks, "В списке не должно остаться неактуальных подзадач!");
    }

    @Test
    public void namesForTasksShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        Task task = new Task("Task", "Description");
        managerTest.addTask(task);
        task.setName("Different name");
        assertNotEquals(task.getName(), managerTest.getTaskById(task.getId()).getName(),
                "Имена должны отличаться!");
    }

    @Test
    public void namesForEpicsShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        Epic epic = new Epic("Epic", "Description");
        managerTest.addTask(epic);
        epic.setName("Different name");
        assertNotEquals(epic.getName(), managerTest.getTaskById(epic.getId()).getName(),
                "Имена должны отличаться!");
    }

    @Test
    public void namesForSubTasksShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        SubTask subTask = new SubTask("Subtask", "Description", 1);
        managerTest.addTask(subTask);
        subTask.setName("Different name");
        assertNotEquals(subTask.getName(), managerTest.getTaskById(subTask.getId()).getName(),
                "Имена должны отличаться!");
    }

}