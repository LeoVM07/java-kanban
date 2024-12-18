import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyOfTasks;

    public InMemoryHistoryManager () {
        this.historyOfTasks = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyOfTasks;
    }

    //метод проверяет размер списка при добавлении нового элемента и удаляет самый, если размер равен 10
    @Override
    public List<Task> add(Task task) {
        if (historyOfTasks.size() == 10) {
            for (int i = 1; i < 10; i++) {
                historyOfTasks.set(i-1, historyOfTasks.get(i));
            }
            historyOfTasks.removeLast();
        }
        historyOfTasks.add(task);
        return historyOfTasks;
    }

}
