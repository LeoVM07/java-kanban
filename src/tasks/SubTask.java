package tasks;

import managers.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
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
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s,", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(),
                getEpicId(), getDuration().toMinutes(), getStartTime().format(DTF.getFormatter()),
                getEndTime().format(DTF.getFormatter()));
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                "min., startTime=" + getStartTime().format(DTF.getFormatter()) +
                ", endTime=" + getEndTime().format(DTF.getFormatter()) +
                '}';
    }
}
