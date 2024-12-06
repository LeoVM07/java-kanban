import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksId; //в списке будут храниться только id подзадач для сокращения расходов памяти

    public Epic(String name, String description) {
        super(name, description);
        this.subTasksId = new ArrayList<>();
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
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", subTasksId.size=" + subTasksId.size() +
                '}';
    }
}

