package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    void clearTasks();

    Optional<Task> getTaskById(int id);

    Task addTask(Task task);

    Task updateTask(Task task);

    Task removeTaskById(int id);

    //*******ЭПИКИ*******
    ArrayList<Epic> getAllEpics();

    void clearEpics();

    Optional<Epic> getEpicById(int id);

    Epic updateEpic(Epic epic);

    Epic removeEpicById(int id);

    Epic addEpic(Epic epic);

    ArrayList<Integer> getExactEpicSubTasks(int id);

    //*******САБТАСКИ*******
    ArrayList<SubTask> getAllSubTasks();

    void clearSubTasks();

    SubTask addSubTask(SubTask subTask);

    Optional<SubTask> getSubTaskById(int id);

    SubTask removeSubTaskById(int id);

    SubTask updateSubTask(SubTask subTask);

    int generateId();

    void epicStateCheck(Integer epicId);

    List<Task> getTaskHistory();

    Set<Task> getPrioritizedTasks();
}

