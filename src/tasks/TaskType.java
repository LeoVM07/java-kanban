package tasks;

public enum TaskType {
    TASK("TASK"),
    EPIC("EPIC"),
    SUBTASK("SUBTASK");

    private final String taskType;

    TaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
