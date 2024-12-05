public class SubTask extends Task {

    private int epicId;
    private String epicName;

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public String getEpicName() {
        return epicName;
    }


    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", epicId=" + epicId +
                ", epicName=" + getEpicName() +
                ", status=" + getStatus() +
                '}';
    }
}
