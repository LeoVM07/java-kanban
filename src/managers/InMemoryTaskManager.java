package managers;

import exceptions.TaskTimeException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected int id = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //*******ТАСКИ*******

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.keySet().forEach(historyManager::remove);
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
        boolean isClashing = true; //переменная для проверки наложения задач по времени

        try {
            isClashing = validateTaskTime(task);
        } catch (TaskTimeException e) {
            System.out.println(e.getMessage());
        }

        if (task.getStatus() == null) {
            task.setStatus(Task.Status.NEW);
        }
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getDuration(),
                task.getStartTime());
        newTask.setId(task.getId());
        tasks.put(newTask.getId(), newTask);

        if (newTask.getStartTime() != null && !isClashing) prioritizedTasks.add(newTask);
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
        prioritizedTasks.remove(tasks.get(id));
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
        subTasks.values().forEach(prioritizedTasks::remove);
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.keySet().forEach(historyManager::remove);
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
        epicStateCheck(currentEpic.getId());
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
        epicStateCheck(newEpic.getId());

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
            epicStateCheck(epic.getId());
        }
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.values().forEach(prioritizedTasks::remove);
        subTasks.clear();
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(generateId());
        boolean isClashing = true;

        try {
            isClashing = validateTaskTime(subTask);
        } catch (TaskTimeException e) {
            System.out.println(e.getMessage());
        }

        if (subTask.getStatus() == null) {
            subTask.setStatus(Task.Status.NEW);
        }

        SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(),
                subTask.getEpicId(), subTask.getDuration(), subTask.getStartTime());
        newSubTask.setId(subTask.getId());
        if (newSubTask.getStatus() == null) {
            newSubTask.setStatus(Task.Status.NEW);
        }
        subTasks.put(newSubTask.getId(), newSubTask);
        if (newSubTask.getStartTime() != null && !isClashing) prioritizedTasks.add(newSubTask);
        epic.addSubTaskId(newSubTask.getId());
        epicStateCheck(newSubTask.getEpicId());

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
        prioritizedTasks.remove(subTasks.get(id));
        subTasksIds.remove(Integer.valueOf(id));
        epics.get(epicId).setSubTasksId(subTasksIds);
        subTasks.remove(id);
        epicStateCheck(epicId);
        historyManager.remove(id);
        return subTasks.get(id);
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        boolean isClashing = true;
        if (subTasks.containsKey(subTask.getId())) {
            try {
                isClashing = validateTaskTime(subTask);
            } catch (TaskTimeException e) {
                System.out.println(e.getMessage());
            }
            subTasks.replace(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epicStateCheck(epic.getId());
            if (subTask.getStartTime() != null && !isClashing) {
                prioritizedTasks.remove(subTask);
                prioritizedTasks.add(subTask);
            }
        }
        return subTask;
    }


    @Override
    public int generateId() {
        return id++;
    }

    @Override
    public void epicStateCheck(Integer epicId) { //цикл проходит по мапам с эпиками и сабтасками и выставляет
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


        long durationTime = subTasksId.stream()
                .map(subTasks::get)
                .mapToLong(subTask -> subTask.getDuration().toMinutes())
                .sum();

        currentEpic.setDuration(Duration.ofMinutes(durationTime));
        currentEpic.setStartTime(getMinimalStartTime(currentEpic));
    }

    public boolean validateTaskTime(Task task) throws TaskTimeException {
        List<Integer> clashingTasks = prioritizedTasks.stream()
                .filter(t -> (task.getStartTime().equals(t.getStartTime())) ||
                        (task.getStartTime().isAfter(t.getStartTime())) &&
                                (task.getStartTime().isBefore(t.getEndTime())) ||
                        (task.getStartTime().isBefore(t.getStartTime())) &&
                                (task.getEndTime().isAfter(t.getStartTime())) ||
                        (task.getStartTime().isBefore(t.getStartTime())) &&
                                (task.getEndTime().isAfter(t.getEndTime())) ||
                        (task.getStartTime().isAfter(t.getStartTime())) &&
                                (task.getEndTime().isBefore(t.getEndTime())) ||
                        (task.getEndTime().isAfter(t.getStartTime())) &&
                                (task.getEndTime().isBefore(t.getEndTime())) ||
                        (task.getEndTime().equals(t.getEndTime())))
                .map(Task::getId)
                .toList();

        if (!clashingTasks.isEmpty()) {
            throw new TaskTimeException("Время задачи под id " + task.getId() + " пересекается с задачами под id: " +
                    clashingTasks);
        } else return false;
    }

    @Override
    public List<Task> getTaskHistory() {
        return historyManager.getHistory();
    }

    //Comparator<LocalDateTime> startTimeComparator = Comparator.comparing(LocalDateTime::from);

    private LocalDateTime getMinimalStartTime(Epic epic) {
        return epic.getSubTasksId().stream()
                .map(subTasks::get)
                .map(Task::getStartTime)
                .sorted(Comparator.comparing(LocalDateTime::from))
                .toList()
                .get(0);
    }
}
