import java.util.List;

public interface HistoryManager {

    List<Task> add(Task task);

    List<Task> getHistory();
}
