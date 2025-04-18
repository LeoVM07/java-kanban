package tasks;

import managers.DTF;

import java.time.Duration;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksId; //в списке будут храниться только id подзадач для сокращения расходов памяти

    public Epic(String name, String description) {
        super(name, description, Duration.ofMinutes(0));
        this.subTasksId = new ArrayList<>();
        this.setStatus(Status.NEW);
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    public void addSubTaskId(Integer subTaskId) {
        subTasksId.add(subTaskId);
    }

    @Override
    public String taskToString() {
        return String.format("%s, %s, %s, %s, %s, -, %s, %s, %s,",
                getId(), TaskType.EPIC, getName(), getStatus(), getDescription(), getDuration().toMinutes(),
                getStartTime().format(DTF.getFormatter()), getEndTime().format(DTF.getFormatter()));
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", subTasksId.size=" + subTasksId.size() +
                ", duration=" + getDuration() +
                "min., startTime=" + getStartTime().format(DTF.getFormatter()) +
                ", endTime=" + getEndTime().format(DTF.getFormatter()) +
                '}';
    }
}

