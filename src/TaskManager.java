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
        if (!tasks.isEmpty()) {
            return new ArrayList<>(tasks.values());
        } else {
            System.out.println("Список задач пуст");
            return null;
        }
    }

    public HashMap<Integer, Task> clearTasks() {
        tasks.clear();
        System.out.println("Все задачи очищены!");
        return tasks;
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Введен некорректный id");
            return null;
        }
    }

    public Task addTask(Task task) {
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        System.out.println("Задача " + newTask.getName() + " под id " + newTask.getId() + " добавлена");
        return task;
    }

    public Task updateTask(Task task, Task.Status status) {
        if (tasks.containsKey(task.getId())) {
            Task currentTask = tasks.get(task.getId());
            currentTask.setStatus(status);
            tasks.put(currentTask.getId(), currentTask);
            System.out.println("Задача " + task.getName() + " под id " + task.getId() + " обновлена");
            return task;
        } else {
            System.out.println("Такой задачи ещё нет");
            return null;
        }
    }

    public Task removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            System.out.println("Задача " + tasks.get(id).getName() + " удалена");
            tasks.remove(id);
            return tasks.get(id);
        } else {
            System.out.println("Введён некорректный id");
            return null;
        }
    }

    //*******ЭПИКИ*******
    public ArrayList<Epic> getAllEpics() {
        if (!epics.isEmpty()) {
            return new ArrayList<>(epics.values());
        } else {
            System.out.println("Список эпиков пуст");
            return null;
        }
    }

    public HashMap<Integer, Epic> clearEpics() {
        epics.clear();
        System.out.println("Эпики очищены!");
        return epics;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic currentEpic = epics.get(epic.getId());
            currentEpic.setName(epic.getName());
            currentEpic.setDescription(epic.getDescription());
            currentEpic.subTasksStatusCheck();
            System.out.println("Эпик " + currentEpic.getName() + " обновлён");
            return epic;
        } else {
            System.out.println("Такого эпика ешё нет");
            return null;
        }
    }

    public Epic removeEpicById(int id) {
        if (epics.containsKey(id)) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                if (subTask.getEpicId() == id) {
                    subTasks.remove(subTask.getId());
                }
            }
            System.out.println("Эпик " + epics.get(id).getName() + " и все его подзадачи удалены");
            epics.remove(id);
            return epics.get(id);

        } else {
            System.out.println("Введён некорректный id");
            return null;
        }
    }

    public Epic addEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(generateId());
        newEpic.subTasksStatusCheck();
        epics.put(newEpic.getId(), newEpic);
        System.out.println("Эпик " + newEpic.getName() + " под id " + newEpic.getId() + " добавлен!");
        return epic;
    }

    public ArrayList<SubTask> getExactEpicSubTasks(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getSubTasks();
        } else {
            System.out.println("Введён некорректный id");
            return null;
        }
    }

    public Task.Status getEpicStatusById(int id) {
        return epics.get(id).getStatus();
    }


    //*******САБТАСКИ*******
    public ArrayList<SubTask> getAllSubTasks() {
        if (!subTasks.isEmpty()) {
            return new ArrayList<>(subTasks.values());
        } else {
            System.out.println("Подзадач пока что нет");
            return null;
        }
    }

    public HashMap<Integer, SubTask> clearSubTasks() {
        subTasks.clear();
        System.out.println("Все подзадачи удалены");
        return subTasks;
    }

    public SubTask addSubTaskToEpicById(SubTask subTask, int epicId) {
        if (epics.containsKey(epicId)) {

            Epic epic = epics.get(epicId);
            subTask.setId(generateId());
            subTask.setEpicId(epicId);
            subTask.setEpicName(epics.get(epicId).getName());
            subTasks.put(subTask.getId(), subTask);
            epic.addSubTask(subTask);
            epic.subTasksStatusCheck();

            System.out.println("Подзадача " + subTask.getName() + " c id " + subTask.getId() + " добавлена в эпик "
                    + subTask.getEpicName() + " под id " + subTask.getEpicId());
            return subTask;

        } else {
            System.out.println("Введён некорректный id эпика");
            return null;
        }
    }

    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            System.out.println("Введён некорректный id");
            return null;
        }
    }

    public SubTask removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            System.out.println("Подзадача " + subTasks.get(id).getName() + " удалена");
            subTasks.remove(id);
            return subTasks.get(id);
        } else {
            System.out.println("Введён неверный id");
            return null;
        }
    }

    public SubTask updateSubTask(SubTask subTask, Task.Status status) {
        if (subTasks.containsValue(subTask)) {
            SubTask currentSubTask = subTasks.get(subTask.getId());
            currentSubTask.setStatus(status);
            subTasks.put(currentSubTask.getId(), currentSubTask);
            Epic epic = epics.get(currentSubTask.getEpicId());
            epic.subTasksStatusCheck();
            return subTask;
        } else {
            System.out.println("Такой подзадачи ещё нет");
            return null;
        }
    }

    private int generateId() {
        return id++;
    }

}