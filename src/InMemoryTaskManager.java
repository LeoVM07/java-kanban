import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private int id = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
        tasks.put(task.getId(), task);
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
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasks.remove(subTask.getId());
            }
        }
        epics.remove(id);
        return epics.get(id);
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epicStatusCheck(epic.getId());
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
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask.getId());
        epicStatusCheck(subTask.getEpicId());
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


    /*Сильно сомневаюсь в правильности этого метода, но мне не хватило фантазии, чтобы придумать,
     как ещё можно получить доступ к полю другого класса в мэйне или тестах.
     Разве что наследовать тут от InMemoryHistoryManager ¯\_(ツ)_/¯  */
    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }

}
