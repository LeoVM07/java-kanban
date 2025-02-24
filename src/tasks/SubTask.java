package tasks;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String taskToString() {
        return String.format("%s, %s, %s, %s, %s, %s", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(),
                getEpicId());
    }

    @Override
    public String toString() {
        return "Tasks.SubTask{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }
}
