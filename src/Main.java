import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import managers.FileBackedTaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //Реализация тестового задания
        File data = new File("Saved_Tasks.csv");
        FileBackedTaskManager manager = Managers.getFileBackedTaskManager(data);

        Task task1 = new Task("Задача 1", "Описание для задачи 1");
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание для задачи 2", Task.Status.IN_PROGRESS);
        manager.addTask(task2);

        Task task3 = new Task("Задача 3", "Описание для задачи 3", Task.Status.DONE);
        manager.addTask(task3);

        tasks.Epic epic1 = new tasks.Epic("Эпик 1", "Описание для эпика 1");
        manager.addEpic(epic1);

        tasks.SubTask subTask1 = new tasks.SubTask("Подзадача 1", "Описание для подзадачи 1", epic1.getId());
        manager.addSubTask(subTask1);

        tasks.SubTask subTask2 = new tasks.SubTask("Подзадача 2", "Описание для подзадачи 2", epic1.getId());
        manager.addSubTask(subTask2);

        tasks.SubTask subTask3 = new tasks.SubTask("Подзадача 3", "Описание для подзадачи 3", Task.Status.DONE,
                epic1.getId());
        manager.addSubTask(subTask3);

        tasks.Epic epic2 = new tasks.Epic("Эпик 2", "Описание для эпика 2");
        manager.addEpic(epic2);

        System.out.println("Сохранённые задачи созданного с нуля менеджера:");
        printAllTasks(manager);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(data);
        System.out.println("Загруженные задачи с помощью метода loadFromFile:");
        printAllTasks(loadedManager);

    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("Список подзадач:");
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println(subTask);
        }
    }
}


