import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void subTasksStatusCheck() { //метод ставит статус эпика в соответствии с состоянием всех его подзадач
        int statusCount = 0;
        if (subTasks.isEmpty()) {
            setStatus(Status.NEW);
        }
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                setStatus(Status.IN_PROGRESS);
                break;
            }
            if (subTask.getStatus() == Status.DONE) {
                statusCount++;
            }
            if (statusCount == subTasks.size()) {
                setStatus(Status.DONE);
            } else if (statusCount > 0 && statusCount < subTasks.size()) {
                setStatus(Status.IN_PROGRESS);
            } else {
                setStatus(Status.NEW);
            }
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", subTasks.size=" + subTasks.size() +
                '}';
    }


}
