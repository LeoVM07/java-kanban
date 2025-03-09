package managers;

import exceptions.ManagerSaveException;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File data;

    public FileBackedTaskManager(File data) {
        this.data = data;
    }

    //********ТАСКИ********
    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public Task removeTaskById(int id) {
        Task newTask = super.removeTaskById(id);
        save();
        return newTask;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    //********ЭПИКИ********
    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = super.updateEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic removeEpicById(int id) {
        Epic newEpic = super.removeEpicById(id);
        save();
        return newEpic;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    //********САБТАСКИ********
    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask newSubTask = super.addSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask newSubTask = super.updateSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public SubTask removeSubTaskById(int id) {
        SubTask newSubTask = super.removeSubTaskById(id);
        save();
        return newSubTask;
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        ArrayList<String> loadedTasks = getTaskArray(file);

        for (String task : loadedTasks) {
            String[] splitedTask = task.trim().split(", ");
            String taskType = splitedTask[1];
            int taskId = Integer.parseInt(splitedTask[0]);

            switch (taskType) {
                case "TASK":
                    Task newTask = new Task(splitedTask[2], splitedTask[4], getStatusFromString(splitedTask[3]),
                            Duration.ofMinutes(Long.parseLong(splitedTask[6])),
                            LocalDateTime.parse(splitedTask[7], DTF.getFormatter()));
                    newTask.setId(taskId);
                    loadedManager.tasks.put(taskId, newTask);
                    break;

                case "EPIC":
                    Epic newEpic = new Epic(splitedTask[2], splitedTask[4]);
                    newEpic.setId(taskId);
                    newEpic.setStatus(getStatusFromString(splitedTask[3]));
                    newEpic.setDuration(Duration.ofMinutes(Long.parseLong(splitedTask[6])));
                    newEpic.setStartTime(LocalDateTime.parse(splitedTask[7], DTF.getFormatter()));
                    loadedManager.epics.put(taskId, newEpic);
                    break;

                case "SUBTASK":
                    int epicID = Integer.parseInt(splitedTask[5]);
                    SubTask newSubTask = new SubTask(splitedTask[2], splitedTask[4], getStatusFromString(splitedTask[3]),
                            epicID, Duration.ofMinutes(Long.parseLong(splitedTask[6])),
                            LocalDateTime.parse(splitedTask[7], DTF.getFormatter()));
                    newSubTask.setStatus(getStatusFromString(splitedTask[3]));
                    newSubTask.setId(taskId);

                    Optional<Epic> epicOpt = loadedManager.getEpicById(epicID);
                    if (epicOpt.isPresent()) {
                        epicOpt.get().addSubTaskId(taskId); //добавляем эпику ID соответствующего сабтаска
                        loadedManager.subTasks.put(taskId, newSubTask);
                        break;
                    } else {
                        throw new ManagerSaveException("Эпика с id " + epicID + " не существует!");
                    }
            }
        }

        //Код ниже выставляет текущий id задачи в соответствии с размером списка загруженных задач
        loadedManager.setManagerID(loadedTasks.size() + 3);
        return loadedManager;
    }

    //Метод пробегает по строкам из файла и собирает их в список для дальнейшего извлечения из него задач
    public static ArrayList<String> getTaskArray(File file) {
        ArrayList<String> tasksFromFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String taskAsString = br.readLine();
                tasksFromFile.add(taskAsString);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        tasksFromFile.removeFirst(); //для удаления заголовка с обозначениями
        return tasksFromFile;
    }


    //Метод конвертирует строчное обозначение статуса задачи в необходимый тип
    public static Task.Status getStatusFromString(String status) {
        return switch (status) {
            case "NEW" -> Task.Status.NEW;
            case "IN_PROGRESS" -> Task.Status.IN_PROGRESS;
            case "DONE" -> Task.Status.DONE;
            default -> throw new RuntimeException("Ошибка статуса задачи");
        };
    }

    private void save() {
        ArrayList<String> tasksAsStrings = new ArrayList<>();
        try {
            for (Task savedTask : tasks.values()) {
                String taskToSave = savedTask.taskToString();
                tasksAsStrings.add(taskToSave);
            }

            for (Epic savedEpic : epics.values()) {
                String epicToSave = savedEpic.taskToString();
                tasksAsStrings.add(epicToSave);
            }

            for (SubTask savedSubTask : subTasks.values()) {
                String subTaskToSave = savedSubTask.taskToString();
                tasksAsStrings.add(subTaskToSave);
            }
            writeStringsToFile(tasksAsStrings);
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    //Метод принимает список задач, конвертированных в строчный тип, и заносит их в созданный файл
    private void writeStringsToFile(ArrayList<String> tasksAsStrings) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(data))) {

            String title = "id, type, name, status, description, epicID, duration, startTime, endTime";// Первая строка -- заголовок файла
            bw.write(title);
            bw.newLine();

            for (String line : tasksAsStrings) {
                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            String errorMessage = "Не удалось сохранить файл" + e.getMessage();
            System.out.println(errorMessage);
            throw new ManagerSaveException(errorMessage);
        }
    }

    //При загрузке задач из файла необходимо выровнять ID задач во избежание ошибок, для чего и был реализован этот метод
    private void setManagerID(int id) {
        this.id = id;
    }
}
