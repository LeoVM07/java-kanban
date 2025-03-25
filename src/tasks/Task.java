package tasks;

import managers.DTF;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, Duration duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = LocalDateTime.now();
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public enum Status {
        NEW("NEW"),
        IN_PROGRESS("IN_PROGRESS"),
        DONE("DONE");

        private final String taskStatus;

        Status(String taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getTaskStatus() {
            return taskStatus;
        }


    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public String taskToString() {
        return String.format("%s, %s, %s, %s, %s, -, %s, %s, %s", id, TaskType.TASK, name, status,
                description, duration.toMinutes(), startTime.format(DTF.getFormatter()),
                getEndTime().format(DTF.getFormatter()));
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                "min., startTime=" + startTime.format(DTF.getFormatter()) +
                ", endTime=" + getEndTime().format(DTF.getFormatter()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
