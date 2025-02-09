package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    void clearTasks();

    Task getTaskById(int id);

    Task addTask(Task task);

    Task updateTask(Task task);

    Task removeTaskById(int id);

    //*******ЭПИКИ*******
    ArrayList<Epic> getAllEpics();

    void clearEpics();

    Epic getEpicById(int id);

    Epic updateEpic(Epic epic);

    Epic removeEpicById(int id);

    Epic addEpic(Epic epic);

    ArrayList<Integer> getExactEpicSubTasks(int id);

    //*******САБТАСКИ*******
    ArrayList<SubTask> getAllSubTasks();

    void clearSubTasks();

    SubTask addSubTask(SubTask subTask);

    SubTask getSubTaskById(int id);

    SubTask removeSubTaskById(int id);

    SubTask updateSubTask(SubTask subTask);

    int generateId();

    void epicStatusCheck(Integer epicId);

    List<Task> getTaskHistory();
}

