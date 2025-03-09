import managers.Managers;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    private FileBackedTaskManager testingManager;
    private File fileToSave;

    @BeforeEach
    public void createNecessaryObjectsForTests() {
        try {
            this.fileToSave = File.createTempFile("Testing_File", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.testingManager = Managers.getFileBackedTaskManager(fileToSave);

        Task task1 = new Task("TestTask1", "TestTask1 description",
                Duration.ofMinutes(5), LocalDateTime.now());
        testingManager.addTask(task1);

        Task task2 = new Task("TestTask2", "TestTask2 description", Task.Status.IN_PROGRESS,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        testingManager.addTask(task2);

        Epic epic1 = new Epic("TestEpic1", "TestEpic1 description");
        testingManager.addEpic(epic1);

        Epic epic2 = new Epic("TestEpic2", "TestEpic2 description");
        testingManager.addEpic(epic2);

        Epic epic3 = new Epic("TestEpic3", "TestEpic3 description");
        testingManager.addEpic(epic3);

        SubTask subTask1 = new SubTask("TestSubTask1", "TestSubTask1 description", Task.Status.DONE,
                epic1.getId(), Duration.ofMinutes(5), LocalDateTime.now().minusMinutes(25));
        testingManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("TestSubTask2", "TestSubTask2 description", Task.Status.IN_PROGRESS,
                epic1.getId(), Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));
        testingManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask("TestSubTask3", "TestSubTask3 description", Task.Status.DONE,
                epic3.getId(), Duration.ofMinutes(5), LocalDateTime.now().minusMinutes(35));
        testingManager.addSubTask(subTask3);
    }

    @Test
    public void createdManagerShouldNotBeEmpty() {
        Assertions.assertNotNull(testingManager.getAllTasks());
        Assertions.assertNotNull(testingManager.getAllEpics());
        Assertions.assertNotNull(testingManager.getAllSubTasks());
    }

    @Test
    public void fileToSaveShouldNotBeEmpty() {
        ArrayList<String> tasksFromFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileToSave))) {
            while (br.ready()) {
                String line = br.readLine();
                tasksFromFile.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(tasksFromFile);
    }

    @Test
    public void savedAndLoadedTasksShouldBeIdentical() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(fileToSave);
        Assertions.assertEquals(loadedManager.getAllTasks(), testingManager.getAllTasks());
        Assertions.assertEquals(loadedManager.getAllEpics(), testingManager.getAllEpics());
        Assertions.assertEquals(loadedManager.getAllSubTasks(), testingManager.getAllSubTasks());
    }
}
