import exceptions.ManagerException;
import managers.Managers;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


class InMemoryTaskManagerTest {
    private TaskManager managerTest;

    @BeforeEach
    void createNewTaskManagerForEachTest() {
        this.managerTest = Managers.getDefault();
    }

    @Test
    void shouldNotBeNullAndEqualsForTasksTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(task);
        int taskId = task.getId();
        final Task savedTask = managerTest.getTaskById(taskId).get();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void taskListShouldNotBeNullAndEqualsTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
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
        final Task savedEpic = managerTest.getEpicById(epicId).get();

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
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addSubTask(subTask);
        int subTaskId = subTask.getId();
        final Task savedSubTask = managerTest.getSubTaskById(subTaskId).get();

        assertNotNull(savedSubTask, "Сабтаск не найден.");
        assertEquals(subTask, savedSubTask, "Сабтаски не совпадают.");
    }

    @Test
    void subTaskListShouldNotBeNullAndEqualsTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addSubTask(subTask);

        final List<SubTask> subTasks = managerTest.getAllSubTasks();
        assertNotNull(subTasks, "Сабтаски не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество сабтасков.");
        assertEquals(subTask, subTasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void taskByIdAndTaskFromHistoryShouldBeEqual() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(task);
        managerTest.getTaskById(task.getId());
        Task taskInHistory = managerTest.getTaskHistory().get(0);
        assertEquals(task, taskInHistory, "Созданная задача отличается от задачи в истории");
    }

    @Test
    void newTaskAndTaskWithGeneratedIdShouldNotConflictWTFDoesThatEvenMean() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(task1);
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        task2.setId(1);
        managerTest.addTask(task2);
        Assertions.assertNotEquals(task1, task2, "ID задач не должен совпадать!");
    }

    @Test
    void allFieldsShouldBeEqualButItIsImpossibleBecauseTheIDIsGeneratedIndividuallyForEachTaskWhenTaskManagerAddsIt() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(task);
        int taskId = task.getId();
        Assertions.assertEquals(task.getName(), managerTest.getTaskById(taskId).get().getName());
        Assertions.assertEquals(task.getDescription(), managerTest.getTaskById(taskId).get().getDescription());
        Assertions.assertEquals(task.getStatus(), managerTest.getTaskById(taskId).get().getStatus());

    }

    @Test
    void shouldReturnDifferentTasksById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Task.Status.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().minusMinutes(10));
        managerTest.addTask(task);
        int taskId = task.getId();

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
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
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addSubTask(subTask);
        assertNotNull(managerTest.getExactEpicSubTasks(epicId), "Подзадачи для эпика не найдены!");
    }

    @Test
    void shouldNotShowWrongSubTaskId() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        managerTest.addEpic(epic);
        int epicId = epic.getId();
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addSubTask(subTask);
        managerTest.removeSubTaskById(subTask.getId());
        boolean isEmptySubTasks = managerTest.getExactEpicSubTasks(epicId).isEmpty();
        assertTrue(isEmptySubTasks, "В списке не должно остаться неактуальных подзадач!");
    }

    @Test
    public void namesForTasksShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        Task task = new Task("Tasks.Task", "Description", Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(task);
        task.setName("Different name");
        assertNotEquals(task.getName(), managerTest.getTaskById(task.getId()).get().getName(),
                "Имена должны отличаться!");
    }

    @Test
    public void namesForEpicsShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        Epic epic = new Epic("Tasks.Epic", "Description");
        managerTest.addTask(epic);
        epic.setName("Different name");
        assertNotEquals(epic.getName(), managerTest.getTaskById(epic.getId()).get().getName(),
                "Имена должны отличаться!");
    }

    @Test
    public void namesForSubTasksShouldNotBeEqualAfterSettingNameToUserTaskWithoutManager() {
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                1, Duration.ofMinutes(5), LocalDateTime.now());
        managerTest.addTask(subTask);
        subTask.setName("Different name");
        assertNotEquals(subTask.getName(), managerTest.getTaskById(subTask.getId()).get().getName(),
                "Имена должны отличаться!");
    }

    @Test
    public void epicStatusConfirmationAccordingToSubTaskStatus() {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        managerTest.addEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask1", "SubTask1 desription", Task.Status.NEW,
                epic1.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        SubTask subTask2 = new SubTask("SubTask1", "SubTask1 desription", Task.Status.NEW,
                epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(15));
        managerTest.addSubTask(subTask1);
        managerTest.addSubTask(subTask2);
        Assertions.assertEquals(Task.Status.NEW, managerTest.getEpicById(epic1.getId()).get().getStatus());

        subTask1.setStatus(Task.Status.IN_PROGRESS);
        subTask2.setStatus(Task.Status.IN_PROGRESS);
        managerTest.updateSubTask(subTask1);
        managerTest.updateSubTask(subTask2);
        Assertions.assertEquals(Task.Status.IN_PROGRESS, managerTest.getEpicById(epic1.getId()).get().getStatus());

        subTask1.setStatus(Task.Status.DONE);
        subTask2.setStatus(Task.Status.DONE);
        managerTest.updateSubTask(subTask1);
        managerTest.updateSubTask(subTask2);
        Assertions.assertEquals(Task.Status.DONE, managerTest.getEpicById(epic1.getId()).get().getStatus());

        subTask1.setStatus(Task.Status.NEW);
        managerTest.updateSubTask(subTask1);
        Assertions.assertEquals(Task.Status.IN_PROGRESS, managerTest.getEpicById(epic1.getId()).get().getStatus());
    }

    @Test
    public void prioritizedTasksTimeClashingTest() {
        LocalDateTime testingTime = LocalDateTime.of(2025, 2, 20, 12, 30);
        Task task1 = new Task("task1", "description1", Task.Status.NEW,
                Duration.ofMinutes(10), testingTime);
        Task task2 = new Task("task2", "description2", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(10), testingTime.plusMinutes(10));
        Task task3 = new Task("task3", "description3", Task.Status.DONE,
                Duration.ofMinutes(60), testingTime.minusMinutes(50));
        Task task4 = new Task("task4", "description4", Task.Status.NEW,
                Duration.ofMinutes(10), testingTime);
        Task task5 = new Task("task5", "description5", Task.Status.NEW,
                Duration.ofMinutes(20), testingTime.minusMinutes(10));
        Task task6 = new Task("task6", "description6", Task.Status.NEW,
                Duration.ofMinutes(20), testingTime.minusMinutes(15));
        Task task7 = new Task("task7", "description7", Task.Status.DONE,
                Duration.ofMinutes(70), testingTime.minusMinutes(60));
        Task task8 = new Task("task3", "description3", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(120), testingTime.minusMinutes(60));
        try {
            managerTest.addTask(task1);
            managerTest.addTask(task2);
            managerTest.addTask(task3);
            managerTest.addTask(task4);
            managerTest.addTask(task5);
            managerTest.addTask(task6);
            managerTest.addTask(task7);
            managerTest.addTask(task8);
        } catch (ManagerException e) {
            Assertions.assertEquals(2, managerTest.getPrioritizedTasks().size());
        }
    }
}