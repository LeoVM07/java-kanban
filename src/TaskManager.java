import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    public int id = 1;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }


    //*******ТАСКИ*******
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        Task currentTask = tasks.get(task.getId());
        tasks.put(currentTask.getId(), currentTask);
        return task;
    }

    public Task removeTaskById(int id) {
        tasks.remove(id);
        return tasks.get(id);
    }

    //*******ЭПИКИ*******
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        epicStatusCheck(currentEpic.getId());
        return epic;
    }

    public Epic removeEpicById(int id) {
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasks.remove(subTask.getId());
            }
        }
        epics.remove(id);
        return epics.get(id);
    }

    public Epic addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epicStatusCheck(epic.getId());
        return epic;
    }

    public ArrayList<Integer> getExactEpicSubTasks(int id) {
        return epics.get(id).getSubTasksId();
    }


    //*******САБТАСКИ*******
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void clearSubTasks() {
        ArrayList<Integer> emptyList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epic.setSubTasksId(emptyList); //заменяет существующий список с id в каждом эпике на пустую копию
            epicStatusCheck(epic.getId());
        }
        subTasks.clear();
    }

    public SubTask addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask.getId());
        epicStatusCheck(subTask.getEpicId());
        return subTask;
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public SubTask removeSubTaskById(int id) {
        int epicId = subTasks.get(id).getEpicId();
        ArrayList<Integer> subTasksIds = epics.get(epicId).getSubTasksId();
        subTasksIds.remove(Integer.valueOf(id));
        epics.get(epicId).setSubTasksId(subTasksIds);
        subTasks.remove(id);
        epicStatusCheck(epicId);
        return subTasks.get(id);

    }

    public SubTask updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getId());
        subTasks.put(currentSubTask.getId(), currentSubTask);
        Epic epic = epics.get(currentSubTask.getEpicId());
        epicStatusCheck(epic.getId());
        return subTask;
    }

    private int generateId() {
        return id++;
    }

    private void epicStatusCheck(Integer epicId) { //цикл проходит по мапам с эпиками и сабтасками и выставляет
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

}
