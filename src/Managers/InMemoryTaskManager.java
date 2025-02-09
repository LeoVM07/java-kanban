package Managers;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected int id = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    //*******ТАСКИ*******
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task addTask(Task task) {
        task.setId(generateId());
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.NEW);
        }
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(task.getId());
        tasks.put(newTask.getId(), newTask);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());
        tasks.put(currentTask.getId(), currentTask);
        return task;
    }

    @Override
    public Task removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        return tasks.get(id);
    }

    //*******ЭПИКИ*******
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        epicStatusCheck(currentEpic.getId());
        return epic;
    }

    @Override
    public Epic removeEpicById(int id) {
        for (Integer subTaskId : epics.get(id).getSubTasksId()) {
            if (subTasks.get(subTaskId).getEpicId() == id) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
        return epics.get(id);
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateId());

        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(epic.getId());

        epics.put(newEpic.getId(), newEpic);
        epicStatusCheck(newEpic.getId());

        return epic;
    }

    @Override
    public ArrayList<Integer> getExactEpicSubTasks(int id) {
        return epics.get(id).getSubTasksId();
    }


    //*******САБТАСКИ*******
    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearSubTasks() {
        ArrayList<Integer> emptyList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epic.setSubTasksId(emptyList); //заменяет существующий список с id в каждом эпике на пустую копию
            epicStatusCheck(epic.getId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(generateId());
        if (subTask.getStatus() == null) {
            subTask.setStatus(Task.Status.NEW);
        }

        SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicId());
        newSubTask.setId(subTask.getId());
        if (newSubTask.getStatus() == null) {
            newSubTask.setStatus(Task.Status.NEW);
        }
        subTasks.put(newSubTask.getId(), newSubTask);
        epic.addSubTaskId(newSubTask.getId());
        epicStatusCheck(newSubTask.getEpicId());

        return subTask;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public SubTask removeSubTaskById(int id) {
        int epicId = subTasks.get(id).getEpicId();
        ArrayList<Integer> subTasksIds = epics.get(epicId).getSubTasksId();
        subTasksIds.remove(Integer.valueOf(id));
        epics.get(epicId).setSubTasksId(subTasksIds);
        subTasks.remove(id);
        epicStatusCheck(epicId);
        historyManager.remove(id);
        return subTasks.get(id);
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getId());
        subTasks.put(currentSubTask.getId(), currentSubTask);
        Epic epic = epics.get(currentSubTask.getEpicId());
        epicStatusCheck(epic.getId());
        return subTask;
    }

    @Override
    public int generateId() {
        return id++;
    }

    @Override
    public void epicStatusCheck(Integer epicId) { //цикл проходит по мапам с эпиками и сабтасками и выставляет
        ArrayList<Integer> subTasksId = epics.get(epicId).getSubTasksId(); //статус эпика в соответствии с состоянием
        Epic currentEpic = epics.get(epicId);                              //сабтасков
        int statusCount = 0;
        if (currentEpic.getSubTasksId().isEmpty()) {
            currentEpic.setStatus(Task.Status.NEW);
            return;
        }
        for (Integer currentSubTaskId : subTasksId) {
            if (subTasks.get(currentSubTaskId).getStatus() == Task.Status.IN_PROGRESS) {
                currentEpic.setStatus(Task.Status.IN_PROGRESS);
                break;
            }
            if (subTasks.get(currentSubTaskId).getStatus() == Task.Status.DONE) {
                statusCount++;
            }
            if (statusCount == subTasksId.size()) {
                currentEpic.setStatus(Task.Status.DONE);
            } else if (statusCount > 0 && statusCount < subTasksId.size()) {
                currentEpic.setStatus(Task.Status.IN_PROGRESS);
            } else {
                currentEpic.setStatus(Task.Status.NEW);
            }
        }
    }

    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }

}
